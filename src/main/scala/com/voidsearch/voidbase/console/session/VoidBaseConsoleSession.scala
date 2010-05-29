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
import jline.ConsoleReader
import com.voidsearch.voidbase.console.protocol.commands.VoidBaseConsoleCommand
import com.voidsearch.voidbase.console.scheduler.VoidBaseTaskScheduler
import com.voidsearch.voidbase.console.protocol.{ConsoleProtocol, VoidBaseCommandFactory}

class VoidBaseConsoleSession(_hostname: String, reader: ConsoleReader) {

  // session-level variables
  var hostname = _hostname
  var domain   = "queue"    // default domain
  var startTime = System.currentTimeMillis

  var scheduler = new VoidBaseTaskScheduler()
  scheduler.start()

  // command history queues
  var commandTextQueue = new ListBuffer[String]();
  var commandQueue = new ListBuffer[VoidBaseConsoleCommand]();

  // session symbol table
  var symTable = new HashMap[SessionObject, Any]();
  
  def containsVariable(variable : SessionObject):boolean = {
    return symTable.contains(variable)
  }

  def getVariable(variable : SessionObject):Any = {
    return symTable.get(variable)
  }

  def addVariable(variable: SessionObject, value: Any) = {
    symTable.put(variable,value);
  }

  // get command from stdin
  def getCommand() : VoidBaseConsoleCommand = {

    var commandText = reader.readLine(ConsoleProtocol.getCursor())
    commandText = commandText.trim()
    commandTextQueue += commandText
    return getCommand(commandText)

  }

  // get command object from given command text
  def getCommand(commandText : String) : VoidBaseConsoleCommand = {

    var cmd = VoidBaseCommandFactory.getCommand(commandText.trim, this)
    commandQueue += cmd;
    return cmd;

  }

}

