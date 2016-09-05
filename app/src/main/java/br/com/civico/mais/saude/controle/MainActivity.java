package br.com.civico.mais.saude.controle;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.servico.GPSService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button  btnUnidade = (Button) findViewById(R.id.btnUnidade);
        btnUnidade.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            if(v.getId()== R.id.btnUnidade){
                Intent intent = new Intent(MainActivity.this, UnidadeActivity.class);
                startActivity(intent);
            }
        }
    };

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
      //      return true;
      //  }

        return super.onOptionsItemSelected(item);
    }
}
