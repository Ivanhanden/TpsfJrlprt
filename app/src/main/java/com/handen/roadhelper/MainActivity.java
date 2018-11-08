package com.handen.roadhelper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase _cameraBridgeViewBase;
    static boolean isFilterAdded = false;

    private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load ndk built module, as specified in moduleName in build.gradle
                    // after opencv initialization
                    System.loadLibrary("native-lib");
                    initializeImages();
                    _cameraBridgeViewBase.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // Permissions for Android 6+
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);

        _cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.main_surface);
        _cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        _cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        disableCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, _baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initializeImages() {
        if(isFilterAdded)
            return;
        Mat pedastrian = null;
        try {
            pedastrian = Utils.loadResource(getApplicationContext(),
                    R.drawable.pedastrian,
                    Imgcodecs.CV_LOAD_IMAGE_COLOR
            );
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        addFilter(pedastrian.getNativeObjAddr(), 5612, 4);

        Mat sign27 = new Mat();
        try {
            sign27 = Utils.loadResource(getApplicationContext(),
                    R.drawable.znak27,
                    Imgcodecs.CV_LOAD_IMAGE_ANYCOLOR
            );
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        addFilter(sign27.getNativeObjAddr(), 27, 4);

        Mat sign530 = null;
        try {
            sign530 = Utils.loadResource(getApplicationContext(),
                    R.drawable.znak530,
                    Imgcodecs.CV_LOAD_IMAGE_COLOR
            );
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        addFilter(sign530.getNativeObjAddr(), 530, 4);

        Mat sign121 = null;
        try {
            sign121 = Utils.loadResource(getApplicationContext(),
                    R.drawable.sign121,
                    Imgcodecs.CV_LOAD_IMAGE_COLOR
            );
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        addFilter(sign121.getNativeObjAddr(), 121, 3);
        Mat sign324_60 = null;
        try {
            sign324_60 = Utils.loadResource(getApplicationContext(),
                    R.drawable.sign324_60,
                    Imgcodecs.CV_LOAD_IMAGE_COLOR
            );
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        addFilter(sign324_60.getNativeObjAddr(), 32460, 0);

        isFilterAdded = true;
    }

    public void onDestroy() {
        super.onDestroy();
        disableCamera();
    }

    public void disableCamera() {
        if (_cameraBridgeViewBase != null)
            _cameraBridgeViewBase.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

    }

    public void onCameraViewStopped() {

    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
   //     Mat matGray = inputFrame.gray();
        Mat matRgba = inputFrame.rgba();
        nativeOnFrame(matRgba.getNativeObjAddr(), 2000);
     //   Mat copyMat = new Mat();
     //   matRgba.copyTo(copyMat);
     //   matRgba = null;
        return matRgba;
    }

    public native void nativeOnFrame(long matAddrGray, int nbrElem);
    public native void addFilter(long matAddr, int code, int corners);
}


/*
 // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }


    // * A native method that is implemented by the 'native-lib' native library,
    // * which is packaged with this application.

public native String stringFromJNI();
 */
