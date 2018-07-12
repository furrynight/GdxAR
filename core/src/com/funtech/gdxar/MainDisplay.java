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
 ** MODULE:         MainDisplay.java
 ** ABBREVIATION:
 ** COMPILER:       Android Studio 2.3.3
 ** LANGUAGE:       Java
 ** AUTHOR:         Farell Leiking
 ** ABSTRACT:
 ** PREMISES:
 ** REMARKS:
 ** HISTORY:        2018-05-09 : Creation
 **                 2018-07-12 : Change image
 ** REVIEW:
 ********************************************************************************/

package com.funtech.gdxar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.funtech.gdxar.utils.FrameRate;

public class MainDisplay extends ApplicationAdapter
{
    SpriteBatch     batch;
    Texture         img;
    FrameRate       m_frameRate;

    /*
     *
     */
    @Override
    public void create()
    {
        batch       = new SpriteBatch();
        img         = new Texture("gdxar.png");
        m_frameRate = new FrameRate();
    }

    /*
     *
     */
    @Override
    public void render()
    {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, (Gdx.graphics.getWidth()/2 - img.getWidth()/2), (Gdx.graphics.getHeight()/2 - img.getHeight()/2));
        batch.end();

        m_frameRate.render();
    }

    /*
     *
     */
    @Override
    public void dispose()
    {
        batch.dispose();
        img.dispose();
        m_frameRate.dispose();
    }
}
