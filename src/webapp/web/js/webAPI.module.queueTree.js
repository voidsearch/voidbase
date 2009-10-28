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
    var defaultObjectRefreshRate = 6000; //miliseconds

    getFieldData=function(fieldName, JSONData){

        var data = [];
        var qs = [];

        qs.push(JSONData.queue.response.queueElements.val);
        if (JSONData.queue.header.results.totalResults > 1) {
            qs = qs[0];
        }

        qs.each(function(element) {
            data.push(element[fieldName]);
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
                this.drawObjectGrid();
               // console.log(this.fieldNames);
               // console.log(this._queueData);
            }
        },


        drawObjectGrid:function() {
            var self = this;
            this.gridContainerWidth=$('qtView').getWidth()-(4*22);

            // TODO move this code to the "grid generator" method
            var tableHTML = '<table class="gridTable">';
            tableHTML += '<tr><td id="gf_a1" class="gf bbt">a1</td><td id="gf_b1" class="gf bet">b1</td><td id="gf_bc1" class="gf bet">c1</td><td id="gf_bd1" class="gf bet">d1</td></tr>';
            tableHTML += '<tr><td id="gf_a2" class="gf bbb">a2</td><td id="gf_b2" class="gf beb">b2</td><td id="gf_bc2" class="gf beb">c2</td><td id="gf_bd2" class="gf beb">d2</td></tr>';
            tableHTML += '<tr><td id="gf_a3" class="gf bbb">a3</td><td id="gf_b3" class="gf beb">b3</td><td id="gf_bc3" class="gf beb">c3</td><td id="gf_bd3" class="gf beb">d3</td></tr>';
            tableHTML += '</table><div id="gridCtrl"><input type="button" value="&laquo;" id="gridSizeReduce"/><input type="button" value="&raquo;" id="gridSizeIncrease"/></div>';
            $('qtView').innerHTML = tableHTML;

            var availableGridFields = $$('td.gf');
            this.maxCellSize=this.gridContainerWidth/4 ;
            availableGridFields.each(function(cell){
                cell.style.width=self.maxCellSize+'px';

            });


            var feedName = this._queueData.response.queueMetadata.name;
            this.fieldNames.each(function(field, index) {
                self.registerNewObject(field, feedName, availableGridFields[index].id);
                //console.log(availableGridFields[index].id);
            });

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

            this.fetchSize=100;
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
            var self=this;

            new Ajax.Request('/webapi/queuetree/?method=GET&queue=' +queue+ '&size=' + this.fetchSize, {
                method: 'get',
                onSuccess: function(transport) {
                    // leave more advanced stuff to hanfler funciton
                    self.updateSingleObjectHandler(field, queue, chartInstance, transport.responseJSON);
                }
            });

        },

        updateSingleObjectHandler:function(field, queue, chartInstance, data){

            //fetch data
            var canvasElement=$(chartInstance.canvas.ctx.canvas.id);
            var canvasContainer=canvasElement.parentNode;
            var fieldData=getFieldData(field,data);

            var containerWidth=canvasContainer.getWidth();
            var canvasWidth=canvasElement.getWidth();

            var newWidth=containerWidth-22;

            //console.log('container width: '+containerWidth+' canvas width: '+canvasWidth+'  new width: '+newWidth)

            //resize canvas to fit container
            //canvasElement.width=container;
            canvasElement.width=newWidth;
            canvasElement.height=150;

            // redraw 
            chartInstance.options.chartData=fieldData;
            chartInstance.resetGraph();
            chartInstance.drawLineGraph();

        },



        registerNewObject:function(field, queue, container) {

            var canvasId = 'graph-canvas-' + container;
            $(container).innerHTML = '<canvas id="' + canvasId + '" width="250" height="150"></canvas>';

            var instance = new ChartEngine({
                'canvasID':canvasId,
                'tooltip':'scatter-tooltip',
                'type':'instance',
                'xTitle':'time',
                'yTitle':field

            });
            this.objectRegister.activeObjects.push([field,queue,container,instance]);


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
            $('graph-canvas').width = this.API.screenWidth - 40;
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
                'xTitle':'time',
                'yTitle':this.fieldToDraw,
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
    }; // end return
}();