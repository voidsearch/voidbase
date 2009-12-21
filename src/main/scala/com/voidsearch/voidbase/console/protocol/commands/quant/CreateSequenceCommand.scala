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

package com.voidsearch.voidbase.console.protocol.commands.quant

import session.{SessionObject, VoidBaseConsoleSession}
import voidbase.quant.timeseries.DirectSequence

case class CreateSequenceCommand(session: VoidBaseConsoleSession, variableName: String, sequenceString : String) extends VoidBaseConsoleCommand {

  val SEQUENCE_GENERATOR_PACKAGE = "com.voidsearch.voidbase.quant.timeseries"
  
  def exec() = {

    if (isDirectArray(sequenceString)) {
      registerSequence()
    } else {
      registerClass()
    }

  }

  // check if passed value is a directly-addressed array
  def isDirectArray(sequenceString : String) : boolean = {
    return sequenceString.startsWith("[") && sequenceString.endsWith("]")
  }

  // register direct sequence
  def registerSequence() = {

    var valArrayString = sequenceString.replaceAll("\\[","").replaceAll("\\]","")
    var seq = valArrayString.split(",")
    session.addVariable(SessionObject(variableName,"SequenceGenerator"), new DirectSequence(seq))

  }

  // reqister sequence generator class
  def registerClass() = {

    var className = sequenceString
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