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

package com.voidsearch.voidbase.console.protocol.commands

class HelpCommand extends VoidBaseConsoleCommand {

  def exec() = {
    println()
    println("For information, visit:")
    println("   http://www.voidsearch.com/voidbase/")
    println()
    println("List of all voidbase commands: ")
    println()
    println("DOMAIN   : domain to use (QUEUE|CACHE|BROKER)")
    println("LIST     : list all entries within given DOMAIN")
    println()
  }

  def getResult() : String = {
    return ""
  }


}