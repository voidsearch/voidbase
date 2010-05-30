/*
 * Copyright 2010 VoidSearch.com
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

package com.voidsearch.voidbase.tools.storage.queuetree.persistence;

import com.voidsearch.voidbase.storage.queuetree.persistence.FilesystemQueuePersistenceReader;
import org.apache.avro.generic.GenericRecord;

import java.io.File;

public class QueuePersistenceDump {

    private String[] fieldList = null;
    private String outputFormat = "regular";

    /**
     * dump contents of avro-serialized queue data file
     * 
     * @param path
     * @throws Exception
     */
    public void dump(String path) throws Exception {

        File fp = new File(path);

        if (!fp.exists()) {
            System.out.println("invalid file : " + path);
        }

        FilesystemQueuePersistenceReader reader = new FilesystemQueuePersistenceReader(fp);

        while (reader.hasMore()) {
            GenericRecord rec = reader.next();
            if (fieldList != null) {
                for (String field : fieldList) {
                    System.out.print(rec.get(field));
                    System.out.print("\t");
                }
                System.out.println();
            } else {
                System.out.println(rec);
            }
        }

    }

    /**
     * set list of fields to be rendered
     * 
     * @param fields
     */
    public void setFieldFilter(String fields) {
        fieldList = fields.split(",");
    }

    public static void main(String[] args) throws Exception {
        
        if (args.length < 1) {
            System.out.println("usage: QueuePersistenceDump [persistence_file] {field_list} {format}");
            return;
        }

        QueuePersistenceDump dumper = new QueuePersistenceDump();

        if (args.length > 1) {
            dumper.setFieldFilter(args[1]);
        }
        
        dumper.dump(args[0]);

    }


}
