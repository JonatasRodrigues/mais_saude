package br.com.civico.mais.saude.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Jônatas Rodrigues on 15/10/2016.
 */
public class LocationPermissionsUtil implements DialogInterface.OnClickListener {
    private static final String TAG = "LocationPermissionsUtil";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    static private Activity activity;


    public LocationPermissionsUtil(Activity activity) {
        this.activity = activity;
    }

    public void requestLocationPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this.activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showMessageOKCancel("Este aplicativo foi projetado para exibir as Unidades Hospitalares próximas a sua localização.",
                    this.activity, this, this);
            return;
        }
        activityRequestPermission();
    }

    private static void showMessageOKCancel(String message, Activity activity, DialogInterface.OnClickListener okListener,
                                            DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(activity).setMessage(message).setPositiveButton("OK", okListener).create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ActivityCompat.requestPermissions(this.activity, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_PERMISSION_REQUEST_CODE);
        }else if (which == DialogInterface.BUTTON_NEGATIVE) {
            Log.e(TAG, "Permissão negada!");
        }
    }

    private void activityRequestPermission() {
        ActivityCompat.requestPermissions(this.activity, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                LOCATION_PERMISSION_REQUEST_CODE);
    }

}