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

package com.voidsearch.voidbase.module;

import java.util.*;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.buffer.ChannelBuffer;

public class VoidBaseModuleRequest {

    private Map<String, String> params;
    private String requestURI;
    private long timestamp;
    private HttpMethod method;
    private ChannelBuffer content;

    public VoidBaseModuleRequest(Map<String, String> params) {
        this.method = null;
        this.params = params;
        this.requestURI = null;
        this.timestamp = System.currentTimeMillis();
        this.content = null;
    }

    public VoidBaseModuleRequest(HttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
        
        this.method = request.getMethod();
        this.params = normalizeParams(queryStringDecoder.getParameters());
        this.requestURI = request.getUri();
        this.timestamp = System.currentTimeMillis();
        this.content = null;
    }

    public VoidBaseModuleRequest(HttpRequest request, ChannelBuffer content) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());

        this.method = request.getMethod();
        this.params = normalizeParams(queryStringDecoder.getParameters());
        this.requestURI = request.getUri();
        this.timestamp = System.currentTimeMillis();
        this.content = content;
    }

    public VoidBaseModuleRequest(String requestURI) {
        this.requestURI = requestURI;
        this.timestamp = System.currentTimeMillis();
    }

    public String getResource() {
        List<String> route = getRoute(requestURI);
        if (route.size() > 0) {
            return route.get(0);
        } else {
            return requestURI;
        }
    }

    public String getRequest() {
        String resource = getResource();

        if (requestURI == null)
            return null;

        return (requestURI.substring(requestURI.indexOf(resource) + resource.length()));
    }

    public List<String> getRoute() {
        return getRoute(requestURI);
    }

    public List<String> getRoute(String uri) {
        boolean start = false;
        StringBuilder routeString = new StringBuilder();

        if (uri == null)
            return new ArrayList<String>();

        for (char c: uri.toCharArray()) {
            if (c == '/' && start == false)
                start = true;
            else if (c == '?' && start == true)
                break;
            else
                routeString.append(c);
        }

        return new ArrayList<String>(Arrays.asList(routeString.toString().split("/")));
    }

    public String getURI() {
        return requestURI;
    }


    public String toString() {
        return requestURI;
    }

    public long getStartTime() {
        return timestamp;
    }

    public String getParam(String param) {
        if (params.containsKey(param)) {
            return params.get(param);
        }
        return null;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getContent() {
        if (content == null)
            return null;

        return content.toString("UTF-8");
    }

    protected Map<String, String> normalizeParams(Map<String, List<String>> params) {
        HashMap<String, String> normalizedParams = new HashMap<String, String>();

        if (params != null) {
            for(Map.Entry<String, List<String>> entry: params.entrySet()) {
                normalizedParams.put(entry.getKey(), entry.getValue().get(0));
            }
        }

        return normalizedParams;
    }
    
    // get handler

    // get params nice & sweet

    // match the "protocol spec"
    

}
