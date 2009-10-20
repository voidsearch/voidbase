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

//UTILITIES
var DBG = function (v) {
    console.log('val: ' + v + ' | type: ' + typeof(v));
}

function print_r(x, max, sep, l) {

    l = l || 0;
    max = max || 10;
    sep = sep || ' ';
    var nlc = '<br />';
    if (l > max) {
        return "[WARNING: Too much recursion]\n";
    }
    var
    i, r = '',
    t = typeof x,
    tab = '';

    if (x === null) {
        r += "(null)" + nlc;
    } else if (t == 'object') {
        l++;
        for (i = 0; i < l; i++) {
            tab += sep;
        }
        if (x && x.length) {
            t = 'array';
        }
        r += '(' + t + ") :" + nlc;
        for (i in x) {
            try {
                r += tab + '[' + i + '] : ' + print_r(x[i], max, sep, (l + 1));
            } catch(e) {
                return "[ERROR: " + e + "]" + nlc;
            }
        }
    } else {
        if (t == 'string') {
            if (x == '') {
                x = '(empty)';
            }
        }
        r += '(' + t + ') ' + x + "" + nlc;
    }
    return r;
};