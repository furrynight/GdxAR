/*
 * Copyright 2017 Google Inc.
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
/* NOTE:
 * This file was copied from ARCore Android SDK: https://github.com/google-ar/arcore-android-sdk
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
 */

package arcore4gdx.helper;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Helper to ask camera permission.
 */
public class CameraPermissionHelper
{
    private static final String     CAMERA_PERMISSION           = Manifest.permission.CAMERA;
    private static final int        CAMERA_PERMISSION_CODE      = 0;

    /**
     * Check to see we have the necessary permissions for this app.
     */
    public static boolean hasCameraPermission(Activity activity)
    {
        return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check to see we have the necessary permissions for this app, and ask for them if we don't.
     *
     * @param fragment
     */
    public static void requestCameraPermission(Fragment fragment)
    {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
        {
            fragment.requestPermissions( new String[]{ CAMERA_PERMISSION }, CAMERA_PERMISSION_CODE );
        }
    }
}
