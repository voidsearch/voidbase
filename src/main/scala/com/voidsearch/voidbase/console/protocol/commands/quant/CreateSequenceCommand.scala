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

import collection.mutable.ListBuffer
import console.syntax.SequenceMapping
import java.util.ArrayList
import session.{SessionObject, VoidBaseConsoleSession}
import voidbase.quant.timeseries.DirectSequence
import voidbase.util.GenericUtil

case class CreateSequenceCommand(session: VoidBaseConsoleSession, variableName: String, sequenceString : String) extends VoidBaseConsoleCommand {

  val VOIDBASE_PACKAGE = "com.voidsearch.voidbase"
  
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

    var className = getClassName(sequenceString)

    if (SequenceMapping.map.contains(className)) {
      className = SequenceMapping.map(className)
    }

    var classParams: ListBuffer[String] = getClassParams(sequenceString)

    var canonicalPath = VOIDBASE_PACKAGE + "." + className

    try {

      val obj = Class.forName(canonicalPath);

      if (classParams.size == 0) {

        var seqGenerator = obj.newInstance()
        session.addVariable(SessionObject(variableName, "SequenceGenerator"), seqGenerator)

      } else {

        var constructors = obj.getDeclaredConstructors()
        for (constructor <- constructors) {

          var params = new ArrayList[String]()
          for (param <- classParams) {
            params.add(param)
          }

          var paramTypes = constructor.getParameterTypes()

          if (paramTypes.size == classParams.size) {

            var seqGenerator = GenericUtil.getInstance(constructor, params);
            session.addVariable(SessionObject(variableName, "SequenceGenerator"), seqGenerator)

          }

        }

      }      

    } catch {
      case e => println("ERROR: failed to create sequence: " + className)
    }

  }

  def getClassName(_classString: String) : String = {
    return _classString.substring(0,_classString.indexOf("("))
  }

  def getClassParams(_classText: String) : ListBuffer[String] = {
    var result = new ListBuffer[String]()
    var content = _classText.substring(_classText.indexOf("(") + 1,_classText.indexOf(")"))
    if (content.length > 0) {
      var parts = content.split(",")
      for (part:String <- parts) {
        result += part.replaceAll("\"","")
      }
    }
    return result
  }

}