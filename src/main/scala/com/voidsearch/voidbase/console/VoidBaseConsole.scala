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


/**
 * VoidBase console
 * enabling console-based interaction with voidbase api
 */

package com.voidsearch.voidbase.console

import protocol.ConsoleProtocol
import session.VoidBaseConsoleSession
import syntax.ConsoleSyntax
import protocol.commands.ExitCommand

object VoidBaseConsole {

  def main(args: Array[String]) {

    ConsoleProtocol.printHeader()
    ConsoleProtocol.printCursor()

    var session = new VoidBaseConsoleSession("localhost:8080");
    var cmd = session.getCommand();

    while (!(cmd.isInstanceOf[ExitCommand])) {

      try {

        cmd.exec()

      } catch {
        case e => e.printStackTrace()
      }

      ConsoleProtocol.printCursor()
      cmd = session.getCommand();
      
    }

    cmd.exec()

  }

}

