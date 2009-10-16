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

package com.voidsearch.voidbase.util;

import java.util.ArrayList;

public class VoidBaseUtil {

    public static ArrayList<Double> getZeroDoubleArray(int size) {
        return getDoubleArray(size,0.0);
    }

    public static ArrayList<Double> getDoubleArray(int size, double value) {
        ArrayList<Double> array = new ArrayList<Double>(size);
        for (int i=0; i<size; i++) {
            array.add(0.0);
        }
        return array;
    }


    public static String XMLEncode(String value)
    {
        if (value == null) return value;

         return value.replaceAll("&", "&amp;")
                     .replaceAll("'", "&apos;")
                     .replaceAll("\"", "&quot;")
                     .replaceAll("<", "&lt;")
                     .replaceAll(">", "&gt;");
    } 


}
