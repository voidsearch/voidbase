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

package com.voidsearch.voidbase.console.protocol.commands.environment

import java.io.{BufferedWriter, FileWriter}
import com.voidsearch.voidbase.console.syntax.ConsoleSyntax
import com.voidsearch.voidbase.console.protocol.commands.VoidBaseConsoleCommand
import com.voidsearch.voidbase.console.session.VoidBaseConsoleSession

case class SaveSessionCommand(session: VoidBaseConsoleSession, outFile : String) extends VoidBaseConsoleCommand {
  
  def exec() = {

    try {

      var fstream = new FileWriter(outFile)
      var out = new BufferedWriter(fstream)
      var cnt = 0

      for (command: String <- session.commandTextQueue) {

        command match {

          case ConsoleSyntax.SAVE_SESSION(outFile)
          => // do not log save session command

          case _
          =>
            out.write(command)
            out.write("\n")
            cnt += 1
        }

      }
      out.close()

      println("session.save() : written total of " + cnt + " commands")

    } catch {
      case e => println("error writing to file : " + outFile)
    }


  }

}

