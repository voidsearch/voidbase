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


package com.voidsearch.voidbase.core;

import com.voidsearch.voidbase.module.VoidBaseModule;

import java.util.HashMap;

/**
 *
 * Register of Module -> uri mappings
 *
 */

public class VoidBaseResourceRegister {


    private HashMap<String, VoidBaseModule> uriRegister = new HashMap<String, VoidBaseModule>();

    private static final VoidBaseResourceRegister INSTANCE = new VoidBaseResourceRegister();

    private VoidBaseResourceRegister() {
    }

    public static VoidBaseResourceRegister getInstance() {
      return INSTANCE;
    }

    public void register(String uri, VoidBaseModule module) throws ResourceAlreadyRegisteredException {
        if (!uriRegister.containsKey(uri))  {
            uriRegister.put(uri,module);
        } else {
            throw new ResourceAlreadyRegisteredException();
        }
    }

    public VoidBaseModule getHandler(String uri) throws HandlerNotRegisteredException {
        if (uriRegister.containsKey(uri)) {
            return uriRegister.get(uri);
        } else {
            throw new HandlerNotRegisteredException();
        }
    }


}
