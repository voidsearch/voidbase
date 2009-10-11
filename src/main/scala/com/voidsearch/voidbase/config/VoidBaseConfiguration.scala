package com.voidsearch.voidbase.config

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


import collection.mutable.{ListBuffer, HashMap}
import io.Source
import java.util.LinkedList
import xml.XML

object VoidBaseConfiguration {

  val CONF_FILE = "conf/voidbase.xml"  // todo - get this somehow smarter

  var conf = XML.loadString(Source.fromFile(CONF_FILE).getLines.mkString(""))

  var confStorage = new HashMap[String, HashMap[String, HashMap[String, String]]]()

  loadConf();

  private def loadConf() = {

    for (entry <- conf\"_") {

      if (!confStorage.contains(entry.label)) {
        confStorage(entry.label) = new HashMap[String, HashMap[String, String]]()
      }
      var domainMap = confStorage(entry.label)

      for (segment <- entry\"_" ) {

        if (!domainMap.contains(segment.label)) {
          domainMap(segment.label) = new HashMap[String, String]();
        }
        var segmentMap = domainMap(segment.label)

        for (param <- segment\"_") {
          segmentMap(param.label) = param.text
        }
        domainMap(segment.label) = segmentMap

      }
      confStorage(entry.label) = domainMap
    }
  }

  def getXPath(xpath: String): String = {
    return (conf \ xpath).text
  }

  def get(domain: String, segment: String, param: String): String = {
    if (confStorage.contains(domain)) {
      var segmentMap = confStorage(domain)
      if (segmentMap.contains(segment)) {
        var paramMap = segmentMap(segment)
        if (paramMap.contains(param)) {
          return paramMap(param)
        } else {
          return null
        }
      }
    }
    return null
  }

  def contains(domain: String, segment: String, param: String): boolean = {
    var value = get(domain,segment,param);
    if (value == null) {
      return false
    } else {
      return true
    }
  }


  def getKeyList(domain: String): LinkedList[String] = {
    if (confStorage.contains(domain)) {
      var domainMap:HashMap[String, HashMap[String, String]] = confStorage(domain)
      var result = new LinkedList[String]();
      for (key <- domainMap.keys) {
        result.add(key);
      }
      return result
    }
    return null;
  }


   def getKeyList(domain: String,segment: String): LinkedList[String] = {
    if (confStorage.contains(domain)) {
      var domainMap:HashMap[String, HashMap[String, String]] = confStorage(domain)
      if(domainMap.contains(segment)){
          var segmentMap: HashMap[String, String]=domainMap(segment);
          var result = new LinkedList[String]();
          for (key <- segmentMap.keys) {
            result.add(key);
          }
          return result
      }
      return null
    }
    return null;
  }
  
}