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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

import com.voidsearch.voidbase.module.VoidBaseModuleRequest;

public class WebAPIVoidBaseHandler implements WebAPIHandler{

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    String parsedURI="";

    public String handle(VoidBaseModuleRequest request){
        try{
            //  mapping scheme
            //  /webapi/voidbase/* action */ ? params ?

            logger.info("");
            String action = request.getRoute(request.getURI()).get(2);

        }catch (Exception e){
            //
        }
        return "test";
    }


     private String parseURI(String uri) {
        parsedURI = "";
        try {
            ArrayList<String> tmp = new ArrayList<String>(Arrays.asList(uri.split("\\?")));
            parsedURI = "?" + tmp.get(1);
            //parsedURI=URLEncoder.encode(parsedURI,"UTF-8");

        } catch (Exception e) {
            logger.info("error when parsing uri");
        }

        return parsedURI;
    }
}
