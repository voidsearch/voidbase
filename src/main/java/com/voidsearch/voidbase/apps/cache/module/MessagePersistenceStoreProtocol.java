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

import com.voidsearch.voidbase.protocol.VoidBaseProtocol;
import com.voidsearch.voidbase.protocol.VoidBaseOperationType;

public class MessagePersistenceStoreProtocol extends VoidBaseProtocol {
    public MessagePersistenceStoreProtocol() { }

    // module-specific protocol params
    public static final String KEY = "key";
    public static final String CONTENT = "content";
    public static final String METHOD = "method";
    public static final String HANDLER = "handler";

    // required params rules
    static {
        requiredParams.put(VoidBaseOperationType.GET, new String[]    {KEY, METHOD, HANDLER});
        requiredParams.put(VoidBaseOperationType.PUT, new String[]    {KEY, CONTENT, METHOD, HANDLER});
        requiredParams.put(VoidBaseOperationType.DELETE, new String[] {KEY, METHOD, HANDLER});
        requiredParams.put(VoidBaseOperationType.FLUSH, new String[]  {METHOD, HANDLER});
    }
}
