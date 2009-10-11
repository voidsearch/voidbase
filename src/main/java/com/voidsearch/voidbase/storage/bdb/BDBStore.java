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

import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.DatabaseException;
import com.voidsearch.voidbase.storage.StorageException;
import com.voidsearch.voidbase.util.GenericUtil;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BDBStore {
    protected Environment env = null;
    protected EntityStore store = null;
    protected StoreConfig storeConfig = new StoreConfig();
    protected EnvironmentConfig envConfig = new EnvironmentConfig();

    protected PrimaryIndex<String, BDBRecord> pIdx;

    protected static final Logger logger = LoggerFactory.getLogger(BDBStorage.class.getName());

    public BDBStore(String name, String path) throws StorageException {
        Environment env;

        envConfig.setAllowCreate(true);
        envConfig.setSharedCache(true);
        storeConfig.setAllowCreate(true);

        try {
            env = new Environment(new File(path), envConfig);
            store = new EntityStore(env, name, storeConfig);
            pIdx = store.getPrimaryIndex(String.class, BDBRecord.class);
        } catch (DatabaseException e) {
            logger.error("Failed to open database " + name + " with path: " + path);
            GenericUtil.logException(e);

            throw new StorageException("Failed to open database " + name + " with path: " + path);
        }
    }

    public void close() throws StorageException {
        try {
            store.close();
            env.close();
        } catch (DatabaseException e) {
            logger.error("Failed to close database: " + e.getMessage());
            GenericUtil.logException(e);

            throw new StorageException("Failed to close database");
        } finally {
            store = null;
            env = null;
        }

    }

    public void put (String key, String value) throws StorageException {
        try {
            pIdx.put(new BDBRecord(key, value));
         } catch (DatabaseException e) {
            logger.error("Failed to put " + key + " to database: " + e.getMessage());
            GenericUtil.logException(e);

            throw new StorageException("Failed to put " + key + " to database: " + e.getMessage());
        }
    }

    public String get(String key) throws StorageException {
        BDBRecord record;

        try {
            record = pIdx.get(key);
        } catch (DatabaseException e) {
            logger.error("Failed to get " + key + " from database: " + e.getMessage());
            GenericUtil.logException(e);

            throw new StorageException("Failed to get " + key + " from database: " + e.getMessage());
        }

        return record != null ? record.value : null;
    }

    public void delete(String key) throws StorageException {
        try {
            pIdx.delete(key);    
        } catch (DatabaseException e) {
            logger.error("Failed to delete " + key + " from database: " + e.getMessage());
            GenericUtil.logException(e);

            throw new StorageException("Failed to close database");
        }
    }

    public synchronized void flush() throws StorageException {
        try {
            for (BDBRecord record: pIdx.entities()) {
                pIdx.delete(record.key);
            }
        } catch (DatabaseException e) {
            logger.error("Failed to flush database: " + e.getMessage());
            GenericUtil.logException(e);

            throw new StorageException("Failed to flush database: " + e.getMessage());
        }

    }
}
