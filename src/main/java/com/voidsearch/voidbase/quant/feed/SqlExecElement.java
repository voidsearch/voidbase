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
    private String field = null;

    public SqlExecElement(String hostname, String username, String password, String database, String query) {
        this.query = query;
        init(hostname,username,password,database);
    }

    public SqlExecElement(String hostname, String username, String password, String database, String query, String field) {
        this.query = query;
        this.field = field;
        init(hostname,username,password,database);
    }

    public void init(String hostname, String username, String password, String database) {
        try {
            String mysqlUri = "jdbc:mysql://"+hostname+"/"+database+"?user="+username+"&password="+password;
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(mysqlUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double next() {
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            result.first();
            if (field == null) {
                return result.getDouble(1);
            } else {
                return result.getDouble(field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
