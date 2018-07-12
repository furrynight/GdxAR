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
 ** MODULE:         MainGameFragment.java
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.funtech.gdxar.interfaces.GdxSessionCallbacks;

import utils.DebugPrint;

/*
 *
 */
public class MainGameFragment extends AndroidFragmentApplication implements GdxSessionCallbacks
{
    private final boolean               ENABLE_DEBUG                = true;
    private final String                DEBUG_CLASS_NAME            = MainGameFragment.class.toString();
    private final DebugPrint            m_DebugPrint;

    private MainDisplay                 m_MainDisplay               = null;

    /*
     *
     */
    public MainGameFragment()
    {
        m_DebugPrint    = new DebugPrint( DEBUG_CLASS_NAME, ENABLE_DEBUG );
    }

    /*
     *
     */
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        AndroidApplicationConfiguration config  = new AndroidApplicationConfiguration();
        m_MainDisplay                           = new MainDisplay();
        return initializeForView(m_MainDisplay, config);
    }

    /*
     *
     */
    @Override
    public void onStart()
    {
        m_DebugPrint.print("+onStart()");

        super.onStart();

        m_DebugPrint.print("-onStart()");
    }

    /*
     *
     */
    @Override
    public void onPause()
    {
        m_DebugPrint.print("+onPause()");

        super.onPause();

        m_DebugPrint.print("-onPause()");
    }

    /*
     *
     */
    @Override
    public void onResume()
    {
        m_DebugPrint.print("+onResume()");

        super.onResume();

        m_DebugPrint.print("-onResume()");
    }

    /*
     *
     */
    @Override
    public void onStop()
    {
        m_DebugPrint.print("+onStop()");

        super.onStop();

        m_DebugPrint.print("-onStop()");
    }

    /*
     *
     */
    @Override
    public void onDestroy()
    {
        m_DebugPrint.print("+onDestroy()");

        super.onDestroy();

        m_DebugPrint.print("-onDestroy()");
    }

    /*
     *
     */
    public ApplicationAdapter getGdxSession()
    {
        return m_MainDisplay;
    }
}
