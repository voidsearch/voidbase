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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;

public class FilesystemQueuePersistenceReader {

    private Schema s;
    private GenericDatumReader<GenericRecord> r;
    private Decoder decoder;

    GenericRecord nextEntry = null;

    public FilesystemQueuePersistenceReader(File path) throws Exception {

        s = Schema.parse(FilesystemQueuePersistence.getSchema());
        r = new GenericDatumReader<GenericRecord>(s);
        decoder = new JsonDecoder(s, new FileInputStream(path));

    }

    /**
     * returns true if persistence iterator contains more data
     *
     * @return
     */
    public boolean hasMore() {
        try {
        GenericRecord rec = (GenericRecord) r.read(null, decoder);
        if (s.equals(rec.getSchema())) {
            nextEntry = rec;
        } else {
            nextEntry = null;
        }
        } catch (EOFException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * get next persistence entry
     *
     * @return
     * @throws Exception
     */
    public GenericRecord next() throws Exception {
        return nextEntry;
    }

}
