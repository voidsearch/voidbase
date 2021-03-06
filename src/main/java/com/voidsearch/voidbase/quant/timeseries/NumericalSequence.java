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

public abstract class NumericalSequence implements SequenceGenerator {

    protected double increment = DEFAULT_INCREMENT;
    protected double counter = 0.0;
    
    public void setIncrement(double increment) {
        this.increment = increment;
    }

    public void incrementCounter() {
        counter += increment;
    }


}
