/*
 * Copyright 2010 VoidSearch.com
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

package com.voidsearch.voidbase.storage.queuetree.persistence;

import com.voidsearch.voidbase.storage.queuetree.QueueEntry;
import com.voidsearch.voidbase.storage.queuetree.QueueMetadata;
import org.testng.annotations.Test;

public class FilesystemQueuePersistenceTest {

    @Test
    public void nullTest() {

        try {

            QueuePersistence persistenceEngine1 = QueuePersistenceFactory.getPersistence("testqueue1");
            QueuePersistence persistenceEngine2 = QueuePersistenceFactory.getPersistence("testqueue2");

            persistenceEngine1.add("testqueue1", new QueueEntry("1"), new QueueMetadata("testqueue1",100));
            persistenceEngine1.add("testqueue1", new QueueEntry("2"), new QueueMetadata("testqueue1",100));
            persistenceEngine2.add("testqueue2", new QueueEntry("1"), new QueueMetadata("testqueue2",100));
            persistenceEngine1.add("testqueue1", new QueueEntry("3"), new QueueMetadata("testqueue1",100));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
