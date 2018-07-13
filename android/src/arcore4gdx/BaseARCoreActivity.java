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
 *      2018-05-10  -   Farell Leiking  :   Modified to implement ARSessionInterface
 *      2018-05-16  -   Farell Leiking  :   Defined isARAvailable()
 */

package arcore4gdx;


import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationBase;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidApplicationLogger;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AndroidClipboard;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.backends.android.AndroidInputFactory;
import com.badlogic.gdx.backends.android.AndroidNet;
import com.badlogic.gdx.backends.android.surfaceview.FillResolutionStrategy;
import com.badlogic.gdx.utils.GdxRuntimeException;


import java.lang.reflect.Method;

import arcore4gdx.util.ARSessionSupport;

/**
 * Android Activity subclass that handles initializing ARCore and the underlying graphics engine
 * used for drawing 3d and 2d models in the context of the ARCore Frame. This class is based on the
 * libgdx library for Android game development.
 */
public class BaseARCoreActivity extends AndroidApplication
                                implements LifecycleOwner,
                                ARSessionSupport.StatusChangeListener,
                                ARSessionSupport.ARSessionInterface
{
    private final String        DEBUG_CLASS_NAME    = BaseARCoreActivity.class.toString();

    // ARCore specific stuff
    private ARSessionSupport    sessionSupport;
    private Snackbar            messageSnackbar;

    // Implement the LifecycleOwner interface since AndroidApplication does not extend AppCompatActivity.
    // All this means is forward the events to the lifecycleRegistry object.
    private LifecycleRegistry   lifecycleRegistry;

    /**
     * Gets the ARCore session.  It can be null if the
     * permissions were not granted by the user or if the device does not support ARCore.
     */
    @NonNull
    public ARSessionSupport getSessionSupport()
    {
        return sessionSupport;
    }

    /**
     * This method has to be called in the {@link Activity#onCreate(Bundle)} method. It sets up all
     * the things necessary to get input, render via OpenGL and so on. You can configure other aspects
     * of the application with the rest of the fields in the {@link AndroidApplicationConfiguration}
     * instance.
     *
     * @param listener the {@link ApplicationListener} implementing the program logic
     * @param config   the {@link AndroidApplicationConfiguration}, defining various settings of the
     *                 application (use accelerometer, etc.).
     */
    public void initialize(ApplicationListener listener, AndroidApplicationConfiguration config)
    {
        init(listener, config, false);
    }

    /*
     *
     */
    @NonNull
    @Override
    public Lifecycle getLifecycle()
    {
        return lifecycleRegistry;
    }

    /*
     *
     */
    @Override
    public void onStatusChanged()
    {
        if (getSessionSupport().getStatus() != ARSessionSupport.ARStatus.Ready)
        {
            showSnackbarMessage("Error with ARCore: " + getSessionSupport().getStatus(), true);
        }
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

    /*
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        lifecycleRegistry   = new LifecycleRegistry(this);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
        sessionSupport      = new ARSessionSupport(this, lifecycleRegistry, this);
    }

    /*
     *
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    /*
     *
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    /*
     *
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
    }

    /*
     *
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        lifecycleRegistry.markState(Lifecycle.State.RESUMED);
    }



    /********************************************************************************
     *                              PRIVATE                                         *
     ********************************************************************************/

    /*
     *
     */
    private void showSnackbarMessage(String message, boolean finishOnDismiss)
    {
        messageSnackbar = Snackbar.make( getWindow().getDecorView(),
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
                        finish();
                    }
                });
        }

        messageSnackbar.show();
    }

    /*
     *
     */
    private void init(ApplicationListener listener, AndroidApplicationConfiguration config, boolean isForView)
    {
        if (this.getVersion() < MINIMUM_SDK)
        {
            throw new GdxRuntimeException( "LibGDX requires Android API Level " + MINIMUM_SDK + " or later." );
        }

        setApplicationLogger(new AndroidApplicationLogger());

        graphics                = new ARCoreGraphics(   this,
                                                        config,
                                                        (config.resolutionStrategy == null) ? new FillResolutionStrategy() : config.resolutionStrategy
                                                    );

        input                   = AndroidInputFactory.newAndroidInput(this, this, graphics.getView(), config);
        audio                   = new AndroidAudio(this, config);
        this.getFilesDir(); // workaround for Android bug #10515463
        files                   = new AndroidFiles(this.getAssets(), this.getFilesDir().getAbsolutePath());
        net                     = new AndroidNet(this);
        this.listener           = listener;
        this.handler            = new Handler();
        this.useImmersiveMode   = config.useImmersiveMode;
        this.hideStatusBar      = config.hideStatusBar;
        this.clipboard          = new AndroidClipboard(this);

        // Add a specialized audio lifecycle listener
        addLifecycleListener(
            new LifecycleListener()
            {
                @Override
                public void resume()
                {
                    // No need to resume audio here
                }

                @Override
                public void pause()
                {
                    //     audio.pause();
                }

                @Override
                public void dispose()
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

        if(!isForView)
        {
            try
            {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
            catch (Exception ex)
            {
                log(DEBUG_CLASS_NAME, "Content already displayed, cannot request FEATURE_NO_TITLE", ex);
            }

            getWindow().setFlags(   WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                                );

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            setContentView(graphics.getView(), createLayoutParams());
        }

        createWakeLock(config.useWakelock);
        hideStatusBar(this.hideStatusBar);
        useImmersiveMode(this.useImmersiveMode);

        if(this.useImmersiveMode && getVersion() >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                Class<?> vlistener  = Class.forName("com.badlogic.gdx.backends.android.AndroidVisibilityListener");
                Object o            = vlistener.newInstance();
                Method method       = vlistener.getDeclaredMethod("createListener", AndroidApplicationBase.class);
                method.invoke(o, this);
            }
            catch (Exception e)
            {
                log(DEBUG_CLASS_NAME, "Failed to create AndroidVisibilityListener", e);
            }
        }
    }
}

