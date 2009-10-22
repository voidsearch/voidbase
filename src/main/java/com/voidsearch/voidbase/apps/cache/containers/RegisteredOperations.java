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

package com.voidsearch.voidbase.apps.cache.containers;

import java.util.HashMap;

/**
 * Container of all registered operations and their behavior and locking for certain Cache handler
 */
public class RegisteredOperations {
    protected HashMap<String, CacheOperation> registeredOperations = new HashMap<String, CacheOperation>();

    /**
     * Creates a new instance of a RegisteredOperations
     */
    public RegisteredOperations() { }

    /**
     * Verifies if operation is registered 
     * @param operation
     * @return
     */
    public Boolean isRegistered(String operation) {
        if (operation == null)
            return false;

        return registeredOperations.containsKey(operation.toUpperCase());
    }

    /**
     * Verifies if operation is registered 
     * @param operation
     * @return
     */
    public Boolean isRegistered(CacheOperation operation) {
        if (operation == null)
            return false;

        return registeredOperations.containsKey(operation.name.toUpperCase());
    }

    /**
     * Gets CacheOperation from registeredOperations container
     * @param key
     * @return
     */
    public CacheOperation get(String key) {
        if (key == null)
            return null;

        return registeredOperations.get(key.toUpperCase());
    }

    /**
     * Puts CacheOperation in registeredOperations container
     * @param key
     * @param operation
     */
    public void put(String key, CacheOperation operation) {
        if (key == null)
            return;

        registeredOperations.put(key.toUpperCase(), operation);
    }

    /**
     * Verifies if operation is registered
     * @param key
     * @return
     */
    public Boolean contains(String key) {
        if (key == null)
            return false;

        return registeredOperations.containsKey(key);
    }

    /**
     * Gets a lock type for operation
     * @param operation
     * @return
     */
    public CacheLockType getLockType(String operation) {
        if (operation == null)
            return CacheLockType.DEFAULT;

        return registeredOperations.get(operation.toUpperCase()).lockType;
    }

    /**
     * Returns a string from RegisteredOperations object
     * @return
     */
    public String toString() {
        return registeredOperations.toString();
    }
}
