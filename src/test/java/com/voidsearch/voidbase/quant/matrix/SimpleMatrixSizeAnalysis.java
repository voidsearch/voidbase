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

package com.voidsearch.voidbase.quant.matrix;

import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;

public class SimpleMatrixSizeAnalysis {

    private long MATRIX_ENTRY_SIZE = 64;    // 64bit entry

    @Test
    public void nullTest() throws Exception {
        FileWriter writer = new FileWriter(new File("matrixsize.csv"));
        writer.write("vector_entries,memory_size\n");
        for (int i=0; i<=50000000; i+=5000) {
            writer.write(i + "," + (Math.pow(i,2)*MATRIX_ENTRY_SIZE)/(double)(1024*1024*1024) + "\n");
        }
        writer.flush();
        writer.close();
    }
}
