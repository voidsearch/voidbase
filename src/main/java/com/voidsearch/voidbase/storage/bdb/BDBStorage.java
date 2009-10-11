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

package com.voidsearch.voidbase.storage.bdb;

import com.voidsearch.voidbase.storage.KeyValueStorage;
import com.voidsearch.voidbase.storage.StorageException;
import com.voidsearch.voidbase.util.GenericUtil;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.io.File;

public class BDBStorage implements KeyValueStorage {
    protected static BDBStorage storage = null;
    protected Map<String, BDBStore> stores = new ConcurrentHashMap<String, BDBStore>();
    protected static final Logger logger = LoggerFactory.getLogger(BDBStorage.class.getName());

    protected BDBStorage() {
        super();
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static synchronized BDBStorage getInstance() {
        if (storage == null) {
            storage = new BDBStorage();
        }

        return storage;
    }

    public synchronized void open(String name, String path) throws StorageException {
        if (name == null || stores.containsKey(name))
            return;

        stores.put(name, new BDBStore(name, path));
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

    public void flush(String name) throws StorageException {
        if (!stores.containsKey(name))
            throw new StorageException("There is no store with name: " + name);

        stores.get(name).flush();
    }

    public void put(String name, String key, String value) throws StorageException {
        if (!stores.containsKey(name))
            throw new StorageException("There is no store with name: " + name);
        if (key == null) 
            throw new StorageException("Key not set.");
        if (value == null)
            throw new StorageException("Value not set.");

        stores.get(name).put(key, value);
    }

    public String get(String name, String key) throws StorageException {
        if (!stores.containsKey(name))
            throw new StorageException("There is no store with name: " + name);
        if (key == null)
            throw new StorageException("Key not set.");

        return stores.get(name).get(key);
    }

    public void delete(String name, String key) throws StorageException {
        if (!stores.containsKey(name))
            throw new StorageException("There is no store with name: " + name);
        if (key == null)
            throw new StorageException("Key not set.");

        stores.get(name).delete(key);
    }

    public void put(String name, byte[] key, byte[] val) throws StorageException {

    }

    public byte[] get(String name, byte[] key) throws StorageException {
        return null;
    }

    public void delete(String name, byte[] key) throws StorageException {
    
    }
}
