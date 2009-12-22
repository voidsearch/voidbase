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
import common.{KillProcessCommand, ProcessListCommand, DetachCommand, ListCommand}
import quant.{CreateSequenceCommand, NextSequenceValueCommand}

import queue.{DeleteQueueCommand, InsertToQueueCommand, CreateQueueCommand}
import system.{ExitCommand, HelpCommand}

import environment._
import console.syntax.ConsoleSyntax



import scala.util.matching.Regex
import session.VoidBaseConsoleSession

/**
 * note - chunked structure due to the workaround of scala bug :
 * http://lampsvn.epfl.ch/trac/scala/ticket/1133
 * 
 */

object VoidBaseCommandFactory {

  /**
   * factor appropriate command object instance
   * based on passed command string 
   */
  def getCommand(commandText : String, session : VoidBaseConsoleSession) : VoidBaseConsoleCommand = {

    try { return matchSystemCommand(commandText,session)  } catch {case e => }
    try { return matchCommonCommand(commandText,session)  } catch {case e => }
    try { return matchSessionCommand(commandText,session) } catch {case e => }
    try { return matchSequenceCommand(commandText,session) } catch {case e => }
    try { return matchQTreeCommand(commandText,session)   } catch {case e => }
    return InvalidCommand(commandText)

  }

  /**
   * get system commands
   */
  def matchSystemCommand(commandText: String, session: VoidBaseConsoleSession): VoidBaseConsoleCommand = {
    return commandText match {
      case ConsoleSyntax.TEST()
      => TestCommand(session)
      case ConsoleSyntax.EXIT()
      => ExitCommand(session)
      case ConsoleSyntax.QUIT()
      => ExitCommand(session)
      case ConsoleSyntax.HELP()
      => HelpCommand(session)
      case ConsoleSyntax.SYMBOL_TABLE()
        => DumpSymbolTableCommand(session)
      case _ => throw new Exception()
    }
  }

  /**
   * get commands common for all apps
   */
  def matchCommonCommand(commandText: String, session: VoidBaseConsoleSession): VoidBaseConsoleCommand = {
    return commandText match {
      case ConsoleSyntax.LIST()
        => ListCommand(session)
      case ConsoleSyntax.DETACH_PROCESS(process)
        => DetachCommand(session,process,0)
      case ConsoleSyntax.DETACH_REPEATED(process,interval)
        => DetachCommand(session,process,interval.toInt)
      case _ => throw new Exception()
    }

  }

  /**
   * get session-manipulation commands
   */
  def matchSessionCommand(commandText: String, session: VoidBaseConsoleSession): VoidBaseConsoleCommand = {
    return commandText match {
      case ConsoleSyntax.PROCESS_LIST()
        => ProcessListCommand(session)
      case ConsoleSyntax.KILL_PROCESS(pid)
        => KillProcessCommand(session,pid.toInt) 
      case ConsoleSyntax.GET_DOMAIN()
        => GetDomainCommand(session)
      case ConsoleSyntax.SET_DOMAIN(cmd,domain)
        => SetDomainCommand(session,domain)
      case ConsoleSyntax.SAVE_SESSION(outFile)
        => SaveSessionCommand(session,outFile)
      case ConsoleSyntax.RESTORE_SESSION(inFile)
        => RestoreSessionCommand(session,inFile)
      case _ => throw new Exception()
    }
  }

  /**
   * get sequence-creation commands
   */
  def matchSequenceCommand(commandText: String, session: VoidBaseConsoleSession): VoidBaseConsoleCommand = {
    return commandText match {
      case ConsoleSyntax.CREATE_SEQUENCE(cmd,variable,sequenceClass)
        => CreateSequenceCommand(session,variable,sequenceClass)
      case ConsoleSyntax.SEQUENCE_NEXT_VALUE(variable)
        => NextSequenceValueCommand(session,variable)
      case _ => throw new Exception()
    }
  }

  /**
   *  get qtree-specific commands
   */
  def matchQTreeCommand(commandText: String, session: VoidBaseConsoleSession): VoidBaseConsoleCommand = {
    return commandText match {
      case ConsoleSyntax.CREATE_QUEUE(name,size)
        => CreateQueueCommand(session, name, size)
      case ConsoleSyntax.DELETE_QUEUE(name)
        => DeleteQueueCommand(session, name)
      case ConsoleSyntax.INSERT_TO_QUEUE(queue,content)
        => InsertToQueueCommand(session, queue, content)
      case _ => throw new Exception()
    }
  }


}
