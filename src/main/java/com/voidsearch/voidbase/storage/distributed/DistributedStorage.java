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

package com.voidsearch.voidbase.storage.distributed;

import com.voidsearch.voidbase.storage.StorageException;
import com.voidsearch.voidbase.storage.bdb.BDBStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedStorage {
    protected static DistributedStorage storage = null;
    protected Map<String, DistributedStore> stores = new ConcurrentHashMap<String, DistributedStore>();
    protected static final Logger logger = LoggerFactory.getLogger(DistributedStorage.class.getName());

    protected DistributedStorage() {
        super();
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static synchronized DistributedStorage getInstance() {
        if (storage == null) {
            storage = new DistributedStorage();
        }

        return storage;
    }

    public synchronized void open(String name, String path) throws StorageException {
        if (name == null || stores.containsKey(name))
            return;

        stores.put(name, new DistributedStore(name, path));
    }

    public synchronized void close(String name) throws StorageException {
        if (name == null || !stores.containsKey(name))
            return;

        stores.get(name).close();
    }

    public Boolean isOpened(String name) {
        if (name == null)
            return false;

        return stores.containsKey(name);
    }


}
