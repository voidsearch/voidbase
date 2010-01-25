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


package com.voidsearch.test.prototype;

import org.testng.annotations.Test;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SimpleVoidUtilsTest {

    @Test
    public void nullTest() {

        System.out.println("TEST");

        //String regex = "<title>(.*)</title>";

        String regex = "<span id=\"ref_983582_l\">(\\w.*)</span>";

        Pattern pattern = Pattern.compile(regex);

        //String data = "fd <title>Dow Jones Industrial Average - Google Finance</title> fdsfds";
        String data = "<span class=\"pr\"><span id=\"ref_983582_l\">10,228.00</span></span><div id=price-change class=nwp><span class=\"ch bld\"><span class=\"chg\" id=";
        Matcher matcher = pattern.matcher(data);

        //while (matcher.matches()) {
        //    System.out.println("MATCHES : " + matcher.group());
        //}

        while (matcher.find()) {
            System.out.println("FIND : " + matcher.group(1));
        }

        System.out.println("/TEST");
        
    }

}
