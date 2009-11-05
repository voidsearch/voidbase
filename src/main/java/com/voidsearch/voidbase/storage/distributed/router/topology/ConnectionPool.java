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

package com.voidsearch.voidbase.storage.distributed.router.topology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionPool {
    ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<String, Connection>();

    protected static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class.getName());

    public ConnectionPool() { }

    public void addConnection(String name, String host, Integer port) {
        if (connections.containsKey(name)) {
            logger.warn("Connection " + name + " already exists, skipping...");
            return;
        }

        connections.put(name, new Connection(host, port));
    }
}
