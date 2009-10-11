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

package com.voidsearch.voidbase.module;

public class VoidBaseModuleResponse {

  public String message = null;
  public VoidBaseResponseType type = VoidBaseResponseType.UNKNOWN;
  public VoidBaseResponseStatus status = VoidBaseResponseStatus.UNKNOWN;

  public StringBuilder responseBuilder = new StringBuilder();

  public byte[] buffer = null;
  public boolean  isBinary=false;

  public VoidBaseModuleResponse() { }

  public VoidBaseModuleResponse(String message) {
    this.message = message;
    this.type = VoidBaseResponseType.HTML;
    this.status = VoidBaseResponseStatus.OK;
  }

  public VoidBaseModuleResponse(VoidBaseResponseStatus status, VoidBaseResponseType type) {
    this.status = status;
    this.type = type;
  }

  public VoidBaseModuleResponse(String message, VoidBaseResponseStatus status, VoidBaseResponseType type) {
    this.message = message;
    this.status = status;
    this.type = type;
  }

  public VoidBaseModuleResponse(byte[] buffer, VoidBaseResponseStatus status, VoidBaseResponseType type) {
    this.buffer = buffer;
    this.status = status;
    this.type = type;
    this.isBinary=true;
  }

  public void append(String content) {
      responseBuilder.append(content);
  }

  public void setStatus(VoidBaseResponseStatus status) {
      this.status = status;
  }

  public String getMessage() {
    if (message != null) {
        return message;
    } else {
        return responseBuilder.toString();
    }
  }
  

}
