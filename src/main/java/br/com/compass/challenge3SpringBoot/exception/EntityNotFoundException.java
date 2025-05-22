package br.com.compass.challenge3SpringBoot.exception;

public class EntityNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntityNotFoundException(String message) {
        super(message);
    }
}
