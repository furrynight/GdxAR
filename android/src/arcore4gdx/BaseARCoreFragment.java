/*
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
 * NOTE:
 * This file is created with references from BaseArCoreActivity by Clayton Wilkinson
 * and AndroidFragmentApplication.java of LibGDX
 */

package arcore4gdx;

/********************************************************************************
 ********************************************************************************
 ** MODULE:          BaseArCoreFragment.java
 ** ABBREVIATION:
 ** COMPILER:
 ** LANGUAGE:
 ** AUTHOR:         Farell Leiking
 ** ABSTRACT:
 ** PREMISES:
 ** REMARKS:
 ** HISTORY:        2018-05-09 : Creation
 **                 2018-05-16 : (FL) Defined isARAvailable()
 ** REVIEW:
 ********************************************************************************/

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;

import arcore4gdx.util.ARSessionSupport;


public class BaseARCoreFragment extends AndroidFragmentApplication implements
                                ARSessionSupport.StatusChangeListener,
                                ARSessionSupport.ARSessionInterface
{
    private final String        DEBUG_CLASS_NAME    = BaseARCoreFragment.class.toString();

    // ARCore specific stuff
    private ARSessionSupport    sessionSupport;
    private Snackbar            messageSnackbar;

    private boolean             m_bInstallARCore    = true;


    /**
     * Gets the ARCore session.  It can be null if the
     * permissions were not granted by the user or if the device does not support ARCore.
     */
    @NonNull
    public ARSessionSupport getSessionSupport()
    {
        return sessionSupport;
    }

    /*
     *
     */
    @Override
    public void onStatusChanged()
    {
        if (getSessionSupport().getStatus() != ARSessionSupport.ARStatus.Ready)
        {
            //showSnackbarMessage("Error with ARCore: " + getSessionSupport().getStatus(), true);
        }
    }

    /*
     *
     */
    public void setARCoreInstallAllow(boolean isAllowed)
    {
        m_bInstallARCore = isAllowed;
    }

    /*
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sessionSupport      = new ARSessionSupport(this.getActivity(), getLifecycle(), this, m_bInstallARCore);
    }

    /*
     *
     */
    @Override
    public void onStart()
    {
        super.onStart();
    }

    /*
     *
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    /*
     *
     */
    @Override
    public void onPause()
    {
        super.onPause();
    }

    /*
     *
     */
    @Override
    public void onResume()
    {
        super.onResume();
    }

    /*
     *
     */
    @Override
    public View initializeForView(ApplicationListener listener, AndroidApplicationConfiguration config)
    {
        /*
        if (this.getVersion() < MINIMUM_SDK)
        {
            throw new GdxRuntimeException( "LibGDX requires Android API Level " + MINIMUM_SDK + " or later." );
        }

        setApplicationLogger(new AndroidApplicationLogger());

        graphics                = new ARCoreGraphics(   this,
                                                        this,
                                                        config,
                                                        (config.resolutionStrategy == null) ? new FillResolutionStrategy() : config.resolutionStrategy
                                                    );

        input                   = AndroidInputFactory.newAndroidInput(this, this.getActivity(), graphics.getView(), config);
        audio                   = new AndroidAudio(this.getActivity(), config);
        this.getActivity().getFilesDir(); // workaround for Android bug #10515463
        files                   = new AndroidFiles(this.getActivity().getResources().getAssets(), this.getActivity().getFilesDir().getAbsolutePath());
        net                     = new AndroidNet(this);
        this.listener           = listener;
        this.handler            = new Handler();
        this.clipboard          = new AndroidClipboard(getActivity());

        // Add a specialized audio lifecycle listener
        addLifecycleListener(new LifecycleListener()
        {
            @Override
            public void resume()
            {
                //audio.resume();
            }

            @Override
            public void pause()
            {
                //audio.pause();
            }

            @Override
            public void dispose ()
            {
                audio.dispose();
            }
        });

        Gdx.app                 = this;
        Gdx.input               = this.getInput();
        Gdx.audio               = this.getAudio();
        Gdx.files               = this.getFiles();
        Gdx.graphics            = this.getGraphics();
        Gdx.net                 = this.getNet();

        createWakeLock(config.useWakelock);
        useImmersiveMode(config.useImmersiveMode);

        if(config.useImmersiveMode && getVersion() >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                Class<?> vlistener = Class.forName("com.badlogic.gdx.backends.android.AndroidVisibilityListener");
                Object o = vlistener.newInstance();
                Method method = vlistener.getDeclaredMethod("createListener", AndroidApplicationBase.class);
                method.invoke(o, this);
            }
            catch (Exception e)
            {
                log("AndroidApplication", "Failed to create AndroidVisibilityListener", e);
            }
        }

        return graphics.getView();
        */

        super.initializeForView( listener, config );
        // mark these null so they get GC as they are assigned / created from parent call
        Gdx.graphics    = null;
        graphics        = null;
        ////////////////////////////////////
        graphics        = new ARCoreGraphics(   this,
                                                config,
                                                (config.resolutionStrategy == null) ? new FillResolutionStrategy() : config.resolutionStrategy
                                            );

        Gdx.graphics    = this.getGraphics();

        return graphics.getView();
    }


    /*
     *
     */
    public boolean isARAvailable()
    {
        return (sessionSupport.getSession() != null);
    }


    /********************************************************************************
     *                            PROTECTED                                         *
     ********************************************************************************/



    /********************************************************************************
     *                              PRIVATE                                         *
     ********************************************************************************/

    /*
     *
     */
    private void showSnackbarMessage(String message, boolean finishOnDismiss)
    {
        messageSnackbar = Snackbar.make( this.getActivity().getWindow().getDecorView(),
                message + "\n",
                Snackbar.LENGTH_INDEFINITE);

        messageSnackbar.getView().setBackgroundColor(0xbf323232);

        if(finishOnDismiss)
        {
            messageSnackbar.setAction(
                    "Dismiss",
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            messageSnackbar.dismiss();
                        }
                    });

            messageSnackbar.addCallback(
                    new BaseTransientBottomBar.BaseCallback<Snackbar>()
                    {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event)
                        {
                            super.onDismissed(transientBottomBar, event);
                            exit();
                        }
                    });
        }

        messageSnackbar.show();
    }
}
