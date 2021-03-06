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
 * Cache lock types, currently supported are - ATOMIC, GLOBAL and NONE locks where:
 * - ATOMIC level locks on a level of key, thus guarantees atomicity of an operation
 * - GLOBAL level locks on a level of operation blocking all other similar operations running concurrently
 * - NONE level doesn't lock concurrent similar operations in any respect 
 */
public enum CacheLockType {
    ATOMIC,
    GLOBAL,
    NONE,
    DEFAULT;

    /**
     * Serializes a content of a CacheLockType
     * @return serialized CacheLockType object
     */
    public String serialize() {
        return toString();
    }

    /**
     * Initializes a CacheLockType object
     * @param str
     * @return a new CacheLockType object from a String
     */
    public static CacheLockType deserialize(String str) {
        if (str.toUpperCase().equals("ATOMIC")) {
            return ATOMIC;
        }
        if (str.toUpperCase().equals("GLOBAL")) {
            return GLOBAL;
        }
        if (str.toUpperCase().equals("NONE")) {
            return NONE;
        }

        return DEFAULT;
    }
}
