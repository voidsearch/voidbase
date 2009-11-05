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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.util.LinkedList;

public abstract class XMLFeedResource implements FeedResource {

    private LinkedList<ResourceEntry> resourceEntries = new LinkedList<ResourceEntry>();
    XMLInputFactory factory = XMLInputFactory.newInstance();

    private String ENTRY_DELIMITER = "entry";

    protected XMLFeedResource(byte[] content) {
        try {
            deserialize(content);
        } catch (ResourceDeserializationException e) {
        }
    }

    protected XMLFeedResource(byte[] content, String entryDelimiter) {
        try {
            setDelimiter(entryDelimiter);
            deserialize(content);
        } catch (ResourceDeserializationException e) {
        }
    }

    protected void setDelimiter(String delimiter) {
        this.ENTRY_DELIMITER = delimiter;
    }

    /**
     * deserialize via simple stax pull loop
     * 
     * @param content
     * @throws ResourceDeserializationException
     */
    public void deserialize(byte[] content) throws ResourceDeserializationException {
        try {

            XMLStreamReader parser = factory.createXMLStreamReader(new ByteArrayInputStream(content));

            String currentTagValue = "";
            ResourceEntry currentEntry = new ResourceEntry();

            for (int event = parser.next();
                event != XMLStreamConstants.END_DOCUMENT; event = parser.next()) {
                switch (event) {
                    case XMLStreamConstants.END_DOCUMENT:
                        parser.close();
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        currentTagValue = parser.getText();
                        break;                    
                    case XMLStreamConstants.START_ELEMENT:
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        currentEntry.put(parser.getLocalName(), currentTagValue);
                        if (parser.getLocalName().equals(ENTRY_DELIMITER)) {
                            resourceEntries.add(currentEntry);
                            currentEntry = new ResourceEntry();
                        }
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceDeserializationException();
        }
    }


    public LinkedList<ResourceEntry> getEntries() {
        return resourceEntries;
    }

    public LinkedList<ResourceEntry> getDelta(FeedResource resource) {
        return getDelta(resource, new SimpleMetric());
    }

    public LinkedList<ResourceEntry> getDelta(FeedResource resource,ResourceMetric metric) {
        return metric.getDelta(resourceEntries, resource.getEntries());
    }


}