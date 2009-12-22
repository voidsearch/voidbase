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

package com.voidsearch.voidbase.console.syntax

/**
 * single-object aggregate of all reserved keywords
 */

object ConsoleSyntax {

  // system commands
  val TEST                  = """test|TEST""".r
  val EXIT                  = """exit|EXIT""".r
  val QUIT                  = """quit|QUIT""".r
  val HELP                  = """help|HELP""".r

  // console  functions
  val RESTORE_SESSION       = """session.restore\(\"(.*)\"\)""".r
  val SAVE_SESSION          = """session.save\(\"(.*)\"\)""".r
  val EXEC_SCRIPT           = """script.exec\((.*)\)""".r

  val DETACH_PROCESS        = """\[(.*)\]""".r
  val DETACH_REPEATED       = """\[(.*)\].repeat\((\d+)\)""".r

  // void global commands
  val MODULES               = """modules|MODULES""".r
  val PROCESS_LIST          = """ps""".r

  // common
  val LIST                  = """list|LIST""".r
  val LIST_QUEUE            = """list (.*)""".r 

  // environment operations
  val SET_DOMAIN            = """(domain|DOMAIN) (.*)""".r
  val GET_DOMAIN            = """domain|DOMAIN""".r
  val SYMBOL_TABLE          = """symtable|SYMTABLE""".r

  // sequence manipulation
  val CREATE_SEQUENCE       = """(seq|SEQ) (.*) <- (.*)""".r
  val SEQUENCE_NEXT_VALUE   = """([^ ]*)\.next\(\)""".r

  // queue manipulation
  val CREATE_QUEUE          = """create queue (.*) size (\d+)""".r
  val CREATE_QUEUE_SCHEMA   = """create queue (.*) size (\d+) schema\((.*)\)""".r
  val INSERT_TO_QUEUE       = """([^ ]*) \| ([^ ]*)""".r 
  
}