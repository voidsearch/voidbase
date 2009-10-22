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
 * Cache value implementation of CacheContent
 */
public class CacheValue extends CacheContent {

    /**
     * Creates a new instance of a CacheContent
     */
    public CacheValue() { super(); }

    /**
     * Creates a new instance of a CacheConten
     * @param value
     */
    public CacheValue(String value) { super(value); }

    /**
     * Creates a new instance of a CacheConten
     * @param value
     */
    public CacheValue(byte[] value) { super(value); }

    /**
     * Appends a content to CacheValue - needs rewrite to support better content merging
     * @param text
     */
    public void append(String text) {
        if (this.text == null)
            this.text = text;
        else
            this.text += text;
    }

    /**
     * Appends a content to CacheValue - needs rewrite to support better content merging
     * @param value
     */
    public void append(CacheValue value) {
        if (this.text == null)
            this.text = value.text;
        else
            this.text += value.text;
    }
}
