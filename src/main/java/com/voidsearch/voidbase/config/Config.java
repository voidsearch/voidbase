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

package com.voidsearch.voidbase.config;

public class Config {

    // top-level domains
    public static String GLOBAL  = "global";
    public static String MODULES = "modules";
    public static String STORAGE = "storage";

    // global

    public static String DISPATCHER    = "HttpRequestDispatcher";
    public static String REQUEST_QUEUE = "VoidBaseRequestQueue";


    // modules

    public static String STATUS = "status";
    public static String ACTIVE = "active";
    public static String CLASS  = "class";
    public static String RESOURCE_URI = "resource";
    public static String MAX_REQUESTS = "maxRequests";

    public static String CONTENT_ROOT       = "contentRoot";
    public static String NOT_FOUND          = "fileNotFoundResponse";
    public static String DIRECTORY_INDEX    = "directoryIndex";

    // common variables

    public static String PORT   = "port";
    public static String LOG_FILE   = "log_file";
    

}
