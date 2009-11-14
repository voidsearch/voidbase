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

import com.voidsearch.voidbase.module.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.voidsearch.voidbase.config.VoidBaseConfiguration;
import com.voidsearch.voidbase.config.Config;

import java.io.*;
import java.util.*;



public class WebAPIModule implements VoidBaseModule {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String jsConfigPath="./src/webapp/web/js/Config.js"; //@todo put this into config

    public void initialize(String name) throws VoidBaseModuleException {
        generateJavascriptConfig();
    }

    public VoidBaseModuleResponse handle(VoidBaseModuleRequest request) throws VoidBaseModuleException {
        //##    mapping scheme:
        //##    /webapi/ * handlerResource * / ? params ?

        String JSONResponse = null;
        String uri = request.getURI();

        try {
            String handlerResource = request.getRoute(uri).get(1);
            WebAPIHandler responseHandler =  WebAPIProtocol.getHandlerClass(handlerResource);
            JSONResponse = responseHandler.handle(request);

        } catch (IndexOutOfBoundsException e) {
            logger.error("invalid URI");
            JSONResponse = " {\"errorMessage\":\"URI does not contains resource handler\"} ";
        } catch (UnsupportedOperationException e) {
            logger.error("invalid handler resource");
            String handlerResource = request.getRoute(uri).get(1);
            JSONResponse = " {\"errorMessage\":\"invalid handler resource detected in URI: " + handlerResource + "  \"} ";
        } catch (WebAPIException e) {
            logger.error(e.getMessage());
        }

        return new VoidBaseModuleResponse(JSONResponse, VoidBaseResponseStatus.OK, VoidBaseResponseType.JSON);
    }

    public void run() {
    }

    private void generateJavascriptConfig(){

        String port = VoidBaseConfiguration.get(Config.GLOBAL, Config.DISPATCHER, Config.PORT);

        StringBuilder sb=new StringBuilder();
        sb.append("VOIDSEARCH.VoidBase.Config=function(){\n")
                .append("\treturn{\n")
                .append("\t\thostName:'localhost',\n")  //@todo add hostname  to config
                .append("\t\tprotocol:'http://',\n")    //@todo add protocol  to config
                .append("\t\tport:").append(port).append("\n")
                .append("\t}\n")
                .append("}();");

        try {
			BufferedWriter out = new BufferedWriter(new FileWriter(jsConfigPath));
            out.write(sb.toString());
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
