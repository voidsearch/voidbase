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

package com.voidsearch.voidbase.protocol;

import java.util.HashMap;
import java.util.Map;

public class VoidBaseOperationType implements Comparable {
    protected String name;
    protected static HashMap<String, VoidBaseOperationType> values = new HashMap<String, VoidBaseOperationType>();

    public VoidBaseOperationType() { }

    public VoidBaseOperationType(String name) {
        this.name = name;

        if (!values.containsKey(name)) {
            values.put(name.toUpperCase(), this);
        }
    }

    public static final VoidBaseOperationType PUT;
    public static final VoidBaseOperationType GET;
    public static final VoidBaseOperationType ADD;
    public static final VoidBaseOperationType DELETE;
    public static final VoidBaseOperationType FLUSH;
    public static final VoidBaseOperationType LIST;
    public static final VoidBaseOperationType RESIZE;
    public static final VoidBaseOperationType STAT;
    public static final VoidBaseOperationType UNKNOWN;

    static {
        PUT     = new VoidBaseOperationType("PUT");
        GET     = new VoidBaseOperationType("GET");
        ADD     = new VoidBaseOperationType("ADD");
        DELETE  = new VoidBaseOperationType("DELETE");
        FLUSH   = new VoidBaseOperationType("FLUSH");
        LIST    = new VoidBaseOperationType("LIST");
        RESIZE  = new VoidBaseOperationType("RESIZE");
        STAT    = new VoidBaseOperationType("STAT");
        UNKNOWN = new VoidBaseOperationType("UNKNOWN");
    }

    public static VoidBaseOperationType deserialize(String code) {
        if (code == null)
            return null;

        return values.get(code.toUpperCase());
    }

    public String serialize(VoidBaseOperationType type) {
        if (type == null)
            return null;

        return type.name;
    }

    public String getName() {
        return name;
    }

    public int compareTo(Object obj) throws ClassCastException {
        if (!(obj instanceof VoidBaseOperationType))
            throw new ClassCastException("A Person object expected.");

        if (name.equals(obj))
            return 0;

        return -1;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
			return false;

        VoidBaseOperationType type = (VoidBaseOperationType)obj;

        return name.equals(type.name);
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return name;
    }

    public static String inspect() {
        StringBuilder content = new StringBuilder();

        for (Map.Entry<String, VoidBaseOperationType> value: values.entrySet()) {
            if (content.length() > 0) {
                content . append(", ");
            }

            content . append(value.getKey()) . append("=") . append(value.getValue());
        }

        return content.toString();
    }
}