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

package com.voidsearch.voidbase.apps.webapi;

import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.core.VoidBaseResourceRegister;
import com.voidsearch.voidbase.apps.queuetree.module.QueueTreeModule;

public class WebAPIQueueTreeHandler implements WebAPIHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    String JSONResponse = null;
    String parsedURI = "";

    public String handle(VoidBaseModuleRequest request) {

        return executeQuery(request);
    }

    public String executeQuery(VoidBaseModuleRequest request) {
        try {

            QueueTreeModule queuetree =(QueueTreeModule) VoidBaseResourceRegister.getInstance().getHandler("queuetree");
            String xmlResponse=queuetree.buildResponse(request);
            //cast XML response to JSON string
            JSONResponse = XML.toJSONObject(xmlResponse).toString();

        } catch (Exception e) { 
            System.err.println("Fatal error: " + e.getMessage());
        }
        return JSONResponse;
    }

}
