package com.voidsearch.test.storage.jobqueue;

import org.testng.annotations.*;
import com.voidsearch.voidbase.storage.jobqueue.JobQueue;
import com.voidsearch.voidbase.storage.jobqueue.JobRequest;
import com.voidsearch.voidbase.storage.jobqueue.JobResult;
import com.voidsearch.voidbase.storage.jobqueue.SimpleJobResult;
import com.voidsearch.voidbase.supervision.SupervisionException;

import java.util.LinkedList;
import java.util.Random;

/**
 * set of prority queue tests
 *
 * @author Aleksandar Bradic
 */

public class JobPriorityQueueTest {

    @Test
    public void nullTest() {

        int TOTAL_JOBS = 100;
        long TIMEOUT = 1000;

        System.out.println("simple JobQueue test");

        JobQueue queue = new JobQueue();

        long startTime = System.currentTimeMillis();

        LinkedList<JobRequest> requests = new LinkedList<JobRequest>();

        for (int i=0; i<TOTAL_JOBS; i++) {

            TestRequest req = new TestRequest();
            req.setTimeout(TIMEOUT);
            requests.add(req);

            try {
                queue.put(req);
            } catch (SupervisionException e) {
                e.printStackTrace();
            }

        }

        for (JobRequest req : requests) {
            try {
                JobResult result = queue.get(req);
                System.out.println(req.getID() + "\t" + result);
            } catch (SupervisionException e) {
                e.printStackTrace();
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("processed total of : " + requests.size() + " jobs");
        System.out.println("total time elapsed : " + elapsedTime + " milliseconds");

        assert (elapsedTime <= TOTAL_JOBS*TIMEOUT);

    }

    public class TestRequest extends JobRequest {

        private Random rnd = new Random();

        // delete this after fixing it
        // added just to fix broken build
        public JobResult expired(){
            JobResult deleteMe=null;
            return deleteMe;
        }
        
        public JobResult execute() {
            try {
                Thread.sleep(rnd.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new SimpleJobResult("DONE");
        }

    }

}
