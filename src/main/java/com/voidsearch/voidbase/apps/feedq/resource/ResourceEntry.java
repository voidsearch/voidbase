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

package com.voidsearch.voidbase.apps.feedq.resource;

import java.util.HashMap;

public class ResourceEntry {

    private HashMap<String, String> entryContent = new HashMap<String, String>();

    /**
     * add a tag value to resource content
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        entryContent.put(key,value);
    }

    /**
     * get given content tag
     * @param key
     * @return
     */
    public String get(String key) {
        return entryContent.get(key);
    }

    /**
     * render a simple representation of entry content
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : entryContent.keySet()) {
            sb.append(key).append("\t").append(entryContent.get(key)).append("\n");
        }
        return sb.toString();
    }

}