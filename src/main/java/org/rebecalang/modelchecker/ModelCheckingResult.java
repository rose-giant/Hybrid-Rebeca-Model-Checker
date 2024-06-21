package org.rebecalang.modelchecker;

public class ModelCheckingResult {

	public static final String DEADLOCK = "Deadlock";
	public static final String SATISFIED = "Satisfied";
	
	private String message;

	public ModelCheckingResult(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
