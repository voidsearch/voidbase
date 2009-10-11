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

package com.voidsearch.voidbase.apps.feedq.connector.twitter

import geo.{GeoData, TimezoneData}
import java.io.File
import java.text.SimpleDateFormat
//import org.noodle.feed.twitter.StopwordsContainer
import scala.xml._

// Tweet structure
abstract class Tweet {

  val id : String
  val published : String
  val updated : String
  val location : String
  val language : String
  val source : String
  val link : String
  val title : String
  val content : String
  val author_name : String
  val author_uri : String
  val valid : String
  val top_keywords : String
  val local_time : String
  val part_of_day : String
  val day_of_week : String
  val dow_numeric : Int

  var country = "unknown"
  var rlocation = "unknown"
  var lat = "-1.0"
  var lon = "-1.0"
  var hot_keywords = ""
  var offset = ""
  var local_fetch_time = ""

  //  val timezone_offset : String

  def updateGeoInfo(geoInfer : GeoData) = {
    country = geoInfer.CountryName
    rlocation = geoInfer.name
    lat = geoInfer.lat
    lon = geoInfer.lon
  }

  def updateTimezoneInfo(timezone: TimezoneData) = {
    offset = timezone.offset
    local_fetch_time = timezone.localtime
  }

  def updateHotKeywords(_hot_keywords: String) = {
    hot_keywords = _hot_keywords
  }

  // serialization to Solr format
  def toXML =
    <doc>
      <field name="id">{id}</field>
      <field name="published">{published}</field>
      <field name="updated">{updated}</field>
      <field name="location">{location}</field>
      <field name="language">{language}</field>
      <field name="link">{link}</field>
      <field name="title">{title}</field>
      <field name="author_name">{author_name}</field>
      <field name="author_uri">{author_uri}</field>
      <field name="valid">{valid}</field>
      <field name="top_keywords">{top_keywords}</field>
      <field name="local_time">{local_time}</field>
      <field name="part_of_day">{part_of_day}</field>
      <field name="day_of_week">{day_of_week}</field>
      <field name="dow_numeric">{dow_numeric}</field>
      <field name="country">{country}</field>
      <field name="rlocation">{rlocation}</field>
      <field name="lat">{lat}</field>
      <field name="lon">{lon}</field>
      <field name="hot_keywords">{hot_keywords}</field>
      <field name="offset">{offset}</field>
      <field name="local_fetch_time">{local_fetch_time}</field>
    </doc>

}

object TweetIndexEntry {

  val archiveDir = "/home/largo/data/tweets/"

  //val archiveDir = "/Data/SVN/ramenlogic/svn/branches/noodle/data/sample/tweet/"
  val queueFile = "/home/largo/data/queue/tweet_queue.xml"
  val delayInterval = 10000
  def postBufferSize = 30

  def podMap = Map(
    "01" -> "night",
    "02" -> "night",
    "03" -> "night",
    "04" -> "night",
    "05" -> "night",
    "06" -> "morning",
    "07" -> "morning",
    "08" -> "morning",
    "09" -> "worktime",
    "10" -> "worktime",
    "11" -> "worktime",
    "12" -> "worktime",
    "13" -> "worktime",
    "14" -> "worktime",
    "15" -> "worktime",
    "16" -> "worktime",
    "17" -> "worktime",
    "18" -> "afternoon",
    "19" -> "afternoon",
    "20" -> "evening",
    "21" -> "evening",
    "22" -> "evening",
    "23" -> "evening",
    "24" -> "night",
    "00" -> "night"
    )

  def dowMap = Map(
    0 -> "Sunday",
    1 -> "Monday",
    2 -> "Tuesday",
    3 -> "Wednesday",
    4 -> "Thursday",
    5 -> "Friday",
    6 -> "Saturday"
    )

  def processArchive(dir : String) = {
    println("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
    println("<add>")
    var files = (new java.io.File(archiveDir)).listFiles
    for (file <- files) {
      if (!file.isHidden())
        getSolrImportXML(file)
    }
    println("</add>")
  }

  def getSolrImportXML(file : File) = {
    var loadnode = scala.xml.XML.loadFile(file)
    for (val entry <- loadnode \\ "entry") {
      val piece = fromXML(entry)
      println(piece.toXML)
    }
  }

  // define deserialization from twitter format
  def fromXML(node: scala.xml.Node): Tweet = {
    new Tweet {
      val id = (node \ "id").text
      val published = (node \ "published").text
      val updated = (node \ "updated").text
      val location = (node \ "location").text
      val language = (node \ "lang").text
      val source = (node \ "source").text
      val link = (node \ "link").text
      val title = (node \ "title").text
      val content = (node \ "content").text
      val author_name = (node \ "author" \ "name").text
      val author_uri = (node \ "author" \ "uri").text
      val valid = "true"
      // derived fields
      val top_keywords = getTopKeywords(title)
      val local_time = getLocalTime(updated)
      val part_of_day = getPartOfTheDay(local_time)
      val day_of_week = dowMap(getDayOfWeek(updated))
      val dow_numeric = getDayOfWeek(updated)

    }
  }

  // extract top keywords
  def getTopKeywords(text : String) : String = {
    var res = ""
    var separator = ""
    var keywords = text.split(" ")
    for (word <- keywords) {
      if (word.size > 3) {
        var token = (word.toLowerCase).replaceAll("[^a-zA-Z0-9@#]", "")
        if ((token.size > 3) && (token.indexOf("http") == -1) && (token.indexOf("url") == -1) && (token.indexOf("www") == -1)) {
          res += separator + token
          separator = " "
        }
      }
    }
    return res
  }

  // extract local time
  def getLocalTime(updated : String) : String = {
    var hour = ((updated.split("T"))(1)).split(":")(0)
    return hour
  }

  // extract part of the day
  def getPartOfTheDay(localTime : String) : String = {
    return podMap(localTime)
  }

  // get day of the week
  def getDayOfWeek(updated : String) : Int = {
    var df = new SimpleDateFormat("yyyy-MM-dd");
    var date = ((updated.split("T"))(0))
    var today = df.parse(date);
    return today.getDay()
  }

}
