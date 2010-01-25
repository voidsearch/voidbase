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

import com.voidsearch.voidbase.quant.timeseries.SequenceGenerator;
import com.voidsearch.voidbase.quant.timeseries.NumericalSequence;
import com.voidsearch.voidbase.client.SimpleHttpClient;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URLDecoder;

public class HttpRegexElement extends NumericalSequence implements SequenceGenerator {

    private static String OPEN_ELEMENT_TAG  = "<elem>";
    private static String CLOSE_ELEMENT_TAG = "</elem>";

    SimpleHttpClient client;
    String url;
    Pattern pattern;

    public HttpRegexElement(String requestUrl, String regex) {
        client = new SimpleHttpClient();
        this.url = requestUrl;

        regex = URLDecoder.decode(regex);
        regex = regex.replaceAll(OPEN_ELEMENT_TAG,"(");
        regex = regex.replaceAll(CLOSE_ELEMENT_TAG,")");

        System.out.println("REGEX : " + regex);
        
        pattern = Pattern.compile(regex);
    }

    public double next() {
        try {
            byte[] content = client.get(url);
            String data = new String(content);
            Matcher matcher = pattern.matcher(data);

            while (matcher.find()) {
                try {
                    Double value = Double.parseDouble(matcher.group(1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
