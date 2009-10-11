package com.voidsearch.voidbase.supervision;

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

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voidsearch.voidbase.storage.SupervisedStorage;

public class StorageSupervisor extends Thread {

    private static Logger logger = LoggerFactory.getLogger(StorageSupervisor.class);

    private LinkedHashMap<SupervisedStorage, StorageStats> supervised =
            new LinkedHashMap<SupervisedStorage, StorageStats>();

    private static int POLL_INTERVAL = 5000;

    private SupervisionStrategy strategy = new ConservativeSupervisionStrategy();

    private StorageSupervisor() {
        this.start();
    }

    private static class SingletonHolder {
        private static final StorageSupervisor INSTANCE = new StorageSupervisor();
    }

    public static StorageSupervisor getInstance() {
          return SingletonHolder.INSTANCE;
    }

    public void run() {

        while(true) {

            for (SupervisedStorage storage : supervised.keySet()) {

                StorageStats stats = supervised.get(storage);
                storage.updateStats(stats);

                try {
                    strategy.supervise(storage, stats);
                } catch (SupervisionException e) {
                    logger.error(storage.getClass() + " supervision failed");
                }

            }

            try {
                Thread.sleep(POLL_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void register(SupervisedStorage storage) {
        supervised.put(storage, new StorageStats(storage));
    }

    // access methods

    public void setStrategy(SupervisionStrategy strategy) {
        this.strategy = strategy;
    }

    // util methods

    public long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    // (dump stats of supervised objects)
    // to be passed to external resource handler
    public String getStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("<StorageSupervisor>\r\n");

        sb.append("<totalMemory>").append(getTotalMemory()).append("</totalMemory>\r\n");
        sb.append("<maxMemory>").append(getMaxMemory()).append("</maxMemory>\r\n");
        sb.append("<freeMemory>").append(getFreeMemory()).append("</freeMemory>\r\n");

        sb.append("<StorageStats>\r\n");
        for (SupervisedStorage storage : supervised.keySet()) {
            sb.append("\t<storage>\r\n");
            sb.append(supervised.get(storage).getStats());
            sb.append("\t</storage>\r\n");
        }
        sb.append("</StorageStats>\r\n");
        sb.append("</StorageSupervisor>\r\n");
        return sb.toString();
    }

}
