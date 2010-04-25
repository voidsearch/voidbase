package com.voidsearch.data.provider.facebook.objects;

import org.json.JSONObject;

/**
 * @author Aleksandar Bradic
 *         (c) Vast.com, Apr 25, 2010
 */

public class NewsEntry extends GraphObject {

    public String createdTime;
    public String updatedTime;

    public NewsEntry(JSONObject data) throws Exception {
        super(data);
        createdTime = (String)data.get("created_time");
        updatedTime = (String)data.get("updated_time");
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    
}
