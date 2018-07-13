/*
 * Copyright 2017 Google Inc.
 * Copyright 2018 Farell Leiking
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 *  This is a Derivative Works copy
 *  Original Author
 *      Clayton Wilkinson
 *  Whitepaper
 *      https://medium.com/@wilkinsonclay/investigating-arcore-with-libgdx-f69b83764118
 *  Source(s)
 *      https://github.com/google/helloargdx
 *
 *  Modification History
 *      2018-05-09  -   Farell Leiking  :   Modified code formats ( i.e. indents, spaces etc. )
 *                                          and placements
 *      2018-05-10  -   Farell Leiking  :   Added interface ARSessionInterface
 *      2018-05-16  -   Farell Leiking  :   Added function isARAvailable() to interface ARSessionInterface
 *                                          Added flag m_bAllowARCoreInstall to disable the request to install
 *                                          ARCore, new constructor with a 4 parameter is added to set this flag
 */

package arcore4gdx.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.SessionPausedException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import arcore4gdx.helper.CameraPermissionHelper;


/**
 * ARCore session creation is a complex flow of exception handling, permission requesting, and
 * possible downloading of the ARCore services APK.  This class encapsulates all this.
 * <p>
 * To use this class create an instance in onCreate().
 */
public class ARSessionSupport implements LifecycleObserver
{
    private static final                String TAG = ARSessionSupport.class.toString();
    private final Activity              activity;
    private Session                     session;
    private ARStatus                    status;
    private StatusChangeListener        statusListener;
    private int                         textureId;
    private int                         rotation;
    private int                         width;
    private int                         height;
    private boolean                     mUserRequestedInstall;
    private boolean                     m_bAllowARCoreInstall;  // flag to allow to install ARCore if not installed
                                                                // This option is given for users who require that
                                                                // that ARCore not be (asked to) automatically downloaded
                                                                // Useful for phones that have not setup their Google account
                                                                // and want to manually install ARCore from github releases


    /**
     * Interface for listening for status changes. This can be used for showing error messages
     * to the user.
     */
    public interface StatusChangeListener
    {
        void onStatusChanged();
    }

    /**
     * Callback interface to retrieve ARSessionSupport object
     */
    public interface ARSessionInterface
    {
        ARSessionSupport    getSessionSupport();
        boolean             isARAvailable();
    }

    /*
     *
     */
    public enum ARStatus
    {
        ARCoreNotInstalled
        ,ARCoreTooOld
        ,SDKTooOld
        ,UnknownException
        ,NeedCameraPermission
        ,Ready
        ,DeviceNotSupported
        ,Uninitialized
    }

    /*
     *
     */
    public ARSessionSupport(Activity activity, Lifecycle lifecycle, StatusChangeListener listener)
    {
        this(activity, lifecycle, listener, true);
    }

    /*
     *
     */
    public ARSessionSupport(Activity activity, Lifecycle lifecycle, StatusChangeListener listener, boolean allowToInstall)
    {
        this.activity           = activity;
        setStatus(ARStatus.Uninitialized);
        lifecycle.addObserver(this);

        // Handle graphics initialization during the ARCore startup.
        textureId               = -1;
        rotation                = -1;
        width                   = -1;
        height                  = -1;
        statusListener          = listener;
        mUserRequestedInstall   = true;
        m_bAllowARCoreInstall   = allowToInstall;
    }

    /**
     * Gets the current status of the ARCore Session.
     */
    public ARStatus getStatus()
    {
        return status;
    }

    /*
     *
     */
    @Nullable
    public Session getSession()
    {
        return session;
    }

    /**
     * Handle setting the display geometry.  The values are cached
     * if they are set before the session is available.
     */
    public void setDisplayGeometry(int rotation, int width, int height)
    {
        if(session != null)
        {
            session.setDisplayGeometry(rotation, width, height);
        }
        else
        {
            this.rotation   = rotation;
            this.width      = width;
            this.height     = height;
        }
    }

    /**
     * Handle setting the texture ID for the background image.  The value is cached
     * if it is called before the session is available.
     */
    public void setCameraTextureName(int textureId)
    {
        if(session != null)
        {
            session.setCameraTextureName(textureId);
            this.textureId  = -1;
        }
        else
        {
            this.textureId  = textureId;
        }
    }

    /*
     *
     */
    @Nullable
    public Frame update()
    {
        Frame frameSession = null;

        if(session != null)
        {
            try
            {
                frameSession = session.update();
            }
            catch(CameraNotAvailableException exp)
            {
            }
            catch(SessionPausedException exp)
            {
            }
        }

        return frameSession;
    }

    /**
     * PermissionFragment handles requesting the camera permission.
     */
    public static class PermissionFragment extends Fragment
    {
        static final String         TAG     = "PermissionFragment";
        private ARSessionSupport    arSessionSupport;

