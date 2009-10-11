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

package com.voidsearch.voidbase.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class GenericUtil {
  protected static final Logger logger = LoggerFactory.getLogger(GenericUtil.class.getName());

  //
  // CONVERT UTILS
  //

  public static String toString(Object o) {

    if (o.getClass() == String.class) {
      return (String)o;
    }
    if (o.getClass() == Integer.class) {
      return Integer.toString((Integer)o);
    }
    if (o.getClass() == Long.class) {
      return Long.toString((Long)o);
    }
    if (o.getClass() == Double.class) {
      return Double.toString((Double)o);
    }
    if (o.getClass() == Float.class) {
      return Float.toString((Float)o);
    }

    return null;
  }

  public static ArrayList<String> split(String str, char delimiter) {
    int prev = 0;
    int len = str.length();
    ArrayList<String> list = new ArrayList();

    for(int i = 0; i < len; i++) {
      char c = str.charAt(i);

      if (c == delimiter) {
        if (i > prev)
          list.add(str.substring(prev, i));

        prev = i + 1;
      }
    }
    if (len > prev) {
      list.add(str.substring(prev, len));
    }

    return list;
  }

  //
  // EXCEPTION LOGGER
  //

  public static void logException(Exception e) {
    logException(e.getMessage(), e.getStackTrace());
  }

  public static void logException(String message, StackTraceElement[] stackTrace) {
    logger.error("===================== EXCEPTION LOG START =====================");
    logger.error("Message: " + message);

    for(StackTraceElement element: stackTrace) {
      logger.error(element.toString());
    }
    logger.error("=====================  EXCEPTION LOG END  =====================");
  }

  //
  // VALIDATION
  //

  public static Boolean isEmpty(String s) {
    if (s != null && !s.equals("") && !s.equals("\\N") && !s.toLowerCase().equals("null"))
      return false;

    return true;
  }

  public static Boolean isTrue(String s) {
    if (s == null)
      return false;

    String val = s.toLowerCase();

    if (val.equals("true") || val.equals("y") || val.equals("yes"))
      return true;

    return false;
  }
}
