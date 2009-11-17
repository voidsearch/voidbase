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

// BASE
VOIDSEARCH = {};

VOIDSEARCH.VoidBase = function() {
    return{
        extend:function(destination, source) {
            for (var property in source) {
                destination[property] = source[property];
            }
            destination._observers = destination._observers || {};
            return destination;
        }
    };
}();

VOIDSEARCH.Events = function() {
    return{
        addObserver:function(eventName, observer) {
            this._observers[eventName] = this._observers[eventName] || [];
            this._observers[eventName].push(observer)
        },

        notify:function(eventName) {
            var args = $A(arguments).slice(1);

            try {
                for (var i = 0; i < this._observers[eventName].length; ++i)
                    this._observers[eventName][i].apply(this._observers[eventName][i], args);
            } catch(e) {
                if (e == $break)
                    return false;
                else
                    throw e;
            }
        }
    };
}();

// CORE
//@todo add onFailure handlers for all methods
VOIDSEARCH.VoidBase.Core = function() {
    return{
        observers:[],

        //get JSON data
        AJAXGetJSON:function(url, callback) {
            new Ajax.Request(url, {
                method: 'get',
                onSuccess: function(transport) {
                    var data = transport.responseJSON;
                    callback(data);
                }
            });
        },

        // get data by AJAX call and return as plain text
        AJAXGet:function(url, callback) {
            new Ajax.Request(url, {
                method: 'get',
                onSuccess: function(transport) {
                    var data = transport.responseText;
                    callback(data);
                }
            });
        },
        //post data to url as postBody
        AJAXPostBody:function(url,data,callback){
             new Ajax.Request(url, {
                method: 'post',
                postBody: data,
                onSuccess: function(transport) {
                    var data = transport.responseText;
                    callback(data);
                }
            });
        }
    };

}();


// UTIL MODULE
VOIDSEARCH.VoidBase.Util = function() {
    // private variables
    var DEFAULT_PATTERN_IMAGE = '/files/img/pattern_bg_200px.png';

    return{
        addGrid:function(elementId) {
            var element = $(elementId);
            if (element.style.background == "") {
                element.style.background = "url('" + DEFAULT_PATTERN_IMAGE + "')";
            } else {
                element.style.background = '';
            }
        },

        test:function() {

            console.log('hello from test');

        }
    };

}();



VOIDSEARCH.VoidBase.extend(VOIDSEARCH.VoidBase.Core, VOIDSEARCH.Events);