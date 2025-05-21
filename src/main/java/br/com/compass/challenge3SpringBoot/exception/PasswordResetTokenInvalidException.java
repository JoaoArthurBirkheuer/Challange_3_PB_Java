package br.com.compass.challenge3SpringBoot.exception;

public class PasswordResetTokenInvalidException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasswordResetTokenInvalidException(String message) {
        super(message);
    }
}
