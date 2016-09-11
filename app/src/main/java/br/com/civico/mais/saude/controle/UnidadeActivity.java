package br.com.civico.mais.saude.controle;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListUnidadeAdapter;
import br.com.civico.mais.saude.dto.ExpandableDTO;
import br.com.civico.mais.saude.exception.ErroServicoTCUException;
import br.com.civico.mais.saude.exception.GPSException;
import br.com.civico.mais.saude.servico.GPSService;
import br.com.civico.mais.saude.servico.UnidadeService;

public class UnidadeActivity extends Activity {

    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unidade_consulta);

        if(hasPermissions()) {

            try {
                Location location = new GPSService(this).getLocation();
                ExpandableDTO dto = UnidadeService.getInstance(location).execute(new String()).get();
                expListView = (ExpandableListView) findViewById(R.id.unidadeListView);

                ExpandableListAdapter listAdapter = new ExpandableListUnidadeAdapter(this, dto.getListDataHeader(),
                        dto.getListDataChild());

                configurarExpList();
                expListView.setAdapter(listAdapter);

            } catch (ErroServicoTCUException e) {
                exibirMensagemErro(e.getMessage());
                voltarMenu();
            } catch (GPSException e) {
               exibirMensagemErro(e.getMessage());
                voltarMenu();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        } else {
            ActivityCompat.requestPermissions(UnidadeActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    private void configurarExpList(){
        ColorDrawable linhaColor = new ColorDrawable(this.getResources().getColor(R.color.lime));
        expListView.setChildDivider(getResources().getDrawable(R.color.transparente));
        expListView.setDivider(linhaColor);
        expListView.setDividerHeight(2);
    }

    private void voltarMenu(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean hasPermissions(){
        boolean permissionFineLocation = ActivityCompat.checkSelfPermission(UnidadeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permissionCoarseLocation = ActivityCompat.checkSelfPermission(UnidadeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return (permissionCoarseLocation && permissionFineLocation);
    }

    private void exibirMensagemErro(String mensagem){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_erro,(ViewGroup) findViewById(R.id.layout_erro));
        TextView text = (TextView) layout.findViewById(R.id.textErro);
        text.setText(mensagem);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        //Configurar tempo de exibição
        int toastDurationInMilliSeconds = 10000;
        CountDownTimer toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }
            public void onFinish() {
                toast.cancel();
            }
        };

        // Show the toast and starts the countdown
       // toast.show();
        toastCountDown.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
    //    if (id == R.id.action_settings) {
    //        return true;
    //    }

        return super.onOptionsItemSelected(item);
    }

}
