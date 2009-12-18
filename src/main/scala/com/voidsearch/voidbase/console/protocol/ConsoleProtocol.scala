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


import java.io.PrintWriter
import java.util.LinkedList
import jline.{Completor, ArgumentCompletor, SimpleCompletor, ConsoleReader}

object ConsoleProtocol {

  val keywords = List(
                      "HELP",
                      "DOMAIN",
                      "SYMTABLE",
                      "LIST",
                      "SEQ",
                      "CREATE",
                      "DELETE",
                      "EXIT",
                      "QUIT"
                      )

  def printHeader(out : PrintWriter) {

    println()
    println("Welcome to voidbase console\r\n")
    println("voidbase version: 0.017\r\n")
    println()
    println("type 'help' for help")
    println()

  }

  def getCursor() : String = {
    return "void> "
  }

  def setCompetors(reader : ConsoleReader) = {

    var keywordCompletors: Array[String] = new Array[String](keywords.size)
    var pos = 0

    for (keyword <- keywords) {
      keywordCompletors(pos) = keyword
      pos += 1
    }

    var completors = new LinkedList[Completor]();
    completors.add(new SimpleCompletor(keywordCompletors))
    
    reader.addCompletor(new ArgumentCompletor(completors));
    
  }

  
}
