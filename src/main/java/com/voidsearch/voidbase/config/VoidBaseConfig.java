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

package com.voidsearch.voidbase.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import com.voidsearch.voidbase.util.GenericUtil;

import java.io.File;
import java.util.*;

public class VoidBaseConfig {
    XMLConfiguration config = null;
    protected static VoidBaseConfig container = null;
    protected static final Logger logger = LoggerFactory.getLogger(VoidBaseConfig.class.getName());

    public static final String DEFAULT_PATH = "conf/";

    protected VoidBaseConfig() {
        super();
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static synchronized VoidBaseConfig getInstance() throws ConfigException {
        if (container == null) {
            container = new VoidBaseConfig();
            container.init();
        }

        return container;
    }

    public static synchronized VoidBaseConfig getInstance(String files) throws ConfigException {
        if (container == null) {
            container = new VoidBaseConfig();
            container.init(files);
        }

        return container;
    }

    public void init() throws ConfigException {
        init(null);    
    }

    public void init(String files) throws ConfigException {
        List<String> fileList;

        // get file list
        if (files == null) {
            logger.info("Configuration not set - initialize from './conf' dir");
            fileList = getConfigFiles();
        } else {
            fileList = Arrays.asList(files.split(","));
        }

        // santy check
        if (fileList == null || fileList.size() == 0) {
            throw new ConfigException("No valid configuration file.");
        }

        config = mergeConfiguration(fileList);
    }

    protected XMLConfiguration mergeConfiguration(List<String> files) throws ConfigException {
        XMLConfiguration config = new XMLConfiguration();

        for (String file: files) {
            Iterator<String> keys;
            XMLConfiguration fragment;

            try {
                logger.info("Loading configuration file: " + file);
                fragment = new XMLConfiguration(file);
                keys = fragment.getKeys();
            } catch(ConfigurationException e) {
                GenericUtil.logException(e);
                throw new ConfigException("Failed to load configuration from: " + file);
            }

            while(keys.hasNext()) {
                String key = keys.next();

                // sanity check
                if (config.containsKey(key)) {
                    throw new ConfigException("Collision of keys for: " + key);
                }

                // merge configuration
                config.addProperty(key, fragment.getProperty(key));
            }
        }
        
        return config;
    }

    protected void setXMLConfig(XMLConfiguration config)  {
        this.config = config;
    }

    protected XMLConfiguration getXMLConfig()  {
        return config;
    }

    public String getString(String name, String key) {
        String path = getPath(name, key);

        // sanity check
        if (path == null)
            return null;

        return config.getString(path.toString());
    }

    public Double getDouble(String name, String key) {
        String path = getPath(name, key);

        // sanity check
        if (path == null)
            return null;

        return config.getDouble(path.toString());
    }

    public Integer getInteger(String name, String key) {
        String path = getPath(name, key);

        // sanity check
        if (path == null)
            return null;

        return config.getInt(path.toString());
    }

    public Boolean getBoolean(String name, String key) {
        String path = getPath(name, key);

        // sanity check
        if (path == null)
            return null;

        return config.getBoolean(path.toString());
    }

    public String getAttribute(String name, String key, String attribute) {
        String path = getPath(name, key);

        // sanity check
        if (path == null)
            return null;

        return getAttribute(path.toString(), attribute);
    }

    public List<String> getAttributes(String name, String key, String attribute) {
        String path = getPath(name, key);

        // sanity check
        if (path == null)
            return null;

        return getAttributes(path.toString(), attribute);
    }

    public String getString(String key) {
        if (key == null)
            return null;

        return config.getString(key);
    }

    public Double getDouble(String key) {
        if (key == null)
            return null;

        return config.getDouble(key);
    }

    public Integer getInteger(String key) {
        if (key == null)
            return null;

        return config.getInt(key);
    }

    public Boolean getBoolean(String key) {
        if (key == null)
            return null;

        return config.getBoolean(key);
    }

    public List<String> getList(String name, String key) {
        String path = getPath(name, key);

        // sanity check
        if (path == null)
            return null;

        return getList(path.toString());
    }

    public Set<String> getKeys(String name, String key) {
        String path = getPath(name, key);

        // sanity check
        if (path == null)
            return null;

        return getKeys(path.toString());
    }

    public Map<String, String> getMap(String name, String key) {
        String path = getPath(name, key);

        // sanity check
        if (path == null)
            return null;

        return getMap(path.toString());
    }


    public List<String> getList(String key)  {
        Iterator iterator;
        ArrayList<String> list = new ArrayList<String>();

        if (key == null)
            return null;

        iterator = config.getKeys(key);
        
        while (iterator.hasNext()) {
            String subKey = (String)iterator.next();
            List elements = config.getList(subKey);

            for (Object element: elements) {
                list.add(element.toString());
            }
        }

        return list;
    }

    public Set<String> getKeys(String key)  {
        Iterator iterator;
        Set<String> list = new HashSet<String>();

        if (key == null)
            return null;

        iterator = config.getKeys(key);

        while (iterator.hasNext()) {
            String subKey = cleanKey((String)iterator.next());
            list.add(subKey);
        }

        return list;
    }

    public Map<String, String> getMap(String key)  {
        Iterator iterator;
        HashMap<String, String> map = new HashMap<String, String>();

        if (key == null)
            return null;

        iterator = config.getKeys(key);

        while (iterator.hasNext()) {
            String subKey = (String)iterator.next();
            List elements = config.getList(subKey);

            for (Object element: elements) {
                map.put(cleanKey(subKey), element.toString());
            }
        }

        return map;
    }

    public String getAttribute(String key, String attribute) {
        StringBuilder attributeKey = new StringBuilder(key);

        if (key == null)
            return null;

        attributeKey . append("[@") . append(attribute) . append("]");

        return config.getString(attributeKey.toString());
    }

    public List<String> getAttributes(String key, String attribute) {
       Iterator iterator;
       ArrayList<String> list = new ArrayList<String>();
       StringBuilder attributeKey = new StringBuilder(key);

        if (key == null)
            return null;

        attributeKey . append("@") . append(attribute);

        iterator = config.getKeys(attributeKey.toString());

        while (iterator.hasNext()) {
            String subKey = (String)iterator.next();
            List elements = config.getList(subKey);

            for (Object element: elements) {
                list.add(element.toString());
            }
        }

        return list;
    }

    protected String getPath(String name, String key) {
       StringBuilder path = new StringBuilder();

        // sanity check
        if (name == null || key == null) {
            logger.debug("'name' or 'key' are null");
            return null;
        }
        if (config == null) {
            logger.warn("Configuration not initialized.");
            return null;
        }

        // build a path
        path . append(name) . append(".")
             . append(key);

        return path.toString();
    }

    protected ArrayList<String> getConfigFiles() {
        File dir = new File(DEFAULT_PATH);
        ArrayList<String> files = new ArrayList<String>();          

        for (File file: dir.listFiles()) {
            if (file.isFile()) {
                files.add(file.getAbsolutePath());
            }
        }

        return files;
    }

    protected String cleanKey(String key) {
        Integer lpos;
        Integer rpos;

        if (key == null)
            return null;

        lpos = key.lastIndexOf(".");

        if (lpos < 0)
            return key;
        if (lpos == key.length())
            return "";

        rpos = key.indexOf("[");

        if (rpos < 0)
            return key.substring(lpos + 1, key.length());

        return key.substring(lpos + 1, rpos);
    }
}
