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

import feed.FeedFetcher
import scala.xml._
import scala.collection.immutable.Map

class LatLon(lat: Double, lon: Double) {
  def getLat(): Double = {return lat}
  def getLon(): Double = {return lon}
}

class TwitterGeoFeedFetcher extends FeedFetcher {

  val baseURL = "http://search.twitter.com/search.atom"

  val defaultRange = 500
  val delayInterval = 5000

  val geolocation = Map(
    "New York" -> new LatLon(40.709740, -74.003480),
    "London" -> new LatLon(51.516700, -0.0833000),
    "San Francisco" -> new LatLon(37.775000, -122.418333),
    "Tokyo" -> new LatLon(35.670000, 139.770000),
    "Boston" -> new LatLon(42.358370, -71.060220),
    "Chicago" -> new LatLon(41.647040, -87.620670),
    "Seattle" -> new LatLon(47.647300, -122.399980),
    "Berlin" -> new LatLon(52.500000, 13.4166667),
    "Belgrade" -> new LatLon(44.819000, 20.46800),
    "Dublin" -> new LatLon(53.330000, -6.250000)
    )

  val locationList = (geolocation.keys).toList
  var position = 0

  // as defined by FeedFetcher interface :

  def fetch():Elem = {
    return fetchFeed(nextLocation)
  }

  def getID():String = {
    return getLocation
  }

  def getInterval():int = {
    return delayInterval
  }

  // implementation

  def getTwitterGeocodeURL(latlon: LatLon, range: Int): String = {
    return getTwitterGeocodeURL(latlon.getLat(), latlon.getLon(), range)
  }

  def getTwitterGeocodeURL(lat: Double, lon: Double, range: Int): String = {
    return baseURL + "?geocode=" + lat + "%2C" + lon + "%2C" + range + "km"
  }

  def fetchXML(url: String): Elem = {
    println("fetch: " + url)
    val rtxmlstr = _root_.scala.io.Source.fromURL(url).getLines.mkString("")
    return _root_.scala.xml.XML.loadString(rtxmlstr)
  }

  def fetchFeed(location : String): Elem = {
    return fetchXML(getTwitterGeocodeURL(geolocation(location), defaultRange))
  }

  def getLocation:String = {
    return locationList(position)
  }

  def nextLocation:String = {
    val location = locationList(position)
    position = (position + 1)%locationList.size
    return location
  }

}

