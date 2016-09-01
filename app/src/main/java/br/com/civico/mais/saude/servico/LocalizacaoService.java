package br.com.civico.mais.saude.servico;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Jônatas Rodrigues on 27/08/2016.
 */
public class LocalizacaoService {

    private Context context;

    public LocalizacaoService(Context context){
       this.context=context;
    }

    @SuppressWarnings("ResourceType")
    private Location getLocation(){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public double getLongitude(){
        if(getLocation()!=null){
            return getLocation().getLongitude();
        }
        return -47.32255;
    }

    public double getLatitude(){
        if(getLocation()!=null){
            return getLocation().getLatitude();
        }
        return -15.53375;
    }
}
