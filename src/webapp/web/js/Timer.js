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

/**
 *
 * TIMING
 */
var Timer = function() {
    this.init = function(elmID) {
        this.elmID = elmID;
        $(elmID).update('');
        this.globalTime = [];
        this.mStack=[];
    }
    this.start = function(message) {
        this.globalTime = [];
        var now = new Date();
        this.startTime = now.getHours() * 60 * 60 * 1000 + now.getMinutes() * 60 * 1000 + now.getSeconds() * 1000 + now.getMilliseconds();
        this.message(message);
    }

    this.message = function(message) {
        var now = new Date();
        var mss = now.getHours() * 60 * 60 * 1000 + now.getMinutes() * 60 * 1000 + now.getSeconds() * 1000 + now.getMilliseconds();

        if (this.globalTime.length == 0) {
            this.mStack.push("[timer start]\t[" + (mss-this.startTime) + "]\t[" + message + "]\n");
            this.globalTime.push(mss);
            return 0;
        } else {
            var oldmss = this.globalTime.pop();
            this.mStack.push("[time (ms)]\t[" + (mss-this.startTime) + "]\t[" + message + "]\n");
            this.globalTime.push(mss);
            return (mss - oldmss);
        }
    }

    this.dumpTimer=function(){
        var self=this;
        this.mStack.each(function(elm){
            $(self.elmID).innerHTML+=elm;
        });
    }
}