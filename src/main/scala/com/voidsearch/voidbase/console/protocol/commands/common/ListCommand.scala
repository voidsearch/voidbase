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

package com.voidsearch.voidbase.console.protocol.commands.common

import util.VoidBaseConsoleUtil
import com.voidsearch.voidbase.console.util.VoidBaseConsoleUtil
import com.voidsearch.voidbase.apps.queuetree.client.QueueTreeClient
import com.voidsearch.voidbase.console.protocol.commands.VoidBaseConsoleCommand
import com.voidsearch.voidbase.console.session.VoidBaseConsoleSession

case class ListCommand(session: VoidBaseConsoleSession) extends VoidBaseConsoleCommand {

  def exec() = {

    session.domain match {
      case "queue" =>  listQTree()
      case "qtree" =>  listQTree()
      case "queuetree" => listQTree()
      case _ => println("Invalid Domain: " + session.domain)
    }

  }

  def listQTree() = {

    var client = new QueueTreeClient(session.hostname)
    println()
    println("List of active qtree queues:")
    println()

    var res = client.list()
    println("-------------")
    processList(VoidBaseConsoleUtil.getXML(res))
    println("-------------")

  }

  def processList(node: scala.xml.Node) = {
    println(node.getClass)
  }

}