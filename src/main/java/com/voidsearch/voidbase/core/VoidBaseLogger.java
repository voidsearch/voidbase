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

package com.voidsearch.voidbase.core;

import com.voidsearch.voidbase.apps.queuetree.client.QueueTreeClient;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.io.IOException;


public class VoidBaseLogger {

    private String name;
    private String logFile;
    private VoidBaseLogService logService;
    private String logQueue = null;

    protected static Logger log = Logger.getLogger(VoidBaseLogger.class.getName());

    public void setLogQueue(String logQueue) {
        this.logQueue = logQueue;
    }

    public VoidBaseLogger(String name, String logFile, VoidBaseLogService logService) throws IOException {

        this.name = name;
        this.logService = logService;
        this.logFile = logFile;

        if (logFile != null) {
            FileHandler handler = new FileHandler(logFile);
            handler.setFormatter(new VoidBaseLogFormatter());
            log.addHandler(handler);
        }
    }

    public void log(String message) {

        if (logFile != null) {
            log.info(getLogEntry(message));
        } else {
            logService.log(name, message);
        }

        if (logQueue != null) {
            try {
                QueueTreeClient client = new QueueTreeClient(logQueue);
                client.put(name,message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public class VoidBaseLogFormatter extends Formatter {
        public String format(LogRecord rec) {
            StringBuffer buf = new StringBuffer();
            buf.append(formatMessage(rec))
               .append("\n");
            return buf.toString();
        }
    }


    public String getLogEntry(String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<entry>\n")
          .append(content)
          .append("</entry>\n");
        return sb.toString();
    }

}
