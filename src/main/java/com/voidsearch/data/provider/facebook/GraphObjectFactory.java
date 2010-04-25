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

package com.voidsearch.data.provider.facebook;

import com.voidsearch.data.provider.facebook.objects.*;
import org.json.JSONObject;

public class GraphObjectFactory {

    /**
     * get object corresponding to given class, deserialized from response data
     * 
     * @param name
     * @param data
     * @return
     * @throws Exception
     */
    public static GraphObject getObject(String name, JSONObject data) throws Exception {
        
        if (name.equals("friends")) {
            return new FacebookUser(data);
        } else if (name.equals("likes")) {
            return new LikedEntry(data);
        } else if (name.equals("photos")) {
            return new PhotoEntry(data);
        } else if (name.equals("groups")) {
            return new GroupEntry(data);
        } else if (name.equals("home")) {
            return new NewsEntry(data);
        } else {
            throw new Exception();
        }

    }


}
