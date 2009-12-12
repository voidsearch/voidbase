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

package com.voidsearch.voidbase.storage.queuetree;

import com.voidsearch.voidbase.apps.queuetree.protocol.QueueTreeProtocol;
import com.voidsearch.voidbase.serialization.VoidBaseSerialization;

import java.util.LinkedHashMap;

public class QueueMetadata {

    private LinkedHashMap<String, Object> metadata = new LinkedHashMap<String,Object>();
    private VoidBaseSerialization type = VoidBaseSerialization.UNKNOWN;

    public QueueMetadata(QueueMetadata other) {
        setType(other.getType());
        setMetadata((LinkedHashMap<String,Object>)other.getMetadata().clone());
    }

    public QueueMetadata(String name, int size) {
        set(QueueTreeProtocol.NAME,name);
        set(QueueTreeProtocol.TIMESTAMP,System.currentTimeMillis());
        set(QueueTreeProtocol.SIZE,size);
    }

    public void setType(VoidBaseSerialization type) {
        this.type = type;
    }

    public VoidBaseSerialization getType() {
        return type;
    }

    public void set(String key, Object value) {
        metadata.put(key,value);
    }

    public Object get(String key) {
        return metadata.get(key);
    }

    public boolean contains(String key) {
        return metadata.containsKey(key);
    }

    public void setMetadata(LinkedHashMap<String,Object> metadata) {
        this.metadata = metadata;
    }

    public LinkedHashMap<String, Object> getMetadata() {
        return metadata;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : metadata.keySet()) {
            sb.append("<").append(key).append(">").append(metadata.get(key)).append("</").append(key).append(">\n");
        }
        return sb.toString();
    }

}
