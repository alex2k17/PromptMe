package es.whoisalex.promptme.Activitys;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
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
    private TextView text;
    private File f;
    private Toast toast;

    private boolean isRecording = false;
    private boolean isSave = false;
    CameraPreview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadPreview();

        ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    closeToast();
                    //text.clearAnimation();
                    releaseMediaRecorder();
                    isRecording = false;
                    resetCam();
                    if (isSave) {
                        makeToast(getApplicationContext(), "Guardando...", Toast.LENGTH_SHORT);
                    }
                } else {
                    closeToast();
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
        text = (TextView) findViewById(R.id.texto);
        Animation translatebu = AnimationUtils.loadAnimation(this, R.anim.animationfile);
        translatebu.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                text.setText("");
                text.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        text.setText("Lorem ipsum dolor sit amet, consectetuer adipiscing elit." +
                " Aenean commodo ligula eget dolor. Aenean massa." +
                " Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. ");
        text.setVisibility(View.VISIBLE);
        text.setTextColor(Color.parseColor("#FF0000"));
        text.startAnimation(translatebu);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(1); // Instanciamos la camara
        } catch (Exception e) {
            Log.e("ERROR", "No se pudo instanciar la camara");
        }
        return c;
    }

    private void releaseMediaRecorder() {
        if (text!=null) {
            text.setText("");
            text.setVisibility(View.INVISIBLE);
        }
        if (mMediaRecorder != null) {
            stopRecording();
            mMediaRecorder.reset();   // limpiamos la configuracion del grabador
            mMediaRecorder.release();
            mMediaRecorder = null;
            durationVideo(f);
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void resetCam() {
        try {
            mCamera.reconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        if (mCamera != null) {
            isRecording = false;
            releaseMediaRecorder();
            mCamera.stopPreview();
            preview.setCamera(null);
            releaseCamera();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                mCamera = Camera.open(1);
                mCamera.startPreview();
                preview.setCamera(mCamera);
            } catch (RuntimeException ex) {
                Log.e("ERROR", "onResume "+ex.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {}


    private void stopRecording() {
        try {
            mMediaRecorder.stop();
        } catch (RuntimeException stopException) {
            Log.e("ERROR", "Error al parar la grabacion: " + stopException.getMessage());
        }
        mCamera.lock();
    }

    private boolean prepareVideoRecorder() {
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }
        mMediaRecorder = new MediaRecorder();

        // Desbloqueamos la camara para que la pueda utilizar otro proceso y la asignamos a MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setOrientationHint(270);
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        saveVideo();
        mMediaRecorder.setPreviewDisplay(preview.getmHolder().getSurface());
        mMediaRecorder.setVideoSize(preview.getPreviewSize().width, preview.getPreviewSize().height);
        //Preparamos la configuracion
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.e("ERROR", "IllegalStateException preparando MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.e("ERROR", "IOException preparando MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void loadPreview() {
        preview = new CameraPreview(this, (SurfaceView) findViewById(R.id.surfaceView));
        preview.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);
    }

    //Generamos el archivo de video
    public void saveVideo() {
        f = null;
        f = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
    }

    public void makeToast(Context context, CharSequence text, int duration) {
        toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
        toast.show();
    }

    public void closeToast(){
        if (toast != null) {
            toast.cancel();
        }
    }

    public void refreshGallery(File f) {
        MediaScannerConnection.scanFile(this,
                new String[]{f.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Ruta: " + path + ":");
                        Log.i("ExternalStorage", "-> uri= " + uri);
                    }
                });
    }

    public void durationVideo(File videoFile){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoFile.toString());
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong( time );
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        Log.i("RUTA"," Ruta: "+videoFile.toString());
        Log.i("DURACION"," Duracion del video: "+hours+":"+minutes+":"+seconds+":"+timeInmillisec);
        if (timeInmillisec<=0){
            videoFile.delete();
            isSave=false;
            Log.i("BORRADO","Si");
        }else{
            isSave=true;
            Log.i("BORRADO","No");
        }
        refreshGallery(videoFile);
    }
}