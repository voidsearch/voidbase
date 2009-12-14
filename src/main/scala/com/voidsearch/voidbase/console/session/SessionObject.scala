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

package com.voidsearch.voidbase.console.session

case class SessionObject(name : String, _type: String) {

  def getName(): String = {
    return name;
  }

  def getType(): String = {
    return _type;
  }

  def isType(otherType : String): boolean = {
    return _type.equals(otherType)
  }

  override def hashCode(): Int = {
    return name.hashCode()
  }

}