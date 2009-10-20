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

package com.voidsearch.test.apps.queuetree.performance;

import org.testng.annotations.*;
import com.voidsearch.voidbase.storage.queuetree.QueueTreeStorage;
import com.voidsearch.voidbase.apps.queuetree.client.QueueTreeClient;

public class SimpleQueueTreePerformanceTest {

    int BLOCK_SIZE = 4096;
    int MAX_SIZE = 256 * BLOCK_SIZE;

    int QUEUE_SIZE = 1000;
    int MAX_ENTRIES = 100 * QUEUE_SIZE;

    int NUM_QUEUES = 1000;

    String TEST_QUEUE = "test";

    @Test(enabled = true)
    public void storageTest() {

        try {
            QueueTreeStorage queueStore = QueueTreeStorage.factory();

            System.out.println("num_entries,data_size,insert_elapsed");
            for (int dataSize = BLOCK_SIZE; dataSize < MAX_SIZE; dataSize += BLOCK_SIZE) {
                for (int numEntries = QUEUE_SIZE; numEntries < MAX_ENTRIES; numEntries += QUEUE_SIZE) {
                    if (queueStore.queueExists(TEST_QUEUE)) {
                        queueStore.deleteQueue(TEST_QUEUE);
                    }
                    queueStore.createQueue(TEST_QUEUE,QUEUE_SIZE);
                    long elapsed = getPutEnqueueTime(queueStore, TEST_QUEUE, numEntries, dataSize);
                    System.out.println(numEntries + "," + dataSize + "," + elapsed);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test(enabled = false)
    public void serviceTest() {
        QueueTreeClient client = new QueueTreeClient("localhost:8080");

        try {
            System.out.println("num_entries,data_size,insert_elapsed");
            for (int dataSize = BLOCK_SIZE; dataSize < MAX_SIZE; dataSize += BLOCK_SIZE) {
                for (int numEntries = QUEUE_SIZE; numEntries < MAX_ENTRIES; numEntries += QUEUE_SIZE) {
                    client.create("test",QUEUE_SIZE);
                    long elapsed = getClientPutEnqueueTime(client, TEST_QUEUE, numEntries, dataSize);
                    System.out.println(numEntries + "," + dataSize + "," + elapsed);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // utils

    public long getPutEnqueueTime(QueueTreeStorage queueStore, String queueName, int NUM_ENTRIES, int DATA_SIZE) throws Exception {

        String content = getRandomQueueEntry(DATA_SIZE);

        long start = System.currentTimeMillis();
        for (int i = 0; i < NUM_ENTRIES; i++) {
            queueStore.putFIFO(queueName,content);
        }
        return System.currentTimeMillis() - start;
    }

    public long getClientPutEnqueueTime(QueueTreeClient client, String queueName, int NUM_ENTRIES, int DATA_SIZE) throws Exception {

        String content = getRandomQueueEntry(DATA_SIZE);

        long start = System.currentTimeMillis();
        for (int i = 0; i < NUM_ENTRIES; i++) {
            client.put(queueName,content);
        }
        return System.currentTimeMillis() - start;
    }
    

    public static String getRandomQueueEntry(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<size; i++) {
            sb.append("t");
        }
        return sb.toString();
    }


}
