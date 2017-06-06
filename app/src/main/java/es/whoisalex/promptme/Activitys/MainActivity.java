package es.whoisalex.promptme.Activitys;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
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
    private ScrollView scroll;
    private ImageView punto;
    private ImageButton imgClose, imgMoreSpeed, imgLessSpeed;
    private ShapeDrawable sd;
    private File f;
    private Toast toast;
    private FrameLayout caja;
    private ObjectAnimator objectAnimator;

    private boolean isRecording = false;
    private boolean isSave = false;
    private boolean isFinish = false;
    CameraPreview preview;
    int width, height;
    int speed=48000;
    float dX, dY;
    String texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadPreview();
        ViewTreeObserver vto = text.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                text.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = text.getWidth();
                height = text.getHeight();
                Log.e("Max", "width: " + width + ", " + height);
            }
        });

        imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    imgClose.setBackgroundResource(R.drawable.rec_off);
                    closeToast();
                    objectAnimator.cancel();
                    releaseMediaRecorder();
                    isRecording = false;
                    resetCam();
                    if (isSave) {
                        makeToast(getApplicationContext(), "Guardando...", Toast.LENGTH_SHORT);
                    }
                } else {
                    closeToast();
                    if (prepareVideoRecorder()) {
                        imgClose.setBackgroundResource(R.drawable.rec_on);
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


    private void msgSpeedUp(){
        imgMoreSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectAnimator!=null)objectAnimator.cancel();
                closeToast();
                makeToast(getApplicationContext(),"Velocidad+", 200);
                if (speed>=4001) {
                    speed -= 4000;
                    Log.i("ClickAdd","Velocidad: "+speed);
                }
                Log.i("ClickAdd","ScrollY: "+scroll.getScrollY());
                if (isFinish){
                    isFinish=false;
                    animationText(speed, 0);
                }else{
                    animationText(speed, scroll.getScrollY());
                }
            }
        });
    }

    private void msgSpeedLess(){
        imgLessSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objectAnimator != null) objectAnimator.cancel();
                closeToast();
                makeToast(getApplicationContext(), "Velocidad-", 200);
                speed += 4000;
                Log.i("ClickLess", "ScrollY: " + scroll.getScrollY());
                if (isFinish){
                    isFinish=false;
                    animationText(speed, 0);
                }else{
                    animationText(speed, scroll.getScrollY());
                }

            }
        });
    }

    private void animationText(int speed, int scrollY){
        objectAnimator = ObjectAnimator.ofInt(scroll, "scrollY", scrollY, height).setDuration(speed);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //Log.i("Animacion","Animacion: "+animation.getAnimatedValue());
                if ((int) animation.getAnimatedValue()>=height){
                    animation.cancel();
                    isFinish=true;
                }
            }
        });
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    private void animateOnTouch(){
        text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (objectAnimator!=null)objectAnimator.cancel();
                        break;
                    case MotionEvent.ACTION_UP:
                        animationText(speed, scroll.getScrollY());
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    //Lanzamos el mensaje
    private void msgShow() {
        if (objectAnimator!=null)objectAnimator.cancel();
        objectAnimator = ObjectAnimator.ofInt(scroll, "scrollY", 0, text.length()+200).setDuration(speed);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //Log.i("Animacion","Animacion: "+animation.getAnimatedValue());
                if ((int) animation.getAnimatedValue()==text.length()+100){
                    animation.cancel();
                }
            }
        });
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }


    private void animateBox(){
        punto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        dX = caja.getX() - event.getRawX();
                        dY = caja.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        caja.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
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
        if (objectAnimator!=null) objectAnimator.cancel();
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

    private void stopRecording() {
        try {
            mMediaRecorder.stop();
        } catch (RuntimeException stopException) {
            Log.e("ERROR", "Error al parar la grabacion: " + stopException.getMessage());
        }
        mCamera.lock();
    }

    private boolean prepareVideoRecorder() {
        if (mCamera == null) mCamera = getCameraInstance();
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
        caja = (FrameLayout) findViewById(R.id.caja);
        scroll = (ScrollView) findViewById(R.id.scrolltext);
        text = (TextView) findViewById(R.id.texto2);
        punto = (ImageView) findViewById(R.id.dot);
        imgMoreSpeed = (ImageButton) findViewById(R.id.imgAdd);
        imgLessSpeed = (ImageButton) findViewById(R.id.imgLess);
        drawDot();
        texto = "Al contrario del pensamiento popular, el texto de Lorem Ipsum no es simplemente texto aleatorio. Tiene sus raices en una pieza cl´sica de la literatura del Latin, que data del año 45 antes de Cristo, haciendo que este adquiera mas de 2000 años de antiguedad. Richard McClintock, un profesor de Latin de la Universidad de Hampden-Sydney en Virginia, encontró una de las palabras más oscuras de la lengua del latín, \"consecteur\", en un pasaje de Lorem Ipsum, y al seguir leyendo distintos textos del latín, descubrió la fuente indudable. Lorem Ipsum viene de las secciones 1.10.32 y 1.10.33 de \"de Finnibus Bonorum et Malorum\" (Los Extremos del Bien y El Mal) por Cicero, escrito en el año 45 antes de Cristo. Este libro es un tratado de teoría de éticas, muy popular durante el Renacimiento. La primera linea del Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", viene de una linea en la sección 1.10.32";
        text.setText(texto);
        punto.setBackground(sd);
        preview.setKeepScreenOn(true);
        Log.e("Length", "textview: "+text.length());
        loadAnimations();
    }

    public void drawDot(){
        sd = new ShapeDrawable(new OvalShape());
        sd.setIntrinsicHeight(40);
        sd.setIntrinsicWidth(40);
        sd.getPaint().setColor(Color.parseColor("#800099ff"));
    }

    private void loadAnimations(){
        animateBox();
        animateOnTouch();
        msgSpeedLess();
        msgSpeedUp();
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
        if (toast != null) {toast.cancel();}
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