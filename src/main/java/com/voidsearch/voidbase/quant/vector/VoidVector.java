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

import com.voidsearch.voidbase.quant.matrix.VoidMatrix;

public interface VoidVector {

    /**
     * multiply with another vector
     * 
     * @param vector
     * @return
     */
    public VoidMatrix multiply(VoidVector vector) throws Exception;

    /**
     * return transposed copy of given vector
     * 
     * @return
     */
    public VoidVector transpose();

    /**
     * render vector contents
     *
     * @return
     */
    public String render();


    /**
     * get vector size
     * @return
     */
    public int size();

    /**
     * get vector param at given position
     *
     * @param position
     * @return
     */
    public double get(int position);

    /**
     * set vector param at given position
     * 
     * @param position
     * @param value
     */
    public void set(int position, double value);

    /**
     * return true if given vector is transposed
     * 
     * @return
     */
    public boolean isTransposed();

}
