package br.com.compass.challenge3SpringBoot.exception;

public class NoDataFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoDataFoundException(String message) {
        super(message);
    }
}
