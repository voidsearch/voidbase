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
 * Cache content type - currently supported are TEXT and BINARY types
 */
public enum CacheContentType {
    TEXT,
    BINARY,
    UNKNOWN;

    /**
     * Serializes a content of a CacheContentType
     * @return serialized CacheContentType object
     */
    public String serialize() {
        return toString();
    }

    /**
     * Initializes a CacheContentType object
     * @param str
     * @return a new CacheContentType from a String value
     */
    public static CacheContentType deserialize(String str) {
        if (str.toUpperCase().equals("TEXT")) {
            return TEXT;
        }
        if (str.toUpperCase().equals("BINARY")) {
            return BINARY;
        }

        return UNKNOWN;
    }
}
