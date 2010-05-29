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

package com.voidsearch.voidbase.console.protocol.commands.queue

import com.voidsearch.voidbase.console.session.VoidBaseConsoleSession
import com.voidsearch.voidbase.console.protocol.commands.quant.NextSequenceValueCommand
import com.voidsearch.voidbase.console.syntax.ConsoleSyntax
import com.voidsearch.voidbase.apps.queuetree.client.QueueTreeClient
import com.voidsearch.voidbase.console.protocol.commands.VoidBaseConsoleCommand


case class InsertToQueueCommand (session: VoidBaseConsoleSession, queue: String, content : String) extends VoidBaseConsoleCommand {

  def exec() = {

    var client = new QueueTreeClient(session.hostname)

    content match {

      case ConsoleSyntax.SEQUENCE_NEXT_VALUE(variable)
        =>
        try {
          var cmd = NextSequenceValueCommand(session,variable)
          var value = cmd.getNextValue()

          if (QueueConfig.isTemporaryQueue(queue)) {
            // for temporary queue, create is always attempted before insert
            
            client.create(queue,QueueConfig.getDefaultQueueSize)
            client.put(queue,getQueueEntry(value))

          } else {

            client.put(queue,getQueueEntry(value))
            
          }
        } catch {
          case e => println("insert to queue " + queue + " failed")
        }

      case _
        =>
        try {
          client.put(queue,getQueueEntry(content))
        } catch {
          case e =>
        }

    }
    
  }

  def getQueueEntry(value : Any): String = {
    //return "<entry>" + value + "</entry>"
    return value.toString();
  }


}