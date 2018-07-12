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
 *      2018-05-10  -   Farell Leiking  :   Modifed class to extends ApplicationAdapter instead
 *                                          of just implements ApplicationListener
 *      2018-05-16  -   Farell Leiking  :   Added boolean m_bRenderAR and functions to enable/disable
 *                                          AR rendering and check if AR rendering is enabled/disabled
 *                                          Added code to resize PerspectiveCamera
 *                                          Added Fade transition when enable/disable AR
 */

package arcore4gdx;


import android.view.View;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;

/**
 * ARCoreScene is the base class for the scene to render. Application specific scenes extend this
 * class to handle input and create and manipulate models which are rendered in a batch at the end
 * of each frame.
 *
 * <p>This class handles the basic boilerplate of rendering the background image, moving the camera
 * based on the ARCore frame pose, and basic batch rendering.
 */
public abstract class ARCoreScene extends ApplicationAdapter
{
    private final float                 VALUE_TRANSPARENT       = 0.0f;
    private final float                 VALUE_OPAQUE            = 1.0f;
    private final float                 VALUE_FADE_IN           = -0.05f;
    private final float                 VALUE_FADE_OUT          = 0.10f;

    // The camera which is controlled by the ARCore pose.
    private     PerspectiveCamera       camera;
    // Renderer for the camera image which is the background for the ARCore app.
    private     BackgroundRenderer      backgroundRenderer;
    // Drawing batch.
    private     ModelBatch              modelBatch;
    private     boolean                 m_bRenderAR;
    private     boolean                 m_bRenderTransition;
    private     Color                   m_cOverlayColor         = new Color(0x000000FF);
    private     float                   m_fOverlayAlpha         = VALUE_OPAQUE;
    private     float                   m_fTransitionVal        = VALUE_FADE_IN;

    protected   int                     m_iScreenWidth;
    protected   int                     m_iScreenHeight;

    protected   OrthographicCamera      m_OrthoCamera;
    protected   ShapeRenderer           m_ShapeRenderer;
    protected   SpriteBatch             m_SpriteBatch;

    /**
     * Called to render the scene and provide the current ARCore frame.
     *
     * @param frame - The ARCore frame.
     */
    protected abstract void render(Frame frame, ModelBatch modelBatch);

    /*
     *
     */
    @Override
    public void create()
    {
        m_iScreenWidth      = Gdx.graphics.getWidth();
        m_iScreenHeight     = Gdx.graphics.getHeight();

        camera              = new PerspectiveCamera(67, m_iScreenWidth, m_iScreenHeight);
        camera.position.set(0, 1.6f, 0f);
        camera.lookAt(0, 0, 1f);
        camera.near         = .01f;
        camera.far          = 30f;
        camera.update();

        m_bRenderAR         = false;
        m_ShapeRenderer     = new ShapeRenderer();
        m_SpriteBatch       = new SpriteBatch();

        backgroundRenderer  = new BackgroundRenderer();

        // TODO(wilkinsonclay): make a better shader provider.
        modelBatch          = new ModelBatch(createShaderProvider());
    }

    /*
     *
     */
    @Override
    public void resize(int width, int height)
    {
        m_iScreenWidth      = width;
        m_iScreenHeight     = height;

        camera              = null;
        camera              = new PerspectiveCamera(67, m_iScreenWidth, m_iScreenHeight);
        camera.position.set(0, 1.6f, 0f);
        camera.lookAt(0, 0, 1f);
        camera.near         = .01f;
        camera.far          = 30f;
        camera.update();

        m_OrthoCamera       = null;
        m_OrthoCamera       = new OrthographicCamera(m_iScreenWidth, m_iScreenHeight);
        m_OrthoCamera.translate(m_iScreenWidth / 2, m_iScreenHeight / 2);
        m_OrthoCamera.update();

        if(m_SpriteBatch != null)
        {
            m_SpriteBatch.setProjectionMatrix( m_OrthoCamera.combined );
        }

        if(m_ShapeRenderer != null)
        {
            m_ShapeRenderer.setProjectionMatrix( m_OrthoCamera.combined );
        }
    }

