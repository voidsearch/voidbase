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

package com.voidsearch.voidbase.console.session


import collection.mutable.{HashMap, ListBuffer}
import protocol.commands.VoidBaseConsoleCommand
import protocol.VoidBaseCommandFactory

class VoidBaseConsoleSession(_hostname: String) {

  // session-level variables
  var hostname = _hostname
  var domain   = ""
  var startTime = System.currentTimeMillis

  // command queue
  var commandQueue = new ListBuffer[VoidBaseConsoleCommand]();

  // session symbol table
  var symTable = new HashMap[String, String]();


  def containsVariable(variable : String) {
    return symTable.contains(variable)
  }

  def getCommand() : VoidBaseConsoleCommand = {

    var commandText = Console.readLine();
    var cmd = VoidBaseCommandFactory.getCommand(commandText, this)
    commandQueue += cmd;
    return cmd;

  }


}

