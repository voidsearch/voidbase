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

//
// WebAPI is the front controller of the whole application 
//

VOIDSEARCH.VoidBase.WebAPI=function(){
    return {
        baseNode:'main-ajax-content',
        modules: {},
        timers:{},
        core:VOIDSEARCH.VoidBase.Core,
        cache:VOIDSEARCH.VoidBase.Cache,

        init: function () {
            this.app = {};
            var self=this;
            this.templates=VOIDSEARCH.VoidBase.Views.loadTemplates({
                onAllLoaded:function(){
                    //wait fot all templates to be loaded
                    self.registerListeners();
                    self.parseURI();
                    self.cache.put('VoidSearch.WebAPI.message','All OK!, starting Web API!',function(){});
                },

                onFailure:function(){
                    //@todo add error handling for missing templates
                }
            });
        },

        registerListeners:function(){
            var self=this;

            // QUEUE TREE MODULE EVENTS
            this.addObserver('queueTree:fetchQueueList',function(){
                self.core.AJAXGetJSON('/webapi/queuetree/?method=LIST', function(data) {
                    VOIDSEARCH.VoidBase.WebAPI.modules.queueTree.listHandler(data);
                });
            });
        },

        parseURI: function () {
            // if location has changed since last time....
            if (window.location.href != this.uri) {
                this.uri = window.location.href;

                //get the part after #
                var tmp = this.uri.split('#');
                if (tmp[1] !== undefined) {
                    this.path = tmp[1];
                } else {
                    this.path = '';
                }
                //console.log("inside: "+this.path);
                this.dispatchModule();
            }

            this.screenWidth=$('padder').getWidth();

            //register timeout function and repeat
            var self = this;
            var timeoutFunc = function () {
                self.parseURI();
            };
            this.timer = setTimeout(timeoutFunc, 150);

        },

        isModuleDispatchable:function() {
            var dispatchable = false;
            if (typeof(this.modules[this.app.module].API == undefined)) {
                this.modules[this.app.module]._init(this);
            }

            if (typeof(this.modules[this.app.module]) != 'undefined' && typeof(this.modules[this.app.module][this.app.action]) != 'undefined') {
                dispatchable = true;
            }
            return dispatchable;
        },


        // STOP TIMERS 
        // clear all refresh timers when changing controller

        stopTimers:function(){
            try{
                for(var i in this.timers){
                    clearTimeout(this.timers[i]);
                }
            }catch (e){
                console.log(e);
            }
        },

        // DISPATCH MODULE
        dispatchModule: function () {
            this.stopTimers();
            this.app.module = this.getModuleFromPath();
            this.app.action = this.getActionFromPath();
            this.app.params = this.getParamsFromPath();

            if (this.isModuleDispatchable(this.app.module, this.app.action)) {
                this.modules[this.app.module][this.app.action](this.app.params);
            } else {
                console.log("undefined module/action :[" + this.app.module + "][" + this.app.action + "]");
            }

        },

        executeModule:function(module, action, params) {
            //console.log('executing: ' + module + ' action: ' + action);
            this.app.module = module;
            this.app.action = action;
            this.app.params = params;

            //execute module
            if (this.isModuleDispatchable(this.app.module, this.app.action)) {
                this.modules[this.app.module][this.app.action](this.app.params);
            } else {
                console.log("undefined module/action :[" + this.app.module + "][" + this.app.action + "]");
            }
        },

        getModuleFromPath: function () {
            var module = 'defaultModule';
            var tmp = this.path.split('/');
            if (tmp[0] !== undefined && tmp[0] !== '') {
                module = tmp[0];
            }
            return module;
        },

        getActionFromPath: function () {
            var tmp = this.path.split('/');
            var action = 'defaultAction';
            if (tmp[1] !== undefined && tmp[1] !== '') {
                action = tmp[1];
                var tmp2 = action.split('?');
                if (tmp2[0] !== undefined && tmp2[0] !== '') {
                    var action = tmp2[0];
                }
            }
            return action;
        },

        getParamsFromPath: function () {
            var tmp = this.path.split('/');
            var params = '';
            if (tmp[2] !== undefined && tmp[2] !== '') {
                params = tmp[2].replace('?', '');
            } else {
                var tmp2 = this.path.split('?');
                if (tmp2[1] !== undefined && tmp2[1] !== '') {
                    params = tmp2[1];
                }
            }
            var out = {};
            if (params != '') {
                var tmpArray = params.split('&');
                for (var i = 0; i < tmpArray.length; i++) {
                    var a = tmpArray[i];
                    a = a.split('=');
                    out[a[0]] = a[1];
                }
            }
            return out;
        },

        parseIncludes:function(element) {
            var self = this;
            var placeholder = element;
            var childElements = placeholder.childElements();
            if (childElements.length != 0) {
                childElements.each(function(elm) {
                    // try to read include attribute of dom node
                    if (elm.hasAttribute('include')) {
                        var include = elm.readAttribute('include');
                        //check if include attribute is not empty
                        if (include != '') {
                            self.includeTemplate(elm, include);
                        }
                    }
                    self.parseIncludes(elm);
                });
            }

        },

        includeTemplate:function(targetElement, templateName) {
            $(targetElement).innerHTML = VOIDSEARCH.VoidBase.Views.templates[templateName];
            this.parseIncludes(targetElement);

        },
        requiresNode:function(nodeId, moduleReference) {
            var contains = false;
            var self = this;
            self._GM = this.app.module;
            self._GA = this.app.action;
            self._GP = this.app.params;
            try {
                var elm = $(nodeId);
                if (elm == null) {
                    var timeoutFunc = function() {
                        self.modules[self._GM][self._GA](self._GP);
                    };

                    //execute method requred to fetch nodeId
                    var moduleAction = moduleReference.requireMap[nodeId];
                    moduleAction = moduleAction.split('::');
                    this.executeModule(moduleAction[0], moduleAction[1]);

                    //there is no requred nodeId at this time, we are trying to load
                    //it in executeModule method above, but we must buy some time and re-run caller
                    //function
                    this.includeWaitTimer = setTimeout(timeoutFunc, 600);

                } else {
                    contains = true;
                }

            } catch(err) {
                //handle some strange error
            }
            return contains;
        }

    };
}();

