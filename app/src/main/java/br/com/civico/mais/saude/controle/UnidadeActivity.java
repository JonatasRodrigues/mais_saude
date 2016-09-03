package br.com.civico.mais.saude.controle;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.concurrent.ExecutionException;

import br.com.civico.mais.saude.R;
import br.com.civico.mais.saude.adapter.ExpandableListUnidadeAdapter;
import br.com.civico.mais.saude.dto.ExpandableDTO;
import br.com.civico.mais.saude.servico.UnidadeService;

public class UnidadeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unidade_consulta);

        try {
            ExpandableDTO dto = UnidadeService.getInstance(this).execute(new String()).get();
            ExpandableListView expListView = (ExpandableListView) findViewById(R.id.unidadeListView);

            ExpandableListAdapter listAdapter = new ExpandableListUnidadeAdapter(this, dto.getListDataHeader(),
                    dto.getListDataChild());

            // setting list adapter
            expListView.setAdapter(listAdapter);
            ColorDrawable linhaColor = new ColorDrawable(this.getResources().getColor(R.color.lime));
            expListView.setDivider(linhaColor);
            expListView.setDividerHeight(2);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
