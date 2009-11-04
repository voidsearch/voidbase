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

package com.voidsearch.voidbase.apps.feedq.resource;

import com.voidsearch.voidbase.apps.feedq.metric.ResourceMetric;

public interface FeedResource {

    /**
     * create a object instance from content
     * @param content
     * @return
     */
    public FeedResource deserialize(byte[] content);

    /**
     * get delta from other resource using default metric
     *
     * @param resource
     * @return
     */
    public int getDelta(FeedResource resource);


    /**
     * get delta from given resource using given metric
     * 
     * @param resource
     * @param metric
     * @return
     */
    public int getDelta(FeedResource resource, ResourceMetric metric);

}