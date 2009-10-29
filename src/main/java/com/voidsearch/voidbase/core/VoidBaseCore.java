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
import com.voidsearch.voidbase.module.VoidBaseModuleException;
import com.voidsearch.voidbase.module.VoidBaseModuleResponse;
import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.supervision.StorageSupervisor;
import com.voidsearch.voidbase.config.VoidBaseConfiguration;
import com.voidsearch.voidbase.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Set;
import java.io.IOException;

public class VoidBaseCore implements VoidBaseModule {

    protected static final Logger logger = LoggerFactory.getLogger(VoidBaseCore.class.getName());

    private LinkedHashMap<String, VoidBaseModule> moduleMap = new LinkedHashMap<String, VoidBaseModule>();

    StorageSupervisor supervisor = StorageSupervisor.getInstance();
    VoidBaseResourceRegister resourceRegister = VoidBaseResourceRegister.getInstance();
    VoidBaseLogService logService = VoidBaseLogService.getInstance();

    private static final VoidBaseCore INSTANCE = new VoidBaseCore();
    
    private VoidBaseCore() {
    }

    public static VoidBaseCore getInstance() {
      return INSTANCE;
    }


    public void initialize(String name) throws VoidBaseModuleException {

        logger.info("VoidBaseCore : initializing");

        // initialize global dispatcher module
        
        if (VoidBaseConfiguration.contains(Config.GLOBAL, Config.DISPATCHER, Config.CLASS)) {
            String className = VoidBaseConfiguration.get(Config.GLOBAL, Config.DISPATCHER, Config.CLASS);
            try {
                VoidBaseModule childModule = reflectModule(className);
                moduleMap.put(getModuleMapKey(childModule, Config.DISPATCHER),childModule);
                childModule.initialize(Config.DISPATCHER);
                childModule.run();
            } catch (Exception e) {
                throw new VoidBaseModuleException("Error initializing dispatcher module");
            }
        } else {
            throw new VoidBaseModuleException("Missing dispatcher module in configuration");
        }
        

        // initialize user modules

        LinkedList<String> modules = VoidBaseConfiguration.getKeyList(Config.MODULES);

        for (String module : modules) {

            String active = VoidBaseConfiguration.get(Config.MODULES,module, Config.STATUS);
            if (active.equals(Config.ACTIVE)) {
                if (VoidBaseConfiguration.contains(Config.MODULES,module, Config.CLASS)) {
                    String className = VoidBaseConfiguration.get(Config.MODULES,module, Config.CLASS);
                    try {
                        VoidBaseModule childModule = reflectModule(className);
                        moduleMap.put(getModuleMapKey(childModule,module),childModule);

                        // preregisted handler
                        try {
                            String uri  = VoidBaseConfiguration.get(Config.MODULES, module, Config.RESOURCE_URI);
                            if (uri != null) {
                                resourceRegister.register(uri,childModule);
                            }
                        } catch (ResourceAlreadyRegisteredException e) {
                            throw new VoidBaseModuleException("failed to register resource");
                        }

                        // preregister logger
                        if (VoidBaseConfiguration.contains(Config.MODULES, module, Config.LOG_FILE)) {
                            String logFile = VoidBaseConfiguration.get(Config.MODULES, module, Config.LOG_FILE);
                            try {
                                logService.registerLogger(module,logFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // preregister logger
                        if (VoidBaseConfiguration.contains(Config.MODULES, module, Config.LOG_QUEUE)) {
                            String logQueue = VoidBaseConfiguration.get(Config.MODULES, module, Config.LOG_QUEUE);
                            try {
                                logService.registerQueue(module,logQueue);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // invoke module-specific initialization
                        childModule.initialize(module);

                        if (childModule instanceof Thread) {
                            ((Thread)childModule).start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void run() {
        
    }

    public VoidBaseModuleResponse handle(VoidBaseModuleRequest request) throws VoidBaseModuleException {
        return null;
    }

    public String getModuleMapKey(VoidBaseModule module, String moduleName) {
        return moduleName; 
    }

    public Set<String> getRegisteredModules() {
        return moduleMap.keySet();
    }

    public boolean containsModule(String moduleName) {
        return moduleMap.containsKey(moduleMap);
    }

    // broadcast command to all modules
    public void moduleBroadcast() {

    }

    protected VoidBaseModule reflectModule(String name) throws Exception {
      Class obj = Class.forName(name);
      logger.info("Getting reflection for class " + name);
      return (VoidBaseModule)obj.newInstance();
    }
    
}
