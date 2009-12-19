package com.voidsearch.test.apps.feedq;

import org.testng.annotations.*;
import com.voidsearch.voidbase.storage.jobqueue.JobQueue;
import com.voidsearch.voidbase.storage.jobqueue.JobRequest;
import com.voidsearch.voidbase.storage.jobqueue.JobResult;
import com.voidsearch.voidbase.storage.jobqueue.SimpleJobResult;
import com.voidsearch.voidbase.supervision.SupervisionException;

/**
 * @author Aleksandar Bradic
 */
public class SimpleJobQueueFeedTaskTest {

    @Test
    public void nullTest() {
        System.out.println("SimpleJobQueueFeedTaskTest");

        JobQueue queue = new JobQueue();
        try {
            JobRequest req = new TwitterGeoFeedRequest();
            queue.put(req);
            System.out.println(queue.get(req));
        } catch (SupervisionException e) {
            e.printStackTrace();
        }

    }

    public class TwitterGeoFeedRequest extends JobRequest {
        public JobResult execute() {
            JobResult result = new SimpleJobResult();
            return result;
        }

        public JobResult expired() {
            return null;
        }
    }

}
