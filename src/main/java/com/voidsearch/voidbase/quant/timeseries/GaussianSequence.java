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

public class GaussianSequence extends RandomSequence implements SequenceGenerator {

    private static double NORMAL_MEAN = 0.0;
    private static double NORMAL_DEVIATION = 1.0;

    private double mean;
    private double deviation;

    public GaussianSequence() {
        this.mean = NORMAL_MEAN;
        this.deviation = NORMAL_DEVIATION;
    }

    public GaussianSequence(int mean, int deviation) {
        this.mean = mean;
        this.deviation = deviation;
    }

    public double next() {
        return random.nextGaussian()*deviation;
    }


}
