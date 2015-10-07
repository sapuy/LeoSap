package imaginamos.prueba.leosap;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class principal extends Activity {
    TextView tv1,tv2,tv3;
    SlidingMenu menu;
    ListView lv;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Funciones.instancia_actionbar(this, R.id.ly_principal_ll, R.layout.action_bar, getString(R.string.principal_titulo));//In
        menu=Funciones.instanciar_menu(this, R.layout.menu_principal);

        lv=(ListView)findViewById(R.id.ly_principal_lv);
        tv1=(TextView)findViewById(R.id.ly_principal_tv_cargando);
        tv1.setTypeface(Funciones.get_font(this));
        lv.setVisibility(View.INVISIBLE);

        tv2=(TextView)findViewById(R.id.menu_principal_item1);
        tv2.setTypeface(Funciones.get_font(this));

        tv3=(TextView)findViewById(R.id.menu_principal_item3);
        tv3.setTypeface(Funciones.get_font(this));
        pref=Funciones.getShared(this);
        carga_inicial();


    }

    private void carga_inicial(){
        //Validar conexion a internet
        if(!Funciones.isConnected(this)){
            Toast.makeText(this, R.string.error_toast_noconnected, Toast.LENGTH_LONG).show();
        }
        enviarGet tarea2 = new enviarGet(Constantes.url_pruebas);
        tarea2.execute();//Se encarga de conectarse a internet y descargar el contenido de la url dada


    }


    @Override
    public void onBackPressed() {

        if(menu.isMenuShowing())
            menu.toggle();
        else{
            finish();
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            menu.toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClickAB(View v){

        if (Constantes.debug)
            Log.d("LeoSap onClick", "View Id: " + getResources().getResourceEntryName(v.getId()));

        switch (v.getId()) {
            case R.id.actionbar_back:
                onBackPressed();
                break;

            case R.id.actionbar_menu:
                menu.toggle();
                break;
        }
    }

    public void onClickMenu(View v){

        if (Constantes.debug)
            Log.d("LeoSap onClick", "View Id: " + getResources().getResourceEntryName(v.getId()));
        menu.toggle();
        switch (v.getId()) {

            case R.id.menu_principal_item1://acerca de
               Funciones.acerca_de(this);
                break;

            case R.id.menu_principal_item3://En caso de recargar, se desaparece el listview y se hace de nuevo el llamado a la función carga_inicial

                Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
                anim2.setDuration(1000);
                anim2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lv.setVisibility(View.INVISIBLE);
                        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                        anim.setDuration(1000);
                        tv1.setVisibility(View.VISIBLE);
                        tv1.startAnimation(anim);//aparecemos el texto "Cargando"
                        carga_inicial();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                lv.startAnimation(anim2);//desaparecemos el listview

                break;
        }
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    private class enviarGet extends AsyncTask<Void, Integer, Boolean> {

        private JSONObject response;
        private String url;
        ArrayList<apps> applications;
        BaseAdapter adapter;

        public enviarGet(String url){
            this.url=url;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                if(Funciones.isConnected(principal.this)){
                    response = ManejadorHTTP.sendGet(url);//Obtenemos la respuesta usando la clase ManejadorHTTP
                }
                else{
                    response=new JSONObject(pref.getString(Constantes.pref_json,null));//si no hay internet, usar el Json guardado en las shared //offline
                }

                try{
                    if(response==null){
                        return false;//Si no se tiene respuesta se retorna falso
                    }
                    else if(response.has("feed")){//si se tiene respuesta instanciamos el objeto JSON y extraemos el contenido
                      applications=new ArrayList<apps>();

                        pref.edit().putString(Constantes.pref_json,response.toString()).commit();//Guardamos el JSON en las shared preferences

                        JSONArray apps=response.getJSONObject("feed").getJSONArray("entry");
                        for (int i = 0; i <= apps.length() - 1; i++) {//recorremos el array de aplicaciones
                            JSONObject childJSONObject = apps.getJSONObject(i);
                            String name = childJSONObject.getJSONObject("im:name").getString("label");
                            String resumen = childJSONObject.getJSONObject("summary").getString("label");
                            float precio = Float.parseFloat(childJSONObject.getJSONObject("im:price").getJSONObject("attributes").getString("amount"));
                            String moneda = childJSONObject.getJSONObject("im:price").getJSONObject("attributes").getString("currency");
                            String autor=childJSONObject.getJSONObject("im:artist").getString("label");
                            String link_d=childJSONObject.getJSONObject("im:artist").getJSONObject("attributes").getString("href");
                            JSONArray images=childJSONObject.getJSONArray("im:image");
                            String icon=images.getJSONObject(2).getString("label");
                            long id=childJSONObject.getJSONObject("id").getJSONObject("attributes").getLong("im:id");
                            String fecha=childJSONObject.getJSONObject("im:releaseDate").getJSONObject("attributes").getString("label");
                            //String fecha="nada";
                            String categoria=childJSONObject.getJSONObject("category").getJSONObject("attributes").getString("label");
                            String link=childJSONObject.getJSONObject("link").getJSONObject("attributes").getString("href");
                            String pre;
                            if(precio>0)
                                pre=String.valueOf(precio)+" "+moneda;
                            else
                                pre=getString(R.string.detalles_free);

                            String bmp=Funciones.getBitmapFromURL(icon,id);//Guarda el bmp según una url dada y retorna la ruta de almacenamiento
                            apps app=new apps(bmp,name,resumen,pre,autor,fecha,categoria,link,link_d,id);//creamos un nuevo objeto app
                            applications.add(app);//llenamos el arraylist con el objeto app
                            Log.d("LeoSap App " + i, "Name: " + name +  " Precio: " + pre+" Autor: "+autor+" Icon: "+icon+" - "+bmp+" Fecha: "+fecha+" Categoria: "+categoria+" Link: "+link);
                        }
                        adapter=new CustomArrayAdapterList(principal.this,applications);//instanciamos el adaptador personalizado con el arraylist de apps

                        return true;
                    }
                } catch (JSONException e) {
                    if(Constantes.debug) Log.e("LeoSap JSON", e.getMessage());

                }

            } catch (Exception e) {
                if(Constantes.debug) Log.e("LeoSap ManejadorHTTP", e.getMessage());


            }
          return true;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){//Si la respuesta es correcta, se anima el list
                if(adapter!=null){
                lv.setAdapter(adapter);//asignamos el adaptador al listview y animamos la entrada de éste
                Animation anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
                anim2.setDuration(1000);
                anim2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tv1.setVisibility(View.INVISIBLE);
                        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
                        anim.setDuration(1000);
                        lv.setVisibility(View.VISIBLE);
                        lv.startAnimation(anim);//aparecemos el listview

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                tv1.startAnimation(anim2);//desaparecemos el textview "cargando"
            }else{//Si hubo respuesta del servidor, pero no se pudo generar el adaptador
                    Toast.makeText(principal.this,R.string.error_toast_errordesconocido,Toast.LENGTH_SHORT).show();
                }
            }

            else{//si no hubo respuesta del servidor
                Toast.makeText(principal.this,R.string.error_toast_noservidor,Toast.LENGTH_SHORT).show();
            }

        }





    }





}
