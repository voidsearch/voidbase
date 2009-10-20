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

package com.voidsearch.voidbase.apps.queuetree.client;

import com.voidsearch.voidbase.client.VoidBaseHttpClient;
import com.voidsearch.voidbase.apps.queuetree.protocol.QueueTreeProtocol;

public class QueueTreeClient extends VoidBaseHttpClient {

    public QueueTreeClient(String hostname) {
        this.hostname = hostname;
        this.module = "/queuetree";
    }

    public void create(String queue, Integer size) throws Exception {

        QueueTreeQuery query = new QueueTreeQuery(hostname);
        query.set(QueueTreeProtocol.METHOD, "ADD");
        query.set(QueueTreeProtocol.QUEUE, queue);
        query.set(QueueTreeProtocol.SIZE, size.toString());

        get(query);
    }

    public void put(String queue, String content) throws Exception {

        QueueTreeQuery query = new QueueTreeQuery(hostname);
        query.set(QueueTreeProtocol.METHOD, "PUT");
        query.set(QueueTreeProtocol.QUEUE, queue);

        post(query,content);

    }

    public String get(String queue, Integer size) throws Exception {

        QueueTreeQuery query = new QueueTreeQuery(hostname);
        query.set(QueueTreeProtocol.METHOD, "GET");
        query.set(QueueTreeProtocol.QUEUE, queue);
        query.set(QueueTreeProtocol.SIZE, size.toString());

        return new String(get(query));

    }

    public String list() throws Exception {

        QueueTreeQuery query = new QueueTreeQuery(hostname);
        query.set(QueueTreeProtocol.METHOD, "LIST");

        return new String(get(query));

    }

}
