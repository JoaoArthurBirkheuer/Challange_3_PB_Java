package br.com.compass.challenge3SpringBoot.exception;

public class ReportBadRequestException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReportBadRequestException(String message) {
        super(message);
    }
}
