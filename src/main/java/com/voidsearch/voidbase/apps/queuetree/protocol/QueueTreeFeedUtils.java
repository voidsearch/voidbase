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
import com.voidsearch.voidbase.storage.queuetree.QueueEntry;
import com.voidsearch.voidbase.storage.queuetree.QueueTreeStorage;

import java.util.List;

public class QueueTreeFeedUtils {


    public static String getRequestHeader(VoidBaseHttpRequest req) {

        StringBuilder sb = new StringBuilder();

        String method = req.getParam(QueueTreeProtocol.METHOD);
        sb.append("<request>\r\n");
        sb.append("<method>")
            .append(method)
            .append("</method>\r\n");
        sb.append("</request>\r\n");

        return sb.toString();
    }

    public static String getResultsHeader(QueueTreeResponse response) {

        StringBuilder sb = new StringBuilder();

        sb.append("<results>\r\n");
        sb.append("<totalResults>").append(response.getNumResults()).append("</totalResults>\r\n");
        sb.append("<queryTime>").append(response.getQueryTime()).append("</queryTime>\r\n");
        sb.append("</results>\r\n");
        return sb.toString();

    }


    public static String deserializeList(List list) {
        StringBuilder sb = new StringBuilder();
        for (Object elem : list) {
            if (elem instanceof QueueEntry) {
                sb.append("<entry timestamp=\"")
                  .append(((QueueEntry)elem).getTimestamp())
                  .append("\">")
                  .append("<val>");
            } else {
                sb.append("<entry><val>");
            }
            sb.append(elem.toString())
              .append("</val></entry>\r\n");
        }
        return sb.toString();
    }

}
