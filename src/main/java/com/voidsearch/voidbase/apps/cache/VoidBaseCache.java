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

package com.voidsearch.voidbase.apps.cache;

import com.voidsearch.voidbase.apps.cache.containers.CacheValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.voidsearch.voidbase.apps.cache.VoidBaseCacheHandler;
import com.voidsearch.voidbase.apps.cache.containers.RegisteredOperations;
import com.voidsearch.voidbase.apps.cache.containers.CacheLockType;
import com.voidsearch.voidbase.apps.cache.containers.CacheOperation;
import com.voidsearch.voidbase.config.VoidBaseConfig;
import com.voidsearch.voidbase.config.ConfigException;
import com.voidsearch.voidbase.util.GenericUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Abstract class as an prototype for all cache handhers, handles initialization of atomicity and cache handler
 * operation subscriptions based on configuration. 
 */
public abstract class VoidBaseCache implements VoidBaseCacheHandler, Cloneable {
    protected String name = null;
    protected VoidBaseConfig config = null;
    protected RegisteredOperations operations = new RegisteredOperations();
    protected static final Logger logger = LoggerFactory.getLogger(VoidBaseCache.class.getName());

    /**
     * Creates a new instance of VoidBaseCache
     * @throws CacheException
     */
    public VoidBaseCache() throws CacheException {
       try {
            config = VoidBaseConfig.getInstance();
        } catch (ConfigException e) {
            logger.error("Failed to get configuration instance.");
            GenericUtil.logException(e);
        }
    }

    /**
     * Not implemented for VoidBaseCache - needs to be implemented in subclasses
     * @return instance of a VoidBaseCache
     * @throws CacheException
     */
    public static VoidBaseCache getInstance() throws CacheException {
        throw new CacheException("Not Implemented");
    }

    /**
     * Initializes cache operations and their atomicity 
     * @throws CacheException
     */
    public void initialize() throws CacheException {
        Set<String> operationKeys = config.getKeys(name, "operations");

        // sanity check
        if (name == null) {
            throw new CacheException("Module name not known - please initialize 'name' in your cache");
        }

        // register operations
        for (String operation: operationKeys) {
            operations.put(operation, new CacheOperation(operation,
                                                         config.getAttribute(name, "operations." + operation, "lock")));
        }
    }

    /**
     * Clones a VoidBaseCache object
     * @return a clone of a VoidBaseCache object
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error("Failed to clone: " + this.getClass());
            throw new Error("Failed to clone: " + this.getClass());
        }
    }

    /**
     * Check if operation is registered
     * @param operation
     * @return <code>true</code> if operation is registered and <code>false</code> if it's not
     */
    public Boolean isRegistered(String operation) {
        if (operation == null)
            return false;

        return operations.isRegistered(operation);
    }

    /**
     * Returns lock type for operation
     * @param operation
     * @return lock type for operation
     */
    public CacheLockType getLockType(String operation) {
        if (operation == null)
            return CacheLockType.DEFAULT;

        return operations.getLockType(operation);
    }

    /**
     * One is not required to implement byte array implementation for key/value cache handler - if it's not provided
     * NotImplementedException will be thrown in case it is being used
     * @param method
     * @param route
     * @param params
     * @param key
     * @param value
     * @return
     * @throws CacheException
     */
    public CacheValue process(String method, List<String> route, Map<String, String> params, byte[] key, byte[] value) throws CacheException {
        throw new NotImplementedException();
    }

    /**
     * One is not required to implement cache handler for binary content - if it's not provided by cache implementeation
     * NotImplementedException will be thrown
     * @param method
     * @param route
     * @param params
     * @param content
     * @return
     * @throws CacheException
     */
    public CacheValue process(String method, List<String> route, Map<String, String> params, byte[] content) throws CacheException {
        throw new NotImplementedException();    
    }
}
