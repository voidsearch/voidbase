package com.voidsearch.voidbase.apps.example.module;

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

import com.voidsearch.voidbase.module.VoidBaseModule;
import com.voidsearch.voidbase.module.VoidBaseModuleException;
import com.voidsearch.voidbase.module.VoidBaseModuleResponse;
import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.supervision.StorageSupervisor;
import com.voidsearch.voidbase.storage.example.ExampleVoidBaseStorage;

public class ExampleVoidBaseModule implements VoidBaseModule {

    StorageSupervisor supervisor = StorageSupervisor.getInstance();

    public void initialize(String name) throws VoidBaseModuleException {

        ExampleVoidBaseStorage storage = new ExampleVoidBaseStorage();
        //supervisor.register(storage);

    }

    public VoidBaseModuleResponse handle(VoidBaseModuleRequest request) throws VoidBaseModuleException {
        return new VoidBaseModuleResponse("ExampleVoidBaseModule");
    }

    public void run() {
    }


}
