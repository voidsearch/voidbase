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

package com.voidsearch.voidbase.storage;

import com.voidsearch.voidbase.supervision.SupervisionException;
import com.voidsearch.voidbase.supervision.StorageStats;

public interface SupervisedStorage {

    // disable storage operation
    public void blockOperation(StorageOperation op) throws SupervisionException;

    // enable storage operation
    public void enableOperation(StorageOperation op) throws SupervisionException;

    // update stats structure
    public void updateStats(StorageStats stats);

    // get total count of storage queries
    public long getTotalQueries();

    // get storage memory usage (in bytes)
    public long getMemorySize();


}
