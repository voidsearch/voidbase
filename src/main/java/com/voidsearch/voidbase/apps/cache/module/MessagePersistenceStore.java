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

package com.voidsearch.voidbase.apps.cache.module;

import com.voidsearch.voidbase.apps.cache.VoidBaseCache;
import com.voidsearch.voidbase.apps.cache.CacheException;
import com.voidsearch.voidbase.apps.cache.containers.CacheValue;
import com.voidsearch.voidbase.storage.StorageException;
import com.voidsearch.voidbase.storage.bdb.BDBStorage;
import com.voidsearch.voidbase.util.GenericUtil;
import com.voidsearch.voidbase.protocol.VoidBaseOperationType;

import java.util.List;
import java.util.Map;

public class MessagePersistenceStore extends VoidBaseCache {
    BDBStorage storage = BDBStorage.getInstance();
    
    protected static MessagePersistenceStore instance = null;
    protected static final String STORE = "messages";

    /**
     * Creates a new instance of a MessagePersistenceStore
     * @throws CacheException
     */
    public MessagePersistenceStore() throws CacheException {
        super();

        // initialize module name
        name = "MessagePersistenceStore";

        // initialize STORE
        initializeStore();
    }

    /**
     * Creates a new instance of a MessagePersistenceStore
     * @return
     * @throws CacheException
     */
    public static MessagePersistenceStore getInstance() throws CacheException {
        if (instance == null) {
            instance = new MessagePersistenceStore();
        }

        return instance;
    }

    /**
     * Initializes BDB store for message persistence
     * @throws CacheException
     */
    protected synchronized void initializeStore() throws CacheException {
        String store = config.getString(name, "storage");

        if (store == null) {
            logger.error("Failed to initialize storage - storage not set for MessagePersistenceStore");
            throw new CacheException("STORE not set for MessagePersistenceStore");
        }

        try {
            if (!storage.isOpened(store)) {
                logger.info("Opening storage: " + store);
                storage.open(STORE, store);
            }
        } catch (StorageException e) {
            logger.error("Failed to open storage: " + store);
            throw new CacheException("Failed to open storage: " + store);
        }
    }

    /**
     * Processes cache requests
     * @param method
     * @param route
     * @param params
     * @param key
     * @param value
     * @return
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

    /**
     * Puts a key/value pair in messages store
     * @param key
     * @param value
     * @throws CacheException
     */
    public void put(String key, String value) throws CacheException {

        // sanity check
        if (key == null)
            throw new CacheException("Key not set.");
        if (value == null)
            throw new CacheException("Value not set.");

        // put hotel
        try {
            storage.put(STORE, key, value);
        } catch (StorageException e) {
            logger.info("Couldn't put key: " + key + " in store " + STORE);
            GenericUtil.logException(e);

            throw new CacheException("Couldn't put key: " + key + " in store " + STORE);
        }
    }

    /**
     * Returns value from a messages store
     * @param key
     * @return
     * @throws CacheException
     */
    public CacheValue get(String key) throws CacheException {

        // sanity check
        if (key == null)
            throw new CacheException("Key not set.");

        // get hotel
        try {
            return new CacheValue(storage.get(STORE, key));
        } catch (StorageException e) {
            logger.info("Couldn't fetch key: " + key + " in store " + STORE);
            GenericUtil.logException(e);

            throw new CacheException("Couldn't fetch key: " + key + " in store " + STORE);
        }
    }

    /**
     * Deletes a key from a messages store
     * @param key
     * @throws CacheException
     */
    public void delete(String key) throws CacheException {

        // sanity check
        if (key == null)
            throw new CacheException("Key not set.");

        // delete hotel
        try {
            storage.delete(STORE, key);
        } catch (StorageException e) {
            logger.info("Couldn't delete key: " + key + " from store " + STORE);
            GenericUtil.logException(e);

            throw new CacheException("Couldn't delete key: " + key + " from store " + STORE);
        }
    }

    /**
     * Flushes a messages store
     * @throws CacheException
     */
    public void flush() throws CacheException {
        try {
            storage.flush(STORE);
        } catch (StorageException e) {
            logger.info("Failed to flush store: " + STORE);
            GenericUtil.logException(e);

            throw new CacheException("Failed to flush store: " + STORE);
        }
    }
}
