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
import com.voidsearch.voidbase.client.SimpleHttpClient;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.DataInputStream;
import java.io.ByteArrayInputStream;

public class SimpleXMLFeedElement implements SequenceGenerator {

    SimpleHttpClient client;
    String url;
    String xmlPath;

    public SimpleXMLFeedElement(String requestUrl, String elementPath) {

        client = new SimpleHttpClient();
        url = requestUrl;
        xmlPath = elementPath;
    }

    public double next() {
        try {
            byte[] content = client.get(url);
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(new DataInputStream(new ByteArrayInputStream(content)));
            XPath xpath = XPathFactory.newInstance().newXPath();

            StringBuilder xpathExpression = new StringBuilder();
            xpathExpression.append("//");

            String[] parts = xmlPath.split("\\.");

            for (String part : parts) {
                xpathExpression.append(part).append("/");
            }
            xpathExpression.append("text()");

            XPathExpression expr = xpath.compile(xpathExpression.toString());

            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;

            if (nodes.getLength() == 1) {
                return Double.parseDouble(nodes.item(0).getNodeValue());
            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
