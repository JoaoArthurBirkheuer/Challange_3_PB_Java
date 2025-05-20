package br.com.compass.challenge3SpringBoot.exception;

public class EmailJaCadastradoException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmailJaCadastradoException(String message) {
        super(message);
    }
}