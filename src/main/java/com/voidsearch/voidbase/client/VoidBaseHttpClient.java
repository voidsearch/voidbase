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

package com.voidsearch.voidbase.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class VoidBaseHttpClient implements VoidBaseClient {

    protected HttpClient client = new HttpClient();

    protected static String hostname;
    protected static String module;     // depreciate this

    public VoidBaseHttpClient() {
    }

    public VoidBaseHttpClient(String hostname, String module) {
        this.hostname = hostname;
        this.module = module;
    }

    protected byte[] get(VoidBaseQuery query) throws Exception {
        return get(query.getQuery());
    }

    public byte[] get(String query) throws Exception {

        GetMethod method = new GetMethod(query);
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(3, false));

        StringBuilder sb = new StringBuilder();

        // TODO : add a content dependent fetch

        try {
            int statusCode = client.executeMethod(method);

            if (statusCode == HttpStatus.SC_OK) {
                InputStreamReader is = new InputStreamReader(method.getResponseBodyAsStream());
                BufferedReader in = new BufferedReader(is);

                String line = null;
                while((line = in.readLine()) != null) {
                    sb.append(line);
                }
            }

        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }

        return (sb.toString()).getBytes();

    }

    protected void post(VoidBaseQuery query, String content) throws Exception {

        PostMethod post = new PostMethod(query.getQuery());
        post.setRequestBody(content);

        try {
            client.executeMethod(post);
            post.getResponseBodyAsStream(); // ignore response
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
    }

}
