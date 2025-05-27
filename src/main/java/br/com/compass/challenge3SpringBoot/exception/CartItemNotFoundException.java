package br.com.compass.challenge3SpringBoot.exception;

public class CartItemNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CartItemNotFoundException(String message) {
        super(message);
    }
}
