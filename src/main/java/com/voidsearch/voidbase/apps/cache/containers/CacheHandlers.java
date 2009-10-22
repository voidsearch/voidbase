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

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.voidsearch.voidbase.apps.cache.VoidBaseCache;

public class CacheHandlers {
    protected static CacheHandlers handlers = null;
    protected static final ConcurrentHashMap<String, VoidBaseCache> container = new ConcurrentHashMap<String, VoidBaseCache>();

    protected static final Logger logger = LoggerFactory.getLogger(CacheHandlers.class.getName());

    /**
     * Singleton object - concstructor is protected
     */
    protected CacheHandlers() { super(); }

    /**
     * Singleton object - cloning not allowed
     * @return
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static synchronized CacheHandlers getInstance() {
        if (handlers == null) {
            handlers = new CacheHandlers();
        }

        return handlers;
    }

    public void put(String name, VoidBaseCache cache) {
        if (name != null)
            handlers.put(name, cache);
    }

    public VoidBaseCache get(String name) {
        if (name == null)
            return null;

        return container.get(name);
    }
}
