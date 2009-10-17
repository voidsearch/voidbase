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

import java.io.IOException;

public abstract class VoidBaseHttpClient implements VoidBaseClient {

    protected HttpClient client = new HttpClient();

    protected byte[] get(VoidBaseQuery query) throws Exception {

        GetMethod method = new GetMethod(query.getQuery());
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(3, false));

        byte[] responseBody = null;

        try {
            int statusCode = client.executeMethod(method);

            if (statusCode == HttpStatus.SC_OK) {
                responseBody = method.getResponseBody();
            }

        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }

        return responseBody;

    }

    protected void post(VoidBaseQuery query, String content) throws Exception {

        System.out.println("POSTING : " + query.getQuery());

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
