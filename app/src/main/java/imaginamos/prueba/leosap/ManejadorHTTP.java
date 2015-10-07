package imaginamos.prueba.leosap;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;


/**
 * Created by Leonardo on 06/10/15.
 */
public class ManejadorHTTP {


    public static JSONObject sendGet(String url) throws Exception
    {
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            if(Constantes.debug) Log.d("LeoSap HTTP url", url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();//recibimos la respuesta en un inputstream
            if(inputStream != null)
                result = Funciones.convertInputStreamToString(inputStream);//convertimos el inputstream en string

        } catch (Exception e) {
            if(Constantes.debug) Log.e("LeoSap HTTP", e.getMessage());
        }
        if(Constantes.debug) Log.d("LeoSap HTTP result", result);

        JSONObject jObject = new JSONObject(result);//retornamos un JSONObject basado en el string
        return jObject;


    }



}
