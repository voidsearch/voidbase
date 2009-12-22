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


import com.voidsearch.voidbase.quant.timeseries.SequenceGenerator
import session.{SessionObject, VoidBaseConsoleSession}
import voidbase.quant.timeseries.SequenceGenerator

case class NextSequenceValueCommand(session: VoidBaseConsoleSession, variable: String) extends VoidBaseConsoleCommand {

  def exec() = {

    try {
      println(getNextValue())
    } catch {
      case e:VariableNotAllocatedException => println("sequence : " + variable + " not allocated")
      case e:CastSequenceGeneratorException => println("sequence object not instance of SequenceGenerator")
    }

  }

  def getNextValue(): Double = {
    var generatorVariable = SessionObject(variable,"SequenceGenerator")
    if (session.containsVariable(generatorVariable)) {

      var generator = session.getVariable(generatorVariable) match {
        case Some(s) => s
        case None => ""
      }

      if (generator.isInstanceOf[SequenceGenerator]) {
        return generator.asInstanceOf[SequenceGenerator].next()

      } else {
        throw new CastSequenceGeneratorException()
      }
      
    } else {
      throw new VariableNotAllocatedException()
    }
  }


}

// exception hierarchy

class VariableNotAllocatedException()  extends Throwable { }
class CastSequenceGeneratorException() extends Throwable { }