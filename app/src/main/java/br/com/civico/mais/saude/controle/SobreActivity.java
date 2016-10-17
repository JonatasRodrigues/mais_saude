package br.com.civico.mais.saude.controle;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import br.com.civico.mais.saude.R;

public class SobreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobre);
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

    @Override
    public void onBackPressed() {
      voltarMenu();
    }

}
