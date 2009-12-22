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

package com.voidsearch.voidbase.console.scheduler


import actors.Actor
import collection.mutable.{ListBuffer, HashMap}
import protocol.commands.VoidBaseConsoleCommand

class VoidBaseTaskScheduler extends Actor {

  // task queue management

  var taskQueue = new ListBuffer[VoidBaseConsoleCommand]();

  var scheduledTasks = new HashMap[Int, VoidBaseConsoleCommand]();
  var taskIntervals = new HashMap[Int, Int]();
  var taskDescription = new HashMap[Int, String]();

  val controller = new VoidBaseTaskSchedulerController(this)
  controller.start()

  var pidCounter = 0;

  def submitTask(task : VoidBaseConsoleCommand) = {
    taskQueue += task
  }

  def submitTask(task : VoidBaseConsoleCommand, interval: Int, description : String) = {

    scheduledTasks.put(pidCounter,task)
    taskIntervals.put(pidCounter,interval)
    taskDescription.put(pidCounter,description)

    pidCounter += 1

  }

  // actor logic
  def act() {
    react {
      case ("SCHED",pool_interval:int) =>
        sched(pool_interval)
        act()
      case ("STOP") =>
        controller.stop = true
        exit()
      case msg =>
        act()
    }
  }

  def sched(poll_interval: Int) {

    // process tasks from task queue
    for (task <- taskQueue) {
      task.exec()
    }

    // process scheduled tasks
    for (pid <- scheduledTasks.keys) {
      if ((poll_interval % taskIntervals(pid)) == 0) {
        scheduledTasks(pid).exec()
      }
    }

  }

  // process control

  def listProcesses() {
    println("pid\tinterval\ttask")
    for (pid <- scheduledTasks.keys) {
      println(pid + "\t" + taskIntervals(pid) + "\t\t" + "[" + taskDescription(pid) + "]")

    }
  }

  // remove single pid from task queue
  // (todo : add synchronization blocks)
  def killPid(pid : Int) {
    scheduledTasks -= pid
    taskIntervals -= pid
    taskDescription -= pid
  }

}


// feed processing controller
class VoidBaseTaskSchedulerController(scheduler: VoidBaseTaskScheduler) extends Actor {
  val POLL_INTERVAL = 1000
  val MAX_COUNTER   = 1000000
  var counter = 0

  var stop = false

  def act() {
    while(true) {
      counter = (counter + POLL_INTERVAL)%MAX_COUNTER
      scheduler ! ("SCHED",counter)
      Thread.sleep(POLL_INTERVAL)
      if (stop) {
        exit()
      }
    }
  }
  
}