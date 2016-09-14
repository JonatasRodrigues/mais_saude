package br.com.civico.mais.saude.exception;

/**
 * Created by Jônatas Rodrigues on 05/09/2016.
 */
public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String detailMessage) {
        super(detailMessage);
    }
}
