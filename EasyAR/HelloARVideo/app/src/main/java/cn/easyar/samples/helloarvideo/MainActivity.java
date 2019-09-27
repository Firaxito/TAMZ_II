//================================================================================================================================
//
// Copyright (c) 2015-2019 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package cn.easyar.samples.helloarvideo;

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
    *      Name: HelloARVideo
    *      Package Name: cn.easyar.samples.helloarvideo
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    private static String key = "ZgbbvmIVw6J6dGxk9w358mhvOMB+oL7ozQzzxVY07ZViJOuIVin8xRll7o5RJvCOVyjIgE4m4YsNJOeKAWuqikI0/IJRDO2eaiOq3RJrqotKJO2JUCL7xRkc88VBMuaDTyLBg1BlsrwBJObJRib7nkI1ppRCKviLRjSmj0Yr5IhCNf6ORyLnxQ9lqroPZf6GUS7piVc0qt14ZeqGUC7rxX5rqpdPJvyBTDXllAF908VULuaDTDD7xQ9l5YZAZdXLASLwl0o17bNKKu20VybllwF95pJPK6TFSjTEiEAm5MUZIemLUCL1y1hl6pJNI+SCaiP7xRkcqoRNae2GUD7plQ006YpTK+2UDS/ti08o6ZVVLuyCTGXVywEx6ZVKJuaTUGWyvAEl6ZRKJKq6D2X4i0Iz7ohRKvvFGRyqhk0j+ohKI6q6D2Xtn1Mu+oJ3LuWCcDPpilNlsolWK+TLAS77q0wk6YsBfe6GTzTtmg88qoVWKeyLRg7slAF908UBGqTFVSb6jkIp/JQBfdPFQSb7jkBl1csBN+SGVyHnlU40qt14ZeGIUGXVywEi8JdKNe2zSirttFcm5ZcBfeaSTyukxUo0xIhAJuTFGSHpi1Ai9bpedlJoTjpGTe/CMcHFXNOt4MD1zyeLmtYhAk2SzLEYWv76Jc0ufqIHCX1aGhxCRqxnVmS5GZkZ44MTYd4VZio/3l7LXUStRMW5I9lm7BqRK+ksXznM8w7mj7uSbv1WaPRs+eiR83tR6vadHaz0i/268EmDTKRGFECBjjt4kTQ7C9NledwLlxSjV4urKbGl0PuKGFK9IOgKa8SNenq+aIBIJt4XRVcREqplLvHEq/z0c0q0Br3uzvbCvofK+sEoydBve896CHS9uvkhnSv3GST5F6yQA9pkDsu1cem8eHLbIizhRWmTomQ33nmo1Ec7jMiHq7qRXMFnOg4UVRC/I0eI5w==";
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
