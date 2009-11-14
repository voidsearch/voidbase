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

VOIDSEARCH.VoidBase.Cache = function() {

    //private properties and methods
    var cacheHost=VOIDSEARCH.VoidBase.Config.hostName;
    var cachePort=VOIDSEARCH.VoidBase.Config.port;
    var protocol=VOIDSEARCH.VoidBase.Config.protocol;
    var Core=VOIDSEARCH.VoidBase.Core;

    //public properties and methods
    return{

        get:function(key,callback){
            var url=protocol+cacheHost+cachePort+'/cache?handler=store&method=get&key='+key
            Core.AJAXGet(url,function(data){
                callback(data);        
            });
        },

        put:function(key, value,callback){
            var url=protocol+cacheHost+cachePort++'/cache?handler=store&method=put&key='+key
            Core.AJAXPostBody(url,value,function(data){
                callback(data);
            });
        },

        flush:function(key){
            // @todo implement this
        },


        readMetadata:function(key){
            // @todo implement this
        }
    };
}();