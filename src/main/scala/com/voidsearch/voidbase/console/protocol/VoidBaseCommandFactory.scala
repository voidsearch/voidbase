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

import commands._
import console.syntax.ConsoleSyntax

import scala.util.matching.Regex
import session.VoidBaseConsoleSession

object VoidBaseCommandFactory {

  /**
   * factor appropriate command object instance
   * based on passed command string 
   *
   */
  def getCommand(commandText : String, session : VoidBaseConsoleSession) : VoidBaseConsoleCommand = {

    return commandText match {

      case ConsoleSyntax.EXIT()
        => ExitCommand(session)
      case ConsoleSyntax.QUIT()
        => ExitCommand(session)
      case ConsoleSyntax.HELP()
        => HelpCommand(session)
      case ConsoleSyntax.GET_DOMAIN()
        => GetDomainCommand(session)
      case ConsoleSyntax.SET_DOMAIN(domain)
        => SetDomainCommand(session,domain)
      case ConsoleSyntax.LIST()
        => ListCommand(session)
      case ConsoleSyntax.CREATE_SEQUENCE(variable,sequenceClass)
        => CreateSequenceCommand(session,variable,sequenceClass)
      case ConsoleSyntax.CREATE_QUEUE(name,size)
        => CreateQueueCommand(session, name, size) 

      case _ => InvalidCommand(commandText)

    }

  }


}
