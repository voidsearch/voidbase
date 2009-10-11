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

package com.voidsearch.voidbase.storage.jobqueue;

public abstract class JobRequest implements Comparable {

    private static int idCnt = 0;
    private int ID = idCnt++;
    
    private int priority;
    private long timestamp = System.currentTimeMillis();
    private long timeout = 0;

    public boolean equals(JobRequest req) {
        return (req.getID() == this.ID) ? true : false;
    }

    public int compareTo(Object req) throws ClassCastException {
        if (!(req instanceof JobRequest)) {
            throw new ClassCastException();
        }
        JobRequest o1 = (JobRequest)req;
        return this.priority - o1.getPriority();
    }

    public int getID() {
        return this.ID;
    }
    
    public int getPriority() {
        return this.priority;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public boolean isExpired() {
        return ((System.currentTimeMillis() - timestamp) > timeout);
    }

    // invoked when request is scheduled for execution
    public abstract JobResult execute();

    // invoked when request is expired
    public abstract JobResult expired();

}
