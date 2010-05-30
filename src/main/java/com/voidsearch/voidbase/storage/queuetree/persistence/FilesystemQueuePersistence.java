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

package com.voidsearch.voidbase.storage.queuetree.persistence;

import com.voidsearch.voidbase.storage.queuetree.QueueEntry;
import com.voidsearch.voidbase.storage.queuetree.QueueMetadata;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.JsonEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FilesystemQueuePersistence implements QueuePersistence {

    private static String PERSISTENCE_PATH = "data/queue/persistence/";

    private static String schemaDescription =
            " {    \n" +
                    " \"name\": \"QueueEntry\", \n" +
                    " \"type\": \"record\",\n" +
                    " \"fields\": [\n" +
                    "   {\"name\": \"queueName\", \"type\": \"string\"},\n" +
                    "   {\"name\": \"timestamp\", \"type\": \"long\"},\n" +
                    "   {\"name\": \"queueEntry\", \"type\": \"string\"},\n" +
                    "   {\"name\": \"metadataEntry\", \"type\": \"string\"} ]\n" +
                    "}";

    Schema s;
    GenericDatumWriter w;
    Encoder e;

    public FilesystemQueuePersistence(String queueName) {

        try {

            s = Schema.parse(schemaDescription);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            w = new GenericDatumWriter(s);
            e = new JsonEncoder(s, bao);
            e.init(new FileOutputStream(new File(PERSISTENCE_PATH + queueName + ".avro")));
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getSchema() {
        return schemaDescription;
    }

    public void add(String queueName, QueueEntry queueEntry, QueueMetadata metadataEntry) throws Exception {

        GenericRecord r = new GenericData.Record(s);

        r.put("queueName",new org.apache.avro.util.Utf8(queueName));
        r.put("queueEntry",new org.apache.avro.util.Utf8(queueEntry.toString()));
        r.put("metadataEntry", new org.apache.avro.util.Utf8(metadataEntry.toString()));
        r.put("timestamp", System.currentTimeMillis());

        w.write(r, e);
        e.flush();
        
    }


}
