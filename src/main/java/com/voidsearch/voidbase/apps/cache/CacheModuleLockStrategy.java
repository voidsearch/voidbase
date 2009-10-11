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
import com.voidsearch.voidbase.apps.cache.containers.CacheLockType;
import com.voidsearch.voidbase.util.GenericUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheModuleLockStrategy {
    private static final Map<String, ReentrantLock> locks = new ConcurrentHashMap<String, ReentrantLock>();

    protected static final Logger logger = LoggerFactory.getLogger(CacheModuleLockStrategy.class.getName());

    public CacheModuleLockStrategy() { }

    protected CacheValue execute(VoidBaseCache handler,
                                 String operation,
                                 List<String> route,
                                 Map<String, String> params,
                                 String key, String value) throws CacheException
    {
        CacheLockType lockType = handler.getLockType(operation);

        try {
            if (lockType == CacheLockType.ATOMIC)
                return executeAtomicLockOperation(handler, operation, route, params, key, value);
            if (lockType == CacheLockType.GLOBAL)
                return executeGlobalLockOperation(handler, operation, route, params, key, value);
            if (lockType == CacheLockType.NONE || lockType == CacheLockType.DEFAULT)
                return executeOperation(handler, operation, route, params, key, value);
        } catch (InterruptedException e) {
            logger.error("Operation interrupted: " + operation);
            GenericUtil.logException(e);

            throw new CacheException("Operation interrupted: " + operation);
        }

        throw new CacheException("Cache execute lock strategy failed - unknown lock type");
    }


    protected synchronized CacheValue executeAtomicLockOperation(VoidBaseCache handler,
                                                    String operation,
                                                    List<String> route,
                                                    Map<String, String> params,
                                                    String key, String value) throws CacheException, InterruptedException
    {
        CacheValue result;
        Long started = System.currentTimeMillis() / 1000;

        // create lock
        synchronized(locks) {
            if (!locks.containsKey(key)) {
                locks.put(key, new ReentrantLock());
            }
        }

        // execute operation
        locks.get(key).lock();
        try {
            result = handler.process(operation, route, params, key, value);
        } finally {
            synchronized(locks) {
                if (locks.containsKey(key)) {
                    locks.get(key).unlock();
                    locks.remove(key);
                }
            }
        }

        return result;
    }

    protected CacheValue executeGlobalLockOperation(VoidBaseCache handler,
                                                    String operation,
                                                    List<String> route,
                                                    Map<String, String> params,
                                                    String key, String value) throws CacheException
    {
        CacheValue result;

        synchronized(handler) {
            result = handler.process(operation, route, params, key, value);
        }

        return result;
    }

    protected CacheValue executeOperation(VoidBaseCache handler,
                                          String operation,
                                          List<String> route,
                                          Map<String, String> params,
                                          String key, String value) throws CacheException
    {
        return handler.process(operation, route, params, key, value);
    }
}
