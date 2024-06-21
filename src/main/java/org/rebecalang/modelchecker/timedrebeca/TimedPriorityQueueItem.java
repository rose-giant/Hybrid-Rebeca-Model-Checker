package org.rebecalang.modelchecker.timedrebeca;

public class TimedPriorityQueueItem<T> implements Comparable<TimedPriorityQueueItem<T>> {
    private int time;
    private T item;

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
