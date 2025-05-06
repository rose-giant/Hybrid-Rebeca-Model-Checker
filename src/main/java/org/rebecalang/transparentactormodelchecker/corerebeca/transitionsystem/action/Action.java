package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action;

public abstract class Action {
	public final static Action TAU = new Action() {
		@Override
		public String getActionLable() {
			return "tau";
		}
	};
	
	public abstract String getActionLable();
}
