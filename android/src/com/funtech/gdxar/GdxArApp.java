/********************************************************************************
 ********************************************************************************
 ** COPYRIGHT:      (c) 2018 Farell Leiking
 ** LICENSE:        Licensed under the Apache License, Version 2.0 (the "License");
 **                 you may not use this file except in compliance with the License.
 **                 You may obtain a copy of the License at
 **
 **                     https://www.apache.org/licenses/LICENSE-2.0
 **
 **                 Unless required by applicable law or agreed to in writing, software
 **                 distributed under the License is distributed on an "AS IS" BASIS,
 **                 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 **                 See the License for the specific language governing permissions and
 **                 limitations under the License.
 **
 ** MODULE:         GdxArApp.java
 ** ABBREVIATION:
 ** COMPILER:       Android Studio 2.3.3
 ** LANGUAGE:       Java
 ** AUTHOR:         Farell Leiking
 ** ABSTRACT:
 ** PREMISES:
 ** REMARKS:
 ** HISTORY:        2018-05-09 : Creation
 ** REVIEW:
 ********************************************************************************/

package com.funtech.gdxar;

import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.funtech.gdxar.utils.FrameRate;
import com.google.ar.core.Frame;

import arcore4gdx.ARCoreScene;
import arcore4gdx.SimpleShaderProvider;

/*
 *
 */
public class GdxArApp extends ARCoreScene
{
    private FrameRate               m_frameRate;

    /*
     *
     */
    @Override
    public void create()
    {
        super.create();

        m_frameRate = new com.funtech.gdxar.utils.FrameRate();
    }

    /*
     *
     */
    @Override
    public void pause()
    {
        m_frameRate.pause();
        super.pause();
    }

    /*
     *
     */
    @Override
    public void resume()
    {
        super.resume();
        m_frameRate.resume();
    }

    /*
     *
     */
    @Override
    public void dispose()
    {
        m_frameRate.dispose();
        super.dispose();
    }

    /*
     *
     */
    @Override
    public void resize(int width, int height)
    {
        super.resize(width, height);
        m_frameRate.resize(width, height);
    }


    /*
     *
     */
    @Override
    public void render()
    {
        super.render(); // call the parent which will clear the screen and call the 3d rendering batch

        // here add 2D drawings
        m_ShapeRenderer.begin( ShapeRenderer.ShapeType.Filled );
        m_ShapeRenderer.setColor(Color.RED);
        m_ShapeRenderer.circle( 100, 100, 20 );
        m_ShapeRenderer.end();

        m_frameRate.render();
    }

    /********************************************************************************
     *                            PROTECTED                                         *
     ********************************************************************************/

    /**
     * This is the main render method. It is called on each frame. This is where all scene operations
     * need to be. This includes interacting with the ARCore frame for hit tests, plane detection
     * updates and anchor updates.
     *
     * <p>It also is where application specific objects are created and ultimately rendered.
     */
    //@Override
    protected void render(Frame frame, ModelBatch modelBatch)
    {
    }


    /** Create a new shader provider that is aware of the Plane material custom shader. */
    @Override
    protected ShaderProvider createShaderProvider()
    {
        return new SimpleShaderProvider()
        {
            /*
             *
             */
            @Override
            protected Shader createShader(Renderable renderable)
            {
                return super.createShader(renderable);
            }
        };
    }


    /********************************************************************************
     *                              PRIVATE                                         *
     ********************************************************************************/
}
