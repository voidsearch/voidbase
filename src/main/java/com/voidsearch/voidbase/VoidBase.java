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

package com.voidsearch.voidbase;

import com.voidsearch.voidbase.core.VoidBaseCore;
import com.voidsearch.voidbase.module.VoidBaseModuleException;
import com.voidsearch.voidbase.util.GenericUtil;
import com.voidsearch.voidbase.config.ConfigException;
import com.voidsearch.voidbase.config.VoidBaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.*;

public class VoidBase {
    protected CommandLine cmd = null;
    protected Options options = new Options();
    protected static final Logger logger = LoggerFactory.getLogger(VoidBase.class.getName());

    public static void main(String[] args) {
        VoidBase app = new VoidBase();
        app.start(args);
    }

    public void start(String[] args) {
        VoidBaseCore core = VoidBaseCore.getInstance();

        // general initialization - configuration etc.
        try {
            initialize(args);
        } catch (VoidBaseException e) {
            logger.error("Failed to initialize application.");
            GenericUtil.logException(e);

            System.exit(1);
        }

        // start core module
        try {
            core.initialize(args.toString());
            core.run();
        } catch (VoidBaseModuleException e) {
            logger.error("Core Module Error - exiting...");
            GenericUtil.logException(e);
        }
    }

    public void initialize(String[] args) throws VoidBaseException {
        VoidBaseConfig config = null;

        // parse arguments
        cmd = parseArgs(args);        

        // initialize configuration
        try {
            if (!cmd.hasOption("config"))
                config = VoidBaseConfig.getInstance();
            else
                config = VoidBaseConfig.getInstance(cmd.getOptionValue("config"));
        } catch (ConfigException e) {
            GenericUtil.logException(e);
            throw new VoidBaseException("Failed to parse configuration file: " + cmd.getOptionValue("config"));
        }
    }

    protected CommandLine parseArgs(String[] args) throws VoidBaseException {
        CommandLineParser parser = new PosixParser();

        options.addOption("config", true,  "configuration file");

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            throw new VoidBaseException("Failed to command line parameters");
        }

        return cmd;
    }

    protected void usage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("./bin/voidbase.sh", options);

        System.exit(1);
    }
}
