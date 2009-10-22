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

/**
 * Placeholder object for Cache operations - each operation is assigned one CacheOperation object
 */
public class CacheOperation {
    public String name = null;
    public CacheLockType lockType = CacheLockType.NONE;

    /**
     * Creates a new instance of a CacheOperation
     */
    public CacheOperation() { }

    /**
     * Creates a new instance of a CacheOperation
     * @param name
     */
    public CacheOperation(String name) {
        if (name == null)
            return;

        this.name = name.toUpperCase();
    }

    /**
     * Creates a new instance of a CacheOperation
     * @param name
     * @param lockType
     */
    public CacheOperation(String name, String lockType) {
        if (name != null)
            this.name = name.toUpperCase();
        if (lockType != null)
            this.lockType = CacheLockType.deserialize(lockType);
    }

    /**
     * Creates a new instance of a CacheOperation
     * @param name
     * @param lockType
     */
    public CacheOperation(String name, CacheLockType lockType) {
        if (name != null)
            this.name = name.toUpperCase();
        if (lockType != null)
            this.lockType = lockType;
    }

    /**
     * Returns a String from CacheOperation object
     * @return
     */
    public String toString() {
        StringBuilder str = new StringBuilder();

        str . append("{ name = ") . append(name) . append(", lockType = ") . append(lockType) . append(" }");

        return str.toString();
    }
}
