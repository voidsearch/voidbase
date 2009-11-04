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

package com.voidsearch.voidbase.storage.distributed.router.topology;

public class StorageNode {

    // node params
    protected Integer id = null;
    protected String name = null;

    // network params
    protected String ip = null;
    protected String hostname = null;

    public StorageNode(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public Integer getID() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIP (String ip) {
        this.ip = ip;
    }

    public String getIP() {
        return ip;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }
}
