package com.voidsearch.voidbase.storage.queuetree;

public class QueueEntry {

    private long timestamp;
    private Object value;

    public QueueEntry(Object value) {
        timestamp = System.currentTimeMillis();
        this.value = value;
    }

    // allow for overriding of getKey method
    public String getKey()  {
        return (String)value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return value.toString();
    }

}
