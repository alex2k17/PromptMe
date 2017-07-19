package es.whoisalex.promptme.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import java.sql.SQLException;

import es.whoisalex.promptme.POJOS.PromptMe;

/**
 * Created by Alex on 16/07/2017.
 */

public class DAO {

    // Variables a nivel de clase.
    private Helper helper; // Ayudante para la creación y gestión de la BD.
    private SQLiteDatabase bd; // BD que manejará el DAO.

    // Constructor. Recibe el contexto.
    public DAO(Context contexto) {
        // Se obtiene el helper.
        helper = new Helper(contexto, DataBaseColumns.BD_NOMBRE, null, DataBaseColumns.BD_VERSION);
    }

    // Abre la BD. Se retorna a sí mismo.
    public DAO open() throws SQLException {
        // Se obtiene una BD actualizable a través del helper.
        bd = helper.getWritableDatabase();
        // Nos retornamos a nosotros mismos, es decir, al objeto
        // adaptador de BD sobre el que se ha ejecutado el método open.
        return this;
    }

    // Cierra la BD.
    public void close() {
        // Se cierra la BD a través del helper.
        helper.close();
    }

    public long createFrase(PromptMe frase) {
        // Se crea la lista de pares campo-valor para realizar la inserción.
        ContentValues valores = new ContentValues();
        valores.put(DataBaseColumns.Frases.FRASE, frase.getFrase());

        return bd.insert(DataBaseColumns.Frases.TABLA, null, valores);
    }

    public boolean deleteFrase(long id) {
        // Se realiza el delete, retornando si ha ido bien o no.
        return bd.delete(DataBaseColumns.Frases.TABLA, DataBaseColumns.Frases._ID + " = "
                + id, null) > 0;
    }

    public boolean updateFrase(PromptMe frase) {
        // Se crea la lista de pares clave-valor con cada campo-valor.
        ContentValues valores = new ContentValues();
        valores.put(DataBaseColumns.Frases.FRASE, frase.getFrase());



        return bd.update(DataBaseColumns.Frases.TABLA, valores, DataBaseColumns.Frases._ID
                + " = " + frase.getId(), null) > 0;
    }


    public Cursor queryAllAvisos() {
        // Se realiza la consulta, retornando el cursor resultado.
        return bd.query(DataBaseColumns.Frases.TABLA, DataBaseColumns.Frases.TODOS, null,
                null, null, null, DataBaseColumns.Frases.FRASE);
    }


    public PromptMe cursorToFrase(Cursor cursorFrase) {
        PromptMe frase = new PromptMe();
        frase.setId(cursorFrase.getLong(0));
        frase.setFrase(cursorFrase.getString(1));

        return frase;
    }

    public List<PromptMe> getAllFrases() {

        List<PromptMe> lista = new ArrayList<PromptMe>();
        Cursor cursor = this.queryAllAvisos();
        // LLena la lista convirtiendo cada registro del cursor
        // en un elemento de la lista.
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PromptMe frase = cursorToFrase(cursor);
            lista.add(frase);
            cursor.moveToNext();
        }
        // Cierro el cursor (IMPORTANTE).
        cursor.close();
        // Retorno la lista.
        return lista;
    }

    public PromptMe queryFrases(long id) {
        // Se realiza la query SQL sobre la BD.
        Cursor cursor = bd.query(true, DataBaseColumns.Frases.TABLA,
                DataBaseColumns.Frases.TODOS, DataBaseColumns.Frases._ID + " = " + id,
                null, null, null, null, null);
        // Se mueve al primer registro del cursor.
        if (cursor != null) {
            cursor.moveToFirst();
            return cursorToFrase(cursor);
        } else {
            return null;
        }
    }

    public void dropBD() {
        String sql="DELETE FROM Frases";
        // Se realiza la query SQL sobre la BD.
        bd.execSQL(sql);
    }

    public Cursor getFrases(String sql) {
        // Se realiza la query SQL sobre la BD.
        Cursor cursor = bd.rawQuery(sql,null);
        // Se mueve al primer registro del cursor.
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }
}
