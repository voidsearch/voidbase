/*
 * Copyright 2009 VoidSearch.com
 *
 * Licensed under the Apac0he License, Version 2.0 (the "License"); you may not
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

package com.voidsearch.voidbase.apps.feedq.metric;

import com.voidsearch.voidbase.apps.feedq.resource.ResourceEntry;

import java.util.LinkedList;

public interface ResourceMetric {


    /**
     * get difference between two lists of entries
     *
     * @param oldEntries
     * @param newEntries
     * @return
     */
    public LinkedList<ResourceEntry> getDelta(LinkedList<ResourceEntry> oldEntries,
                                              LinkedList<ResourceEntry> newEntries);


}
