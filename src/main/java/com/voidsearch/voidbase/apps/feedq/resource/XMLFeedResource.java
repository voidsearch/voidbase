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
import com.voidsearch.voidbase.apps.feedq.metric.SimpleMetric;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public abstract class XMLFeedResource implements FeedResource {

    public XMLFeedResource() {
        
    }

    protected Document xmlDocument;

    protected XMLFeedResource(byte[] content) {
        System.out.println("INIT FROM CONTENT ");
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            xmlDocument = db.parse(new ByteArrayInputStream(content));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getDelta(FeedResource resource) {
        return getDelta(resource, new SimpleMetric());
    }
    
}