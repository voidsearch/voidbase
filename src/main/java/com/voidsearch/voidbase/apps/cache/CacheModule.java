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

package com.voidsearch.voidbase.apps.cache;

import com.voidsearch.voidbase.module.*;
import com.voidsearch.voidbase.config.VoidBaseConfig;
import com.voidsearch.voidbase.config.ConfigException;
import com.voidsearch.voidbase.util.GenericUtil;
import com.voidsearch.voidbase.apps.cache.VoidBaseCache;
import com.voidsearch.voidbase.apps.cache.containers.CacheValue;
import com.voidsearch.voidbase.apps.cache.containers.CacheResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CacheModule is a generic cache module which implements strategies for containing and routing to cache handlers and
 * handling a high-level atomicity of operations on cache implementations based on individual cache handler configurations.
 */
public class CacheModule implements VoidBaseModule {    
    protected String name = null;
    protected String defaultHandler = null;
    protected VoidBaseConfig config = null;
    protected ConcurrentHashMap<String, VoidBaseCache> handlers = new ConcurrentHashMap<String, VoidBaseCache>();
    
    protected static final String CONFIG_PATH = "cache";
    protected static final VoidBaseResponseType DEFAULT_TYPE = VoidBaseResponseType.TEXT;

    protected static final Logger logger = LoggerFactory.getLogger(VoidBaseModule.class.getName());

    /**
     * Creates a new instance of CacheModule
     */
    public CacheModule() {
        try {
            config = VoidBaseConfig.getInstance();
        } catch (ConfigException e) {
            logger.error("Failed to get configuration instance.");
            GenericUtil.logException(e);
        }
    }

    /**
     * Initializes CacheModule and all configured cache handlers
     * @param name
     * @throws VoidBaseModuleException
     */
    public void initialize(String name) throws VoidBaseModuleException {
        Map<String, String> modules = config.getMap(CONFIG_PATH, "handlers");

        if (modules == null || modules.size() == 0) {
            throw new VoidBaseModuleException("Failed to initialize module " + name + " - no cache handlers");
        }

        // set module name
        this.name = name;

        // get default handler
        defaultHandler = config.getString(CONFIG_PATH, "default_handler");

        // initialize cache handler
        initializeCacheModules(modules);
    }

    /**
     * Handler for all cache requests which are further proxied down to specific cache handler
     * @param request
     * @return a response from VoidBaseModule handler
     * @throws VoidBaseModuleException
     */
    public VoidBaseModuleResponse handle(VoidBaseModuleRequest request) throws VoidBaseModuleException {
        CacheValue value = new CacheValue();
        Map<String, String> params = request.getParams();
        List<String> route = getRoute(request.getRoute());
        CacheModuleLockStrategy lockStrategy = new CacheModuleLockStrategy();

        // check if the method and handler or list of handlers has been set
        if (params == null) {
            return new VoidBaseModuleResponse("Error - Params not set", VoidBaseResponseStatus.INTERNAL_ERROR, VoidBaseResponseType.TEXT);
        }
        if (!params.containsKey("method")) {
            return new VoidBaseModuleResponse("Error - Method not set", VoidBaseResponseStatus.ERROR, VoidBaseResponseType.TEXT);
        }
        if (!params.containsKey("handler")) {
            return new VoidBaseModuleResponse("Error - Handler not set", VoidBaseResponseStatus.ERROR, VoidBaseResponseType.TEXT);
        }

        // get methd and process handler list respectively - throw error if handler has not been initialized
        String key = getKey(route, params);
        String content = getContent(request.getContent(), params);
        String method = params.containsKey("method") ? params.get("method").toLowerCase() : null;
        String[] requestedHandlers = params.get("handler").split(",");

        for (String handlerName: requestedHandlers) {
            if (handlers.contains(handlerName)) {
                return new VoidBaseModuleResponse("Error - Unknown handler " + handlerName, VoidBaseResponseStatus.ERROR, VoidBaseResponseType.TEXT);
            }

            // initialize handler
            VoidBaseCache handler = (VoidBaseCache)handlers.get(handlerName).clone();

            // check if valid method
            if (!handler.isRegistered(method)) {
                return new VoidBaseModuleResponse("Error - Unknown method '" + method + "' for handler " + handlerName,
                                                  VoidBaseResponseStatus.ERROR,
                                                  VoidBaseResponseType.TEXT);
            }

            // handle the method
            try {
                value.append(lockStrategy.execute(handler, method, route, params, key, content));
            } catch (CacheException e) {
                logger.error("Cache Exception - " + e.getMessage());
                GenericUtil.logException(e);

                return new VoidBaseModuleResponse("Error - " + e.getMessage());
            }
        }

        return new VoidBaseModuleResponse(value.text, VoidBaseResponseStatus.OK, VoidBaseResponseType.TEXT);
    }

    /**
     * Currently just logs when CacheModule starts
     */
    public void run() {
        logger.info("Starting Cache Module...");
    }

