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

package com.voidsearch.data.avro.simple;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.io.JsonEncoder;
import org.testng.annotations.Test;

import java.io.*;

public class SimpleAvroSerializationTest {

    @Test
    public void nullTest() {

        try {

            Schema s = Schema.parse(new File("src/data/avro/sample/fb_user.avpr"));
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            GenericDatumWriter<GenericRecord> w = new GenericDatumWriter<GenericRecord>(s);
            Encoder jsonEncoder = new JsonEncoder(s, bao);
            jsonEncoder.init(new FileOutputStream(new File("test_data_json.avro")));

            GenericRecord rec = new GenericData.Record(s);
            rec.put("name", new org.apache.avro.util.Utf8("test"));
            rec.put("num_likes", 1);
            rec.put("num_photos", 1);
            rec.put("num_groups", 1);
            w.write(rec, jsonEncoder);
            jsonEncoder.flush();

            System.out.println("READING !");

            GenericDatumReader<GenericRecord> r = new GenericDatumReader<GenericRecord>(s);
            Decoder decoder = new JsonDecoder(s, new FileInputStream(new File("test_data_json.avro")));
            GenericRecord rr = (GenericRecord)r.read(null, decoder);
            Schema sch = rr.getSchema();
            if (s.equals(sch)) {
                //
            } else {
                // handle differences
            }

            System.out.println(rr);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
