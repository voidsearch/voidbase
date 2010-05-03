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
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.JsonEncoder;
import org.testng.annotations.Test;

import java.io.*;
import java.util.Random;

public class SimpleAvroPerformanceTest {

    static Random rnd = new Random();
    static final String VALID_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Test
    public void nullTest() throws Exception {

        try {
            FileWriter fstream = new FileWriter("testresults.csv");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("num_entries, json_serialized, binary_serialized\n");
            for (int i=100; i<10000; i+=100) {
                //getSerializationSize(i, 4096, 64, out);
                getSerializationSize(i, Integer.MAX_VALUE, 1024, out);
            }
            out.write("\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get size for binary and json serialization 
     *
     * @param num_entries
     * @param maxIntValue
     * @param stringSize
     * @param out
     * @throws Exception
     */
    public void getSerializationSize(int num_entries, int maxIntValue, int stringSize, Writer out) throws Exception {

        Schema s = Schema.parse(new File("src/data/avro/sample/fb_user.avpr"));
        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        GenericDatumWriter<GenericRecord> w = new GenericDatumWriter<GenericRecord>(s);
        Encoder jsonEncoder = new JsonEncoder(s, bao);
        Encoder binaryEncoder = new BinaryEncoder(bao);

        jsonEncoder.init(new FileOutputStream(new File("test_data_json.avro")));
        binaryEncoder.init(new FileOutputStream(new File("test_data_binary.avro")));


        for (int i=0; i<num_entries; i++) {
            GenericRecord r = new GenericData.Record(s);
            r.put("name", new org.apache.avro.util.Utf8(getRandomString(stringSize)));
            r.put("num_likes", rnd.nextInt(maxIntValue));
            r.put("num_photos", rnd.nextInt(maxIntValue));
            r.put("num_groups", rnd.nextInt(maxIntValue));
            w.write(r, jsonEncoder);
            w.write(r, binaryEncoder);
        }

        jsonEncoder.flush();
        binaryEncoder.flush();

        File jStat = new File("test_data_json.avro");
        File bStat = new File("test_data_binary.avro");
        out.write(num_entries + "," + jStat.length() + "," + bStat.length() + "\n");
        System.out.println(num_entries + "," + jStat.length() + "," + bStat.length() + " = " + (double)jStat.length()/(double)bStat.length() + " x");

    }

    /**
     * get random string of specified size
     *
     * @param len
     * @return
     */
    public String getRandomString( int len )
    {
      StringBuilder sb = new StringBuilder(len);
      for( int i = 0; i < len; i++ ) {
          sb.append(VALID_CHARS.charAt(rnd.nextInt(VALID_CHARS.length())));
      }
      return sb.toString();
    }


}
