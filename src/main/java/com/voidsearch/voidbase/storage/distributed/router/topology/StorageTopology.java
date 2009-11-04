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

import com.voidsearch.voidbase.storage.distributed.router.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class StorageTopology {
    protected static Strategy strategy = null;
    protected static StorageTopology topology = null;
    protected static final ConcurrentHashMap<String, StorageNode> nodes = new ConcurrentHashMap<String, StorageNode>();

    protected static final Logger logger = LoggerFactory.getLogger(StorageTopology.class.getName());

    protected StorageTopology() {
        super();
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static synchronized StorageTopology getInstance() {
        if (topology == null) {
            topology = new StorageTopology();
        }

        return topology;
    }

    public synchronized void addNode(StorageNode node) throws StorageTopologyException {
        if (node == null) {
            throw new StorageTopologyException("Invalid node object.");
        }
        if (node.id == null || node.id < 0) {
            throw new StorageTopologyException("Invalid node ID.");
        }
        if (node.name == null) {
            throw new StorageTopologyException("Invalid node name.");
        }
        if (nodes.containsKey(node.name)) {
            throw new StorageTopologyException("Node with name " + node.name + " already exists.");
        }

        nodes.put(node.name, node);
    }
}
