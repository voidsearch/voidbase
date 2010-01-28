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

package com.voidsearch.voidbase.quant.feed;

import com.voidsearch.voidbase.quant.timeseries.SequenceGenerator;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class SqlExecElement implements SequenceGenerator {

    private static Connection connection = null;
    private String query = null;

    public SqlExecElement(String hostname, String username, String password, String database, String query) {
        try {
            String mysqlUri = "jdbc:mysql://"+hostname+"/"+database+"?user="+username+"&password="+password;
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(mysqlUri);
            this.query = query;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double next() {
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            result.first();
            return result.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
