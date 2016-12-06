package br.com.civico.mais.saude.controle;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.constantes.ConstantesAplicacao;
import br.com.civico.mais.saude.util.LocationPermissionsUtil;

public class MainActivity extends BaseActivity {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private Context context;
    private SharedPreferences settings ;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context=this;
        this.settings = PreferenceManager.getDefaultSharedPreferences(this);
        this.editor = settings.edit();

        Button  btnUnidade = (Button) findViewById(R.id.btnUnidade);
        btnUnidade.setOnClickListener(onClickListenerUnidade);

        Button  btnMedicamento = (Button) findViewById(R.id.btnMedicamento);
        btnMedicamento.setOnClickListener(onClickListenerMedicamento);

        Button  btnSobre = (Button) findViewById(R.id.btnSobre);
        btnSobre.setOnClickListener(onClickListenerSobre);

        Button  btnSair = (Button) findViewById(R.id.btnSair);
        btnSair.setOnClickListener(onClickListenerSair);

    }
    private Dialog popUpPesquisa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(R.array.search_array_medicamentoPor, -1,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int opcaoEscolhida) {
                Intent intent = new Intent(MainActivity.this, MedicamentoActivity.class);
                switch (opcaoEscolhida) {
                    case 0:
                        editor.putString("tipoPesquisaMedicamento", ConstantesAplicacao.SEARCH_MEDICAMENTOPOR_CODBARRA);
                        editor.commit();
                        startActivity(intent);
                        arg0.dismiss();
                        break;
                    case 1:
                        editor.putString("tipoPesquisaMedicamento", ConstantesAplicacao.SEARCH_MEDICAMENTOPOR_LISTARTODOS);
                        editor.commit();
                        startActivity(intent);
                        arg0.dismiss();
                        break;
                }
            }
        });

        return builder.create();
    }
    private View.OnClickListener onClickListenerMedicamento = new View.OnClickListener() {
        public void onClick(View view) {
            if(view.getId() == R.id.btnMedicamento){
                popUpPesquisa().show();
            }
        }
    };

    private View.OnClickListener onClickListenerSobre = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnSobre){
                Intent intent = new Intent(MainActivity.this, SobreActivity.class);
                startActivity(intent);
            }
        }
    };

    private View.OnClickListener onClickListenerUnidade = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnUnidade){
                if(hasPermissions()){
                    Intent intent = new Intent(MainActivity.this, UnidadeActivity.class);
                    startActivity(intent);
                }else{
                    LocationPermissionsUtil permissions = new LocationPermissionsUtil(MainActivity.this);
                    permissions.requestLocationPermission();
                }
            }
        }
    };

    private View.OnClickListener onClickListenerSair = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnSair){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.clear();     // CLEAR ALL FILEDS
                                editor.commit();    // COMMIT CHANGES

                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(startMain);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Deseja encerrar o aplicativo?").setPositiveButton("Ok", dialogClickListener)
                        .setNegativeButton("Cancelar", dialogClickListener).show();
            }
        }
    };

    private boolean hasPermissions(){
        boolean permissionFineLocation = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permissionCoarseLocation = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return (permissionCoarseLocation && permissionFineLocation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, UnidadeActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
