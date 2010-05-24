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
import org.testng.annotations.Test;

public class VoidVectorTest {

    @Test
    public void nullTest() {

        try {

            System.out.println("\nX : \n");
            VoidVector vector = new SparseRandomVector(8, 0.9);
            System.out.println(vector.render());

            System.out.println("\nX' : \n");
            VoidVector transposed = vector.transpose();
            System.out.println(transposed.render());

            System.out.println("\n(X*X') : \n");
            VoidMatrix result = vector.multiply(transposed);
            System.out.println(result.render());

            System.out.println("\n(X*X')^{-1} : \n");
            VoidMatrix resultInverted = result.transpose();
            System.out.println(resultInverted.render());

            System.out.println("\n(x*X')^{-1} * X' : \n");
            VoidVector finalResult = resultInverted.multiply(transposed);
            System.out.println(finalResult.render());

            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
