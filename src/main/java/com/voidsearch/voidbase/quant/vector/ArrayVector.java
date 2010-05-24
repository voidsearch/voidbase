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

import com.voidsearch.voidbase.quant.matrix.ArrayMatrix;
import com.voidsearch.voidbase.quant.matrix.VoidMatrix;

public class ArrayVector implements VoidVector {

    protected double[] vector;
    protected boolean TRANSPOSED = false;

    public ArrayVector(int size) {
        vector = new double[size];
    }

    public ArrayVector(double[] vector, boolean isTransposed) {
        this.vector = vector;
        this.TRANSPOSED = isTransposed;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        String prefix = "[";
        
        for (int i=0; i<vector.length; i++) {
            sb.append(prefix).append(vector[i]);
            if (TRANSPOSED) {
                sb.append("]\n");   
            } else {
                prefix = " ";
            }
        }

        if (!TRANSPOSED) {
            sb.append( "]");
        }
        
        return sb.toString().trim();
    }


    public int size() {
        return vector.length;
    }


    public double get(int i) {
        return vector[i];
    }


    public void set(int i, double value) {
        vector[i] = value;    
    }

    public VoidVector transpose() {
        return new ArrayVector(vector, true);
    }

    public boolean isTransposed() {
        return TRANSPOSED;
    }

    /**
     * multiple this vector with another vector
     * 
     * @param otherVector
     * @return
     * @throws Exception
     */
    public VoidMatrix multiply(VoidVector otherVector) throws Exception {
        if (size() == otherVector.size()) {
            VoidMatrix result = new ArrayMatrix(size(), size());
            for (int i=0; i<this.size(); i++) {
                for (int j=0; j<otherVector.size(); j++) {
                    result.set(i,j,get(i)*otherVector.get(j));
                }
            }
            return result;
        } else {
            throw new Exception();
        }
    }

}
