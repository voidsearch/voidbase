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

package com.voidsearch.voidbase.apps.feedq.metric;

import com.voidsearch.voidbase.apps.feedq.resource.ResourceEntry;

import java.util.Iterator;
import java.util.LinkedList;

public class SimpleMetric implements ResourceMetric {

    /**
     * get simple difference between two resource lists
     * 
     * @param oldEntries
     * @param newEntries
     * @return
     */
    public LinkedList<ResourceEntry> getDelta(LinkedList<ResourceEntry> oldEntries,
                                              LinkedList<ResourceEntry> newEntries) {

        LinkedList<ResourceEntry> result = new LinkedList<ResourceEntry>();
        ResourceEntry lastEntry = oldEntries.getLast();

        Iterator it = newEntries.descendingIterator();
        while (it.hasNext()) {
            ResourceEntry entry = (ResourceEntry)it.next();
            if (!entry.equals(lastEntry)) {
                result.add(entry);
            } else {
                break;
            }
        }

        return result;

    }


}