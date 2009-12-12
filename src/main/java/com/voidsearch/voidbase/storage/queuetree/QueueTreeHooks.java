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

package com.voidsearch.voidbase.storage.queuetree;

import com.voidsearch.voidbase.serialization.VoidBaseSerialization;
import com.voidsearch.voidbase.storage.StorageOperationHooks;
import com.voidsearch.voidbase.apps.queuetree.protocol.QueueTreeProtocol;
import com.voidsearch.voidbase.util.VoidBaseUtil;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.ArrayList;

public class QueueTreeHooks implements StorageOperationHooks {

    public static void handlePut(QueueMetadata metadata, ArrayBlockingQueue queue, Object value) {

        if (metadata.getType().equals(VoidBaseSerialization.NUMERIC)) {
            try {
                dupValue(metadata,value);
                updateMinMax(metadata,value);
                updateAverage(metadata,queue,value);
                updateVariance(metadata,queue,value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        else if (metadata.getType().equals(VoidBaseSerialization.NUMERIC_TUPLE)) {
            try {
                updateMinMaxMultipleDimensions(metadata,value);
                updateAverageMultipleDimensions(metadata,queue,value);
                updateVarianceMultipleDimensions(metadata,queue,value);
                updateStandardDeviationMultipleDimensions(metadata);
                updateCovariance(metadata,queue,value);
                updateCorrelation(metadata);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

    }

    // copy value to metadata
    public static void dupValue(QueueMetadata metadata, Object value) throws NumberFormatException {
        metadata.set(QueueTreeProtocol.CURRENT,value);
    }

    // update minimum and maximum for single value
    public static void updateMinMax(QueueMetadata metadata, Object value) throws NumberFormatException {
        double val = VoidBaseSerialization.getDouble(value);

        // update min
        if (metadata.contains(QueueTreeProtocol.MIN)) {
            double min = VoidBaseSerialization.getDouble(metadata.get(QueueTreeProtocol.MIN));
            if (val < min)
                metadata.set(QueueTreeProtocol.MIN, val);
        }
        else {
            metadata.set(QueueTreeProtocol.MIN, val);
        }

        // update max
        if (metadata.contains(QueueTreeProtocol.MAX)) {
            double max = VoidBaseSerialization.getDouble(metadata.get(QueueTreeProtocol.MAX));
            if (val > max)
                metadata.set(QueueTreeProtocol.MAX, val);
        }
        else {
            metadata.set(QueueTreeProtocol.MAX, val);
        }
    }

    // update average value
    public static void updateAverage(QueueMetadata metadata, ArrayBlockingQueue queue, Object value) throws NumberFormatException {
        double val = VoidBaseSerialization.getDouble(value);

        if (metadata.contains(QueueTreeProtocol.AVG)) {
            double avg = (Double) metadata.get(QueueTreeProtocol.AVG);
            int numElems = queue.size();
            avg = (avg * (numElems - 1) + val) / queue.size();
            metadata.set(QueueTreeProtocol.AVG, avg);
        }
        else {
            metadata.set(QueueTreeProtocol.AVG, val);
        }
    }


    // update variance
    public static void updateVariance(QueueMetadata metadata, ArrayBlockingQueue queue, Object value) throws NumberFormatException {
        double val = VoidBaseSerialization.getDouble(value);

        if (metadata.contains(QueueTreeProtocol.VAR)) {
            if (metadata.contains(QueueTreeProtocol.AVG)) {

                double avg = (Double) metadata.get(QueueTreeProtocol.AVG);
                double var = (Double) metadata.get(QueueTreeProtocol.VAR);

                int numElems = queue.size();
                var = var + (Math.pow((val - avg), 2) / (numElems - 1));

                metadata.set(QueueTreeProtocol.VAR, var);
            }
            else {
                metadata.set(QueueTreeProtocol.VAR, 0.0);
            }
        }
        else {
            metadata.set(QueueTreeProtocol.VAR, 0.0);
        }
    }

    // update deviation
    public static void updateDeviation(QueueMetadata metadata) throws NumberFormatException {
        if (metadata.contains(QueueTreeProtocol.VAR)) {
            double var = (Double)metadata.get(QueueTreeProtocol.VAR);
            metadata.set(QueueTreeProtocol.DEVIATION,Math.sqrt(var));
        }
    }

    // update minimum and maximum for multiple dimensions
    public static void updateMinMaxMultipleDimensions(QueueMetadata metadata, Object value) throws NumberFormatException {

        ArrayList<Double> valArray = VoidBaseSerialization.getDoubleArray(value);

        // update min
        if (metadata.contains(QueueTreeProtocol.MIN)) {
            ArrayList<Double> minArray = VoidBaseSerialization.getDoubleArray(metadata.get(QueueTreeProtocol.MIN));
            int cnt = 0;
            for (double val: valArray) {
                if (val < minArray.get(cnt))
                    minArray.set(cnt,val);
                cnt++;
            }
            metadata.set(QueueTreeProtocol.MIN, minArray);
        } else {
            metadata.set(QueueTreeProtocol.MIN, VoidBaseUtil.getZeroDoubleArray(valArray.size()));
        }

        // update max
        if (metadata.contains(QueueTreeProtocol.MAX)) {
            ArrayList<Double> maxArray = VoidBaseSerialization.getDoubleArray(metadata.get(QueueTreeProtocol.MAX));
            int cnt = 0;
            for (double val: valArray) {
                if (val > maxArray.get(cnt))
                    maxArray.set(cnt,val);
                cnt++;
            }
            metadata.set(QueueTreeProtocol.MAX, maxArray);
        } else {
            metadata.set(QueueTreeProtocol.MAX, VoidBaseUtil.getZeroDoubleArray(valArray.size()));
        }
    }


    // update average value
    public static void updateAverageMultipleDimensions(QueueMetadata metadata, ArrayBlockingQueue queue, Object value) throws NumberFormatException {
        ArrayList<Double> valArray = VoidBaseSerialization.getDoubleArray(value);

        if (metadata.contains(QueueTreeProtocol.AVG)) {
            ArrayList<Double> avgArray = VoidBaseSerialization.getDoubleArray(metadata.get(QueueTreeProtocol.AVG));
            int numElems = queue.size();

            for (int i=0; i<valArray.size(); i++) {
                double avg = (avgArray.get(i) * (numElems - 1) + valArray.get(i)) / queue.size();
                avgArray.set(i,avg);
            }

            metadata.set(QueueTreeProtocol.AVG, avgArray);
        }
        else {
            metadata.set(QueueTreeProtocol.AVG, VoidBaseUtil.getZeroDoubleArray(valArray.size()));
        }
    }



    // update variance
    public static void updateVarianceMultipleDimensions(QueueMetadata metadata, ArrayBlockingQueue queue, Object value) throws NumberFormatException {
        ArrayList<Double> valArray = VoidBaseSerialization.getDoubleArray(value);

        if (metadata.contains(QueueTreeProtocol.VAR)) {
            if (metadata.contains(QueueTreeProtocol.AVG)) {

                ArrayList<Double> avgArray = VoidBaseSerialization.getDoubleArray(metadata.get(QueueTreeProtocol.AVG));
                ArrayList<Double> varArray = VoidBaseSerialization.getDoubleArray(metadata.get(QueueTreeProtocol.VAR));
                int numElems = queue.size();

                for (int i=0; i<valArray.size(); i++) {
                    double var = varArray.get(i) + (Math.pow((valArray.get(i) - avgArray.get(i)), 2) / (numElems - 1));
                    varArray.set(i,var);
                }

                metadata.set(QueueTreeProtocol.VAR, varArray);
            }
            else {
                metadata.set(QueueTreeProtocol.VAR, VoidBaseUtil.getZeroDoubleArray(valArray.size()));
            }
        }
        else {
            metadata.set(QueueTreeProtocol.VAR, VoidBaseUtil.getZeroDoubleArray(valArray.size()));
        }
    }

    // update standard deviation
    public static void updateStandardDeviationMultipleDimensions(QueueMetadata metadata) {
        if (metadata.contains(QueueTreeProtocol.VAR)) {
            ArrayList<Double> varArray = VoidBaseSerialization.getDoubleArray(metadata.get(QueueTreeProtocol.VAR));
            ArrayList<Double> deviation = new ArrayList<Double>();
            for (Double val : varArray) {
                deviation.add(Math.sqrt(val));
            }
            metadata.set(QueueTreeProtocol.DEVIATION, deviation);
        }
    }

    // update variance
    public static void updateCovariance(QueueMetadata metadata, ArrayBlockingQueue queue, Object value) throws NumberFormatException {
        ArrayList<Double> valArray = VoidBaseSerialization.getDoubleArray(value);

        if (metadata.contains(QueueTreeProtocol.COVARIANCE)
            && metadata.contains(QueueTreeProtocol.AVG)) {

                double cov = (Double) metadata.get(QueueTreeProtocol.COVARIANCE);
                ArrayList<Double> avgArray = VoidBaseSerialization.getDoubleArray(metadata.get(QueueTreeProtocol.AVG));
                int numElems = queue.size();

                if (numElems > 2) {

                    double covProduct = 1.0;

                    for (int i=0; i<valArray.size(); i++) {
                        covProduct *= (valArray.get(i) - avgArray.get(i));
                    }

                    metadata.set(QueueTreeProtocol.COVARIANCE, (cov*(numElems -2) + covProduct)/(numElems -1));
                }
        }
        else {
            metadata.set(QueueTreeProtocol.COVARIANCE, 0.0);
        }
    }

    // update standard deviation
    public static void updateCorrelation(QueueMetadata metadata) {
        if (metadata.contains(QueueTreeProtocol.COVARIANCE)
            && metadata.contains(QueueTreeProtocol.VAR)) {

            double corr = (Double) metadata.get(QueueTreeProtocol.COVARIANCE);
            ArrayList<Double> varArray = VoidBaseSerialization.getDoubleArray(metadata.get(QueueTreeProtocol.VAR));

            double deviationDenominator = 1.0;
            for (Double val : varArray) {
                deviationDenominator *= val;
            }
            metadata.set(QueueTreeProtocol.CORRELATION, corr/deviationDenominator);
        }
    }


}
