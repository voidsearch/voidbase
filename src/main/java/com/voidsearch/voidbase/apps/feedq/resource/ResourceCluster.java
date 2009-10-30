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

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;

public class ResourceCluster {

    private String name;

    private HashMap<String,String> resourceKeys = new HashMap<String,String>();
    private LinkedHashMap<String, String> resources = new LinkedHashMap<String, String>();
    private LinkedHashMap<String,Integer> resourceStats = new LinkedHashMap<String, Integer>();

    public ResourceCluster(String name) {
        this.name = name;
    }
    
    public void add(String resourceName, String resource) {
        resourceKeys.put(resource,resourceName);
        resources.put(resource, null);
    }

    public Set<String> resources() {
        return resources.keySet();
    }

    public String get(String resource) {
        return resources.get(resource);
    }

    public String getName() {
        return this.name;
    }

    public void setStat(String resource, Integer stat) {
        resourceStats.put(resource, stat);
    }

    public String getQueueStatEntry() {
        StringBuilder sb = new StringBuilder();
        for (String key : resourceStats.keySet()) {
            sb.append("<").append(resourceKeys.get(key)).append(">")
              .append(resourceStats.get(key))
              .append("</").append(resourceKeys.get(key)).append(">");
        }
        return sb.toString();
    }



}