    /**
     * Initializes specific cache modules/handlers
     * @param modules
     * @throws VoidBaseModuleException
     */
    protected void initializeCacheModules(Map<String, String> modules) throws VoidBaseModuleException {
        Class obj;

        for (Map.Entry<String, String> handler: modules.entrySet()) {
            logger.info("Initializing cache interface: " + handler.getValue());
            
            try {
                obj = Class.forName(handler.getValue());
                VoidBaseCache cacheHandler = (VoidBaseCache)obj.newInstance();

                cacheHandler.initialize();
                handlers.put(handler.getKey(), cacheHandler);
            } catch (ClassNotFoundException e) {
                logger.info("Class not found: " + handler.getValue());
                GenericUtil.logException(e);

                throw new VoidBaseModuleException("CacheModule - failed to initialize cache interface: " + handler.getValue());
            } catch (IllegalAccessException e) {
                logger.info("Access Exception in constructor for: " + handler.getValue());
                GenericUtil.logException(e);

                throw new VoidBaseModuleException("CacheModule - access exception for cache interface: " + handler.getValue());
            } catch (InstantiationException e) {
                logger.info("Instantiation Exception for: " + handler.getValue());
                GenericUtil.logException(e);

                throw new VoidBaseModuleException("CacheModule - failed to instantiate cache interface: " + handler.getValue());
            } catch (CacheException e) {
                logger.info("Cache exception for: " + handler.getValue());
                GenericUtil.logException(e);

                throw new VoidBaseModuleException("CacheModule - failed to initialize cache interface: " + handler.getValue());
            } 
        }
    }

    /**
     * Returns a Cache specific route from original request route 
     * @param route
     * @return a vector of route
     */
    protected List<String> getRoute(List<String> route) {
        Boolean start = false;
        String resource = getResource();
        ArrayList<String> newRoute = new ArrayList<String>();

        for (String str: route) {
            if (str.equals(resource)) {
                start = true;
            } else if (start == true) {
                newRoute.add(str);
            }
        }

        return newRoute;
    }

    /**
     * Returns a CacheModule's resource
     * @return a module's resource
     */
    protected String getResource() {
        StringBuilder key = new StringBuilder("modules.");

        key . append(name)
            . append(".resource");

        return config.getString(key.toString());
    }

    /**
     * Returns rendered Cache response from CacheValue object
     * @param value
     * @param format
     * @param status
     * @return rendered response from VoidBaseModule handler
     */
    protected VoidBaseModuleResponse renderResponse(CacheValue value, String format, CacheResponseStatus status) {
        return renderResponse(value.text == null ? "" : value.text, format, status);
    }

    /**
     * Returns rendered Cache response
     * @param message
     * @param format
     * @param status
     * @return rendered response from VoidBaseModule handler
     */
    protected VoidBaseModuleResponse renderResponse(String message, String format, CacheResponseStatus status) {
        String responseText;
        VoidBaseResponseType type = VoidBaseResponseType.deserialize(format);

        // default type
        if (type == VoidBaseResponseType.UNKNOWN) {
            type = DEFAULT_TYPE;
        }

        // render depending on type
        if (type == VoidBaseResponseType.JSON) {
            responseText = getJSON(message, status);
        }
        else if (type == VoidBaseResponseType.TEXT) {
            responseText = message;
        }
        else {
            return renderResponse("Unknown Format - " + format, DEFAULT_TYPE.serialize(), CacheResponseStatus.ERROR);
        }

        return new VoidBaseModuleResponse(responseText, renderStatus(status), type);
    }

    /**
     * Returns a key from request params
     * @param route
     * @param params
     * @return a key from a request
     */
    protected String getKey(List<String> route, Map<String, String> params) {
        if (params.containsKey("key"))
            return params.get("key");
        if (route.size() > 0)
            return route.get(route.size() - 1);

        return null;
    }

    /**
     * Returns a content from request params
     * @param content
     * @param params
     * @return a content from a request
     */
    protected String getContent(String content, Map<String, String> params) {
        if (params.containsKey("content"))
            return params.get("content");
        if (content == null || content.equals(""))
            return null;

        return content;
    }

    /**
     * Builds JSON from a cache result response
     * @param message
     * @param status
     * @return serialized JSON
     */
    protected String getJSON(String message, CacheResponseStatus status) {
        StringBuilder response = new StringBuilder();

        response . append("{message=\"") . append(message)            . append("\",")
                 . append(" status=\"")  . append(status.serialize()) . append("\"}");

        return response.toString();
    }

    /**
     * Returnds response status from a cache response
     * @param status
     * @return response status of a VoidBaseModule handler
     */
    protected VoidBaseResponseStatus renderStatus(CacheResponseStatus status) {
        if (status == CacheResponseStatus.OK)
            return VoidBaseResponseStatus.OK;
        if (status == CacheResponseStatus.ERROR)
            return VoidBaseResponseStatus.ERROR;
        if (status == CacheResponseStatus.FATAL_ERROR || status == CacheResponseStatus.INTERNAL_ERROR)
            return VoidBaseResponseStatus.INTERNAL_ERROR;

        return VoidBaseResponseStatus.UNKNOWN;
    }

    
}
