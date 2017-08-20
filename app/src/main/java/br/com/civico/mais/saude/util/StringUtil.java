package br.com.civico.mais.saude.util;

/**
 * Created by Jônatas Rodrigues on 19/08/2017.
 */
public class StringUtil {

    public static String formatar(String msg){
        if("".equals(msg.trim()))
            return msg;

        String[] array = msg.split(":");

        if(array.length == 2){
            return array[0] + ": " + "<b>" + array[1] + "</b>";
        }else{
            String[] array2 = array[1].split("/");
            return array[0] + ": " + "<b>" + array2[0] + "</b>"  + "&emsp;&emsp;&emsp;" + array2[1] + ": " + "<b>" + array[2] + "</b>" ;
        }

    }
}
