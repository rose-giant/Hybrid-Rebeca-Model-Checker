package org.rebecalang.transparentactormodelchecker;

public class TransparentActorModelCheckingResult {

	public static final String DEADLOCK = "Deadlock";
	public static final String SATISFIED = "Satisfied";
	
	private String message;

	public TransparentActorModelCheckingResult(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
