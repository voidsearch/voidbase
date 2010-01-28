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

package com.voidsearch.test.apps.queuetree.client;

import org.testng.annotations.*;
import com.voidsearch.voidbase.apps.queuetree.client.QueueTreeClient;

import java.util.LinkedList;

public class VoidQueueResultParsingTest {


    @Test
    public void nullTest() {
        try {

            QueueTreeClient client = new QueueTreeClient();
            String result = client.get("_UserShardNode.size",2);
            LinkedList<String> resultList = client.getResultList(result);

            for (String elem : resultList) {
                System.out.println("ELEM : " + elem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