    /*
     *
     */
    @Override
    public void render()
    {
        // Boiler plater rendering code goes here, the intent is that this sets up the scene object,
        // Application specific rendering should be done from render(Frame).

        ARCoreGraphics arCoreGraphics   = ( ARCoreGraphics ) Gdx.graphics;
        Frame frame                     = (m_bRenderAR || m_bRenderTransition) ? arCoreGraphics.getCurrentFrame() : null;

        //Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Frame can be null when initializing or if ARCore is not supported on this device.
        if(frame != null)
        {
            backgroundRenderer.render(frame);

            Gdx.gl.glDepthMask(true);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glEnable(GL20.GL_CULL_FACE);

            // Move the camera, and then render.
            float vm[] = new float[16];

            frame.getCamera().getProjectionMatrix(vm, 0, camera.near, camera.far);
            camera.projection.set(vm);
            frame.getCamera().getViewMatrix(vm, 0);
            camera.view.set(vm);
            camera.combined.set(camera.projection);
            Matrix4.mul(camera.combined.val, camera.view.val);

            // Here is the rendering batch.
            modelBatch.begin(camera);
            render(frame, modelBatch);
            modelBatch.end();
        }

        if(m_bRenderTransition)
        {
            m_fOverlayAlpha += m_fTransitionVal;

            if(m_fOverlayAlpha > VALUE_OPAQUE)
            {
                m_fOverlayAlpha     = VALUE_OPAQUE;
                m_bRenderTransition = false;
            }
            else
            if(m_fOverlayAlpha < VALUE_TRANSPARENT)
            {
                m_fOverlayAlpha     = VALUE_TRANSPARENT;
                m_bRenderTransition = false;
            }
        }

        Gdx.graphics.getGL20().glEnable( GL20.GL_BLEND );
        Gdx.gl.glBlendFunc( GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA );
        m_ShapeRenderer.begin( ShapeRenderer.ShapeType.Filled );
        m_ShapeRenderer.setColor( m_cOverlayColor.r, m_cOverlayColor.g, m_cOverlayColor.b, m_fOverlayAlpha );
        m_ShapeRenderer.rect( 0, 0, m_iScreenWidth, m_iScreenHeight );
        m_ShapeRenderer.end();
    }

    /*
     *
     */
    @Override
    public void pause()
    {
        super.pause();
    }

    /*
     *
     */
    @Override
    public void resume()
    {
        super.resume();
    }

    /*
     *
     */
    @Override
    public void dispose()
    {
        super.dispose();
    }

    /*
     *
     */
    public void enableAR(boolean isEnable)
    {
        if(m_bRenderAR != isEnable) // if current AR state not same, do the fade transition
        {
            if(m_bRenderAR)
            {
                m_fTransitionVal    = VALUE_FADE_OUT;
            }
            else
            {
                m_fTransitionVal    = VALUE_FADE_IN;
            }

            m_bRenderTransition     = true;
        }

        m_bRenderAR = isEnable;
    }

    /*
     *
     */
    public boolean isAREnabled()
    {
        return m_bRenderAR;
    }



    /********************************************************************************
     *                            PROTECTED                                         *
     ********************************************************************************/

    /**
     * Camera controlled by ARCore. This is used to determine where the user is looking.
     */
    protected PerspectiveCamera getCamera()
    {
        return camera;
    }

    /**
     * Shader provider for creating shaders that are used by custom materials. It is protected access
     * to allow overriding to inject other shaders.
     */
    protected ShaderProvider createShaderProvider()
    {
        return new SimpleShaderProvider();
    }

    /**
     * ARCore session object.
     */
    protected Session getSession()
    {
        return ((BaseARCoreActivity) Gdx.app).getSessionSupport().getSession();
    }

    /**
     * Gets the Android View being used for rendering.
     */
    protected View getView()
    {
        return ((ARCoreGraphics) Gdx.graphics).getView();
    }


    /********************************************************************************
     *                              PRIVATE                                         *
     ********************************************************************************/
}