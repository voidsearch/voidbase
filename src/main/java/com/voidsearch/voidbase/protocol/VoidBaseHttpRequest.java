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

package com.voidsearch.voidbase.protocol;

import com.voidsearch.voidbase.module.VoidBaseModuleRequest;

import java.util.HashMap;

public class VoidBaseHttpRequest {

    private static String URI_DELIMITER         = "\\?";
    private static String PARAM_DELIMITER       = "&";
    private static String KEYVALUE_DELIMITER    = "=";

    private HashMap<String, String> queryParams = new HashMap<String, String>();
    private String uri;
    private long startTime;

    public VoidBaseHttpRequest(VoidBaseModuleRequest request) throws InvalidRequestException {
        this(request.getURI());
    }
    
    public VoidBaseHttpRequest(String query) throws InvalidRequestException {
        startTime = System.currentTimeMillis();
        if (query != null) {
            parseQuery(query);
        }
        else {
            throw new InvalidRequestException();
        }
    }

    public void parseQuery(String query) throws InvalidRequestException {
        String[] uriParams = query.split(URI_DELIMITER);

        if (uriParams.length == 2) {
            uri = uriParams[0];
            String[] params = uriParams[1].split(PARAM_DELIMITER);

            for (String param : params) {
                String[] keyVal = param.split(KEYVALUE_DELIMITER);
                if (keyVal.length == 2) {
                    queryParams.put(keyVal[0], keyVal[1]);
                }
            }

        } else {
            throw new InvalidRequestException();
        }
    }

    public String getUri() {
        return uri;
    }

    public int paramCount() {
        return queryParams.size();
    }

    public boolean containsParams(String[] params) {
        for (String param : params) {
            if (!queryParams.containsKey(param)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsParam(String param) {
        return queryParams.containsKey(param);
    }

    public String getParam(String param) {
        return queryParams.get(param);
    }

    public long getStartTime() {
        return startTime;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("uri:\t"+uri+"\n");

        for (String param : queryParams.keySet()) {
            sb.append("\t" + param + ":" + queryParams.get(param) + "\n");
        }

        return sb.toString();
    }

}
