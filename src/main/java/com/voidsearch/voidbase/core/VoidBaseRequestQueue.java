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
import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.config.VoidBaseConfiguration;
import com.voidsearch.voidbase.config.Config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * preserving per-handler connection counts
 * and queue of pending requests
 * operates as thread that iterates on expired requests on queue
 * and executes soft/hard cap handlers
 *
 * @author Aleksandar Bradic
 */


public class VoidBaseRequestQueue implements Runnable {

    private ConcurrentHashMap<VoidBaseModule, Integer> counts = new ConcurrentHashMap<VoidBaseModule, Integer>();
    
    private ConcurrentHashMap<VoidBaseModule, LinkedBlockingQueue<VoidBaseModuleRequest>> queue =
        new ConcurrentHashMap<VoidBaseModule, LinkedBlockingQueue<VoidBaseModuleRequest>>();

    int DEFAULT_CAP = Integer.parseInt(VoidBaseConfiguration.get(Config.GLOBAL, Config.REQUEST_QUEUE, Config.MAX_REQUESTS));

    private static final VoidBaseRequestQueue INSTANCE = new VoidBaseRequestQueue();
    
    private VoidBaseRequestQueue() {
    }

    public static VoidBaseRequestQueue getInstance() {
      return INSTANCE;
    }

    public boolean slotAvailable(VoidBaseModule module, VoidBaseModuleRequest request) {
        if (!counts.containsKey(module)) {
            counts.put(module,1);
        }

        int cap = DEFAULT_CAP;

        if (counts.get(module) < cap) {
            counts.put(module, counts.get(module) +1);
            return true;
        } else {
            return false;
        }
    }

    public void releaseSlot(VoidBaseModule module, VoidBaseModuleRequest request) {
        if (counts.contains(module)) {
            counts.put(module,counts.get(module)-1);
        }
    }

    public void enqueue(VoidBaseModule module, VoidBaseModuleRequest request) {
        if (!queue.containsKey(module)) {
            queue.put(module, new LinkedBlockingQueue<VoidBaseModuleRequest>());
        }

        LinkedBlockingQueue<VoidBaseModuleRequest> moduleQueue = queue.get(module);
        moduleQueue.add(request);

    }


    public void run() {

        // iterates the queue and based on timestamp invokes cap handlers
        
    }

}
