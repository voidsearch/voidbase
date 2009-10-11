/*
 * Copyright 2009 VoidSearch.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.voidsearch.voidbase.supervision;

import com.voidsearch.voidbase.storage.SupervisedStorage;
import com.voidsearch.voidbase.storage.StorageException;

import java.util.HashMap;

public class StorageStats {

    String className;

    public enum State { CLEAR,
                        INDETERMINATE,
                        WARNING,
                        MINOR,
                        MAJOR,
                        CRITICAL }

    public enum Counter { SIZE, TOTAL_ENTRIES, MEMORY_USAGE,
                          QUERY_COUNT, HIT_COUNT, MISS_COUNT
    }

    private State resourceState;

    private HashMap<Counter,Long> statCounters = new HashMap<Counter,Long>();

    public StorageStats(SupervisedStorage storage) {
        className = storage.getClass().getName().toString();
        resourceState = State.INDETERMINATE;
    }

    // operations

    public void setState(State newState) {
        resourceState = newState;
    }

    public void addCounter(Counter counter) {
        setCounter(counter,0);
    }

    public void setCounter(Counter counter, long value) {
        statCounters.put(counter,value);
    }

    public void incrementCounter(Counter counter) {
        incrementCounter(counter,1);
    }

    public void incrementCounter(Counter counter, int increment) {
        if (statCounters.containsKey(counter)) {
            statCounters.put(counter,statCounters.get(counter)+increment);
        }
        else {
            addCounter(counter);
        }
    }

    // access methods

    public State getState() {
        return resourceState;
    }

    public long getCounter(Counter counter) throws StorageException {

        if (statCounters.containsKey(counter)) {
            return statCounters.get(counter);
        }
        else {
            throw new StorageException("invalid counter");
        }

    }


    public String getStats()  {
        StringBuilder sb = new StringBuilder();
        sb.append("\t<class>").append(className).append("</class>\r\n");
        sb.append("\t<state>").append(resourceState).append("</state>\r\n");
        for (Counter counter : statCounters.keySet()) {
            sb.append("\t<"+counter+">"+statCounters.get(counter)+"</"+counter+">\r\n");
        }
        return sb.toString();
    }

}
