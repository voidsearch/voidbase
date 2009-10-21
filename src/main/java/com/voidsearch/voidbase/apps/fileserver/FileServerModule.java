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

package com.voidsearch.voidbase.apps.fileserver;

import com.voidsearch.voidbase.module.*;
import com.voidsearch.voidbase.config.VoidBaseConfiguration;
import com.voidsearch.voidbase.config.Config;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileServerModule implements VoidBaseModule {

    private static String contentRoot;
    private static String fileNotFound;
    private static String directoryIndex;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void initialize(String name) throws VoidBaseModuleException {
        contentRoot = VoidBaseConfiguration.get(Config.MODULES, name, Config.CONTENT_ROOT);
        fileNotFound = VoidBaseConfiguration.get(Config.MODULES, name, Config.NOT_FOUND);
        directoryIndex = VoidBaseConfiguration.get(Config.MODULES, name, Config.DIRECTORY_INDEX);

    }

    public VoidBaseModuleResponse handle(VoidBaseModuleRequest request) throws VoidBaseModuleException {

        String queryRequest = request.getRequest();
        String requestedFile = contentRoot;
        String fileExtension;

        if (queryRequest.equals("/")) {
            requestedFile += directoryIndex;
        } else {
            requestedFile += queryRequest;
        }
        try{
            fileExtension = requestedFile.substring(requestedFile.lastIndexOf(".")).toLowerCase();
        }catch (Exception e){
            fileExtension =""; 
        }
        
        //@TODO add support for gif and png
        if (fileExtension.equals(".jpg") || fileExtension.equals(".png") ) {
            // READ BINARY

            return readBinaryFile(requestedFile);
        } else {
            // READ PLAIN TEXT
            return readTextFile(requestedFile, queryRequest);
        }
    }

    //
    //  Read binary file
    //
    private VoidBaseModuleResponse readBinaryFile(String requestedFile) throws VoidBaseModuleException{

        try {
            File image = new File(requestedFile);
            byte[] bytes = new byte[(int) image.length()];
            DataInputStream in = new DataInputStream(new FileInputStream(image));
            in.readFully(bytes);
            in.close();
            VoidBaseResponseType responseType = getResponseTypeFromFilename(requestedFile);
            return new VoidBaseModuleResponse(bytes, VoidBaseResponseStatus.OK, responseType);

        } catch (Exception e) {
            throw new VoidBaseModuleException("Error reading binary file");
        }
    }

    //
    //  Read plaintext file
    //
    private VoidBaseModuleResponse readTextFile(String requestedFile, String queryRequest) throws VoidBaseModuleException {

        StringBuilder response = new StringBuilder();
        String file;
        
        try {
            response.append(getFileContent(requestedFile));
            file=requestedFile;
        }catch (Exception e){
            try {
                String errorFile = contentRoot + fileNotFound;
                response.append(getFileNotFoundResponse(getFileContent(errorFile), queryRequest));
                file=errorFile;
            } catch (Exception ex) {
                throw new VoidBaseModuleException("Error reading file");
            }
        }
               
        VoidBaseResponseType responseType = getResponseTypeFromFilename(file);
        return new VoidBaseModuleResponse(response.toString(), VoidBaseResponseStatus.OK, responseType);
    }

    public void run() {
    }


    private String getFileContent(String requestedFile) throws Exception {
        StringBuilder response = new StringBuilder();

        FileInputStream fstream = new FileInputStream(requestedFile);
        DataInputStream in = new DataInputStream(fstream);

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        String newline = "";

        while ((strLine = br.readLine()) != null) {
            response.append(newline).append(strLine);
            if (newline.equals("")) {
                newline = "\n";
            }
        }
        in.close();
        return response.toString();

    }


    private String getFileNotFoundResponse(String htmlContent, String url) {
        String port = VoidBaseConfiguration.get(Config.GLOBAL, Config.DISPATCHER, Config.PORT);
        return htmlContent.replaceAll("%URL", url).replaceAll("%PORT", port);
    }

    private VoidBaseResponseType getResponseTypeFromFilename(String fileName) {

        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        if (fileExtension.equals(".html")) {
            return VoidBaseResponseType.HTML;
        } else if (fileExtension.equals(".js")) {
            return VoidBaseResponseType.JS;
        } else if (fileExtension.equals(".xml")) {
            return VoidBaseResponseType.XML;
        } else if (fileExtension.equals(".css")) {
            return VoidBaseResponseType.CSS;
        } else if (fileExtension.equals(".jpg")) {
            return VoidBaseResponseType.JPG;
        } else if (fileExtension.equals(".png")) {
            return VoidBaseResponseType.PNG;    
        } else {
            //default type
            return VoidBaseResponseType.TEXT;
        }
    }

}
