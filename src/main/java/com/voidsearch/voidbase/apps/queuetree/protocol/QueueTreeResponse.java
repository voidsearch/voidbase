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

package com.voidsearch.voidbase.apps.queuetree.protocol;

import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.protocol.VoidBaseHttpRequest;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class QueueTreeResponse {

    VoidBaseHttpRequest req;
    Object response;
    LinkedHashMap<String,Object> metadata;

    String responseType;
    int numResults;

    public QueueTreeResponse() {
        responseType = QueueTreeProtocol.MESSAGE;
    }

    public void setRequest(VoidBaseHttpRequest req) {
        this.req = req;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public int getNumResults() {
        return numResults;
    }

    public long getQueryTime() {
        return (System.currentTimeMillis() - req.getStartTime());
    }

    public void setType(String responseType) {
        this.responseType = responseType;
    }

    public String getType() {
        return responseType;
    }


    public void setResponse(Object response) {
        this.response = response;
    }

    public void setMetadata(LinkedHashMap<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getResponse() {

        StringBuilder sb = new StringBuilder();

        if (metadata != null) {
            sb.append("<queueMetadata>\r\n");
            for (String key : metadata.keySet()) {
                sb.append("<").append(key).append(">")
                        .append(metadata.get(key))
                        .append("</").append(key).append(">\n");
            }
            sb.append("</queueMetadata>\r\n");
        }

        sb.append("<").append(responseType).append(">");

        if (!responseType.equals(QueueTreeProtocol.MESSAGE))
            sb.append("\r\n");

        if (response instanceof String) {
            sb.append((String)response);
        }
        else if (response instanceof LinkedList) {
            sb.append(QueueTreeFeedUtils.deserializeList((List)response));
        }
        else if (response instanceof List) {
            sb.append(QueueTreeFeedUtils.deserializeList((List)response));
        }

        sb.append("</").append(responseType).append(">\r\n");

        return sb.toString();
    }

}
