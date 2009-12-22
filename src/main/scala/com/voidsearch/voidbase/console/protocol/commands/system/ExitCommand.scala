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

package com.voidsearch.voidbase.console.protocol.commands.system

import scala.util.matching.Regex
import session.VoidBaseConsoleSession

case class ExitCommand(session: VoidBaseConsoleSession) extends VoidBaseConsoleCommand {

  def exec() = {

    // terminate process scheduler
    session.scheduler ! ("STOP")

    // finalize
    println("Terminating session | total time elapsed : " + getElapsedTimeString())
  }

  def getElapsedTimeString(): String = {
    return (System.currentTimeMillis - session.startTime)/1000 + " s"
  }

}


