package br.com.compass.challenge3SpringBoot.exception;

public class CartNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CartNotFoundException(String message) {
        super(message);
    }
}
