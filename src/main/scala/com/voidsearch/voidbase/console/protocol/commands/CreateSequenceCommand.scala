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


import quant.timeseries.{GaussianSequence, SequenceGenerator}
import session.{SessionObject, VoidBaseConsoleSession}

case class CreateSequenceCommand(session: VoidBaseConsoleSession, variableName: String, sequenceClass : String) extends VoidBaseConsoleCommand {

  val SEQUENCE_GENERATOR_PACKAGE = "com.voidsearch.voidbase.quant.timeseries"
  
  def exec() = {

    var className = sequenceClass
    if (className.indexOf("(") > -1) {
      className = className.substring(0,(className.indexOf("(")))
    }

    var canonicalPath = SEQUENCE_GENERATOR_PACKAGE + "." + className 

    try {

      val obj = Class.forName(canonicalPath);
      var seqGenerator = obj.newInstance()
      session.addVariable(SessionObject(variableName,"SequenceGenerator"),seqGenerator)

    }  catch {
        case e => println("ERROR: failed to create sequence: " + className)
    }
  }

}