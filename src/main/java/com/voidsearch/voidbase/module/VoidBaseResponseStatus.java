package com.voidsearch.voidbase.module;

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

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public enum VoidBaseResponseStatus {
  OK,
  ERROR,
  INTERNAL_ERROR,
  UNKNOWN;

  public static VoidBaseResponseStatus deserialize(String type) {
    if (type.toUpperCase().equals("OK"))
      return OK;
    if (type.toUpperCase().equals("ERROR"))
      return ERROR;
    if (type.toUpperCase().equals("INTERNAL_ERROR"))
      return INTERNAL_ERROR;

    return UNKNOWN;
  }

  public String serialize() {
    return this.toString();
  }

  public static VoidBaseResponseStatus deserializeFromHttpResponseStatus(HttpResponseStatus status) {
    if (status == HttpResponseStatus.OK)
      return OK;
    if (status == HttpResponseStatus.BAD_REQUEST)
      return ERROR;
    if (status == HttpResponseStatus.INTERNAL_SERVER_ERROR)
      return INTERNAL_ERROR;

    return UNKNOWN;
  }

  public HttpResponseStatus serializeToHttpResponseStatus() {
    if (this == OK)
      return HttpResponseStatus.OK;
    if (this == ERROR)
      return HttpResponseStatus.BAD_REQUEST;
    if (this == INTERNAL_ERROR)
      return HttpResponseStatus.INTERNAL_SERVER_ERROR;

    return HttpResponseStatus.NOT_IMPLEMENTED;
  }
}
