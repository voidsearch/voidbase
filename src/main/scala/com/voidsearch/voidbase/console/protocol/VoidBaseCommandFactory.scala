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

package com.voidsearch.voidbase.console.protocol


import commands.{HelpCommand, ExitCommand, InvalidCommand}
import console.syntax.ConsoleSyntax

object VoidBaseCommandFactory {

  def getCommand(commandText : String) : VoidBaseConsoleCommand = {

    if (commandText.equals(ConsoleSyntax.EXIT_COMMAND)) {
      return new ExitCommand()
    }
    else if (commandText.equals(ConsoleSyntax.HELP_COMMAND)) {
      return new HelpCommand()
    }
    else {
      return new InvalidCommand()      
    }

  }


}
