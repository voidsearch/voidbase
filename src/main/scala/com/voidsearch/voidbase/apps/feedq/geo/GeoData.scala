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

import scala.xml._

abstract class GeoData {

  val name : String
  val address : String
  val CountryNameCode : String
  val CountryName : String
  val AdministrativeAreaName : String
  val LocalityName : String
  val coordinates : String
  val lat : String
  val lon : String

  def toXML =
    <doc>
      <field name="name">{name}</field>
      <field name="address">{address}</field>
      <field name="CountryNameCode">{CountryNameCode}</field>
      <field name="CountryName">{CountryName}</field>
      <field name="AdministrativeAreaName">{AdministrativeAreaName}</field>
      <field name="LocalityName">{LocalityName}</field>
      <field name="coordinates">{coordinates}</field>
      <field name="lat">{lat}</field>
      <field name="lon">{lon}</field>
    </doc>

}

object GoogleMapsInfer {

  val baseURL = "http://maps.google.com/maps/geo?output=xml&oe=utf8&sensor=true_or_false&key=ABQIAAAAlmHjDJ0uvMJUzwabW9IBQxREcAB04tpspBMBpPaqQYfLXdAfmBS5Qw-MjjMP-VJVnrHfKDo3UFqqAg&q="

  def getGeoData(locationString : String): GeoData = {
    return fromXML(fetchXML(baseURL+locationString))
  }

  def fetchXML(url: String): Elem = {
    val rtxmlstr = _root_.scala.io.Source.fromURL(url).getLines.mkString("")
    return _root_.scala.xml.XML.loadString(rtxmlstr)
  }

 // define deserialization from twitter format
  def fromXML(node: scala.xml.Node): GeoData = {
    new GeoData {
      val name = (node \ "Response" \ "name").text
      val address = (node \ "Response" \ "Placemark" \ "address").text
      val CountryNameCode = (node \ "Response" \ "Placemark" \ "AddressDetails" \ "Country" \ "CountryNameCode").text
      val CountryName = (node \ "Response" \ "Placemark" \ "AddressDetails" \ "Country" \ "CountryName").text
      val AdministrativeAreaName = (node \ "Response" \ "Placemark" \ "AddressDetails" \ "Country" \ "AdministrativeArea" \ "AdministrativeAreaName").text
      val LocalityName = (node \ "Response" \ "Placemark" \ "AddressDetails" \ "Country" \ "AdministrativeArea" \ "Locality" \ "LocalityName").text
      val coordinates = (node \ "Response" \ "Placemark" \ "Point" \ "coordinates").text
      val lat = getLat(coordinates)
      val lon = getLon(coordinates)
    }
  }

  def getLat(coordinates: String):String = {
    val parts = coordinates.split(",")
    if (parts.length == 3)
      return parts(0)
    return "-1.0"
  }

  def getLon(coordinates: String):String = {
    val parts = coordinates.split(",")
    if (parts.length == 3)
      return parts(1)
    return "-1.0"
  }

  def main(args: Array[String]) {
  }

}

