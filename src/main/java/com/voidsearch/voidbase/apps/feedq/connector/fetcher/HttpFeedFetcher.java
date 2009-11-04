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


package com.voidsearch.voidbase.apps.feedq.connector.fetcher;

import com.voidsearch.voidbase.apps.feedq.resource.FeedResource;
import com.voidsearch.voidbase.apps.feedq.resource.FeedResourceFactory;
import com.voidsearch.voidbase.client.VoidBaseHttpClient;


public class HttpFeedFetcher extends VoidBaseHttpClient implements FeedFetcher {

    /**
     * get default-size byte array from the given resource
     *
     * @param resource  resource to fetch
     * @return
     * @throws Exception
     */
    public byte[] fetchRaw(String resource) throws Exception {
        return fetchRaw(resource, Integer.MAX_VALUE);
    }

    /**
     * Http-specific tail reader - need to fetch entire content and return last size bytes
     *
     * @param resource resource to fetch
     * @param size buffer size to fetch
     * @return
     */

    public byte[] fetchRaw(String resource, int size) throws Exception {
        return get(resource);
    }

    /**
     *
     *
     * @param resource
     * @return
     * @throws Exception
     */
    public FeedResource fetch(String resourceName) throws Exception {
        byte[] rawContent = get(resourceName);
        return FeedResourceFactory.getResource(resourceName, rawContent);
    }



}
