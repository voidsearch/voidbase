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

package com.voidsearch.voidbase.quant.vector;

import java.util.Random;

public class SparseBinaryVector extends ArrayVector {

    Random rnd = new Random();

    /**
     * create vector of given size with given probability of binary event
     *
     * @param size
     * @param p
     */
    public SparseBinaryVector(int size, double p) {
        super(size);
        init(p);
    }

    private void init(double p) {
        for (int i=0; i<vector.length; i++) {
            if (rnd.nextDouble() <= p) {
                vector[i] = 1;
            }
        }
    }


    


}
