package br.com.civico.mais.saude.exception;

/**
 * Created by Jônatas Rodrigues on 05/09/2016.
 */
public class ErroServicoTCUException extends RuntimeException{

    public ErroServicoTCUException(String mensagem){
       super(mensagem);
    }
}
