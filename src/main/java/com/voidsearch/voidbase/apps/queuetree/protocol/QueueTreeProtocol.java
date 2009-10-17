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

import com.voidsearch.voidbase.protocol.VoidBaseProtocol;
import com.voidsearch.voidbase.protocol.VoidBaseOperationType;
import com.voidsearch.voidbase.protocol.VoidBaseHttpRequest;

import java.util.HashMap;
import java.util.List;

public class QueueTreeProtocol extends VoidBaseProtocol {

    // query parameters

    public static String VALUE      = "value";
    public static String QUEUE      = "queue";
    public static String SIZE       = "size";
    public static String TIME_START = "time_start";

    // metadata values
    public static String NAME           = "name";
    public static String TIMESTAMP      = "timestamp";
    public static String MIN            = "min";
    public static String MAX            = "max";
    public static String MEDIAN         = "median";
    public static String AVG            = "mean";
    public static String VAR            = "variance";
    public static String DEVIATION      = "deviation";
    public static String COVARIANCE     = "covariance";
    public static String CORRELATION    = "correlation";

    // param values
    public static String WILDCARD = "*";

    private static HashMap<VoidBaseOperationType,String[]> requiredParams = new HashMap<VoidBaseOperationType,String[]>();

    static {
        requiredParams.put(VoidBaseOperationType.PUT,     new String[] {QUEUE});
        requiredParams.put(VoidBaseOperationType.ADD,     new String[] {QUEUE,SIZE});
        requiredParams.put(VoidBaseOperationType.GET,     new String[] {QUEUE,SIZE});
        requiredParams.put(VoidBaseOperationType.RESIZE,  new String[] {QUEUE,SIZE});
        requiredParams.put(VoidBaseOperationType.DELETE,  new String[] {QUEUE});
        requiredParams.put(VoidBaseOperationType.FLUSH,   new String[] {QUEUE});
        requiredParams.put(VoidBaseOperationType.LIST,    new String[] {});
    }

    public static String[] getRequiredParams(VoidBaseOperationType operation) throws UnsupportedOperationException {
        if (requiredParams.containsKey(operation)) {
            return requiredParams.get(operation);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    // response messages

    public static String CREATED                = "QueueCreated";
    public static String DELETED                = "QueueDeleted";
    public static String RESIZED                = "QueueResized";
    public static String FLUSHED                = "QueueFlushed";
    public static String ENQUEUED               = "ValueEnqueued";
    public static String INVALID_REQUEST        = "InvalidRequest";
    public static String INVALID_QUEUE          = "InvalidQueue";
    public static String UNSUPPORTED_OPERATION  = "UnsupportedOperation";
    public static String QUEUE_ALREADY_EXISTS   = "QueueAlreadyExists";
    public static String STORAGE_SUPERVISED     = "StorageSupervisionException";

    public static String QUEUE_LIST             = "queueList";
    public static String QUEUE_ELEMENTS         = "queueElements";
    public static String QUEUE_STAT             = "queueStat";

    // response formatting

    public static String getResponse(VoidBaseHttpRequest req, String response) {
        StringBuilder sb = new StringBuilder();
        sb.append("<response>\r\n")
          .append(response)
          .append("</response>\r\n");
        return sb.toString();
    }

    public static String getResponse(VoidBaseHttpRequest req, List response) {
        return getResponse(req,deserializeList(response));
    }

    public static String deserializeList(List list) {
        StringBuilder sb = new StringBuilder();
        for (Object elem : list) {
            sb.append("<val>")
              .append(elem.toString())
              .append("</val>\r\n");
        }
        return sb.toString();
    }

}
