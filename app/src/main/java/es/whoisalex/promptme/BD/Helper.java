package es.whoisalex.promptme.BD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alex on 16/07/2017.
 */

public class Helper extends SQLiteOpenHelper {




    private static final String SQL_CREATE_FRASES =
            "CREATE TABLE " + DataBaseColumns.Frases.TABLA + " (" +
                    DataBaseColumns.Frases._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DataBaseColumns.Frases.FRASE + " TEXT )";

    private static String textoDefault = "Al contrario del pensamiento popular, el texto de Lorem Ipsum no es simplemente texto aleatorio. Tiene sus raices en una pieza cl´sica de la literatura del Latin, que data del año 45 antes de Cristo, haciendo que este adquiera mas de 2000 años de antiguedad. Richard McClintock, un profesor de Latin de la Universidad de Hampden-Sydney en Virginia, encontró una de las palabras más oscuras de la lengua del latín, \"consecteur\", en un pasaje de Lorem Ipsum, y al seguir leyendo distintos textos del latín, descubrió la fuente indudable. Lorem Ipsum viene de las secciones 1.10.32 y 1.10.33 de \"de Finnibus Bonorum et Malorum\" (Los Extremos del Bien y El Mal) por Cicero, escrito en el año 45 antes de Cristo. Este libro es un tratado de teoría de éticas, muy popular durante el Renacimiento. La primera linea del Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", viene de una linea en la sección 1.10.32";
    private static final String SQL_INSERT_INTO =
            "INSERT INTO " + DataBaseColumns.Frases.TABLA + " (" +
                    DataBaseColumns.Frases._ID + ", " +
                    DataBaseColumns.Frases.FRASE + ") VALUES ('"+0+"', '"+textoDefault+"')";

    public Helper(Context contexto, String nombreBD, SQLiteDatabase.CursorFactory factory, int versionBD) {
        // Se llama al constuctor del padre.
        super(contexto, nombreBD, factory, versionBD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Se ejecutan las sentencias SQL de creación de las tablas.
        db.execSQL(SQL_CREATE_FRASES);
        db.execSQL(SQL_INSERT_INTO);
        // ... (sentencias SQL del resto de tablas)
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        // Por simplicidad, se eliminan las tablas existentes y se vuelven a crear,
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseColumns.Frases.TABLA);
        db.execSQL(SQL_CREATE_FRASES);
    }
}