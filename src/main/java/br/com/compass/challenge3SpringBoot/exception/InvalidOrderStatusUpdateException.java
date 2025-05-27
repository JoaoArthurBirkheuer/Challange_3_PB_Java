package br.com.compass.challenge3SpringBoot.exception;

public class InvalidOrderStatusUpdateException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidOrderStatusUpdateException(String message) {
        super(message);
    }
}