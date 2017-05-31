package es.whoisalex.promptme.Activitys;


import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

import es.whoisalex.promptme.Clases.CameraAPI1.CameraPreview;
import es.whoisalex.promptme.R;

import static es.whoisalex.promptme.Clases.CameraAPI1.CameraHelper.MEDIA_TYPE_VIDEO;
import static es.whoisalex.promptme.Clases.CameraAPI1.CameraHelper.getOutputMediaFile;

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
                       msgShow();
                       isRecording = true;

                   }else{
                       releaseMediaRecorder();
                   }
                }
            }
        });
    }

    //Lanzamos el mensaje
    private void msgShow(){
        Context context = getApplicationContext();
        CharSequence text = "Mensaje predefinido; Lorem ipsum dolor sit amet, " +
                "consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.BOTTOM, 0, 100);
        toast.show();
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // Instanciamos la camara
        }
        catch (Exception e){
        }
        return c;
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            stopRecording();
            mMediaRecorder.reset();   // limpiamos la configuracion del grabador
            mMediaRecorder.release();
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
            Log.d("ERROR", "Error al parar la grabacion: " + stopException.getMessage());
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
            Log.d("ERROR", "IllegalStateException preparando MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }catch (IOException e){
            Log.d("ERROR", "IOException preparando MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

}