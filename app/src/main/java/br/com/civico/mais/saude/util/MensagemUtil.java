package br.com.civico.mais.saude.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.com.civico.mais.saude.R;

/**
 * Created by Jônatas Rodrigues on 16/09/2016.
 */
public class MensagemUtil {

    public static void exibirMensagemErro(LayoutInflater inflater,Context context,String mensagem,ViewGroup viewGroup){
       // LayoutInflater inflater =inflater;
        // Inflate the Layout
        View layout = inflater.inflate(R.layout.toast_erro,viewGroup);

        TextView text = (TextView) layout.findViewById(R.id.textErro);
        // Set the Text to show in TextView
        text.setText(mensagem);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
