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


public class HttpFeedFetcher implements FeedFetcher {

    
    public byte[] fetch(String resource) throws Exception {
        return fetch(resource, Integer.MAX_VALUE);
    }

    /**
     * Http-specific tail reader - need to fetch entire content and return last size bytes
     *
     * @param resource resource to fetch
     * @param size buffer size to fetch
     * @return
     */

    public byte[] fetch(String resource, int size) throws Exception {
        return null;
    }

}
