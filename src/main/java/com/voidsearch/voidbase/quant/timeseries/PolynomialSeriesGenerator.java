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

import java.util.List;
import java.util.ArrayList;

public class PolynomialSeriesGenerator extends NumericalSequenceGenerator implements SequenceGenerator {

    // polynomial coefficients
    private List<Double> coefficients;

    /**
     * default constructor - generates simple linear (y=x) polynomial
     */
    public PolynomialSeriesGenerator() {
        coefficients = new ArrayList<Double>();
        coefficients.add(0.0);
        coefficients.add(1.0);
    }

    /**
     *
     * @param coefficients - array of polynomial coefficients
     */
    public PolynomialSeriesGenerator(List<Double> coefficients) {
        this.coefficients = coefficients;
    }

    public double next() {
        double value = 0;
        for (int degree=0; degree < coefficients.size(); degree++) {
            value += coefficients.get(degree)*Math.pow(counter,degree);
        }
        incrementCounter();
        return value;
    }




}
