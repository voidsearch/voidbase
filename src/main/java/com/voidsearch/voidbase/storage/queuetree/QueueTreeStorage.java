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

package com.voidsearch.voidbase.storage.queuetree;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.*;

import com.voidsearch.voidbase.storage.SupervisedStorage;
import com.voidsearch.voidbase.storage.StorageOperation;
import com.voidsearch.voidbase.supervision.SupervisionException;
import com.voidsearch.voidbase.supervision.StorageStats;
import com.voidsearch.voidbase.apps.queuetree.protocol.QueueTreeProtocol;
import com.voidsearch.voidbase.serialization.VoidBaseSerialization;

public class QueueTreeStorage implements SupervisedStorage {

    protected static final Logger logger = LoggerFactory.getLogger(QueueTreeStorage.class.getName());
    protected static QueueTreeStorage singleton = null;

    // storage structure
    private ConcurrentHashMap<String, ArrayBlockingQueue> queueTree =
            new ConcurrentHashMap<String, ArrayBlockingQueue>();


    private ConcurrentHashMap<String, QueueMetadata> queueMedatada =
            new ConcurrentHashMap<String, QueueMetadata>();

    // maintenance support
    private HashSet<StorageOperation> opLocks = new HashSet<StorageOperation>();
    private long memorySize = 0;

    // stats counters
    private ConcurrentHashMap<StorageOperation, Long> queryCounter = new ConcurrentHashMap<StorageOperation,  Long>();
    private long totalQueries = 0;

    // queue entry

    class QueueEntry {

        private long timestamp;
        private Object value;

        public QueueEntry(Object value) {
            timestamp = System.currentTimeMillis();
            this.value = value;
        }

        // allow for overriding of getKey method
        public String getKey()  {
            return (String)value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public Object getValue() {
            return value;
        }

        public String toString() {
            return (String)value;
        }

    }

    // instantiation

    protected QueueTreeStorage() { super(); }

    public Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
    }

    public static synchronized QueueTreeStorage factory() {
      if (singleton == null) {
        singleton = new QueueTreeStorage();
      }
      return singleton;
    }

    // create a new queue
    public void createQueue(String queueName, int size) throws QueueAlreadyExistsException, SupervisionException {

        updateQueryCounters(StorageOperation.CREATE);

        if (opLocks.contains(StorageOperation.CREATE))
            throw new SupervisionException();

        if (!queueTree.containsKey(queueName)) {
            queueTree.put(queueName,new ArrayBlockingQueue(size));
            queueMedatada.put(queueName,new QueueMetadata(queueName,size));
        }
        else {
            throw new QueueAlreadyExistsException();
        }
    }

    // resize existing queue
    public void resizeQueue(String queueName, int size) throws InvalidQueueException,SupervisionException {

        updateQueryCounters(StorageOperation.RESIZE);

        if (opLocks.contains(StorageOperation.RESIZE))
            throw new SupervisionException();

        if (opLocks.contains(StorageOperation.CREATE))
            throw new SupervisionException();


        if (queueTree.containsKey(queueName)) {
            ArrayBlockingQueue queue = new ArrayBlockingQueue(size,true,queueTree.get(queueName));
            queueTree.put(queueName,queue);
            if (queueMedatada.contains(queueName)) {
                QueueMetadata metadata = queueMedatada.get(queueName);
                metadata.set(QueueTreeProtocol.SIZE,size);
                queueMedatada.put(queueName,metadata);
            }
        }
        else {
            throw new InvalidQueueException();
        }
    }


    // check if queue exists
    public boolean queueExists(String name) throws SupervisionException {

        updateQueryCounters(StorageOperation.EXISTS);

        if (opLocks.contains(StorageOperation.EXISTS))
            throw new SupervisionException();

        return queueTree.contains(name);
    }


    // delete queue
    public void deleteQueue(String queueName) throws InvalidQueueException,SupervisionException {

        updateQueryCounters(StorageOperation.DELETE);

        if (opLocks.contains(StorageOperation.DELETE))
            throw new SupervisionException();

        // remove queue
        if (queueTree.containsKey(queueName)) {
            decrementMemorySize(queueTree.get(queueName));
            queueTree.remove(queueName);
        }

        // remove metadata
        if (queueMedatada.containsKey(queueName)) {
            decrementMemorySize(queueMedatada.get(queueName));
            queueMedatada.remove(queueName);
        }

        else {
            throw new InvalidQueueException();
        }
    }

    // delete all queues
    public void deleteAll() throws SupervisionException {

        updateQueryCounters(StorageOperation.DELETE);

        if (opLocks.contains(StorageOperation.DELETE))
            throw new SupervisionException();

        for (String queueName : queueTree.keySet()) {
            try {
                deleteQueue(queueName);
            } catch (InvalidQueueException e) {
                e.printStackTrace();
            }
        }

    }

    // flush single queue
    // (high-order operation)
    public void flushQueue(String queueName) throws SupervisionException {

        if (opLocks.contains(StorageOperation.FLUSH))
            throw new SupervisionException();

        decrementMemorySize(queueTree.get(queueName));
        ArrayBlockingQueue queue = queueTree.get(queueName);

        try {
            deleteQueue(queueName);
            createQueue(queueName,(queue.size()+queue.remainingCapacity()));
        } catch (InvalidQueueException e) {
            e.printStackTrace();
        } catch (QueueAlreadyExistsException e) {
            e.printStackTrace();
        }
    }


    // flushAll all queues
    // (high-order operation)
    public void flushAll() throws SupervisionException {

        updateQueryCounters(StorageOperation.FLUSH);

        if (opLocks.contains(StorageOperation.FLUSH))
            throw new SupervisionException();

        for (String queueName : queueTree.keySet())
            flushQueue(queueName);

    }

