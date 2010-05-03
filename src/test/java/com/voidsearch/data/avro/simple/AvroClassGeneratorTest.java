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

import org.apache.avro.specific.SpecificCompiler;
import org.testng.annotations.Test;

import java.io.File;

public class AvroClassGeneratorTest {


    @Test
    public void nullTest() {
        try {
            SpecificCompiler.compileSchema(new File("src/data/avro/sample/fb_user.avpr"), new File("src/test/java/com/voidsearch/data/avro/simple/"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
