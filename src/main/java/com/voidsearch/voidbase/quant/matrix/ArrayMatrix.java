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

import com.voidsearch.voidbase.quant.vector.ArrayVector;
import com.voidsearch.voidbase.quant.vector.VoidVector;

public class ArrayMatrix implements VoidMatrix {

    double[][] matrix;

    public ArrayMatrix(int rows, int columns) {
        matrix = new double[rows][columns];
    }

    public ArrayMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public VoidMatrix transpose() {
        ArrayMatrix result = new ArrayMatrix(rows(), columns());
        for (int i=0; i<rows(); i++) {
            for (int j=0; j<columns(); j++) {
                result.set(j,i,get(i,j));
            }
        }
        return result;
    }

    public VoidMatrix multiply(VoidMatrix matrix2) {
        return null;
    }

    public VoidVector multiply(VoidVector vector) {
        VoidVector result = new ArrayVector(rows());
        for (int i=0; i<rows(); i++) {
            double sum = 0;
            for (int j=0; j<columns(); j++) {
                for (int z=0; z<vector.size(); z++) {
                    if (j == z) {
                        sum += get(i,j)*vector.get(z);
                    }
                }
            }
            result.set(i,sum);
        }
        return result;
    }


    public String render() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<matrix.length; i++) {
            String prefix = "|";
            for (int j=0; j<matrix[i].length; j++) {
                sb.append(prefix).append(matrix[i][j]);
                prefix = " ";
            }
            sb.append("|\n");
        }
        return sb.toString().trim();
    }


    public int rows() {
        return matrix.length;
    }

    public int columns() {
        return matrix[0].length;
    }

    public long size() {
        return rows() * columns();
    }


    public double get(int x, int y) {
        return matrix[x][y];
    }

    public void set(int x, int y, double value) {
        matrix[x][y] = value;
    }

    public boolean equals(VoidMatrix other) {
        if (size() == other.size()) {
            for (int i=0; i<columns(); i++) {
                for (int j=0; j<rows(); j++) {
                    if (get(i,j) != other.get(i,j)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
