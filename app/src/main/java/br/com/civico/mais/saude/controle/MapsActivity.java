package br.com.civico.mais.saude.controle;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.servico.GPSService;
import br.com.civico.mais.saude.servico.MapRouteService;
import br.com.civico.mais.saude.util.ConexaoUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context context;
    private ProgressDialog progressDialog;
    LatLng startLatLng;
    LatLng endLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rota_unidade);
        this.context=this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ConexaoUtil.hasConnection(context)) {
            Location location = new GPSService(context).getLocation();
            if (location == null) {
                exibirMsgErro(ConstantesAplicacao.MENSAGEM_NOT_FOUND_LOCATION);
            }else{
                Double latitudeOrigem = getIntent().getDoubleExtra("latitude",0L);
                Double longitudeOrigem = getIntent().getDoubleExtra("longitude",0L);
                Double latitudeDestino =location.getLatitude();
                Double longitudeDestino = location.getLongitude();

                startLatLng = new LatLng(latitudeOrigem, longitudeOrigem);
                endLatLng = new LatLng(latitudeDestino, longitudeDestino);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(startLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

                showMapa(latitudeOrigem,longitudeOrigem,latitudeDestino,longitudeDestino);
            }
        }else{
            exibirMsgErro(ConstantesAplicacao.MENSAGEM_SEM_CONEXAO_INTERNET);
        }
    }


    private void showMapa(final Double latitudeOrigem, final Double longitudeOrigem, final Double latitudeDestino, final Double longitudeDestino){

        AsyncTask<Void, Void, List<LatLng>> task = new AsyncTask<Void, Void, List<LatLng>>() {

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(MapsActivity.this);
                progressDialog.setMessage("Definindo uma rota...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }

            @Override
            protected List<LatLng> doInBackground(Void... voids) {
                MapRouteService mapRouteService = new MapRouteService();
                return mapRouteService.consumirServicoGoogle(latitudeOrigem,longitudeOrigem,latitudeDestino,longitudeDestino);
            }

            @Override
            protected void onPostExecute(List<LatLng> result) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                drawPath(result);
            }
        };
        task.execute((Void[]) null);
    }

    public void drawPath(List<LatLng> list) {

        mMap.addMarker(new MarkerOptions().position(endLatLng));
        mMap.addMarker(new MarkerOptions().position(startLatLng));

        for (int z = 0; z < list.size() - 1; z++) {
            LatLng src = list.get(z);
            LatLng dest = list.get(z + 1);
             mMap.addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude),
                    new LatLng(dest.latitude, dest.longitude)).width(5).color(Color.BLUE).geodesic(true));
        }
    }


    private void exibirMsgErro(String mensagem){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_erro,(ViewGroup) findViewById(R.id.layout_erro));

        TextView text = (TextView) layout.findViewById(R.id.textErro);
        text.setText(mensagem);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
