package es.whoisalex.promptme.BD;

import android.provider.BaseColumns;

/**
 * Created by Alex on 16/07/2017.
 */

public class DataBaseColumns {

    public static final String BD_NOMBRE = "PromptMe";
    public static final int BD_VERSION = 1;

    public static abstract class Frases implements BaseColumns {

        public static final String TABLA = "Frases";

        public static final String FRASE = "Frase";

        public static final String[] TODOS = new String[] { _ID, FRASE};
    }

    private DataBaseColumns() {
    }
}
