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

import com.voidsearch.voidbase.quant.vector.VoidVector;

public interface VoidMatrix {

    /**
     * return matrix representing inverse of given matrix
     */
    public VoidMatrix transpose();

    /**
     * return matrix product
     *
     * @param matrix
     * @return
     */
    public VoidMatrix multiply(VoidMatrix matrix);


    /**
     * return matrix vector product
     * 
     * @param matrix
     * @return
     */
    public VoidVector multiply(VoidVector matrix);

    /**
     * render matrix contents
     *
     * @return
     */
    public String render();

    /**
     * get number of matrix rows
     *
     * @return
     */
    public int rows();

    /**
     * get number of matrix columns
     *
     * @return
     */
    public int columns();

    /**
     * get matrix size
     * 
     * @return
     */
    public long size();

    /**
     * set matrix value at given position
     *
     * @param x
     * @param y
     * @param value
     */
    public void set(int x, int y, double value);


    /**
     * get matrix value corresponding to given row/column pair
     * 
     * @param x
     * @param y
     */
    public double get(int x, int y);


    /**
     * return true if matrix is equal to given matrix
     *
     * @param other
     * @return
     */
    public boolean equals(VoidMatrix other);

}
