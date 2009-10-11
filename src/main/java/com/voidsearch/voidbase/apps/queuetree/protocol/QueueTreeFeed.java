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

package com.voidsearch.voidbase.apps.queuetree.protocol;

public class QueueTreeFeed {

    public static final char[] XML_HEADER       = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n".toCharArray();
    public static final char[] FEED_START       = "<queue>\r\n".toCharArray();
    public static final char[] FEED_END         = "</queue>\r\n".toCharArray();
    public static final char[] HEADER_START     = "<header>\r\n".toCharArray();
    public static final char[] HEADER_END       = "</header>\r\n".toCharArray();
    public static final char[] RESPONSE_START   = "<response>\r\n".toCharArray();
    public static final char[] RESPONSE_END     = "</response>\r\n".toCharArray();
      

}
