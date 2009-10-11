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

import java.util.HashMap;
import java.lang.*;
import java.lang.UnsupportedOperationException;

public class VoidBaseProtocol {

    // protocol required params container
    protected static HashMap<VoidBaseOperationType,String[]> requiredParams = new HashMap<VoidBaseOperationType,String[]>();

    // common request methods
    public static String METHOD     = "method";
    public static String OUTPUT     = "output";

    // response types
    public static String MESSAGE    = "message";

    // protocol validation methods

    public static String[] getRequiredParams(VoidBaseOperationType operation) throws UnsupportedOperationException {
        if (requiredParams.containsKey(operation)) {
            return requiredParams.get(operation);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }


    public static boolean isValid(VoidBaseOperationType operation, VoidBaseHttpRequest request) throws UnsupportedOperationException {
        String[] requiredParams = getRequiredParams(operation);
        for (String param : requiredParams) {
            if (!request.containsParam(param)) {
                return false;
            }
        }
        return true;
    }

}
