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

package com.voidsearch.voidbase.apps.feedq;

import com.voidsearch.voidbase.apps.feedq.resource.FeedResource;
import com.voidsearch.voidbase.module.VoidBaseModule;
import com.voidsearch.voidbase.module.VoidBaseModuleException;
import com.voidsearch.voidbase.module.VoidBaseModuleResponse;
import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.config.VoidBaseConfiguration;
import com.voidsearch.voidbase.config.Config;
import com.voidsearch.voidbase.apps.feedq.connector.fetcher.FeedFetcher;
import com.voidsearch.voidbase.apps.feedq.connector.fetcher.FeedFetcherFactory;
import com.voidsearch.voidbase.apps.feedq.resource.ResourceCluster;
import com.voidsearch.voidbase.apps.queuetree.module.QueueTreeModule;
import com.voidsearch.voidbase.core.VoidBaseResourceRegister;

import java.util.HashMap;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class FeedQueueModule extends Thread implements VoidBaseModule {

    private static String CONF_FEED_RESOURCES = "FeedQResources";

    private static int POLL_INTERVAL;
    private static int DIFF_WINDOW_SIZE;

    VoidBaseResourceRegister resourceRegister = VoidBaseResourceRegister.getInstance();
    private LinkedList<ResourceCluster> resources = new LinkedList<ResourceCluster>();
    private HashMap<String, FeedResource> contentQueue = new HashMap<String, FeedResource>();

    private Object contentLock;

    private QueueTreeModule queue;

    public void initialize(String name) throws VoidBaseModuleException {

        POLL_INTERVAL = VoidBaseConfiguration.getInt(Config.MODULES, name, "pollInterval");
        DIFF_WINDOW_SIZE = VoidBaseConfiguration.getInt(Config.MODULES, name, "diffWindowByteSize");

        for (String resourceCluster : VoidBaseConfiguration.getKeyList(CONF_FEED_RESOURCES)) {
            ResourceCluster cluster = new ResourceCluster(resourceCluster);
            for (String resourceName : VoidBaseConfiguration.getKeyList(CONF_FEED_RESOURCES,resourceCluster)) {
                String resource =  VoidBaseConfiguration.get(CONF_FEED_RESOURCES,resourceCluster,resourceName);
                cluster.add(resourceName,resource);
            }
            resources.add(cluster);
        }

    }

    public VoidBaseModuleResponse handle(VoidBaseModuleRequest request) throws VoidBaseModuleException {
        VoidBaseModuleResponse response = new VoidBaseModuleResponse();

        synchronized (contentLock) {
             // handle content operation
        }

        return response;
    }

    public void run() {

        // blocking wait for queue to be initialized
        try {
            queue = (QueueTreeModule) resourceRegister.getHandlerBlocking("queuetree");
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {

            for (ResourceCluster cluster : resources) {
                try {
                    if (!queue.queueExists(cluster.getName())) {
                        queue.createQueue(cluster.getName(),100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (String resource : cluster.resources()) {
                    try {
                        FeedFetcher fetcher = FeedFetcherFactory.getFetcher(resource);
                        FeedResource newResource = fetcher.fetch(resource);
                       if (contentQueue.containsKey(resource)) {
                            FeedResource oldResource = contentQueue.get(resource);
                            cluster.setStat(resource, newResource.getDelta(oldResource));
                        }
                        contentQueue.put(resource, newResource);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    if (cluster.getQueueStatEntry().length() != 0) {
                        queue.insertToQueue(cluster.getName(), cluster.getQueueStatEntry());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    Thread.sleep(POLL_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private int getDelta(byte[] oldContent, byte[] newContent) {

        if ((oldContent.length > 0) && (newContent.length > 0)) {
            int j = 0;
            for (int i = 0; i < newContent.length; i++) {
                int offset = i;
                while (newContent[i] == oldContent[j]) {
                    i++;
                    j++;
                    if (j == oldContent.length) { // matched whole sequence
                        if (offset == 0) {
                            return (newContent.length - oldContent.length - 1);
                        } else {
                            return offset;
                        }
                    }
                }
                j = 0;
            }
        }
        return 0;
    }


}
