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

package com.voidsearch.voidbase.module;

public enum VoidBaseResponseType {
    XML,
    TEXT,
    HTML,
    JS,
    CSS,
    JPG,
    JSON,
    UNKNOWN;

    public static VoidBaseResponseType deserialize(String type) {

        if (type == null)
            return UNKNOWN;
        if (type.toUpperCase().equals("XML"))
            return XML;
        if (type.toUpperCase().equals("TEXT"))
            return TEXT;
        if (type.toUpperCase().equals("HTML"))
            return HTML;
        if (type.toUpperCase().equals("JS"))
            return JS;
        if (type.toUpperCase().equals("CSS"))
            return CSS;
        if (type.toUpperCase().equals("JPG"))
            return CSS;
        if (type.toUpperCase().equals("JSON"))
            return JSON;

        return UNKNOWN;
    }

    public String serialize() {
        return this.toString();
    }

    public static VoidBaseResponseType deserializeFromHttpContentType(String contentType) {
        if (contentType == null)
            return UNKNOWN;
        if (contentType.equals("text/xml"))
            return XML;
        if (contentType.equals("text/plain"))
            return TEXT;
        if (contentType.equals("text/html"))
            return HTML;
        if (contentType.equals("application/x-javascript"))
            return JS;
        if (contentType.equals("text/css"))
            return CSS;
        if (contentType.equals("image/jpeg"))
            return JPG;
        if (contentType.equals("application/json"))
            return JSON;
    
        return UNKNOWN;
    }

    public String serializeToHttpContentType() {
        if (this == XML)
            return "text/xml";
        if (this == TEXT)
            return "text/plain";
        if (this == HTML)
            return "text/html";
        if (this == JS)
            return "application/x-javascript";
        if (this == CSS)
            return "text/css";
        if (this == JPG)
            return "image/jpeg";
        if (this == JSON)
            return "application/json";

        return null;
    }
}
