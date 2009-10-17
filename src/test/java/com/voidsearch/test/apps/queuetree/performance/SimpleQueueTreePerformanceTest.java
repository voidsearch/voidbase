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

public class SimpleQueueTreePerformanceTest {

    int BLOCK_SIZE = 4096;
    int MAX_SIZE = 256 * BLOCK_SIZE;

    int QUEUE_SIZE = 1000;
    int MAX_ENTRIES = 100 * QUEUE_SIZE;

    int NUM_QUEUES = 1000;

    String TEST_QUEUE = "test";

    @Test
    public void nullTest() {

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

    // utils

    public long getPutEnqueueTime(QueueTreeStorage queueStore, String queueName, int NUM_ENTRIES, int DATA_SIZE) throws Exception {

        String content = getRandomQueueEntry(DATA_SIZE);

        long start = System.currentTimeMillis();
        for (int i = 0; i < NUM_ENTRIES; i++) {
            queueStore.putFIFO(queueName,content);
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
