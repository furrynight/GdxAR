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
 ** MODULE:         AndroidLauncher.java
 ** ABBREVIATION:
 ** COMPILER:       Android Studio 2.3.3
 ** LANGUAGE:       Java
 ** AUTHOR:         Farell Leiking
 ** ABSTRACT:
 ** PREMISES:
 ** REMARKS:
 ** HISTORY:        2018-05-09 : Creation
 **                 2018-05-17 : Put in sensor to detect phone angle then to enable/disable AR
 ** REVIEW:
 ********************************************************************************/

package com.funtech.gdxar;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import utils.DebugPrint;

/*
 *
 */
public class AndroidLauncher extends AppCompatActivity implements AndroidFragmentApplication.Callbacks,
                                                        Runnable, SensorEventListener
{
    private final boolean               ENABLE_DEBUG                = true;
    private final String                DEBUG_CLASS_NAME            = AndroidLauncher.class.toString();

    private DebugPrint                  m_DebugPrint                = null;
    private MainGameFragment            m_MainGame                  = null;
    private GdxArFragment               m_gdxArFragment             = null;

    private int                         m_iCounter                  = 0;

    private SensorManager               m_SensorManager;
    private Sensor                      m_RotationSensor;

    private static final int            SENSOR_DELAY                = 500 * 1000; // 500ms
    private static final int            FROM_RADS_TO_DEGS           = -57;

    /**
     *
     */
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        m_DebugPrint                = new DebugPrint( DEBUG_CLASS_NAME, ENABLE_DEBUG );

        Toolbar toolbar             = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayShowTitleEnabled( true );

        m_MainGame                  = new MainGameFragment();
        m_gdxArFragment             = new GdxArFragment();
        m_gdxArFragment.setARCoreInstallAllow( false );

        // setup the main display
        FragmentTransaction trans   = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.GameDisplay, m_gdxArFragment, m_gdxArFragment.getClass().toString());
        trans.commit();

        // register the sensor
        m_SensorManager             = (SensorManager) getSystemService( Activity.SENSOR_SERVICE);
        m_RotationSensor            = m_SensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        m_SensorManager.registerListener(this, m_RotationSensor, SENSOR_DELAY);
    }


    /**
     *
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState)
    {
        m_DebugPrint.print("+onPostCreate()");

        super.onPostCreate( savedInstanceState );

        //new Thread(this).start();

        m_DebugPrint.print("-onPostCreate()");
    }


    /**
     *
     */
    @Override
    public void onRestart()
    {
        m_DebugPrint.print("+onRestart()");

        super.onRestart();

        m_DebugPrint.print("-onRestart()");
    }


    /**
     *
     */
    @Override
    public void onStart()
    {
        m_DebugPrint.print("+onStart()");

        super.onStart();

        m_DebugPrint.print("-onStart()");
    }


    /**
     *
     */
    @Override
    public void onPause()
    {
        m_DebugPrint.print("+onPause()");

        super.onPause();

        m_DebugPrint.print("-onPause()");
    }


    /**
     *
     */
    @Override
    public void onResume()
    {
        m_DebugPrint.print("+onResume()");

        super.onResume();

        m_DebugPrint.print("-onResume()");
    }


    /**
     *
     */
    @Override
    public void onStop()
    {
        m_DebugPrint.print("+onStop()");

        super.onStop();

        m_DebugPrint.print("-onStop()");
    }


    /**
     *
     */
    @Override
    public void onDestroy()
    {
        m_DebugPrint.print("+onDestroy()");

        m_SensorManager.unregisterListener( this );

        super.onDestroy();

        // mark them null so can be GC ( quickly ...? )
        m_gdxArFragment = null;
        m_MainGame      = null;

        m_DebugPrint.print("-onDestroy()");
    }


    /**
     *
     */
    @Override
    public void exit()
    {
        m_DebugPrint.print("+exit()");

        finish();

        m_DebugPrint.print("-exit()");
    }


    /**
     *
     */
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        finish();
    }


    /**
     *
     */
    @Override
    public void run()
    {
        try
        {
            Thread.sleep( 5000 );
            runOnUiThread( new Runnable()
            {
                /**
                 *
                 */
                @Override
                public void run()
                {
                    hideToolbar(true);
                }
            } );

            Thread.sleep( 100 );
        }
        catch ( InterruptedException e )
        {
        }
    }

    /**
     *
     */
    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy)
    {
        // TODO Auto-generated method stub
    }

    /**
     *
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor == m_RotationSensor)
        {
            if (event.values.length > 4)
            {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                processSensorValues(truncatedRotationVector);
            }
            else
            {
                processSensorValues(event.values);
            }
        }
    }



    /********************************************************************************
     *                              PRIVATE                                         *
     ********************************************************************************/

    /**
     *
     */
    private void processSensorValues(float[] vectors)
    {
        float[] rotationMatrix          = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);

        int worldAxisX                  = SensorManager.AXIS_X;
        int worldAxisZ                  = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix  = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);

        float[] orientation             = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        int pitch                       = (int)(orientation[1] * FROM_RADS_TO_DEGS);
        int roll                        = (int)(orientation[2] * FROM_RADS_TO_DEGS);

        if( (m_gdxArFragment != null) && (m_gdxArFragment.isARAvailable()) )
        {
            GdxArApp gdxarapp = ( GdxArApp ) m_gdxArFragment.getGdxSession();

            m_DebugPrint.print("pitch " + pitch + ", roll " + roll);

            // enable/disable based on the held angle of the phone
            if( Math.abs( pitch ) < 60 && Math.abs( roll ) < 50)
            {
                if( !gdxarapp.isAREnabled() )
                {
                    gdxarapp.enableAR( true );
                }
            }
            else
            {
                if( gdxarapp.isAREnabled() )
                {
                    gdxarapp.enableAR( false );
                }
            }
        }
    }

    /**
     *
     */
    private void hideToolbar(boolean isHide)
    {
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );

        if(isHide)
        {
            toolbar.setVisibility( View.GONE );
        }
        else
        {
            toolbar.setVisibility( View.VISIBLE );
        }
    }
}
