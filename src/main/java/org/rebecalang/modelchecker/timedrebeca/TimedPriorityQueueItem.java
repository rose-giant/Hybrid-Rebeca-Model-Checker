package org.rebecalang.modelchecker.timedrebeca;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TimedPriorityQueueItem<T> implements Comparable<TimedPriorityQueueItem<T>>, Serializable {
    private int time;
    private T item;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + item.hashCode();
        return result;
    }

    public TimedPriorityQueueItem(int time, T item) {
        super();
        this.time = time;
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public int getTime() {
        return time;
    }

    public int compareTo(TimedPriorityQueueItem<T> timePriorityQueueItem) {
        return -Integer.compare(timePriorityQueueItem.time, this.time);
    }
}
