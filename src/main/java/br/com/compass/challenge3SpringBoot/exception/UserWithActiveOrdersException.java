package br.com.compass.challenge3SpringBoot.exception;

public class UserWithActiveOrdersException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserWithActiveOrdersException(String message) {
		super(message);
	}
}
