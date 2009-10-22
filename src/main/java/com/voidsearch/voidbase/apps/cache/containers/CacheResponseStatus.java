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
 * Cache response types, currently supported are:
 * - OK, healthy response status
 * - ERROR, failed to execute an operation
 * - INTERNAL_ERROR, cache had internally failed to execute an operation
 * - FATAL_ERROR, cache can't recover from internal error
 */
public enum CacheResponseStatus {
    OK,
    ERROR,
    INTERNAL_ERROR,
    FATAL_ERROR,
    UNKNOWN;

    /**
     * Serializes a content of a CacheResponseStatus
     * @return
     */
    public String serialize() {
        return toString();
    }

    /**
     * Initializes a CacheResponseStatus object
     * @param str
     * @return
     */
    public static CacheResponseStatus deserialize(String str) {
        if (str.toUpperCase().equals("OK")) {
            return OK;
        }
        if (str.toUpperCase().equals("ERROR")) {
            return ERROR;
        }
        if (str.toUpperCase().equals("INTERNAL_ERROR")) {
            return INTERNAL_ERROR;
        }
        if (str.toUpperCase().equals("FATAL_ERROR")) {
            return FATAL_ERROR;
        }

        return UNKNOWN;
    }
}
