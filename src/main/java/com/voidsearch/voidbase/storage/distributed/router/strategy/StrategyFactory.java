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

package com.voidsearch.voidbase.storage.distributed.router.strategy;

import com.voidsearch.voidbase.config.ConfigException;
import com.voidsearch.voidbase.config.VoidBaseConfig;
import com.voidsearch.voidbase.util.GenericUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrategyFactory {
    protected static final Logger logger = LoggerFactory.getLogger(StrategyFactory.class.getName());

    public static Strategy getInstance(String node) throws StrategyException {
        String name;
        Strategy strategy;
        VoidBaseConfig config;

        // grab a configuration
        try {
            config = VoidBaseConfig.getInstance();
        } catch (ConfigException e) {
            GenericUtil.logException(e);
            throw new StrategyException("Failed to initialize strategy configuration.");
        }

        // get the class name and instantiate strategy
        name = config.getString(node);

        if (name == null) {
            throw new StrategyException("Strategy not configured.");
        }

        logger.info("Initializing strategy...");
        return getStrategy(name);
    }

    protected static Strategy getStrategy(String name) throws StrategyException {
        Strategy strategy;
        logger.info(" - Loading class: " + name);

        try {
            strategy = (Strategy)Class.forName(name).newInstance();
        } catch (ClassNotFoundException e) {
            GenericUtil.logException(e);
            throw new StrategyException("Class not found: " + name);
        } catch (IllegalAccessException e) {
            GenericUtil.logException(e);
            throw new StrategyException("Illegal access to constructor for: " + name);
        } catch (InstantiationException e) {
            GenericUtil.logException(e);
            throw new StrategyException("Failed to instantiate: " + name);
        }

        logger.info("Strategy initialized.");
        return strategy;
    }
}