        /*
         *
         */
        public void setArSessionSupport(ARSessionSupport arSessionSupport)
        {
            this.arSessionSupport = arSessionSupport;
        }

        /*
         *
         */
        @Override
        public void onAttach(Context context)
        {
            super.onAttach(context);
            CameraPermissionHelper.requestCameraPermission(this);
        }

        /*
         *
         */
        @Override
        public void onRequestPermissionsResult( int requestCode,
                                                @NonNull String[] permissions,
                                                @NonNull int[] results)
        {
            if (!CameraPermissionHelper.hasCameraPermission(getActivity()))
            {
                arSessionSupport.setStatus(ARStatus.NeedCameraPermission);
            }
            else
            {
                arSessionSupport.initializeARCore();
            }

            FragmentManager mgr         = getActivity().getFragmentManager();
            FragmentTransaction trans   = mgr.beginTransaction();
            trans.remove(this);
            trans.commit();
        }
    }



    /********************************************************************************
     *                            PROTECTED                                         *
     ********************************************************************************/

    /**
     * Handle the onResume event.  This checks the permissions and
     * initializes ARCore.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onResume()
    {
        if ( CameraPermissionHelper.hasCameraPermission(activity))
        {
            if (session == null)
            {
                initializeARCore();
            }
            else
            {
                try
                {
                    session.resume();
                }catch(CameraNotAvailableException exp) {}
            }
        }
        else
        {
            requestCameraPermission();
        }
    }

    /*
     *
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected void onPause()
    {
        if (session != null)
        {
            session.pause();
        }
    }

    /*
     *
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onStop()
    {
        statusListener = null;
    }



    /********************************************************************************
     *                              PRIVATE                                         *
     ********************************************************************************/

    /**
     * Start a fragment to handle the permissions.  This is done in a fragment to
     * avoid entangling it with the base Activity.
     */
    private void requestCameraPermission()
    {
        PermissionFragment fragment = new PermissionFragment();
        fragment.setArSessionSupport(this);
        FragmentManager mgr         = activity.getFragmentManager();
        FragmentTransaction trans   = mgr.beginTransaction();
        trans.add(fragment, PermissionFragment.TAG);
        trans.commit();
    }

    /*
     *
     */
    private void setStatus(ARStatus newStatus)
    {
        status = newStatus;
        if (statusListener != null)
        {
            statusListener.onStatusChanged();
        }
    }

    /*
     *
     */
    private void initializeARCore()
    {
        Exception               exception       = null;
        String                  message         = null;

        ArCoreApk.Availability  availability    = ArCoreApk.getInstance().checkAvailability(activity);
        Log.d(TAG, "Availability is " + availability);

        if( (availability != ArCoreApk.Availability.SUPPORTED_INSTALLED && availability != ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD) && !m_bAllowARCoreInstall)
        {
            // since ARCore is not installed or needs updating and flag m_bAllowARCoreInstall is false
            // thus not allowed to install / update, so return
            return;
        }

        try
        {
            ArCoreApk.InstallStatus installStatus = ArCoreApk.getInstance().requestInstall(activity, mUserRequestedInstall);

            if(  installStatus ==  ArCoreApk.InstallStatus.INSTALL_REQUESTED)
            {
                // Ensures next invocation of requestInstall() will either return
                // INSTALLED or throw an exception.
                mUserRequestedInstall = false;
                return;
            }

            session = new Session(activity);
        }
        catch (UnavailableArcoreNotInstalledException e)
        {
            setStatus(ARStatus.ARCoreNotInstalled);
            message     = "Please install ARCore";
            exception   = e;
        }
        catch (UnavailableApkTooOldException e)
        {
            setStatus(ARStatus.ARCoreTooOld);
            message     = "Please update ARCore";
            exception   = e;
        }
        catch (UnavailableSdkTooOldException e)
        {
            message     = "Please update this app";
            setStatus(ARStatus.SDKTooOld);
            exception   = e;
        }
        catch (UnavailableDeviceNotCompatibleException e)
        {
            setStatus(ARStatus.DeviceNotSupported);
            message     = "This device does not support AR";
            exception   = e;
        }
        catch (Exception e)
        {
            setStatus(ARStatus.UnknownException);
            message     = "This device does not support AR";
            exception   = e;
        }

        if (message != null)
        {
            Log.e(TAG, "Exception creating session", exception);
            return;
        }

        // Create default config and check if supported.
        Config defaultConfig = new Config(session);

        if(!session.isSupported(defaultConfig))
        {
            Log.w(TAG, "Configuration is not supported");
        }
        else
        {
            session.configure(defaultConfig);
        }

        // Set the graphics information if it was already passed in.
        if (textureId >= 0)
        {
            setCameraTextureName(textureId);
        }

        if (width > 0)
        {
            setDisplayGeometry(rotation, width, height);
        }

        try
        {
            session.resume();
        }catch(CameraNotAvailableException exp) {}

        setStatus(ARStatus.Ready);
    }
}