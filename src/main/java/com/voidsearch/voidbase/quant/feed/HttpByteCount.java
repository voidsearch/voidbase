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

package com.voidsearch.voidbase.quant.feed;

import com.voidsearch.voidbase.client.SimpleHttpClient;
import com.voidsearch.voidbase.quant.timeseries.NumericalSequence;
import com.voidsearch.voidbase.quant.timeseries.SequenceGenerator;

public class HttpByteCount extends NumericalSequence implements SequenceGenerator {

    static SimpleHttpClient client = new SimpleHttpClient();
    String url;

    public HttpByteCount(String requestURL) {
        url = requestURL;
    }

    public double next() {
        try {
            byte[] result = client.get(url);
            return result.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