VOIDSEARCH.VoidBase.extend(VOIDSEARCH.VoidBase.WebAPI,VOIDSEARCH.Events);

//
// home module
//

VOIDSEARCH.VoidBase.WebAPI.modules.home = {
    _init:function(apiObjectReference) {
        this.API = apiObjectReference;
    },

    // home action that shows welcome text
    defaultAction: function (params) {
        $('main-ajax-content').innerHTML=VOIDSEARCH.VoidBase.Views.templates['homeTemplate']
    }
}

//
//set defaultModule to home module
//

VOIDSEARCH.VoidBase.WebAPI.modules.defaultModule = VOIDSEARCH.VoidBase.WebAPI.modules.home;


//
// AllModules (MODULES INFO) module
//

VOIDSEARCH.VoidBase.WebAPI.modules.modules = function(){

    return{
        _init:function(apiObjectReference) {
            this.API = apiObjectReference;
        },

        defaultAction: function (params) {
            $('main-ajax-content').innerHTML=VOIDSEARCH.VoidBase.Views.templates['modulesTemplate'];
        }
    };
}();


//
// SYSTEM TESTS
//


VOIDSEARCH.VoidBase.WebAPI.modules.test = function() {
    return{
        _init:function(apiObjectReference) {
            this.API = apiObjectReference;
        },

        canvasText: function (params) {

            var canvasId='test_canvasText';
            //create empty canvas element
            $('main-ajax-content').update('<canvas id="'+canvasId+'" width="700px;" height="500px"></canvas>');

            //test the HTML5 text support


            this._testCanvasTextSupport(canvasId);

        },

        _testCanvasTextSupport:function(canvasId){

            var canvas=new Visuals(canvasId);
            this._drawGrid(canvas,10,'#cacaca');

            var isCanvasTextSupported=canvas.hasCanvasTextSupport;

            if(isCanvasTextSupported){
                canvas.text('Hello.. this is some nice canvas text at 20px height',20,20,10,'#000000',1,0);
                canvas.text('Hello.. this is some nice canvas text at 100px height',20,100,10,'#4141cc',1,0);
                canvas.text('Hello.. this is some nice canvas text at 200px height',20,200,15,'#000000',1,0);

            }else{
                canvas.text('Hello.. this is some nice canvas text at 20px',20,20,10,'#000000',1,0);
                canvas.text('Hello.. this is some nice canvas text at 100px height',20,100,10,'#4141cc',1,0);
                canvas.text('Hello.. this is some nice canvas text at 200px height',20,200,15,'#000000',1,0);
            }

        },

        _drawGrid:function(canvasObject,step,color){
            for(var i=0;i<canvasObject.containerHeight;i+=step){
                var alpha=0.5;
                if(i%50==0){
                    alpha=1;
                }
                canvasObject.line(0,i+0.5,canvasObject.containerWidth,i+0.5,color,alpha);

            }
        }

    }// end return

}();
