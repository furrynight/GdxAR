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
 ** MODULE:         FrameRate.java
 ** ABBREVIATION:
 ** COMPILER:       Android Studio 2.3.3
 ** LANGUAGE:       Java
 ** AUTHOR:         Farell Leiking
 ** ABSTRACT:       Provides easy wrapper to render the FPS from the upper left corner
 ** PREMISES:
 ** REMARKS:
 ** HISTORY:        2017-07-05 : Creation
 ** REVIEW:
 ********************************************************************************/

package com.funtech.gdxar.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;


public class FrameRate implements Disposable
{
    private final boolean           ENABLE_DEBUG                = false;
    private final static String     DEBUG_CLASS_NAME            = FrameRate.class.toString();
    private final int               X_POSITION_DEFAULT          = 4;
    private final int               Y_OFFSET_DEFAULT            = 4;

    private int                     m_iFrameRate;
    private BitmapFont              m_font                      = null;
    private SpriteBatch             m_spriteBatch               = null;
    private OrthographicCamera      m_orthoCamera               = null;
    private int                     m_iPosX, m_iPosY;
    private boolean                 m_bIsPaused                 = false;    // flag to keep track that we are paused. For use in cases
                                                                            // the external wants us to pause and not keep rendering
                                                                            // i.e. will cause render() calls to return without rendering


    /*
     *
     */
    public FrameRate()
    {
        FrameRate(X_POSITION_DEFAULT, (Gdx.graphics.getHeight() - Y_OFFSET_DEFAULT));
    }

    /*
     *
     */
    public void FrameRate(int x, int y)
    {
        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "+FrameRate(int x, int y)");
        }

        setPosition(x, y);
        m_bIsPaused = false;
        loadAssets();

        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "-FrameRate(int x, int y)");
        }
    }

    /*
     *
     */
    public void setPosition(int x, int y)
    {
        m_iPosX = x;
        m_iPosY = y;
    }

    /*
     *
     */
    public void resize(int screenWidth, int screenHeight)
    {
        m_orthoCamera   = null;
        m_orthoCamera   = new OrthographicCamera(screenWidth, screenHeight);
        m_orthoCamera.translate(screenWidth / 2, screenHeight / 2);
        m_orthoCamera.update();
        m_spriteBatch.setProjectionMatrix(m_orthoCamera.combined);

        setPosition(X_POSITION_DEFAULT, screenHeight - Y_OFFSET_DEFAULT);
    }

    /*
     *
     */
    public void render()
    {
        if(m_bIsPaused) return;

        m_iFrameRate    = Gdx.graphics.getFramesPerSecond();

        m_spriteBatch.begin();
        m_font.draw(m_spriteBatch, m_iFrameRate + " fps", m_iPosX, m_iPosY);
        m_spriteBatch.end();
    }

    /*
     *
     */
    public void pause()
    {
        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "+pause()");
        }

        m_bIsPaused = true;
        
        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "-pause()");
        }
    }

    /*
     *
     */
    public void resume()
    {
        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "+resume()");
        }

        loadAssets();
        m_bIsPaused = false;

        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "-resume()");
        }
    }

    /*
     *
     */
    public void dispose()
    {
        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "+dispose()");
        }

        m_font.dispose();
        m_font          = null;
        m_spriteBatch.dispose();
        m_spriteBatch   = null;
        m_orthoCamera   = null;
        
        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "-dispose()");
        }
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
    private void loadAssets()
    {
        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "+loadAssets()");
        }

        if(null == m_spriteBatch)
        {
            if(ENABLE_DEBUG)
            {
                Gdx.app.log(DEBUG_CLASS_NAME, "loadAssets() - m_spriteBatch is null");
            }

            m_spriteBatch       = new SpriteBatch();
        }

        if(null == m_orthoCamera)
        {
            if(ENABLE_DEBUG)
            {
                Gdx.app.log(DEBUG_CLASS_NAME, "loadAssets() - m_orthoCamera is null");
            }

            int m_iScreenWidth  = Gdx.graphics.getWidth();
            int m_iScreenHeight = Gdx.graphics.getHeight();
            m_orthoCamera       = new OrthographicCamera(m_iScreenWidth, m_iScreenHeight);
        }

        if(null == m_font)
        {
            if(ENABLE_DEBUG)
            {
                Gdx.app.log(DEBUG_CLASS_NAME, "loadAssets() - font is null");
            }

            m_font              = new BitmapFont();
            m_font.getData().setScale(2, 2);
        }
        
        if(ENABLE_DEBUG)
        {
            Gdx.app.log(DEBUG_CLASS_NAME, "-loadAssets()");
        }
    }
}

