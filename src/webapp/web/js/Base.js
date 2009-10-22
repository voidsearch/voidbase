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
VOIDSEARCH.VoidBase={};




// UTIL MODULE
VOIDSEARCH.VoidBase.Util=function(){
    // private variables
    var DEFAULT_PATTERN_IMAGE='/files/img/pattern_bg_200px.png';

    return{
        addGrid:function(elementId){
            var element=$(elementId);
            if(element.style.background==""){
                element.style.background="url('"+DEFAULT_PATTERN_IMAGE+"')";
            }else{
                element.style.background='';
            }
        },

        test:function(){

            console.log('hello from test');
            
        }
    };

}();