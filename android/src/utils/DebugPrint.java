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
 ** MODULE:         DebugPrint.java
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

package utils;


import android.util.Log;

import com.funtech.gdxar.BuildConfig;

public class DebugPrint
{
    private String      m_strDebugName;
    private boolean     m_bIsEnabled;

    /*
     *
     */
    public DebugPrint(String strDebugName, boolean bEnablePrint)
    {
        m_strDebugName  = strDebugName;
        m_bIsEnabled    = bEnablePrint & BuildConfig.BUILD_TYPE.equals("debug");
    }

    /*
     *
     */
    public void print(String strDebugMsg)
    {
        if(m_bIsEnabled)
        {
            Log.i(m_strDebugName, strDebugMsg);
        }
    }
}
