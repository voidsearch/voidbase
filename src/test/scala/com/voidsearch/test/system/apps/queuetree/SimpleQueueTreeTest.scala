package com.voidsearch.test.system.apps.queuetree

import org.testng.annotations._
import xml.Elem

/**
 * simple test of queuetree http service
 *
 * @author Aleksandar Bradic
 *
 */


class SimpleQueueTreeTest {

  @Test
  def nullTest() = {
    var baseUrl = "http://localhost:8080/queuetree?method=LIST"
    println(fetchXML(baseUrl))
  }


  def fetchXML(url: String): Elem = {
    println("fetch: " + url)
    val rtxmlstr = _root_.scala.io.Source.fromURL(url).getLines.mkString("")
    return _root_.scala.xml.XML.loadString(rtxmlstr)
  }

}