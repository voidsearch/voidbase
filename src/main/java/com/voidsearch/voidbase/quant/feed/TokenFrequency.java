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

import com.voidsearch.voidbase.apps.queuetree.client.QueueTreeClient;
import com.voidsearch.voidbase.client.SimpleHttpClient;
import com.voidsearch.voidbase.quant.timeseries.NumericalSequence;
import com.voidsearch.voidbase.quant.timeseries.SequenceGenerator;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * tracks token frequency on underlying stream data
 */

public class TokenFrequency extends NumericalSequence implements SequenceGenerator {

    private int MAX_TOKENS_LIMIT = 20;

    private SimpleHttpClient client;
    private String url;

    private HashMap<String, Integer> termFreq;
    private LinkedList<TokenEntry> topTerms;

    private HashMap<String, Integer> currentFreq;

    private QueueTreeClient queueClient = new QueueTreeClient();

    String tokenDelimiter = "\t";

    public class TokenEntry implements Comparable {

        String token;
        Integer count = 0;

        public TokenEntry(String token, int count) {
            this.token = token;
            this.count = count;
        }

        public void increment() {
            count++;
        }

        public String getToken() {
            return token;
        }

        public Integer getCount() {
            return count;
        }

        public boolean equals(Object o1) {
            return token.equals(((TokenEntry) o1).getToken());
        }

        public int compareTo(Object o1) {
            return count.compareTo(((TokenEntry) o1).getCount());
        }

        public String toString() {
            return token + "\t" + count;
        }

        public int hashCode() {
            return token.hashCode();
        }
    }

    public TokenFrequency(String requestURL) {
        init(requestURL);
    }

    public TokenFrequency(String requestURL, String tokenDelimiter) {
        this.tokenDelimiter = tokenDelimiter;
        init(requestURL);
    }

    private void init(String requestURL) {
        try {
            client = new SimpleHttpClient();
            url = requestURL;
            termFreq = new HashMap<String, Integer>();
            topTerms = new LinkedList<TokenEntry>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * generate next sequence element
     *
     * @return
     */
    public double next() {
        try {
            dumpTopTerms();
            byte[] result = client.get(url);
            StringTokenizer tokenizer = new StringTokenizer(new String(result));
            int tokenCount = 0;
            currentFreq = new HashMap<String, Integer>();

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (isValidToken(token)) {

                    int count = 1;
                    if (termFreq.containsKey(token)) {
                        count = (Integer) termFreq.get(token) + 1;
                    }
                    termFreq.put(token, count);
                    topTerms.add(new TokenEntry(token, count));
                    tokenCount++;

                    // update local
                    if (currentFreq.containsKey(token)) {
                        currentFreq.put(token, currentFreq.get(token) + 1);
                    } else {
                        currentFreq.put(token, 1);
                    }
                }
            }
            return tokenCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * cleanup frequency tree
     * invoked periodically on next() call
     */
    private void cleanupTree() {

    }

    private void dumpTopTerms() throws Exception {

        int termCnt = 0;

        Collections.sort(topTerms);
        HashSet<String> displaySet = new HashSet<String>();

        for (int i = topTerms.size() - 1; i >= 0; i--) {

            TokenEntry entry = topTerms.get(i);
            if (!displaySet.contains(entry.getToken())) {

                if (termCnt++ <= MAX_TOKENS_LIMIT) {

                    System.out.println(entry + "\t" + currentFreq.get(entry.getToken()));

                    // handle queue entry
                    Integer freq = 0;
                    if (currentFreq.containsKey(entry.getToken())) {
                        freq = currentFreq.get(entry.getToken());
                    }

                    queueClient.create(entry.getToken(), 1000);
                    queueClient.put(entry.getToken(), freq.toString());

                    displaySet.add(entry.getToken());

                } else {

                    // cleanup non-active queues
                    queueClient.delete(entry.getToken());

                }
            }
        }

    }

    /**
     * check whether token is valid
     *
     * @param token
     * @return
     */
    private boolean isValidToken(String token) {
        // filter empty strings
        if (token.isEmpty()) {
            return false;
        }
        // filter html tags
        if (token.contains(">") || token.contains("<")) {
            return false;
        }

        // filter min lenght
        if (token.length() < 5) {
            return false;
        }

        if (!StringUtils.isAlpha(token)) {
            return false;
        }

        return true;
    }

}