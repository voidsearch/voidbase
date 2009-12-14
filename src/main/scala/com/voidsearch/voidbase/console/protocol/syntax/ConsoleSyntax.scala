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

  val EXIT             = """exit|EXIT""".r
  val QUIT             = """quit|QUIT""".r
  val HELP             = """help|HELP""".r
  val LIST             = """list|LIST""".r

  val SET_DOMAIN       = """domain|DOMAIN (.*)""".r
  val GET_DOMAIN       = """domain|DOMAIN""".r
  val CREATE_SEQUENCE  = """seq (.*) <- (.*)""".r
  val CREATE_QUEUE     = """create queue (.*) size (\d+)""".r
  val SYMBOL_TABLE     = """symtable|SYMTABLE""".r
  
}