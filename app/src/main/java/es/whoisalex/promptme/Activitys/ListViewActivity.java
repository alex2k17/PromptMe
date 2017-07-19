package es.whoisalex.promptme.Activitys;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

import es.whoisalex.promptme.Adapters.FrasesAdapter;
import es.whoisalex.promptme.BD.DAO;
import es.whoisalex.promptme.Interfaces.Acctions;
import es.whoisalex.promptme.POJOS.PromptMe;
import es.whoisalex.promptme.R;


public class ListViewActivity extends Activity implements Acctions {


    DAO dao;
    PromptMe promptMe;
    FrasesAdapter lstAdapter;
    Context mContext;
    private ImageButton btnAgregar;
    private EditText input;
    private ListView lstFrases;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        mContext=this;
        loadViews();
        try {
            cargarLista();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarLista() throws SQLException {
        dao = new DAO(this).open();
        ArrayList<PromptMe> frases = (ArrayList<PromptMe>) dao.getAllFrases();
        dao.close();
        lstAdapter = new FrasesAdapter(this, frases);
        lstAdapter.setDelegate(this);
        lstFrases.setAdapter(lstAdapter);
        if (lstFrases != null) {
            lstAdapter.setDelegate(this);
            lstFrases.setAdapter(lstAdapter);
        }
        for (int i = 0; i < lstFrases.getCount(); i++) {
            lstFrases.setItemChecked(i, false);
        }

    }

    public void loadViews(){
        btnAgregar = (ImageButton) findViewById(R.id.btnAgregar);
        lstFrases = (ListView) findViewById(R.id.lstFrases);
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFrase();
            }
        });

        lstFrases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                promptMe = (PromptMe) lstFrases.getItemAtPosition(position);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent i = new Intent(getApplicationContext(),Camera2Activity.class);
                    //Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtra("Frase", promptMe.getFrase());
                    startActivity(i);
                } else {
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtra("Frase", promptMe.getFrase());
                    startActivity(i);
                }
            }
        });
    }

    public void addFrase(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escribe el Prompt");

        input = new EditText(this);

        input.setText("");
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setSingleLine(false);
        input.setLines(5);
        input.setMaxLines(5);
        input.requestFocus();
        input.setGravity(Gravity.LEFT | Gravity.TOP);
        builder.setView(input);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String frase;
                frase = input.getText().toString();
                if (!frase.equalsIgnoreCase("")){
                    PromptMe promptFrase = new PromptMe();
                    promptFrase.setFrase(frase);
                    dao = new DAO(mContext);
                    try {
                        dao.open();
                        long id = dao.createFrase(promptFrase);
                        dao.close();
                        if (id >= 0) {
                            promptFrase.setId(id);
                        }
                        Log.e("Frase añadida", promptFrase.toString());
                        cargarLista();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                        );
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void editFrase(PromptMe frase){
        promptMe = frase;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edita el Prompt");

        input = new EditText(this);

        input.setText(promptMe.getFrase().toString());
        input.setSelection(promptMe.getFrase().length());
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setSingleLine(false);
        input.setLines(5);
        input.setMaxLines(5);
        input.setGravity(Gravity.LEFT | Gravity.TOP);
        input.requestFocus();
        builder.setView(input);
        input.setSelection(promptMe.getFrase().length());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String frase;
                frase = input.getText().toString();
                if (!frase.equalsIgnoreCase("")){
                    promptMe.setFrase(frase);
                    dao = new DAO(mContext);
                    try {
                        dao.open();
                        dao.updateFrase(promptMe);
                        dao.close();
                        Log.e("Frase editada", promptMe.toString());
                        cargarLista();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                        );
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void delete(PromptMe frase) {
        promptMe = frase;
        try {
            dao = new DAO(this).open();
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar")
                    .setMessage("¿Estas seguro?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dao.deleteFrase(promptMe.getId());
                            ((FrasesAdapter) lstFrases.getAdapter()).remove(promptMe);
                            dao.close();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((FrasesAdapter) lstFrases.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void edit(PromptMe frase) {
        editFrase(frase);
        ((FrasesAdapter) lstFrases.getAdapter()).notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
    }
}
