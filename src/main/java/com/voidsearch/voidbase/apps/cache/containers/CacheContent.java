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

public abstract class CacheContent {
    public String text = null;
    public byte[] binary = null;

    public CacheContentType type = CacheContentType.TEXT;

    /**
     * Creates new instance of a CacheContent 
     */
    public CacheContent() {
        this.type = CacheContentType.TEXT;
    }

    /**
     * Creates new instance of a CacheContent with String value
     * @param value
     */
    public CacheContent(String value) {
        this.text = value;
        this.type = CacheContentType.TEXT;
    }

    /**
     * Creates new instance of a CacheContent with bytearray
     * @param value
     */
    public CacheContent(byte[] value) {
        this.binary = value;
        this.type = CacheContentType.BINARY;
    }

    /**
     * Returns String from CacheContent object
     * @return
     */
    public String toString() {
        StringBuilder str = new StringBuilder();

        str . append("[") . append(type) . append("]");

        if (type == CacheContentType.TEXT)
            str . append(text);
        else if (type == CacheContentType.BINARY)
            str . append(binary);
        else if (type == CacheContentType.BINARY)
            str . append("<UNKNOWN>");

        return str.toString();
    }
}
