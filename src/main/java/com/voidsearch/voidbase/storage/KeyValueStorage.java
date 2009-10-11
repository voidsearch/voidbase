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

package com.voidsearch.voidbase.storage;

public interface KeyValueStorage {
    public void open(String name, String path) throws StorageException;
    public void close(String name) throws StorageException;
    public void flush(String name) throws StorageException;

    public void put(String name, String key, String val) throws StorageException;
    public String get(String name, String key) throws StorageException;
    public void delete(String name, String key) throws StorageException;

    public void put(String name, byte[] key, byte[] val) throws StorageException;
    public byte[] get(String name, byte[] key) throws StorageException;
    public void delete(String name, byte[] key) throws StorageException;
}
