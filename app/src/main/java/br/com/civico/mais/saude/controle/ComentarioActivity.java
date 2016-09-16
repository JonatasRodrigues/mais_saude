package br.com.civico.mais.saude.controle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import br.com.civico.mais.saude.R;

public class ComentarioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comentario_list);

        Button  btnNovoComentario = (Button) findViewById(R.id.btnNovoComentario);
        btnNovoComentario.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            Intent intent = new Intent(ComentarioActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

    //    if (id == R.id.action_settings) {
      //      return true;
      //  }

        return super.onOptionsItemSelected(item);
    }
}
