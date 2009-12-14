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

package com.voidsearch.voidbase.apps.streams;

import com.voidsearch.voidbase.module.VoidBaseModule;
import com.voidsearch.voidbase.module.VoidBaseModuleException;
import com.voidsearch.voidbase.module.VoidBaseModuleResponse;
import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.apps.queuetree.module.QueueTreeModule;
import com.voidsearch.voidbase.core.VoidBaseResourceRegister;
import com.voidsearch.voidbase.quant.timeseries.*;

import java.lang.StringBuilder;
import java.lang.Double;

public class StreamsModule extends Thread implements VoidBaseModule {

    VoidBaseResourceRegister resourceRegister = VoidBaseResourceRegister.getInstance();
    private QueueTreeModule queue;

    public void initialize(String name) throws VoidBaseModuleException {
    }

    public VoidBaseModuleResponse handle(VoidBaseModuleRequest request) throws VoidBaseModuleException {
        return new VoidBaseModuleResponse("StreamsModule");
    }

    public void run() {

        try {
            queue = (QueueTreeModule) resourceRegister.getHandlerBlocking("queuetree");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ComplexSequence generator = new ComplexSequence();
//        generator.addGenerator(new LinearSeries());
//        generator.addGenerator(new GaussianSequence(0,1));
        generator.addGenerator(new RandomWalk());

        while(true) {

            Double nextEntry = generator.next();
            StringBuilder sb = new StringBuilder();
            
            System.out.println(nextEntry);
            sb.append("<series>").append(nextEntry).append("</series>");

            try {
                if (!queue.queueExists("test_series")) {
                    queue.createQueue("test_series", 100);
                    queue.createQueue("test_series_metadata", 100);
                }
                queue.insertToQueue("test_series", nextEntry.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {}
        }

    }

    


}
