package imaginamos.prueba.leosap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Leonardo on 6/10/15.
 * Adaptador personalizado para el listview que muestra el listado de aplicaciones
 */
public class CustomArrayAdapterList extends BaseAdapter {
    ArrayList<apps> apps;
    Context context;
    private LayoutInflater inflater=null;

    public CustomArrayAdapterList(Context context, ArrayList<apps> apps) {
        // TODO Auto-generated constructor stub
        this.apps= apps;
        this.context=context;
        inflater = ( LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return apps.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv1,tv2,tv3;
        ImageView img1;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Holder holder=new Holder();//Se utiliza holder para evitar lentitud en el scroll del listado
        View rowView;

        apps app=apps.get(position);//Obtenemos la instancia del objeto app

        rowView = inflater.inflate(R.layout.list_apps, null);
        holder.tv1=(TextView) rowView.findViewById(R.id.lv_historial_tv1);
        holder.tv1.setTypeface(Funciones.get_font(context));
        holder.tv2=(TextView) rowView.findViewById(R.id.lv_historial_tv2);
        holder.tv2.setTypeface(Funciones.get_font(context));
        holder.tv3=(TextView) rowView.findViewById(R.id.lv_historial_tv3);
        holder.tv3.setTypeface(Funciones.get_font(context));
        holder.img1=(ImageView) rowView.findViewById(R.id.lv_historial_iv1);

        //Asignamos los valores a cada fila
        holder.tv1.setText(app.getNombre());
        holder.tv2.setText(app.getCategoria());
        holder.tv3.setText(app.getPrecio());
        Bitmap myBitmap = BitmapFactory.decodeFile(new File(app.getIcon()).getAbsolutePath());//Asignamos la imagen almacenada en almacenamiento interno decodificada
        holder.img1.setImageBitmap(myBitmap);


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constantes.debug)
                    Log.d("LeoSap AppLv", apps.get(position).getNombre() + " - " + apps.get(position).getPrecio() + " - " + apps.get(position).getCategoria());
                // TODO Auto-generated method stub
                //En caso de seleccionar alguna opción, se lanza un nuevo intent, enviando la instancia del objeto serializada como extra a la nueva actividad
                //Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, detalles.class);
                intent.putExtra("app", apps.get(position));
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });
        return rowView;
    }

}