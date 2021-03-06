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

package com.voidsearch.voidbase.console.syntax

// sequence shortcut -> class canonical mappings

object SequenceMapping {


  var map = Map(

    // quant

    "gaussian"      -> "quant.timeseries.GaussianSequence",
    "arima"         -> "quant.timeseries.ARIMASeries",
    "linear"        -> "quant.timeseries.LinearSeries",
    "polynomial"    -> "quant.timeseries.PolynomialSeries",
    "random_walk"   -> "quant.timeseries.RandomWalk",

    // feed processing

    "http.load"             -> "quant.feed.HttpLoadTime",
    "http.bytes"            -> "quant.feed.HttpByteCount",
    "http.regex"            -> "quant.feed.HttpRegexElement",
    "simplexml.element"     -> "quant.feed.SimpleXMLFeedElement",
    "simple.tcp.xml.element"-> "quant.feed.SimpleTCPXMLElement",  
    "file.size"             -> "quant.feed.FileSize",
    "sql.exec"              -> "quant.feed.SqlExecElement",

    // queue high-order operations

    "queue.delta"       -> "quant.queue.QueueDelta",

    // misc
    "token.freq"        -> "quant.feed.TokenFrequency"

    )

}