package es.whoisalex.promptme.Activitys;

import android.app.Activity;
import android.os.Bundle;

import es.whoisalex.promptme.Fragments.Camera2Fragment;
import es.whoisalex.promptme.R;

/**
 * Created by Alex on 07/06/2017.
 */

public class Camera2Activity extends Activity {

    String texto;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        texto=getIntent().getStringExtra("Frase");
        Camera2Fragment f = new Camera2Fragment();
        Bundle args = new Bundle();
        args.putString("Frase", texto);
        f.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, f)
                .commit();
    }
}
