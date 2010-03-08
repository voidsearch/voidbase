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

package com.voidsearch.voidbase.quant.queue;

import com.voidsearch.voidbase.quant.timeseries.NumericalSequence;
import com.voidsearch.voidbase.quant.timeseries.SequenceGenerator;
import com.voidsearch.voidbase.apps.queuetree.client.QueueTreeClient;

import java.util.LinkedList;

/**
 * return delta on given queue
 */

public class QueueDelta extends NumericalSequence implements SequenceGenerator {

    private String queue = null;
    private int delta = 2;

    private static QueueTreeClient client = null;

    public QueueDelta(String queueName) {
        client = new QueueTreeClient();
        this.queue = queueName;
    }

    public QueueDelta(String queueName, String delta) {
        client = new QueueTreeClient();
        this.queue = queueName;
        this.delta = Integer.parseInt(delta);
    }

    public double next() {
        try {
            String response = client.get(queue,delta);
            LinkedList<String> values = client.getResultList(response);

            if (values.size() == delta) {

                double sum = 0;

                for (int i=0; i<values.size()-1; i++) {
                    sum += (Double.parseDouble(values.get(i)) - Double.parseDouble(values.get(i+1)));
                }

                return sum/(delta - 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
