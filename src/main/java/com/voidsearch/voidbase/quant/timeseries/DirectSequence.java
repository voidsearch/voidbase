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


package com.voidsearch.voidbase.quant.timeseries;

import java.util.LinkedList;
import java.util.Iterator;

public class DirectSequence extends NumericalSequence implements SequenceGenerator {

    LinkedList<Double> sequence = new LinkedList();
    Iterator it;

    public DirectSequence(String[] values) {
        for (String value : values) {
            try {
                sequence.add(Double.parseDouble(value.trim()));
            } catch (NumberFormatException e) { }
        }
        it = sequence.iterator();
    }

    public double next() {
        if (it.hasNext()) {
            return (Double)it.next();
        } else {
            return 0.0;
        }
    }

}
