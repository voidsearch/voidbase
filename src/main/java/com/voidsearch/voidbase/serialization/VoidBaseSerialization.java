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

package com.voidsearch.voidbase.serialization;

import java.util.ArrayList;

public enum VoidBaseSerialization {
    NUMERIC,
    NUMERIC_TUPLE,
    STRING_TUPLE,
    STRING,
    UNKNOWN;

    public static String MULTIVALUE_DELIMITER = ",";

    public static VoidBaseSerialization getType(Object o) {
        String val = (String)o;
        try {
            Double.parseDouble(val);
            return NUMERIC;
        } catch (NumberFormatException e) {
            try {
                if (val.indexOf(",") > -1) {
                    val = val.replaceAll("\\[","").replaceAll("\\]","");  // FIX THIS AT PARSER-LEVEL
                    String[] parts = val.split(",");
                    for (String part : parts) {
                        Double.parseDouble(part);
                    }
                    return NUMERIC_TUPLE;
                }
            } catch (NumberFormatException ex) {
                return STRING_TUPLE;
            }
        }
        return STRING;
    }


    public static double getDouble(Object o) throws NumberFormatException {
        if (o instanceof String) {
            String strVal = (String)o;
            return Double.parseDouble(strVal);
        }
        else if (o instanceof Double) {
            return (Double)o;
        }
        else {
            throw new NumberFormatException();
        }
    }


    public static ArrayList<Double> getDoubleArray(Object o) throws NumberFormatException {
        if (o instanceof String) {
            String strVal = (String)o;
            strVal = strVal.replaceAll("\\[","").replaceAll("\\]","");  // FIX THIS AT PARSER-LEVEL
            String[] parts = strVal.split(MULTIVALUE_DELIMITER);
            ArrayList<Double> array = new ArrayList<Double>(parts.length);
            for (String part : parts) {
                array.add(Double.parseDouble(part));
            }
            return array;
        }
        else if (o instanceof ArrayList){
            // TODO - handle conversion issue
            return (ArrayList<Double>)o;
        }
        else {
            throw new NumberFormatException();
        }
    }

}
