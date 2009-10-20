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

package com.voidsearch.test.client;

import org.testng.annotations.*;
import com.voidsearch.voidbase.apps.queuetree.client.QueueTreeClient;

public class QueueTreeClientTest {

    @Test
    public void nullTest() {

        QueueTreeClient client = new QueueTreeClient("localhost:8080");

        try {

            client.create("test",100);

            for (int i=0; i<100; i++) {
                client.put("test","<title>voidbase: [voidbase] http://bit.ly/2o3Pkx internal interface for store</title>");
            }

            System.out.println(client.get("test",500));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    

}
