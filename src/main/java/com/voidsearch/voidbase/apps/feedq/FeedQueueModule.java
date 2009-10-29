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

import com.voidsearch.voidbase.module.VoidBaseModule;
import com.voidsearch.voidbase.module.VoidBaseModuleException;
import com.voidsearch.voidbase.module.VoidBaseModuleResponse;
import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.config.VoidBaseConfiguration;
import com.voidsearch.voidbase.config.Config;
import com.voidsearch.voidbase.apps.feedq.connector.fetcher.FeedFetcher;
import com.voidsearch.voidbase.apps.feedq.connector.fetcher.FeedFetcherFactory;
import com.voidsearch.voidbase.apps.queuetree.client.QueueTreeClient;

import java.util.HashMap;
import java.util.Arrays;

public class FeedQueueModule extends Thread implements VoidBaseModule {

    private static int POLL_INTERVAL;
    private static int DIFF_WINDOW_SIZE;

    private HashMap<String,String> resources;
    private HashMap<String, byte[]> contentQueue = new HashMap<String, byte[]>();
    private Object contentLock;

    public void initialize(String name) throws VoidBaseModuleException {

        POLL_INTERVAL = VoidBaseConfiguration.getInt(Config.MODULES, name, "pollInterval");
        DIFF_WINDOW_SIZE = VoidBaseConfiguration.getInt(Config.MODULES, name, "diffWindowByteSize");

        resources = new HashMap<String,String>();
        for (String resource : VoidBaseConfiguration.getKeyList(name,"resources")) {
            resources.put(resource, VoidBaseConfiguration.get(name,"resources",resource));
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

        QueueTreeClient client = new QueueTreeClient("localhost:8080");

        while(true) {

            for (String resource : resources.keySet()) {

                try {
                    byte[] newContent = fetchContent(resources.get(resource));

                    client.create(resource,100);

                    if (contentQueue.containsKey(resource)) {

                        byte[] oldContent = contentQueue.get(resource);

                        int delta = 0;

                        if (!Arrays.equals(oldContent,newContent)) {
                            delta = getDelta(oldContent,newContent);
                        }

                        // dispatch delta to queue

                        client.put(resource,"<val>"+delta+"</val>");

                    }
                    contentQueue.put(resource, newContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
              Thread.sleep(POLL_INTERVAL);
            } catch (Exception e) {
                e.printStackTrace();
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


    private static byte[] fetchContent(String resource) throws Exception {

        FeedFetcher fetcher = FeedFetcherFactory.getFetcher(resource);

        if (fetcher != null) {
          return fetcher.fetch(resource,DIFF_WINDOW_SIZE);
        } else {
            return null;
        }

    }



}
