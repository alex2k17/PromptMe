package es.whoisalex.promptme.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import java.util.List;

import es.whoisalex.promptme.Interfaces.Acctions;
import es.whoisalex.promptme.POJOS.PromptMe;
import es.whoisalex.promptme.R;

/**
 * Created by Alex on 16/07/2017.
 */

public class FrasesAdapter extends ArrayAdapter<PromptMe> {

    private final List<PromptMe> mFrases;
    private final LayoutInflater mInflador;
    public Acctions delegate;


    public FrasesAdapter(Context contexto, List<PromptMe> frases) {
        super(contexto, R.layout.activity_main_item, frases);
        mFrases = frases;
        mInflador = LayoutInflater.from(contexto);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        // Si no se puede reciclar.
        if (convertView == null) {
            // Se obtiene la vista-fila inflando el layout.
            convertView = mInflador.inflate(R.layout.activity_main_item, parent, false);
            // Se crea el contenedor de vistas para la vista-fila.
            holder = new ViewHolder(convertView);
            // Se almacena el contenedor en la vista.
            convertView.setTag(holder);
        } else {
            // Se obtiene el contenedor de vistas desde la vista reciclada.
            holder = (ViewHolder) convertView.getTag();
        }
        // Se escriben los datos en las vistas del contenedor de vistas.
        onBindViewHolder(holder, position);
        // Se retorna la vista que representa el elemento.
        return convertView;
    }

    private void onBindViewHolder(ViewHolder holder, int position) {
        PromptMe frase = mFrases.get(position);
        holder.lblFrase.setText(frase.getFrase());
        // Se crea un nuevo objeto listener para cuando se pulse en la
        // imagen, a cuyo construtor se le pasa el mAlumno del que se trata.
        holder.imgPopupMenu.setOnClickListener(
                new ImgPopupMenuOnClickListener(mFrases.get(position)));

    }


    static class ViewHolder {

        // El contenedor de vistas para un elemento de la lista debe contener...
        private final TextView lblFrase;
        private final ImageView imgPopupMenu;

        // El constructor recibe la vista-fila.
        public ViewHolder(View itemView) {
            // Se obtienen las vistas de la vista-fila.
            lblFrase = (TextView) itemView.findViewById(R.id.lblFrase);
            imgPopupMenu = (ImageView) itemView.findViewById(R.id.imgPopupMenu);
        }
    }

    // Clase listener para pulsación sobre el icono del PopupMenu.
    private class ImgPopupMenuOnClickListener implements OnClickListener {

        private final PromptMe mFrases;

        // Constructor. Recibe el alumno asociado.
        public ImgPopupMenuOnClickListener(PromptMe frase) {
            mFrases = frase;
        }

        // Cuando se hace click sobre el icono.
        @Override
        public void onClick(View v) {
            // Se crea el menú.
            PopupMenu popup = new PopupMenu(getContext(), v);
            // Se infla la especificación de menú.
            MenuInflater inflador = popup.getMenuInflater();
            inflador.inflate(R.menu.activity_main_item_popup, popup.getMenu());
            // Se crea el listener para cuando se pulse un ítem del menú, a cuyo
            // constructor se le pasa el mAlumno asociado.
            popup.setOnMenuItemClickListener(new PopupMenuOnMenuItemClickListener(mFrases));
            // Se muestra el menú.
            popup.show();
        }
    }

    private class PopupMenuOnMenuItemClickListener implements OnMenuItemClickListener {

        // Alumno asociado.
        final PromptMe frase;

        // Constructor. Recibe el mAlumno asociado.
        public PopupMenuOnMenuItemClickListener(PromptMe frase) {
            this.frase = frase;
        }

        // Cuando se selecciona un ítem del PopupMenu.
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.mnuEditar:
                    delegate.edit(frase);
                    break;
                case R.id.mnuBorrar:
                    delegate.delete(frase);
                    break;
            }
            return true;
        }
    }

    public void setDelegate(Acctions delegate){
        this.delegate=delegate;
    }
}