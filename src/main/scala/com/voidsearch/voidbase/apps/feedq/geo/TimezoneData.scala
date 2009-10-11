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


package com.voidsearch.voidbase.apps.feedq.geo

import scala.xml.Elem

abstract class TimezoneData {

  val offset : String
  val suffix : String
  val localtime : String
  val isotime : String
  val utctime : String
  val dst : String

  def toXML =
    <doc>
      <field name="offset">{offset}</field>
      <field name="suffix">{suffix}</field>
      <field name="localtime">{localtime}</field>
      <field name="isotime">{isotime}</field>
      <field name="utctime">{utctime}</field>
      <field name="dst">{dst}</field>
    </doc>

}

object EarthToolsTimezoneInfer {

  val baseURL = "http://www.earthtools.org/timezone/"

  def getTimezoneData(lat:String, lon:String): TimezoneData = {
    return fromXML(fetchXML(getQueryUrl(lat,lon)))
  }

  def fetchXML(url: String): Elem = {
    val rtxmlstr = _root_.scala.io.Source.fromURL(url).getLines.mkString("")
    return _root_.scala.xml.XML.loadString(rtxmlstr)
  }

  def getQueryUrl(lat:String, lon:String):String = {
    return baseURL + lat + "/" + lon
  }

  def fromXML(node: scala.xml.Node): TimezoneData = {
    new TimezoneData {
      val offset = (node \ "offset").text
      val suffix = (node  \ "suffix").text
      val localtime = (node  \ "localtime").text
      val isotime = (node  \ "isotime").text
      val utctime = (node  \ "utctime").text
      val dst = (node  \ "dst").text
    }
  }

}