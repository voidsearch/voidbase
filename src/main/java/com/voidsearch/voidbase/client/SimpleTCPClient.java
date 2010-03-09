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

package com.voidsearch.voidbase.client;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.PrintStream;

public class SimpleTCPClient {

  private String hostname;
  private int port;

  public SimpleTCPClient(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
  }

  /**
   * get tcp server data obtained by issuing given command
   *
   * @param command
   * @return
   * @throws Exception
   */
  public byte[] get(String command) throws Exception {

    System.out.println("|||| OPENING SOCKET");
    Socket socket = new Socket(hostname, port);
    System.out.println("|||| OPENED SOCKET");

    DataInputStream is = new DataInputStream(socket.getInputStream());
    PrintStream os = new PrintStream(socket.getOutputStream());

    StringBuilder sb = new StringBuilder();

    os.print(command.trim() + "\r\n");
    os.println("\r\n");

    String response = is.readLine();
    while ((response != null) && (response.length() > 0)) {
      sb.append(response).append("\n");
      response = is.readLine();
    }

    os.println("\r\n");
    is.readLine();

    is.close();
    os.close();

    return sb.toString().getBytes();

  }


}
