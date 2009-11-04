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

package com.voidsearch.voidbase.storage.distributed.router;

import com.voidsearch.voidbase.storage.distributed.DistributedStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedStoreRouter implements Router {
    protected static DistributedStoreRouter storage = null;
    protected static final Logger logger = LoggerFactory.getLogger(DistributedStoreRouter.class.getName());

    protected DistributedStoreRouter() {
        super();
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static synchronized DistributedStoreRouter getInstance() {
        if (storage == null) {
            storage = new DistributedStoreRouter();
        }

        return storage;
    }

    
}
