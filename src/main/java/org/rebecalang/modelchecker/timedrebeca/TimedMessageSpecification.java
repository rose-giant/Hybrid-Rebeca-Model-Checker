package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;

import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class TimedMessageSpecification extends MessageSpecification {
    private int minStartTime;
	private int maxStartTime;

    public TimedMessageSpecification(
            String messageName,
            Map<String, Object> parameters,
            BaseActorState baseActorState,
            int minStartTime,
            int maxStartTime) {
        super(messageName, parameters, baseActorState);
        this.maxStartTime = maxStartTime;
        this.minStartTime = minStartTime;
    }

	public int getMinStartTime() {
		return this.minStartTime;
	}

	public void setMinStartTime(int minStartTime) {
		this.minStartTime = minStartTime;
	}

	public void setMaxStartTime(int maxStartTime) {
		this.maxStartTime = maxStartTime;
	}

	public int getMaxStartTime() {
		return this.maxStartTime;
	}

	public void export(PrintStream output) {
		output.print("<message sender=\"" + senderActorState.getName() + 
				"\" arrival=\"" + minStartTime + "\"" +
				" deadline=\"" + maxStartTime + "\">");
		output.print(messageName + "(");
		for(Entry<String, Object> entry : parameters.entrySet())
			output.print(entry.getKey() + "->" + entry.getValue() + ", ");
		output.println(")</message>");
	}
}
