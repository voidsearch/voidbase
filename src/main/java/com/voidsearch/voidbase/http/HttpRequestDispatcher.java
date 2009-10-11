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

package com.voidsearch.voidbase.http;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.net.InetSocketAddress;

import com.voidsearch.voidbase.module.VoidBaseModule;
import com.voidsearch.voidbase.module.VoidBaseModuleException;
import com.voidsearch.voidbase.module.VoidBaseModuleResponse;
import com.voidsearch.voidbase.module.VoidBaseModuleRequest;
import com.voidsearch.voidbase.config.VoidBaseConfiguration;
import com.voidsearch.voidbase.config.Config;

public class HttpRequestDispatcher implements VoidBaseModule {

    protected static final Logger logger = LoggerFactory.getLogger(HttpRequestDispatcher.class.getName());

    int PORT;

    public void initialize(String name) throws VoidBaseModuleException {
        PORT = Integer.parseInt(VoidBaseConfiguration.get(Config.GLOBAL, Config.DISPATCHER, Config.PORT));
    }

    public void run() {

        logger.info("HttpRequestDispatcher: init()");

        try {
            startHTTPServer(new HttpRequestPipelineFactory(new HttpRequestHandler()),PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("HttpRequestDispatcher: registered at port : " + PORT);


    }

    protected void startHTTPServer(ChannelPipelineFactory handler, int port) throws VoidBaseModuleException {

        ChannelFactory factory = new NioServerSocketChannelFactory(
                                 Executors.newCachedThreadPool(),
                                 Executors.newCachedThreadPool());

        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(handler);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        bootstrap.bind(new InetSocketAddress(port));

    }

    public VoidBaseModuleResponse handle(VoidBaseModuleRequest request) throws VoidBaseModuleException {
        return new VoidBaseModuleResponse();
    }
}