    public LinkedHashMap<String, Object> getMetadataMap(String queueName) {
        if (queueMedatada.containsKey(queueName)) {
            QueueMetadata metadata = queueMedatada.get(queueName);
            return metadata.getMetadata();
        }
        else {
            return new LinkedHashMap<String, Object>();
        }
    }

    // push a element in given queue
    public void putFIFO(String queueName, Object value) throws InvalidQueueException,SupervisionException {

        updateQueryCounters(StorageOperation.PUT);

        if (opLocks.contains(StorageOperation.PUT))
            throw new SupervisionException();

        incrementMemorySize(value);

        if (queueTree.containsKey(queueName)) {
            ArrayBlockingQueue queue = queueTree.get(queueName);

            // lazy types - first inserted element defines queue type
            if (queueMedatada.containsKey(queueName)) {
                QueueMetadata metadata = queueMedatada.get(queueName);
                if (metadata.getType().equals(VoidBaseSerialization.UNKNOWN))
                    metadata.setType(VoidBaseSerialization.getType(value));
            }

            synchronized (queue) {
                if (queue.remainingCapacity() == 0) {
                    try {
                        decrementMemorySize(queue.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                queue.add(new QueueEntry(value));
                // update metadata with hooks
                QueueTreeHooks.handlePut(queueMedatada.get(queueName),queue,value);
            }

        }
        else {
            throw new InvalidQueueException();
        }

    }

    public List getFIFO(String queueName,int count) throws InvalidQueueException, SupervisionException {
        return getFIFO(queueName,count,0);
    }

    // retrieve a number of fifo elements element from given queue
    public List getFIFO(String queueName,int count, long timestamp) throws InvalidQueueException, SupervisionException {

        updateQueryCounters(StorageOperation.GET);

        if (opLocks.contains(StorageOperation.GET))
            throw new SupervisionException();

        if (queueTree.containsKey(queueName)) {

            LinkedList resultList = new LinkedList();

            ArrayBlockingQueue queue = queueTree.get(queueName);
            Iterator it = queue.iterator();

            while (it.hasNext()) {
                QueueEntry entry = (QueueEntry)it.next();
                if (entry.getTimestamp() > timestamp) {
                    resultList.add(entry);
                }
            }

            Collections.reverse(resultList);
            int limit = (count < resultList.size()) ? count : resultList.size();

            return resultList.subList(0, limit);
        }
        else {
            throw new InvalidQueueException();
        }
    }

    // list all registered queues
    public LinkedList listQueues() throws SupervisionException {

        updateQueryCounters(StorageOperation.LIST);

        if (opLocks.contains(StorageOperation.LIST))
            throw new SupervisionException();

        LinkedList<String> queueList = new LinkedList<String>();
        for (String queue : queueTree.keySet()) {
            queueList.add(queue);
        }
        return queueList;
    }


    // supervision support

    public void blockOperation(StorageOperation op) throws SupervisionException {
        opLocks.add(op);
    }

    public void enableOperation(StorageOperation op) throws SupervisionException {
        opLocks.remove(op);
    }

    public void updateStats(StorageStats stats) {
        stats.setCounter(StorageStats.Counter.SIZE,getSize());
        stats.setCounter(StorageStats.Counter.TOTAL_ENTRIES,getTotalEntries());
        stats.setCounter(StorageStats.Counter.MEMORY_USAGE,getMemorySize());
        stats.setCounter(StorageStats.Counter.QUERY_COUNT,getTotalQueries());
    }

    // util methods

    public void updateQueryCounters() {
        updateQueryCounters(null);
    }

    public void updateQueryCounters(StorageOperation op) {
        totalQueries++;
        if (queryCounter.containsKey(op)){
            queryCounter.put(op, queryCounter.get(op)+1);
        }
        else {
            queryCounter.put(op,(long)1);
        }
    }

    public long getSize() {
        return queueTree.size();
    }

    public long getTotalEntries() {
        int cnt = 0;
        for (String entry: queueTree.keySet()) {
            cnt += queueTree.get(entry).size();
        }
        return cnt;
    }

    public long getTotalQueries() {
        return totalQueries;
    }

    public void incrementMemorySize(Object obj) {
        synchronized (QueueTreeStorage.class) {
            memorySize += getMemorySize(obj);
        }
    }

    public void decrementMemorySize(Object obj) {
        synchronized (QueueTreeStorage.class) {
            memorySize -= getMemorySize(obj);
        }
    }

    public static long getMemorySize(Object obj) {
        if (obj instanceof String) {
            String str = (String)obj;
            return str.getBytes().length;
        }
        else if (obj instanceof QueueEntry) {
            QueueEntry entry = (QueueEntry)obj;
            return getMemorySize(entry.getValue());
        }
        else if (obj instanceof QueueMetadata) {
            QueueMetadata entry = (QueueMetadata)obj;
            return getMemorySize(entry.getMetadata());
        }
        else if (obj instanceof ArrayBlockingQueue) {
            ArrayBlockingQueue queue = (ArrayBlockingQueue)obj;
            Iterator it = queue.iterator();
            long total = 0;
            while (it.hasNext()) {
                total += getMemorySize(it.next());
            }
            return total;
        }
        else if (obj instanceof HashMap) {
            HashMap map = (HashMap)obj;
            Iterator it = map.keySet().iterator();
            long total = 0;
            while (it.hasNext()) {
                total += getMemorySize(map.get(it.next()));
            }
            return total;
        }
        return 0;
    }

    public long getMemorySize() {
        return memorySize;
    }


}
