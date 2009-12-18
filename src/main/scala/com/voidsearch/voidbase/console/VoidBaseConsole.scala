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

import java.io.PrintWriter
import java.util.LinkedList
import jline.{Completor, SimpleCompletor, ArgumentCompletor, ConsoleReader}
import protocol.ConsoleProtocol
import session.VoidBaseConsoleSession
import syntax.ConsoleSyntax
import protocol.commands.ExitCommand

object VoidBaseConsole {

  def main(args: Array[String]) {

    var reader = new ConsoleReader();
    reader.setBellEnabled(false);

    ConsoleProtocol.setCompetors(reader)

    var out = new PrintWriter(System.out);
    ConsoleProtocol.printHeader(out)

    var session = new VoidBaseConsoleSession("localhost:8080",reader);
    var cmd = session.getCommand();

    while (!(cmd.isInstanceOf[ExitCommand])) {

      try {

        cmd.exec()

      } catch {
        case e => e.printStackTrace()
      }

      out.write(ConsoleProtocol.getCursor() + "\n")
      cmd = session.getCommand();

    }

    cmd.exec()

  }

}

