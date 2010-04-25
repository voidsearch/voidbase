/*
 * Copyright 2010 VoidSearch.com
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

package com.voidsearch.data.provider.facebook.objects;

import org.json.JSONObject;

/**
 * representing single graph-related object
 * 
 */

public abstract class GraphObject {

    protected String id;

    public GraphObject(JSONObject data) throws Exception {
        id = (String)data.get("id");
    }

    public String getID() {
        return id;
    }

}
