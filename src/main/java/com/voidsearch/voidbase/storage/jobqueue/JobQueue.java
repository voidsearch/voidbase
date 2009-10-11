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

import com.voidsearch.voidbase.storage.AsynchronousStorage;
import com.voidsearch.voidbase.storage.SupervisedStorage;
import com.voidsearch.voidbase.storage.StorageOperation;
import com.voidsearch.voidbase.supervision.SupervisionException;
import com.voidsearch.voidbase.supervision.StorageStats;
import com.voidsearch.voidbase.config.VoidBaseConfiguration;
import com.voidsearch.voidbase.config.Config;

import java.util.concurrent.*;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobQueue implements AsynchronousStorage, SupervisedStorage, Runnable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private int POLL_INTERVAL = 1000;       
    private long QUEUE_WAIT_INTERVAL = 1000;
    private Long queryCounter = new Long(0);

    // job queue
    private PriorityBlockingQueue<JobRequest> requestQueue = new PriorityBlockingQueue<JobRequest>();
    private ConcurrentHashMap<JobRequest,CountDownLatch> pendingJobs = new ConcurrentHashMap<JobRequest, CountDownLatch>();
    private HashSet<JobRequest> canceledJobs = new HashSet<JobRequest>();
    private ConcurrentHashMap<JobRequest,JobResult> doneJobs = new ConcurrentHashMap<JobRequest,JobResult>();

    // oplocks
    private HashSet<StorageOperation> opLocks = new HashSet<StorageOperation>();

    public JobQueue() {
        if (VoidBaseConfiguration.contains(Config.STORAGE, "JobQueue", "cleanupInterval")) {
            try {
                POLL_INTERVAL = Integer.parseInt(VoidBaseConfiguration.get(Config.STORAGE, "JobQueue", "cleanupInterval"));
            } catch (Exception e) {
            }
        }
        // spawn threads
        (new Thread(this)).start();
        (new RequestExecutor(this)).start();
    }

    public void registerDequeuer() {
    }

    // storage operations

    public void put(JobRequest req) throws SupervisionException {

        queryCounter++;

        if (opLocks.contains(StorageOperation.PUT))
            throw new SupervisionException();

        CountDownLatch pending = new CountDownLatch(1);

        pendingJobs.put(req,pending);
        requestQueue.put(req);
    }

    public JobResult get(JobRequest req) throws SupervisionException {

        queryCounter++;
        
        if (opLocks.contains(StorageOperation.GET))
            throw new SupervisionException();

        JobResult result = null;

        if (pendingJobs.containsKey(req)) {
            try {
                (pendingJobs.get(req)).await();
                if (doneJobs.containsKey(req)) {
                    result = doneJobs.get(req);
                    doneJobs.remove(req);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pendingJobs.remove(req);
        }

        return result;
    }

    public JobRequest poll() throws InterruptedException {
        return requestQueue.poll(QUEUE_WAIT_INTERVAL,TimeUnit.MILLISECONDS);
    }

    public void addDone(JobRequest request, JobResult result) {
        doneJobs.put(request,result);
    }

    // supervision support

    public void blockOperation(StorageOperation op) throws SupervisionException {
        opLocks.add(op);
    }

    public void enableOperation(StorageOperation op) throws SupervisionException {
        if (opLocks.contains(op)) {
            opLocks.remove(op);
        }
    }

    public void updateStats(StorageStats stats) {
        stats.setCounter(StorageStats.Counter.TOTAL_ENTRIES,requestQueue.size());
        stats.setCounter(StorageStats.Counter.QUERY_COUNT,getTotalQueries());
    }

    public long getTotalQueries() {
        return queryCounter;
    }

    public long getMemorySize() {
        return requestQueue.size();
    }

    //
    // request expiration thread
    //
    public void run() {

        while(true) {
            for (JobRequest req : requestQueue) {
                if (!canceledJobs.contains(req) && req.isExpired()) {
                    canceledJobs.add(req);
                    logger.info("request : " + req + " expired");
                    if (pendingJobs.containsKey(req)) {
                        (pendingJobs.get(req)).countDown();
                    }
                }
            }
            try {
                Thread.sleep(POLL_INTERVAL);
            } catch (Exception e) { }
        }
    }


    public class RequestExecutor extends Thread {

        JobQueue queue;

        public RequestExecutor(JobQueue queue) {
            this.queue = queue;
        }

        public void run() {
            while (true) {
                try {
                    JobRequest request = queue.poll();
                    if (request != null) {
                        if (!canceledJobs.contains(request)) {
                            JobResult result = request.execute();
                            if (pendingJobs.containsKey(request)) {
                                (pendingJobs.get(request)).countDown();
                                queue.addDone(request, result);
                            }
                        }
                    } else {
                        // poll timeouted
                        // handle timeout ? - or just retry ?
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}

