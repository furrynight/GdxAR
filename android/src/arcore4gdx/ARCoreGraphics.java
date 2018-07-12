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
 *      2018-05-16  -   Farell Leiking  :   Modified BaseARCoreActivity to be Context and all the
 *                                          calling of application.getSessionSupport() convert to
 *                                          use callback interface ARSessionInterface
 */

package arcore4gdx;

import android.content.Context;
import android.view.Surface;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplicationBase;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.google.ar.core.Frame;


import java.util.concurrent.atomic.AtomicReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import arcore4gdx.helper.BackgroundRendererHelper;
import arcore4gdx.util.ARSessionSupport.ARSessionInterface;

/**
 * Extended AndroidGraphics that is ARCore aware. This handles creating an OES Texture an passing it
 * to the ARCore session.
 */
public class ARCoreGraphics extends AndroidGraphics
{
    // TODO: refactor session to this class
    private Context                     context;
    private BackgroundRendererHelper    mBackgroundRenderer;
    private AtomicReference<Frame>      mCurrentFrame;
    private ARSessionInterface          m_arSessionInterface;

    /*
     *
     */
    public ARCoreGraphics(  AndroidApplicationBase              arCoreApplication,
                            AndroidApplicationConfiguration     config,
                            ResolutionStrategy                  resolutionStrategy  )
    {
        super(arCoreApplication, config, resolutionStrategy);
        context                 = arCoreApplication.getContext();

        if(m_arSessionInterface == null)
        {
            try
            {
                m_arSessionInterface = ( ARSessionInterface ) arCoreApplication;
            }catch (ClassCastException exp) { m_arSessionInterface = null; }
        }

        mBackgroundRenderer     = new BackgroundRendererHelper();
        mCurrentFrame           = new AtomicReference<>(null);
    }

    /*
     *
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        super.onSurfaceChanged(gl, width, height);

        WindowManager mgr = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            mgr = context.getSystemService(WindowManager.class);
        }

        int rotation = Surface.ROTATION_0;

        if (mgr != null)
        {
            rotation = mgr.getDefaultDisplay().getRotation();
        }

        if(m_arSessionInterface != null)
        {
            m_arSessionInterface.getSessionSupport().setDisplayGeometry( rotation, width, height );
        }
    }

    /*
     *
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        super.onSurfaceCreated(gl, config);
        mBackgroundRenderer.createOnGlThread(context);
        if(m_arSessionInterface != null)
        {
            m_arSessionInterface.getSessionSupport().setCameraTextureName( mBackgroundRenderer.getTextureId() );
        }
    }

    /*
     *
     */
    @Override
    public void onDrawFrame(GL10 gl)
    {
        super.onDrawFrame(gl);
        mCurrentFrame.set(null);
    }

    /*
     *
     */
    public int getBackgroundTexture()
    {
        return mBackgroundRenderer.getTextureId();
    }

    /*
     *
     */
    public float[] getBackgroundVertices(Frame frame)
    {
        return mBackgroundRenderer.getVertices(frame);
    }

    /**
     * Returns the current ARCore frame.  This is reset at the end of the render loop.
     */
    public Frame getCurrentFrame()
    {
        if((mCurrentFrame.get()) == null && (m_arSessionInterface != null))
        {
            mCurrentFrame.compareAndSet(null, m_arSessionInterface.getSessionSupport().update());
        }

        return mCurrentFrame.get();
    }
}