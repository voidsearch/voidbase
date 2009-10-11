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

package com.voidsearch.voidbase.apps.cache.module;

import com.voidsearch.voidbase.apps.cache.VoidBaseCache;
import com.voidsearch.voidbase.apps.cache.CacheException;
import com.voidsearch.voidbase.apps.cache.containers.CacheValue;

import java.util.List;
import java.util.Map;

public class SimpleCache extends VoidBaseCache {

    public SimpleCache() throws CacheException {
        super();

        // initialize module name
        name = "SimpleCache";
    }

    public CacheValue process(String method,
                              List<String> route,
                              Map<String, String> params,
                              String key,
                              String value) throws CacheException
    {
        CacheValue result;

        return null;
    }
}
