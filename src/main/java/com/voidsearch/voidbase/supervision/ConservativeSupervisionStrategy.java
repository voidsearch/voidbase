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
import com.voidsearch.voidbase.storage.StorageOperation;
import com.voidsearch.voidbase.storage.StorageException;

public class ConservativeSupervisionStrategy implements SupervisionStrategy {

    public void supervise(SupervisedStorage storage, StorageStats stats) throws SupervisionException {

        try {

            long memory = stats.getCounter(StorageStats.Counter.MEMORY_USAGE);

            if (memory < -100) {
                storage.blockOperation(StorageOperation.PUT);
            }
            else if (stats.getState() == StorageStats.State.MINOR) {
                storage.enableOperation(StorageOperation.PUT);
            }

        } catch (StorageException e) {
            throw new SupervisionException();
        }
    }

}
