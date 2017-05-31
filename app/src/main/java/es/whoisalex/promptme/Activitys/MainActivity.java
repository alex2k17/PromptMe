package es.whoisalex.promptme.Activitys;


import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.IOException;

import es.whoisalex.promptme.Clases.CameraAPI1.CameraPreview;
import es.whoisalex.promptme.R;

import static es.whoisalex.promptme.Clases.CameraAPI1.CameraHelper.MEDIA_TYPE_VIDEO;
import static es.whoisalex.promptme.Clases.CameraAPI1.CameraHelper.getOutputMediaFile;

/**
 *  This activity uses the camera/camcorder as the A/V source for the {@link android.media.MediaRecorder} API.
 *  A {@link android.view.TextureView} is used as the camera preview which limits the code to API 14+. This
 *  can be easily replaced with a {@link android.view.SurfaceView} to run on older devices.
 */
public class MainActivity extends Activity {

    private Camera mCamera = null;
    private CameraPreview mPreview = null;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        try {
            mCamera = Camera.open();
        }catch (Exception e){
            Log.d("ERROR","Failed to get camera: "+e.getMessage());
        }

        if (mCamera != null){
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mPreview);
        }

        ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording){
                    releaseMediaRecorder();
                    isRecording = false;
                    releaseCamera();
                }else{
                   if (prepareVideoRecorder()){
                       mMediaRecorder.start();
                       isRecording = true;

                   }else{
                       releaseMediaRecorder();
                   }
                }
            }
        });
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            stopRecording();
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            releaseMediaRecorder();
            releaseCamera();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void stopRecording() {
        try{
            mMediaRecorder.stop();
        }catch(RuntimeException stopException){
            //handle cleanup here
        }
        mCamera.lock();
    }

    private boolean prepareVideoRecorder(){
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }

        mMediaRecorder = new MediaRecorder();

        // Desbloqueamos la camara para que la pueda utilizar otro proceso y la asignamos a MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        //Preparamos la configuracion
        try{
            mMediaRecorder.prepare();
        }catch (IllegalStateException e){
            Log.d("ERROR", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }catch (IOException e){
            Log.d("ERROR", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

}