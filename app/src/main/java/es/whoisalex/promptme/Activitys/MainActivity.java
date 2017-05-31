package es.whoisalex.promptme.Activitys;


import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import es.whoisalex.promptme.Clases.CameraAPI1.CameraPreview;
import es.whoisalex.promptme.R;

import static es.whoisalex.promptme.Clases.CameraAPI1.CameraHelper.MEDIA_TYPE_VIDEO;
import static es.whoisalex.promptme.Clases.CameraAPI1.CameraHelper.getOutputMediaFile;

public class MainActivity extends Activity {

    private Camera mCamera = null;
    private MediaRecorder mMediaRecorder;
    private Toast toast;

    private boolean isRecording = false;

    Activity act;
    Context ctx;
    CameraPreview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ctx = this;
        act = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        preview = new CameraPreview(this, (SurfaceView)findViewById(R.id.surfaceView));
        preview.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

        //initCamera();
        ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    releaseMediaRecorder();
                    isRecording = false;
                    releaseCamera();
                    toast.cancel();
                } else {
                    if (prepareVideoRecorder()) {
                        mMediaRecorder.start();
                        msgShow();
                        isRecording = true;
                    } else {
                        releaseMediaRecorder();
                    }
                }
            }
        });
    }

    //Lanzamos el mensaje
    private void msgShow() {
        Context context = getApplicationContext();
        CharSequence text = "Mensaje predefinido; Lorem ipsum dolor sit amet, " +
                "consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa";
        int duration = Toast.LENGTH_LONG;
        toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
        toast.show();
    }



    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(1); // Instanciamos la camara
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            stopRecording();
            mMediaRecorder.reset();   // limpiamos la configuracion del grabador
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPause() {
        if(mCamera != null) {
            isRecording = false;
            releaseMediaRecorder();
            mCamera.stopPreview();
            preview.setCamera(null);
            releaseCamera();
        }
        super.onPause();
    }

    private void resetCam() {
        mCamera.startPreview();
        preview.setCamera(mCamera);
    }


    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                mCamera = Camera.open(1);
                mCamera.startPreview();
                preview.setCamera(mCamera);
            } catch (RuntimeException ex){
            }
        }
    }

    private void stopRecording() {
        try {
            mMediaRecorder.stop();
        } catch (RuntimeException stopException) {
            Log.d("ERROR", "Error al parar la grabacion: " + stopException.getMessage());
        }
        mCamera.lock();
    }

    private boolean prepareVideoRecorder() {
        if (mCamera == null) {
            mCamera = getCameraInstance();
            //mCamera.setDisplayOrientation(90);
        }

        mMediaRecorder = new MediaRecorder();

        // Desbloqueamos la camara para que la pueda utilizar otro proceso y la asignamos a MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        File f = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        refreshGallery(f);
        mMediaRecorder.setPreviewDisplay(preview.getmHolder().getSurface());
        //Preparamos la configuracion
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("ERROR", "IllegalStateException preparando MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("ERROR", "IOException preparando MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void refreshGallery(File f) {
        MediaScannerConnection.scanFile(this,
                new String[]{f.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }


}