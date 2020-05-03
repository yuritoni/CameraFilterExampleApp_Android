package com.example.camerafiltersapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Switch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Permission;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Camera mCamera;
    HorizontalScrollView horizontalScrollView;
    private final int PERMISSION_CONSTANT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView ivCapture = (ImageView) findViewById(R.id.ivCapture);
        ImageView ivFilter = (ImageView) findViewById(R.id.ivFilter);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.filterLaout);

        enforcePermissionGive();

        ivCapture.setOnClickListener(this);
        ivFilter.setOnClickListener(this);


    }

    private void enforcePermissionGive() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            initialize();
        }
        else{
           // checkPermission()
        }
    }

    private void initialize() {
        mCamera = getCameraInstance();
        CameraPreview mPreview  =new CameraPreview(this,mCamera);
        FrameLayout r1CameraPreview = (FrameLayout ) findViewById(R.id.r1CameraPreview);
        if(r1CameraPreview!=null){
            r1CameraPreview.addView(mPreview);
        }
    }

    private Camera getCameraInstance() {

        Camera c= null;
        try {
            c = Camera.open();


        }catch (Exception e){
            e.printStackTrace();
        }
        return  c;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.release();
        mCamera=null;
    }

   private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
       @Override
       public void onPictureTaken(byte[] data, Camera camera) {
           File pictureFile= getOutputMediaFile();
           if(pictureFile ==null){
               return;
           }

           MediaScannerConnection.scanFile(MainActivity.this, new String[]{pictureFile.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
               @Override
               public void onScanCompleted(String path, Uri uri) {
                   mCamera.startPreview();
               }
           });

           try {
               FileOutputStream fos= new FileOutputStream(pictureFile);
               fos.write(data);
               fos.close();
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }
           catch (IOException e){
               e.printStackTrace();
           }
       }


   };

    private File getOutputMediaFile() {
        File mediaDir= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"My Images");
        if(mediaDir.exists()){
            if(!mediaDir.mkdir()){
                return null;

            }
        }

        SecureRandom random = new SecureRandom();
        int num = random.nextInt(100000);
        return  new File(mediaDir.getAbsolutePath()+File.separator+"IMG_"+num +".jpg");
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivFilter:
                if(horizontalScrollView.getVisibility()==View.VISIBLE){
                    horizontalScrollView.setVisibility(View.GONE);
                }
                else{
                    horizontalScrollView.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.ivCapture:

                mCamera.takePicture(null,null,mPicture);


        }




    }

    public void colorEffecFilter(View v){

        Camera.Parameters parameters = mCamera.getParameters();
        switch (v.getId()){
            case R.id.sds:
                parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                mCamera.setParameters(parameters);
                break;

            case R.id.Aqua:
                parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                mCamera.setParameters(parameters);
                break;

        }

    }
}
