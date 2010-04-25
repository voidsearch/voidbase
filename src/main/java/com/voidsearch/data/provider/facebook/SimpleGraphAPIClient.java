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
import com.voidsearch.voidbase.client.VoidBaseHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.LinkedList;

    
public class SimpleGraphAPIClient extends VoidBaseHttpClient {

    private static String GRAPH_API_HOST = "https://graph.facebook.com/";
    private static String ACCESS_TOKEN_PARAM = "access_token";
    private static long   DEFAULT_QUERY_DELAY = 5000;

    private String accessToken;
    private long   queryDelay;

    private long   queryTimer = System.currentTimeMillis(); 
    
    public SimpleGraphAPIClient(String accessToken, long queryDelay) {
        this.accessToken = accessToken;
        this.queryDelay  = queryDelay;
    }

    public SimpleGraphAPIClient(String accessToken) {
        this.accessToken = accessToken;
        this.queryDelay  = DEFAULT_QUERY_DELAY;
    }

    /**
     * set millisecond delay between queries 
     *
     * @param delayTime
     */
    public void setQueryDelay(long delayTime) {
        this.queryDelay = delayTime;
    }

    /**
     * get list of friends for current authenticated user
     *
     * @return
     * @throws Exception
     */
    public LinkedList<FacebookUser> getFriends() throws Exception {
        return getFriends("me");
    }

    /**
     * get list of friends corresponding to specific user
     *
     * @param user
     * @return
     * @throws Exception
     */
    public LinkedList<FacebookUser> getFriends(String user) throws Exception {
        return this.<FacebookUser>getResultList(user,"friends");
    }

    /**
     * get all likes for particular user
     * 
     * @param user
     * @return
     * @throws Exception
     */
    public LinkedList<LikedEntry> getLikes(String user) throws Exception {
        return this.<LikedEntry>getResultList(user,"likes");
    }

    /**
     * get all photos for single user
     *
     * @param user
     * @return
     * @throws Exception
     */
    public LinkedList<PhotoEntry> getPhotos(String user) throws Exception {
        return this.<PhotoEntry>getResultList(user,"photos");
    }

    /**
     * get all groups for single user
     *
     * @param user
     * @return
     * @throws Exception
     */
    public LinkedList<GroupEntry> getGroups(String user) throws Exception {
        return this.<GroupEntry>getResultList(user,"groups");
    }

    /**
     * get all news events for single user
     *
     * @param user
     * @return
     * @throws Exception
     */
    public LinkedList<NewsEntry> getNews(String user) throws Exception {
        return this.<NewsEntry>getResultList(user,"home");
    }

    /**
     * get paginated list of results
     *
     * @param user
     * @param feedName
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> LinkedList<T> getResultList(String user, String feedName) throws Exception {

        LinkedList<T> results = new LinkedList<T>();
        String query = GRAPH_API_HOST + user + "/" + feedName;

        boolean hasMore = true;

        while(hasMore == true) {

            JSONObject response = getResponse(query);
            JSONArray resultArray = response.getJSONArray("data");

            loadResults(results, resultArray, feedName);

            if (hasMore(response)) {
                String nextQuery = getNextPageURL(response);
                if (!nextQuery.equals(query)) {
                    query = nextQuery;
                } else {
                    hasMore = false;                    
                }
            } else {
                hasMore = false;
            }

        }

        return results;
    }

    /**
     * load result list from json response for given feed
     *
     * @param results
     * @param resultArray
     * @param feedName
     * @param <T>
     * @throws Exception
     */
    private <T> void loadResults(LinkedList<T> results, JSONArray resultArray, String feedName) throws Exception {
        for (int i = 0; i < resultArray.length(); i++) {
            results.add((T)GraphObjectFactory.getObject(feedName, (JSONObject)resultArray.get(i)));
        }

    }

    /**
     * get "data" entry fron JSON response
     *
     * @param query
     * @return
     * @throws Exception
     */
    private JSONArray getDataArray(String query) throws Exception {
        JSONObject response = getResponse(query);
        return response.getJSONArray("data");
    }

    /**
     * get response from Graph API
     *
     * @param query
     * @return
     * @throws Exception
     */
    private JSONObject getResponse(String query) throws Exception {
        if (!(query.contains("?") && query.contains(ACCESS_TOKEN_PARAM))) {
            query += "?" + ACCESS_TOKEN_PARAM + "=" + URLEncoder.encode(accessToken);
        }

        // force query delay interval
        long lastQueryElapsed = getQueryDelay();
        if (lastQueryElapsed < queryDelay) {
            Thread.sleep(queryDelay - lastQueryElapsed);
        }
        resetQueryTimer();
        
        byte[] response = get(query);
        return new JSONObject(new JSONTokener(new StringReader(new String(new String(response)))));
    }

    /**
     * get elapsed time since last query
     *
     * @return
     */
    private long getQueryDelay() {
       return System.currentTimeMillis() - queryTimer; 
    }

    /**
     * reset internal query delay counter
     *
     */
    private void resetQueryTimer() {
        queryTimer = System.currentTimeMillis();
    }

    /**
     * check whether return object has pagination
     *
     * @param object
     * @return
     */
    private boolean hasMore(JSONObject object) throws Exception {
        return object.has("paging");
    }

    /**
     * get next page url
     *
     * @param object
     * @return
     * @throws Exception
     */
    private String getNextPageURL(JSONObject object) throws Exception {
       JSONObject paging = (JSONObject)object.get("paging");
       return (String)paging.get("next");
    }

}
