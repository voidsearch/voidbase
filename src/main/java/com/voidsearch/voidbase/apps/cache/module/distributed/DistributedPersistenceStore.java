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

package com.voidsearch.voidbase.apps.cache.module.distributed;

import com.voidsearch.voidbase.apps.cache.CacheException;
import com.voidsearch.voidbase.apps.cache.VoidBaseCache;
import com.voidsearch.voidbase.apps.cache.containers.CacheValue;
import com.voidsearch.voidbase.protocol.VoidBaseOperationType;
import com.voidsearch.voidbase.storage.StorageException;
import com.voidsearch.voidbase.storage.bdb.BDBStorage;
import com.voidsearch.voidbase.storage.distributed.DistributedStorage;

import java.util.List;
import java.util.Map;

public class DistributedPersistenceStore extends VoidBaseCache {
    DistributedStorage storage = DistributedStorage.getInstance();

    protected static DistributedPersistenceStore instance = null;

    /**
     * Creates a new instance of a DistributedPersistenceStore
     * @throws com.voidsearch.voidbase.apps.cache.CacheException
     */
    public DistributedPersistenceStore() throws CacheException {
        super();

        // initialize module name
        name = "DistributedPersistenceStore";
    }

    /**
     * Creates a new instance of a DistributedPersistenceStore
     * @return a DistributedPersistenceStore instance
     * @throws CacheException
     */

    public static DistributedPersistenceStore getInstance() throws CacheException {
        if (instance == null) {
            instance = new DistributedPersistenceStore();
        }

        return instance;

        // initialize distributed store
    }

    /**
     * Processes cache requests
     * @param method
     * @param route
     * @param params
     * @param key
     * @param value
     * @return a result of cache request operation
     * @throws CacheException
     */
    public CacheValue process(String method,
                              List<String> route,
                              Map<String, String> params,
                              String key,
                              String value) throws CacheException
    {
        CacheValue result;
        VoidBaseOperationType type = VoidBaseOperationType.deserialize(method);

        if (type == VoidBaseOperationType.PUT) {
            put(key, value);
            result = new CacheValue("OK");
        }
        else if (type == VoidBaseOperationType.GET) {
            result = get(key);
        }
        else if (type == VoidBaseOperationType.DELETE) {
            delete(key);
            result = new CacheValue("OK");
        }
        else if (type == VoidBaseOperationType.FLUSH) {
            flush();
            result = new CacheValue("OK");
        }
        else {
            throw new CacheException("Method not implemented: " + method);
        }

        return result;
    }

    protected void put(String key, String value) throws CacheException {
        
    }

    protected CacheValue get(String key) throws CacheException {
        return null;
    }

    protected void delete(String key) throws CacheException {

    }

    protected void flush() throws CacheException {

    }
}
