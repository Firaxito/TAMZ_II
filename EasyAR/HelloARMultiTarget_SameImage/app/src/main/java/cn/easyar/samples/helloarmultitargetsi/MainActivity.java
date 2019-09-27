//================================================================================================================================
//
// Copyright (c) 2015-2019 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package cn.easyar.samples.helloarmultitargetsi;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.HashMap;

import cn.easyar.Engine;

public class MainActivity extends Activity
{
    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: HelloARMultiTargetSI
    *      Package Name: cn.easyar.samples.helloarmultitargetsi
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    private static String key = "5uAp0OLzMcz6kmH8a5A3TO7t8f3EZ/ZqH/8Bq9bSH/viwhnm1s8Oq5mDHODRwALg18467s7AE+WNwhXkgY1Y5MLSDuzR6h/w6sVYs5KNWOXKwh/n0MQJq5n6AavB1BTtz8Qz7dCDQNKBwhSnxsAJ8MLTVPrCzArlxtJU4cbNFubC0xf8z9UT/cLTHezX0hOrj4NY1I+DDOjRyBvn19JYs/iDGOjQyBmr/o1Y+c/ADu/M0xf6gZshq9TIFO3M1gmrj4MX6MCDJ6WBxAL5ytMf3crMH9rXwBf5gZsU/M/NVqvK0jbmwMAWq5nHG+XQxAel2IMY/M3FFuzqxQmrmfpY6s2PH+jQ2Bv7jdIb5NPNH/qNyR/lz84b+87UFv3K1Rv7xMQO+sqDJ6WB1xv7ysAU/dCDQNKBwxv6ysJY1I+DCuXC1Rzm0cwJq5n6WOjNxQjmysVY1I+DH/HTyAjs98gX7PDVG+TTg0Dn1s0WpYHICcXMwhvlgZsc6M/SH/SP2ljr1s8e5cboHvqBmyGrgfxWq9XACODCzw76gZshq8HACeDAgyelgdEW6NfHFfvO0liz+IMT5tCDJ6WBxAL5ytMf3crMH9rXwBf5gZsU/M/NVqvK0jbmwMAWq5nHG+XQxAfU3iYYMbKnzPdwNlDJeU0GeNkIlP35F9BhAASFlLss/TInQFPVMGm4Gr3hZ2bMXoUDcNg6wFon3bEjydtoTn+T8II4DT44hSy7LoCq5+GreTtl3X7SCinQZEudBYnEzC9ijGqgTNPVLGNOHUCfl2/0P+TLZehDTAse5mOU1+8ro7djRfNP0xtTbqtuXRrm5EmN0VUMWAv1pNRZsEyjWw+X1SeN7+heq5IlyjcsU9X2i8PwHCRsir498FvK2Ejeif6Z5UMmXcDVs9zDj7dQO6rGG0E5O3hn/hIirY3CnYAN7o1l+8Ol6don+su/GSeyA+fTb2dFhkoYtNFVRozZf6Oheok=";
    private GLView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
            Toast.makeText(MainActivity.this, Engine.errorMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        glView = new GLView(this);

        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private interface PermissionCallback
    {
        void onSuccess();
        void onFailure();
    }
    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
    private int permissionRequestCodeSerial = 0;
    @TargetApi(23)
    private void requestCameraPermission(PermissionCallback callback)
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                int requestCode = permissionRequestCodeSerial;
                permissionRequestCodeSerial += 1;
                permissionCallbacks.put(requestCode, callback);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            } else {
                callback.onSuccess();
            }
        } else {
            callback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (permissionCallbacks.containsKey(requestCode)) {
            PermissionCallback callback = permissionCallbacks.get(requestCode);
            permissionCallbacks.remove(requestCode);
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    callback.onFailure();
                }
            }
            if (!executed) {
                callback.onSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (glView != null) { glView.onResume(); }
    }

    @Override
    protected void onPause()
    {
        if (glView != null) { glView.onPause(); }
        super.onPause();
    }
}
