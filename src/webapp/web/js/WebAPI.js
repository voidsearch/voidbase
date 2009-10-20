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

var WebAPI = Class.create({

    //modules placeholder
    modules: {},
    //
    // init function
    //
    init: function () {
        this.app = {};

        this.uri = "[*******]";
        this.parseURI();
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

        //register timeout function and repeat
        var self = this;
        var timeoutFunc = function () {
            self.parseURI();
        };
        this.timer = setTimeout(timeoutFunc, 150);

    },

    // ID MODULE DISPATHCABLE
    //  check if module and module handler method exists
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

    // DISPATCH MODULE
    dispatchModule: function () {

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

    includeTemplate:function(targetElement, templatePath, callbackFunction) {
        var self = this;
        new Ajax.Request(templatePath, {
            method: 'get',
            onSuccess: function(transport) {
                var ret = transport.responseText;
                $(targetElement).innerHTML = ret;
                if (callbackFunction != null) {
                    callbackFunction();
                }
                self.parseIncludes(targetElement);
            }
        });
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
});

// ############################
//
// MODULES
//
// ############################
var APIModules = WebAPI.prototype.modules;

//
// home module
//

APIModules.home = {
    _init:function(apiObjectReference) {
        this.API = apiObjectReference;
    },


    defaultAction: function (params) {
        this.API.includeTemplate($('main-ajax-content'), '/files/html/webapi/homeTemplate.html');
    }
}


//
//
// QueueTree module
//
//
APIModules.queuetree = {

    _init:function(apiObjectReference) {
        this.API = apiObjectReference;
    },

    //requre map
    requireMap:{
        'qtCanvas':'queuetree::home'
    },


    home:function(params) {
        this.homeParams = params;
        this.API.includeTemplate($('main-ajax-content'), '/files/html/webapi/queuetreeTemplate.html');
    },


    stats:function() {
        if (this.API.requiresNode('qtCanvas', this)) {
            this.API.includeTemplate($('qtCanvas'), '/files/html/webapi/queueTreeStats.html', this._statsHandler);
        }
    },

    view:function(params) {
        var self = this;
        if (this.fetchSize == undefined) {
            this.fetchSize = 100;
        }
        if (this.API.requiresNode('qtCanvas', this)) {
            if (typeof(params.name) !== undefined) {
                new Ajax.Request('/webapi/queuetree/?method=GET&queue=' + params.name + '&size=' + this.fetchSize, {
                    method: 'get',
                    onSuccess: function(transport) {
                        // create simple list html
                        var HTML = '<h4>Queue "' + params.name + '"</h4><div id="qtView"></div>';
                        //HTML += '<h4>Queue "' + params.name + '" Dump</h4>';
                        //HTML += '<textarea rows="10" cols="120" id="qtViewDump">';
                        //HTML += transport.responseText;
                        //HTML += '</textarea><br/><br/><br/>';
                        $('qtCanvas').innerHTML = HTML;
                        // leave more advanced stuff to hanfler funciton
                        self._view(transport.responseJSON);
                    }
                });


            }
        }
    },

    list:function() {
        if (this.API.requiresNode('qtCanvas', this)) {
            this._list();
        }
    },

    //
    // HANDLERS
    //
    _statsHandler:function() {
        $('qtStats').innerHTML = 'test test test';

    },

    _repeatObjectView:function() {
        var self = this;
        var params = this.API.getParamsFromPath();
        var URI = '/webapi/queuetree/?method=GET&queue=' + params.name + '&size=' + this.fetchSize;

        new Ajax.Request(URI, {
            method: 'get',
            onSuccess: function(transport) {

                self._queueData = transport.responseJSON;
                //$('qtViewDump').innerHTML = transport.responseText;
                self._queueData = self._queueData.queue;
                self._drawObjectGraph(this.fieldToDraw);
            }
        });


        var timeoutFunc = function() {
            self._repeatObjectView();
        }
        this.viewTimer = setTimeout(timeoutFunc, 4000);


    },

    _view:function(data) {

        //get the first element of queue and try to resolve type

        var qs = [];
        var numResults = data.queue.header.results.totalResults;
        qs.push(data.queue.response.queueElements.val);

        var firstElement = false;

        if (numResults == 1) {
            firstElement = qs[0];
        }

        if (numResults > 1) {
            qs = qs[0];
        }

        firstElement = qs[0];


        this.fieldNames = this._getPropertyNames(firstElement);
        if (numResults > 1) {
            qs = qs[0];
        }

        var dataType = this._detectType(firstElement);
        this._queueData = data.queue;

        // hanfle according to type
        if (dataType == 'object') {
            this._viewObject();
        }

    },

    _viewObject:function() {
        var self = this;

        //get first element to collect field names



        //construct select drop down with field names
        var selectHTML = '<select id="qtViewSelectField">';
        this.fieldNames.each(function(elm) {
            selectHTML += '<option value="' + elm + '">' + elm + '</option>';
        });
        selectHTML += '</select><select id="fetchSizeSelect"><option value="100" >-- queue elements to fetch --</option><option value="10">10</option><option value="100" selected="selected">100</option><option value="1000">1000</option></select> <br/><canvas id="graph-canvas" ></canvas>';
        $('qtView').insert(selectHTML);

        //attach event to select field change
        $('qtViewSelectField').observe('change', function(event) {
            self._selectFieldNameHandler();
        });

        $('fetchSizeSelect').observe('change', function(event) {
            self._selectSizeChange();
        });

        //draw for first field
        this.fieldToDraw = this.fieldNames.first();
        this._repeatObjectView();

    },

    _drawObjectGraph:function() {
        $('graph-canvas').width = '660';
        $('graph-canvas').height = '160';

        var self = this;
        var data = [];
        var qs = [];
        qs.push(this._queueData.response.queueElements.val);
        if (this._queueData.header.results.totalResults > 1) {
            qs = qs[0];
        }

        qs.each(function(element) {
            data.push(element[self.fieldToDraw]);
        });


        new ChartEngine({
            'canvasID':'graph-canvas',
            'type':'line',
            'xTitle':'x-title',
            'yTitle':'y-title',
            'chartData':data
        });

    },

    _selectFieldNameHandler:function() {

        this.fieldToDraw = $('qtViewSelectField').value;
        clearTimeout(this.viewTimer);
        this._repeatObjectView();
    },

    _selectSizeChange:function() {
        var field = $('fetchSizeSelect').value;
        this.fetchSize = parseInt(field);
        clearTimeout(this.viewTimer);
        this._repeatObjectView();
    },

    //  GET PROPERTY NAMES
    //  return property names as array if que element is object
    _getPropertyNames:function(element) {
        var fields = [];
        for (var i in element) {
            fields.push(i);
        }
        return fields;
    },

    //  DETECT TYPE
    //  detect type of element in que
    _detectType:function(element) {
        var type = 'unknown';

        if (typeof(element) == 'object') {
            type = 'object';
        }

        if (Object.isArray(element)) {
            type = 'array';
        }
        if (Object.isHash(element)) {
            type = 'hash';
        }

        if (Object.isNumber(element)) {
            type = 'number';
        }

        if (Object.isString(element)) {
            type = 'string';
        }

        return type;

    },

    // AJAX CALLS FOR LISTING QUEUES
    _list:function() {
        new Ajax.Request('/webapi/queuetree/?method=LIST', {
            method: 'get',
            onSuccess: function(transport) {
                var resp = transport.responseJSON;
                var qs = [];
                qs.push(resp.queue.response.queueList.val);
                var numResults = resp.queue.header.results.totalResults;

                // create simple list html
                var HTML = '<h4>Queue List</h4>';
                HTML += '<ul class="big-list">';

                //deal with one element arrays
                if (numResults > 1) {
                    qs = qs[0];
                }
                //itterate array
                qs.each(function(c) {
                    HTML += '<li><a href="#queuetree/view/?name=' + c + '">' + c + '</a></li>';
                });
                HTML += '</ul>';
                $('qtCanvas').innerHTML = HTML;
            }
        });

    },


    manage:function() {
        if (this.API.requiresNode('qtCanvas', this)) {
            $('qtCanvas').innerHTML = '<br /><p>QueTree management console ... :)</p>';
        }
    },

    fastDebugChart:function() {
        if (this.API.requiresNode('qtCanvas', this)) {
            $('qtCanvas').innerHTML = '<br /><p>..... WOAAA!!!!! </p>';
        }
    },
    fastDebugSplatter:function() {
        if (this.API.requiresNode('qtCanvas', this)) {
            this.API.includeTemplate($('qtCanvas'), '/files/html/webapi/queueTreeDebugSplatter.html', this._fastDebugSplatterHandler);
        }
    },

    _fastDebugSplatterHandler:function() {
        // @todo make java random data inserter
        // ATM, we use:
        // /bin/insertRandomData.php

        var self = window.APIModules.queuetree;

        TIMER.init('timerDBGArea');
        TIMER.start('init');
        //var q = $('q').value;
        TIMER.message("sending query");

        console.log('sending');
        new Ajax.Request('/webapi/queuetree/?method=GET&queue=scatter&size=100', {
            method: 'get',
            onSuccess: function(transport) {
                TIMER.message("query data received");
                var ret = transport.responseText.evalJSON();
                var tmpArray = [];
                ret.queue.response.queueElements.val.each(function(elm) {
                    tmpArray.push(elm.evalJSON());
                });

                ret.queue.response.queueElements.val = tmpArray;

                ret.queue.response.queueMetadata.max = ret.queue.response.queueMetadata.max.evalJSON();
                ret.queue.response.queueMetadata.min = ret.queue.response.queueMetadata.min.evalJSON();
                ret.error = false;

                TIMER.message("query data evaled to JSON");
                self.drawScatter(ret);
            }
        });
    },

    drawScatter:function(apiData) {
        console.log(apiData);
        if (apiData.error != true) {
            $('debug').innerHTML = '';
            var a = new ChartEngine({
                'canvasID':'graph-canvas',
                'tooltip':'scatter-tooltip',
                'type':'scatter',
                'xTitle':'x-title',
                'yTitle':'y-title',
                'apiXMax':apiData.queue.response.queueMetadata.max[0],
                'apiXMin':apiData.queue.response.queueMetadata.min[0],
                'apiYMax':apiData.queue.response.queueMetadata.max[1],
                'apiYMin':apiData.queue.response.queueMetadata.min[1],
                'chartData':apiData.queue.response.queueElements.val
            });
            //graph.dbg();
        } else {
            $('debug').innerHTML = 'Invalid Query';
        }


    }
}


//
// AllModules (MODULES INFO) module
//
APIModules.modules = {
    _init:function(apiObjectReference) {
        this.API = apiObjectReference;
    },

    defaultAction: function (params) {
        this.API.includeTemplate($('main-ajax-content'), '/files/html/webapi/modulesTemplate.html');
    }
}

APIModules.test = {
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

}


//
//set defaultModule to home module
//
APIModules.defaultModule = APIModules.home;