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

package com.voidsearch.voidbase.tcp;

import com.voidsearch.voidbase.config.ConfigException;
import com.voidsearch.voidbase.config.VoidBaseConfig;
import com.voidsearch.voidbase.tcp.factory.SocketServerFactory;
import com.voidsearch.voidbase.tcp.factory.SocketServerFactoryException;
import com.voidsearch.voidbase.util.GenericUtil;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class TCPServerContainer {
    protected VoidBaseConfig conf = null;
    protected static TCPServerContainer container;
    protected LinkedHashMap<String, ServerBootstrap> servers = new LinkedHashMap<String, ServerBootstrap>();

    protected static final Logger logger = LoggerFactory.getLogger(TCPServerContainer.class.getName());

    protected TCPServerContainer() {
        super();
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static synchronized TCPServerContainer getInstance() throws TCPServerException {
        if (container == null) {
            container = new TCPServerContainer();
            container.init();
        }

        return container;
    }

    protected void init() throws TCPServerException {

        // initialize configuration
        try {
            conf = VoidBaseConfig.getInstance();
        } catch (ConfigException e) {
            GenericUtil.logException(e);
            throw new TCPServerException("Failed to parse configuration");
        }

        // start servers
        registerShutdownHook();
        startServers();
    }

    protected void startServers() throws TCPServerException {
        List<String> serverNames = conf.getList("tcp");

        for(String serverName: serverNames) {
            Integer port = conf.getInteger("tcp." + serverName + ".port");
            String codec = conf.getString("tcp." + serverName + ".codec");
            String messageHandler = conf.getString("tcp." + serverName + ".message_handler");

            logger.info("Initializing TCP server: " + serverName);
            servers.put(serverName, startTCPServer(getChannel(codec, messageHandler), port));
        }
    }

    protected ChannelPipelineFactory getChannel(String codec, String messageHandler) throws TCPServerException {
        ChannelPipelineFactory channel;
        VoidBaseSocketHandler handler = new VoidBaseSocketHandler(messageHandler);

        try {
            channel = SocketServerFactory.getSocketServer(codec, handler); 
        } catch (SocketServerFactoryException e) {
            GenericUtil.logException(e);
            throw new TCPServerException("Failed to initialize handler: " + messageHandler);
        }

        return channel;
    }

    protected ServerBootstrap startTCPServer(ChannelPipelineFactory channel, Integer port) throws TCPServerException {
        ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                                                                   Executors.newCachedThreadPool());

        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(channel);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);

        // start tcp interface
        try {
            bootstrap.bind(new InetSocketAddress(port));
        } catch (NumberFormatException e) {
            GenericUtil.logException(e);
            throw new TCPServerException("Failed to start the server.");
        }

        logger.info("Started TCP server on port " + port);
        return bootstrap;
    }

    protected void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    class ShutdownHook extends Thread {
        public void run() {
            for (Map.Entry<String, ServerBootstrap> server: servers.entrySet()) {
                server.getValue().releaseExternalResources();
                servers.remove(server);
            }
        }
    }
}
