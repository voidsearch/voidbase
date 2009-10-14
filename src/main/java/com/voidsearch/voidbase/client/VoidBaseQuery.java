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

package com.voidsearch.voidbase.client;

import java.util.LinkedHashMap;

public abstract class VoidBaseQuery {

    protected static LinkedHashMap<String,String> queryParams = new LinkedHashMap<String,String>();
    protected static String host;
    protected static String handler;

    public VoidBaseQuery() {
    }

    public VoidBaseQuery(String host, String handler) {
        this.host = host;
        this.handler = handler;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public void set(String param, String value) {
        queryParams.put(param, value);
    }

    public String getQuery() {
        StringBuilder query = new StringBuilder();
        query.append("http://")
             .append(host)
             .append(handler)
             .append("?");

        String amp = "";
        for (String key : queryParams.keySet()) {
            query.append(amp).append(key).append("=").append(queryParams.get(key));
            amp = "&";
        }

        return query.toString();
    }

}
