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

package com.voidsearch.voidbase.storage.example;

import com.voidsearch.voidbase.storage.SupervisedStorage;
import com.voidsearch.voidbase.storage.StorageOperation;
import com.voidsearch.voidbase.supervision.SupervisionException;
import com.voidsearch.voidbase.supervision.StorageStats;

public class ExampleVoidBaseStorage implements SupervisedStorage {


    public void blockOperation(StorageOperation op) throws SupervisionException {
    }

    // enable storage operation
    public void enableOperation(StorageOperation op) throws SupervisionException {
    }

    // update stats structure
    public void updateStats(StorageStats stats) {
    }

    // get total count of storage queries
    public long getTotalQueries() {
        return 0;
    }

    // get storage memory usage (in bytes)
    public long getMemorySize() {
        return 0;
    }



}
