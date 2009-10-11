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

import java.util.HashMap;
import java.util.logging.Logger;
import java.io.IOException;

public class VoidBaseLogService {

    private static String DEFAULT_LOGGER = "default";

    protected static Logger log = Logger.getLogger(VoidBaseLogService.class.getName());
    protected static HashMap<String, VoidBaseLogger> loggerMap = new HashMap<String, VoidBaseLogger>();

    private static final VoidBaseLogService INSTANCE = new VoidBaseLogService();

    private VoidBaseLogService() {
        try {
            registerLogger(DEFAULT_LOGGER);
        } catch (Exception e) {
            // failover - register disk-free logger
        }
    }

    public static VoidBaseLogService getInstance() {
      return INSTANCE;
    }

    public void registerLogger(String name) throws IOException {
        loggerMap.put(name,new VoidBaseLogger(name,null,this));
    }

    public void registerLogger(String name, String logFile) throws IOException {
        loggerMap.put(name,new VoidBaseLogger(name,logFile,this));
    }

    public static VoidBaseLogger getLogger(String name) {
        if (loggerMap.containsKey(name)) {
            return loggerMap.get(name);
        } else {
            return loggerMap.get(DEFAULT_LOGGER);
        }
    }

    // just a simple mock
    public void log(String loggerName, String message) {
       log.info(message);
    }

}
