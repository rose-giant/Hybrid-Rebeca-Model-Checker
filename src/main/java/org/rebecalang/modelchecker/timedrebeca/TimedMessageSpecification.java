package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;

import java.io.PrintStream;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class TimedMessageSpecification extends MessageSpecification {
    int minStartTime;
    int maxStartTime;

    public TimedMessageSpecification(
            String messageName,
            ArrayList<Object> parameters,
            BaseActorState baseActorState,
            int minStartTime,
            int maxStartTime) {
        super(messageName, parameters, baseActorState);
        this.maxStartTime = maxStartTime;
        this.minStartTime = minStartTime;
    }
    
	public void export(PrintStream output) {
		output.print("<message sender=\"" + senderActorState.getName() + 
				"\" arrival=\"" + minStartTime + "\"" +
				" deadline=\"" + maxStartTime + "\">");
		output.print(messageName + "(");
		for(Object object : parameters)
			output.print(object);
		output.println(")</message>");
	}
}
