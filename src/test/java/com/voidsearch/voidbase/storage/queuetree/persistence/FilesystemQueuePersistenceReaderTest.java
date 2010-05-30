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

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.JsonDecoder;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;

public class FilesystemQueuePersistenceReaderTest {

    String inputFile = "data/queue/persistence/_testqueue.avro";

    @Test
    public void nullTest() {
        try {
            Schema s = Schema.parse(FilesystemQueuePersistence.getSchema());
            GenericDatumReader<GenericRecord> r = new GenericDatumReader<GenericRecord>(s);
            Decoder decoder = new JsonDecoder(s, new FileInputStream(new File(inputFile)));
            while (true) {
                GenericRecord rec = (GenericRecord) r.read(null, decoder);
                if (s.equals(rec.getSchema())) {
                    System.out.println(rec);
                    // handle regular fields
                } else {
                    // handle differences
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
