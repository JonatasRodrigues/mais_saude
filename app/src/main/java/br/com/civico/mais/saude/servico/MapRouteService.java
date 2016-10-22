package br.com.civico.mais.saude.servico;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.civico.mais.saude.converter.StreamConverter;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Jônatas Rodrigues on 16/10/2016.
 */
public class MapRouteService {

    public List<LatLng>  consumirServicoGoogle(Double latitudeOrigem,Double longitudeOrigem,Double latitudeDestino,
              Double longitudeDestino){
        String result=null;
        try {
            HttpGet http = new HttpGet("https://maps.googleapis.com/maps/api/directions/json?origin=" + latitudeOrigem + "," + longitudeOrigem +
                    "&destination=" + latitudeDestino +"," + longitudeDestino + "&sensor=false&units=metric&mode=driving");

            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse response = httpclient.execute(http);
            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200) {
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    result = StreamConverter.convertStreamToString(instream);
                    instream.close();
                }

               return obterPontos(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<LatLng> obterPontos(String resultado) {
        List<LatLng> poly = new ArrayList<LatLng>();

        try {
            final JSONObject json = new JSONObject(resultado);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encoded = overviewPolylines.getString("points");

            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return poly;
    }

}
