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
//
// QueueTree  WebAPI module
//
//

VOIDSEARCH.VoidBase.WebAPI.modules.queuetree = function() {
    // private properties
    var defaultObjectRefreshRate = 8000; //miliseconds

    getFieldData = function(fieldName, JSONData) {

        var entries=[];
        var data = [];
        
        entries.push(JSONData.queue.response.queueElements.entry);
        if(JSONData.queue.header.results.totalResults>1){
            entries=entries[0];
        }
        
        entries.each(function(element) {
            data.push(element.val[fieldName]);
        });
        return data;
    }

    return{
        _init:function(apiObjectReference) {
            this.API = apiObjectReference;
            this.objectRegister = {};
            this.objectRegister.activeObjects = [];
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

        viewGrid:function(params) {
            var self = this;
            if (this.API.requiresNode('qtCanvas', this)) {
                if (typeof(params.name) !== undefined) {
                    // use default fetch size
                    if (this.fetchSize == undefined) {
                        this.fetchSize = 100;
                    }
                    new Ajax.Request('/webapi/queuetree/?method=GET&queue=' + params.name + '&size=' + this.fetchSize, {
                        method: 'get',
                        onSuccess: function(transport) {
                            // create simple list html
                            var HTML = '<h4>Queue "' + params.name + '"</h4><div id="qtView"></div>';
                            $('qtCanvas').innerHTML = HTML;
                            // leave more advanced stuff to hanfler funciton
                            self._viewGridDispatcher(transport.responseJSON);
                        }
                    });
                }
            }
        },

        _viewGridDispatcher:function(data) {

            var entries=data.queue.response.queueElements.entry;
            var qs = this.getComplexData(data.queue,'queueElements');
            var numResults = data.queue.header.results.totalResults;
            var firstElement = false;

            firstElement = qs[0];

            var dataType = this._detectType(firstElement);
            this._queueData = data.queue;

            // hanfle according to type
            if (dataType == 'object') {

                this.fieldNames = this._getPropertyNames(firstElement);
                this.drawObjectGrid();
            }
        },


        drawObjectGrid:function() {
            var self = this;
            this.gridContainerWidth = $('qtView').getWidth() - (4 * 8);
            
            $('qtView').innerHTML = VOIDSEARCH.VoidBase.WebAPI.templates.gridTable;

            var availableGridFields = $$('td.gf');
            this.maxCellSize = this.gridContainerWidth / 4;
            availableGridFields.each(function(cell) {
                cell.style.width = self.maxCellSize + 'px';

            });
            
            $('normalizeGraphs').observe('click',function(event){
                var element = event.element();
                self.normalizeGraphs=element.checked;
                self.redrawAllGraphs();
            });

            var feedName = this._queueData.response.queueMetadata.name;
            this.fieldNames.each(function(field, index) {
                self.registerNewObject(field, feedName, availableGridFields[index].id);
            });
            
            this.globalScopeMax=0;
            this.globalScopeMin=0;
            this.normalizeGraphs=$('normalizeGraphs').checked;

            // start grid updater, start from the first element
            this.gridUpdater(0);
        },

        //
        // GRID UPDATER
        // 

        gridUpdater:function(index) {
            var self = this;

            // fetch field, queue and chart instance from active objects array
            var field = self.objectRegister.activeObjects[index][0];
            var queue = self.objectRegister.activeObjects[index][1];
            var objectInstance = self.objectRegister.activeObjects[index][3];

            this.fetchSize = 259;
            this.updateSingleObject(field, queue, objectInstance);

            //console.log(this.gridContainerWidth);
            index += 1;
            if (index >= this.objectRegister.activeObjects.length) {
                index = 0;
            }
            
            
            //calculate and apply timeout
            var timeout = defaultObjectRefreshRate / this.objectRegister.activeObjects.length;
            var timeoutFunc = function () {
                self.gridUpdater(index);
            };
            this.API.timers.multipleObjectRefresh = setTimeout(timeoutFunc, timeout);

        },

        updateSingleObject:function(field, queue, chartInstance) {
            var self = this;
            new Ajax.Request('/webapi/queuetree/?method=GET&queue=' + queue + '&size=' + this.fetchSize, {
                method: 'get',
                onSuccess: function(transport) {
                    // leave more advanced stuff to hanfler funciton
                    self.updateSingleObjectHandler(field, queue, chartInstance, transport.responseJSON);
                }
            });
        },

        updateSingleObjectHandler:function(field, queue, chartInstance, data) {
            //fetch data
            var canvasElement = $(chartInstance.canvas.ctx.canvas.id);
            var canvasContainer = canvasElement.parentNode;
            var fieldData = getFieldData(field, data);
            var containerWidth = canvasContainer.getWidth();
            var scopeChanged=false; 
            
            if(this.normalizeGraphs){
                scopeChanged=this.setGlobalScope(fieldData);
            }
            //resize canvas to fit container
            canvasElement.width = containerWidth - 3;
            canvasElement.height = 150;
            
            if(this.normalizeGraphs){
                chartInstance.forceScope=true;
                chartInstance.forceMaxValue=this.globalScopeMax;
                chartInstance.forceMinValue=this.globalScopeMin;
            }        
            
            // redraw 
            if(!scopeChanged){
                chartInstance.options.chartData = fieldData;
                chartInstance.updated=true;
                chartInstance.resetGraph();
                chartInstance.drawGraph();
            }else{
                chartInstance.options.chartData = fieldData;
                chartInstance.updated=true;
                this.redrawAllGraphs();
            }

        },

        setGlobalScope:function(data){
            var max=data.max();
            var min=data.min();
            var scopeChanged=false;
            
            if(max > this.globalScopeMax) {
                this.globalScopeMax=max;
                scopeChanged=true;
            }
            
            if(min < this.globalScopeMin) {
                this.globalScopeMin=min;
                scopeChanged=true;
            }
            
            return scopeChanged;
        },
        
        redrawAllGraphs:function(){
            
            var self=this;
            this.objectRegister.activeObjects.each(function(elm) {
                
                var chartInstance=elm[3];
                
                chartInstance.forceScope=self.normalizeGraphs;

                if(self.normalizeGraphs){
                    chartInstance.forceMaxValue=self.globalScopeMax;
                    chartInstance.forceMinValue=self.globalScopeMin;
                }
                 
                if(chartInstance.updated){
                    chartInstance.resetAndRedraw();
                    
                }
            });    
            
        },
        
        
        registerNewObject:function(field, queue, container) {

            var canvasId = 'graph-canvas-' + container;
            $(container).innerHTML = '<canvas id="' + canvasId + '" width="250" height="150"></canvas>';

            var instance = new ChartEngine({
                'canvasID':canvasId,
                'tooltip':'scatter-tooltip',
                'type':'line',
                'xTitle':'time',
                'yTitle':field
            });

            var self = this;
            $(canvasId).observe('click', function(event) {
                var elem = event.element();
                self.switchType(elem.id);
            });
            this.objectRegister.activeObjects.push([field,queue,container,instance,canvasId]);
        },

        switchType:function(containerId) {
            this.objectRegister.activeObjects.each(function(elm) {
                if (elm[4] == containerId) {

                    var instance = elm[3];

                    if (instance.options.type == 'line') {
                        instance.options.type = 'bars';

                    } else {
                        instance.options.type = 'line';
                    }
                    // switch type and redraw
                    instance.resetAndRedraw();
                    throw $break;
                }

            }); //each end
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

            this.API.timers.refreshViewTimer = setTimeout(timeoutFunc, 2000);
        },

        _view:function(data) {
            //get the first element of queue and try to resolve type
            var qs = [];
            var numResults = data.queue.header.results.totalResults;
            var entries=data.queue.response.queueElements.entry;

            entries.each(function(entry){
                qs.push(entry.val);
            });

            var firstElement = false;
            if (numResults == 1) {
                firstElement = qs;
            }
            firstElement = qs[0];

            this.fieldNames = this._getPropertyNames(firstElement);
            var dataType = this._detectType(firstElement);

            this._queueData = data.queue;

            // hanfle according to type
            if (dataType == 'object') {
                this._viewObject();
            }

        },

        _viewObject:function() {
            var self = this;

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


       getComplexData:function(JSONData,container){
            var data = [];
            var entries=[];

            entries.push(JSONData.response[container].entry);
            if(JSONData.header.results.totalResults>1){
                entries=entries[0];
            }
            entries.each(function(entry){
                data.push(entry.val);
            });

            return data;
        },

        _drawObjectGraph:function() {
            $('graph-canvas').width = this.API.screenWidth - 40;
            $('graph-canvas').height = '160';

            var self = this;
            var data = [];
            var qs=this.getComplexData(this._queueData,'queueElements');

            qs.each(function(element) {
                data.push(element[self.fieldToDraw]);
            });

            var graph = new ChartEngine({
                'canvasID':'graph-canvas',
                'type':'bars',
                'xTitle':'time',
                'yTitle':this.fieldToDraw,
                'chartData':data
            });

            graph.drawGraph();
        },

        _selectFieldNameHandler:function() {

            this.fieldToDraw = $('qtViewSelectField').value;
            this.API.stopTimers();
            this._repeatObjectView();
        },

        _selectSizeChange:function() {
            var field = $('fetchSizeSelect').value;
            this.fetchSize = parseInt(field);
            this.API.stopTimers();
            this._repeatObjectView();
            VOIDSEARCH.VoidBase.Util.test();
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
            var self=this;
            new Ajax.Request('/webapi/queuetree/?method=LIST', {
                method: 'get',
                onSuccess: function(transport) {
                    var resp = transport.responseJSON;
                    
                    var qs = self.getComplexData(resp.queue,'queueList');

                    // create simple list html
                    var HTML = '<h4>Queue List</h4>';
                    HTML += '<ul class="big-list">';

                    //itterate array
                    qs.each(function(c) {
                        HTML += '<li><a href="#queuetree/view/?name=' + c + '">' + c + '</a>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|| <a href="#queuetree/viewGrid/?name=' + c + '">[VIEW GRID # ]</a> </li>';
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
            var self = window.APIModules.queuetree;

            TIMER.init('timerDBGArea');
            TIMER.start('init');
            //var q = $('q').value;
            TIMER.message("sending query");


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
    }; // end return
}();