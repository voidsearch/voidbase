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
        AJAXPostBody:function(url, data, callback) {
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
        _keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

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

        },

        base64Encode : function (input) {
            var output = "";
            var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
            var i = 0;

            input = this._utf8_encode(input);

            while (i < input.length) {

                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);

                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;

                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }

                output = output +
                         this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
                         this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

            }

            return output;
        },

        // public method for decoding
        base64Decode : function (input) {
            var output = "";
            var chr1, chr2, chr3;
            var enc1, enc2, enc3, enc4;
            var i = 0;

            input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

            while (i < input.length) {

                enc1 = this._keyStr.indexOf(input.charAt(i++));
                enc2 = this._keyStr.indexOf(input.charAt(i++));
                enc3 = this._keyStr.indexOf(input.charAt(i++));
                enc4 = this._keyStr.indexOf(input.charAt(i++));

                chr1 = (enc1 << 2) | (enc2 >> 4);
                chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                chr3 = ((enc3 & 3) << 6) | enc4;

                output = output + String.fromCharCode(chr1);

                if (enc3 != 64) {
                    output = output + String.fromCharCode(chr2);
                }
                if (enc4 != 64) {
                    output = output + String.fromCharCode(chr3);
                }

            }

            output = this._utf8_decode(output);

            return output;

        },

        // private method for UTF-8 encoding
        _utf8_encode : function (string) {
            string = string.replace(/\r\n/g, "\n");
            var utftext = "";
            for (var n = 0; n < string.length; n++) {
                var c = string.charCodeAt(n);

                if (c < 128) {
                    utftext += String.fromCharCode(c);
                }
                else if ((c > 127) && (c < 2048)) {
                    utftext += String.fromCharCode((c >> 6) | 192);
                    utftext += String.fromCharCode((c & 63) | 128);
                }
                else {
                    utftext += String.fromCharCode((c >> 12) | 224);
                    utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                    utftext += String.fromCharCode((c & 63) | 128);
                }
            }
            return utftext;
        },

        // private method for UTF-8 decoding
        _utf8_decode : function (utftext) {
            var string = "";
            var i = 0;
            var c = c1 = c2 = 0;

            while (i < utftext.length) {

                c = utftext.charCodeAt(i);

                if (c < 128) {
                    string += String.fromCharCode(c);
                    i++;
                }
                else if ((c > 191) && (c < 224)) {
                    c2 = utftext.charCodeAt(i + 1);
                    string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                    i += 2;
                }
                else {
                    c2 = utftext.charCodeAt(i + 1);
                    c3 = utftext.charCodeAt(i + 2);
                    string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                    i += 3;
                }
            }
            return string;
        }

    };

}();


VOIDSEARCH.VoidBase.extend(VOIDSEARCH.VoidBase.Core, VOIDSEARCH.Events);