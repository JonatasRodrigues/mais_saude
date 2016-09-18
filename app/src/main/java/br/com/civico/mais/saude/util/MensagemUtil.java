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
        View layout = inflater.inflate(R.layout.toast_erro,viewGroup);
        TextView text = (TextView) layout.findViewById(R.id.textErro);
        text.setText(mensagem);

        final Toast toast = new Toast(context);
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
        toastCountDown.start();
    }

    public static void exibirMensagemSucesso(LayoutInflater inflater,Context context,String mensagem,ViewGroup viewGroup){
        View layout = inflater.inflate(R.layout.toast_sucesso,viewGroup);
        TextView text = (TextView) layout.findViewById(R.id.textSucesso);
        text.setText(mensagem);

        final Toast toast = new Toast(context);
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
        toastCountDown.start();
    }

}
