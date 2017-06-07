package es.whoisalex.promptme.Activitys;

import android.app.Activity;
import android.os.Bundle;

import es.whoisalex.promptme.Fragments.Camera2Fragment;
import es.whoisalex.promptme.R;

/**
 * Created by Alex on 07/06/2017.
 */

public class Camera2Activity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, Camera2Fragment.newInstance())
                .commit();
    }
}
