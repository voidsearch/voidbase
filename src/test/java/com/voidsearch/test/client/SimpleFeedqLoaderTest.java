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

import java.util.Random;

public class SimpleFeedqLoaderTest {

    private int QUEUE_SIZE = 1000;
    private String TEST_QUEUE = "xml_test";
    Random rnd = new Random();

    @Test
    public void nullTest() {

        QueueTreeClient client = new QueueTreeClient("localhost:8080");

        try {

            int queryCnt = 0;
            client.create(TEST_QUEUE,QUEUE_SIZE);

            for (int i=0; i<QUEUE_SIZE*1000; i++) {
                StringBuilder sb = new StringBuilder();

                sb.append("<count>").append(i).append("</count>")
                  .append("<random>").append(rnd.nextInt(1024)).append("</random>")
                  .append("<random2>").append(rnd.nextInt(1024)).append("</random2>")
                  .append("<random3>").append(rnd.nextInt(10)).append("</random3>")
                  .append("<random4>").append(rnd.nextInt(100)).append("</random4>")
                  .append("<random5>").append(rnd.nextInt(10000)).append("</random5>")
                  .append("<random6>").append(rnd.nextInt(100000)).append("</random6>")
                  .append("<random7>").append(rnd.nextInt(10000)).append("</random7>")
                  .append("<random8>").append(rnd.nextInt(1000)).append("</random8>")
                  .append("<random9>").append(rnd.nextInt(100)).append("</random9>")
                  .append("<random10>").append(rnd.nextInt(10)).append("</random10>")
                  .append("<sin>").append(1000*Math.sin(rnd.nextGaussian())).append("</sin>");

                try {
                    System.out.println(queryCnt++);
                    Thread.sleep(1000);
                } catch (Exception e) { }

                client.put(TEST_QUEUE,sb.toString());
            }

            System.out.println(client.get(TEST_QUEUE,QUEUE_SIZE));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
