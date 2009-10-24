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

package com.voidsearch.voidbase.apps.feedq.connector.fetcher;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

public class FilesystemFeedFetcher implements FeedFetcher {

    public byte[] fetch(String resource) throws Exception {
        return fetch(resource, Integer.MAX_VALUE);
    }

    /**
     * fetch a byte buffer from the end of given file
     *
     * @param resource resource to fetch
     * @param size buffer size to fetch
     * @return
     * @throws Exception
     */
    public byte[] fetch(String resource, int size) throws Exception {

        File file = new File(getPath(resource));

        InputStream is = new FileInputStream(file);
        long length = file.length();

        // not supporting large files -> todo: fork a different reeader here
        if (length > Integer.MAX_VALUE)
            throw new Exception();

        int bufferSize = (length > size) ? size : (int)length;
        byte[] data = new byte[bufferSize];

        int offset = (length > size) ? ((int)length - size - 1) : 0;

        is.skip(offset);
        is.read(data);
        is.close();

        return data;

    }


    public String getPath(String resource) {

        int pivot = resource.indexOf(ResourceType.RESOURCE_DELIMITER);
        if (pivot > 0) {
            return resource.substring(pivot + ResourceType.RESOURCE_DELIMITER.length() - 1);
        } else {
            return resource;
        }

    }
}
