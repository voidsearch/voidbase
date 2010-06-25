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

VOIDSEARCH.VoidBase.WebAPI.modules.queueTree = function() {
    // private properties
    var defaultObjectRefreshRate = 8000; //miliseconds

    var ADD_NEW_GRID_ELEMENT = 'addNewGridElement';
    var QT_CANVAS = 'qtCanvas';
    var QT_VIEW = 'qtView';
    var GRID_CONTAINER = 'gridContainer';
    var GRID_CELL = 'gridCell';
    var GRID_CELL_EDIT = 'gridCellEdit';
    var GRID_CELL_CANVAS_CONTAINER = 'gridCellCanvasContainer';
    var GRID_CELL_SETTINGS = 'gridCellSettings';
    var GRID_CELL_SETTINGS_SOURCE_QUEUE = 'cellChartSourceQueue';
    var DEFAULT_FETCH_SIZE = 250;
    var DEFAULT_LINE_WIDTH = 2;
    var DEFAULT_CHART_TYPE = 'line';
    var GRID_TITLE_TEXT='gridTitleText';




    var getFieldData = function(fieldName, JSONData) {

        var entries = [];
        var data = [];

        entries.push(JSONData.queue.response.queueElements.entry);
        if (JSONData.queue.header.results.totalResults > 1) {
            entries = entries[0];
        }

        entries.each(function(element) {
            data.push(element.val[fieldName]);
        });
        return data;
    };

    var getSimpleFieldData = function(JSONData) {

        var entries = [];
        var data = [];

        entries.push(JSONData.queue.response.queueElements.entry);
        if (JSONData.queue.header.results.totalResults > 1) {
            entries = entries[0];
        }

        entries.each(function(element) {
            data.push(element.val);
        });
        return data;
    };

    return{
        _init:function(apiObjectReference) {
            this.API = apiObjectReference;
            this.objectRegister = {};
            this.objectRegister.activeObjects = [];
            this.objectRegister.activeContainers = {};
            this.objectRegister.objectCounter = 0;
            this.registerListeners();

        },

        //require map
        requireMap:{
            'qtCanvas':'queueTree::home'
        },


        home:function(params) {
            this.homeParams = params;
            this.API.includeTemplate($('main-ajax-content'), 'queueTreeTemplate');
        },


        stats:function() {
            if (this.API.requiresNode('qtCanvas', this)) {
                this.API.includeTemplate($(QT_CANVAS), 'queueTreeStats');
            }
        },


        loadPermalink:function(p) {
            if (this.API.requiresNode('qtCanvas', this)) {

                

                var self = this;
                this.objectRegister.activeObjects = [];
                this.API.includeTemplate($(QT_CANVAS), 'queueTreeNewGrid');
                this.gridUpdater(0);
                self.addGridListeners();


                var settings = VOIDSEARCH.VoidBase.Util.base64Decode(p.hash).evalJSON();

                
                console.log(settings);

                settings.each(function(chart, index){
                    self.insertNewGridCell();
                    self.cellSettingsId = ( index +1);
                    var gridCellCanvasContainer = GRID_CELL_CANVAS_CONTAINER + '_' + self.cellSettingsId;
                    var options = {};
                    options.fetchSize = DEFAULT_FETCH_SIZE;
                    options.cellId = self.cellSettingsId;
                    options.type = chart.cht;
                    options.lineWidth = chart.lw;
                    options.color = chart.clr;

                    // insert new or update existing cell
                    self.registerNewObject(chart.fn, chart.n, gridCellCanvasContainer, options);

                });
            }

        },


        newGrid:function() {
            if (this.API.requiresNode('qtCanvas', this)) {
                var self = this;

                this.objectRegister.activeObjects = [];
                this.API.includeTemplate($(QT_CANVAS), 'queueTreeNewGrid');
                this.gridUpdater(0);

                self.addGridListeners();


            }
        },


        addGridListeners : function(){
            var self = this;

            // since all templates are already loaded we can set up our listeners right away
            //
            // adding various listeners

            $(ADD_NEW_GRID_ELEMENT).observe('click', function() {
                self.insertNewGridCell();
                $(ADD_NEW_GRID_ELEMENT).blur();
            });

            $('cellSettingsClose').observe('click', function() {
                $(GRID_CELL_SETTINGS).hide();
            });

            $('cellSettingsCloseButton').observe('click', function() {
                $(GRID_CELL_SETTINGS).hide();
            });

            $('cellSettingsSave').observe('click', function() {
                self.saveGridCellSettings();
            });

            $('cellSettingsDelete').observe('click', function() {
                self.deleteGridCell();
            });

            $(GRID_TITLE_TEXT).observe('click',function(){

            });

            $('generatePermalinkButton').observe('click',function(){
                self.generatePermalink();
            });

        },

        generatePermalink:function(){
            $('permalink').show();
            
            var output = [];
            this.objectRegister.activeObjects.each(function(chart){
               console.log(chart);

               output.push({"n":chart[1],"cht":chart[3].options.type,"lw":chart[3].options.lineWidth,"fn":chart[0],"clr":chart[3].options.color});
            });


            $('permalink').value='http://localhost:8080/files/webapi.html#queueTree/loadPermalink/?hash='+escape(VOIDSEARCH.VoidBase.Util.base64Encode(output.toJSON()));
            
        },

        deleteGridCell:function() {
            var self = this;

            var gridCell = GRID_CELL + '_' + this.cellSettingsId;
            $(gridCell).remove();
            delete(this.objectRegister.activeContainers[this.cellSettingsId]);

            this.objectRegister.activeObjects.each(function(elm, index) {
                var cId = elm[4].cellId;
                if (self.cellSettingsId == cId) {

                    //remove element from active objects
                    self.objectRegister.activeObjects.splice(index, 1);
                    throw $break;
                }
            });

            $(GRID_CELL_SETTINGS).hide();

        },

        saveGridCellSettings:function() {
            var queue = $('cellSettings-queueName').value;
            var queueChartType = $('cellSettings-chartType').value;
            var lineWidth = $('cellSettings-lineWidth').value;
            var color = $('cellSettings-color').value;
            var fieldName = false;
            try {
                if ($('cellSettings-field') != null) {
                    fieldName = $('cellSettings-field').value;
                } else {
                    throw "no such DOM element";
                }
            } catch (e) {
            }

            if (queue != '-1') {

                var gridCellCanvasContainer = GRID_CELL_CANVAS_CONTAINER + '_' + this.cellSettingsId;
                var options = {};
                options.fetchSize = DEFAULT_FETCH_SIZE;
                options.cellId = this.cellSettingsId;
                options.type = queueChartType;
                options.lineWidth = lineWidth;
                options.color = color;

                // insert new or update existing cell
                this.registerNewObject(fieldName, queue, gridCellCanvasContainer, options);

                // hide settings window
                $(GRID_CELL_SETTINGS).hide();

            }

        },

        insertNewGridCell:function() {
            var self = this;
            var nextId = this.objectRegister.objectCounter + 1;
            this.objectRegister.objectCounter += 1;
            var HTML = VOIDSEARCH.VoidBase.Views.templates['queueTreeEmptyGridCell'];

            HTML = HTML.replace(/\%id\%/g, nextId);
            $(GRID_CONTAINER).insert(HTML);

            var gridCellCanvasContainer = GRID_CELL_CANVAS_CONTAINER + '_' + nextId;
            var gridCell = GRID_CELL + '_' + nextId;
            var gridCellEdit = GRID_CELL_EDIT + '_' + nextId;
            var options = {};
            options.fetchSize = DEFAULT_FETCH_SIZE;

            //
            // OBSERVER ON EDIT BUTTON
            //
            $(gridCellEdit).observe('click', function(event) {
                var element = event.element();
                self.gridCellEditHandler(element, event);

            });


        },

        gridCellEditHandler:function(element, event) {
            var self = this;
            var gridContainerOffset = $(GRID_CONTAINER).cumulativeOffset();
            var buttonOffset = $(element).cumulativeOffset();
            var gridContainerWidth = $(GRID_CONTAINER).getWidth();
            var gridCellSettingsBoxWidth = $(GRID_CELL_SETTINGS).getWidth();

            // GET ID FROM CURRENT ELEMENT
            var id = element.id.split('_');
            id = id[1];
            this.cellSettingsId = id;

            self.cellEditInstance = self.getActiveCellInstance(this.cellSettingsId);

            this.API.core.AJAXGetJSON('/webapi/queuetree/?method=LIST', function(data) {
                var qs = self.getComplexData(data.queue, 'queueList',true);
                var HTML = '<select id="cellSettings-queueName">';

                HTML += '<option value="-1">-- choose queue --</option>';

                //iterate queues
                qs.each(function(queueName) {
                    if (self.cellEditInstance) {
                        var selected='';
                        if(self.cellEditInstance.queue==queueName){
                            selected='selected="selected"';
                        }
                    }
                    HTML += '<option value="'+queueName+'" '+ selected+'>'+queueName+'</option>';
                });
                HTML += '</select >';

                //update select box with queue names
                $(GRID_CELL_SETTINGS_SOURCE_QUEUE).update(HTML);

                self.cellEditQueueName = $('cellSettings-queueName').value;
                $('cellFieldSourceHolder').update('');

                if(self.cellEditInstance){
                    self.gridCellEditFetchFields(self.cellEditQueueName,self.cellEditInstance);
                }
                // listener for second drop down box (field names for complex queues)
                $('cellSettings-queueName').observe('change', function() {
                    self.cellEditQueueName = $('cellSettings-queueName').value;
                    self.gridCellEditFetchFields(self.cellEditQueueName,self.cellEditInstance);
                });

            });

            self.cellSettingsAutoSelect(self.cellEditInstance);

            $(GRID_CELL_SETTINGS_SOURCE_QUEUE).update('Loading...');


            if ($(GRID_CELL_SETTINGS).style.display == 'none') {

                $(GRID_CELL_SETTINGS).show();
                $(GRID_CELL_SETTINGS).style.top = (buttonOffset[1] - gridContainerOffset[1]) + 'px';
                var left = event.pointerX() - 150;

                //
                //compensate if out of screen
                //
                if ((left + gridCellSettingsBoxWidth) > gridContainerWidth) {
                    left = (gridContainerWidth - gridCellSettingsBoxWidth);
                }
                $(GRID_CELL_SETTINGS).style.left = left + 'px';
            } else {
                $(GRID_CELL_SETTINGS).hide();
            }

        },

        cellSettingsAutoSelect:function(instanceObject){
            if(instanceObject){

               $('cellSettings-chartType-'+instanceObject.instance.options.type).selected='selected';
               $('cellSettings-lineWidth-'+instanceObject.instance.options.lineWidth).selected='selected';
               $('cellSettings-color-'+instanceObject.instance.options.color.replace('#','')).selected='selected'; 

            }
        },

        gridCellEditFetchFields:function(queueName,instance) {
            var self = this;

            if (queueName != '-1') {
                $('cellSettings-message').update("");
                //fetch queue to examin type and/or field names
                //one entry should be enough
                var url = '/webapi/queuetree/?method=GET&queue=' + queueName + '&size=1';
                this.API.core.AJAXGetJSON(url, function(data) {
                    if (typeof(data.queue.response.queueElements.entry) != 'undefined') {

                        //get value and type
                        var value = data.queue.response.queueElements.entry.val;
                        var valueType = self._detectType(value);

                        // IF OBJECT
                        if (valueType == 'object') {
                            var fields = self.getComplexData(data.queue, 'queueElements');
                            var HTML = 'Field <select id="cellSettings-field">';

                            //iterate and create drop down menu
                            $H(fields[0]).each(function(field) {
                                var selected='';
                                if (instance) {
                                    if(instance.field==field[0]){
                                        selected='selected="selected"';
                                    }
                                }

                                HTML += '<option value="'+field[0]+'"  '+selected+' >' + field[0] + '</option>';
                            });
                            HTML += '</select>';
                            $('cellFieldSourceHolder').update(HTML);
                        }

                        // IF NUMBER
                        if (valueType == 'number') {
                            $('cellFieldSourceHolder').update('');
                        }
                    } else {
                        //console.log("SIMPLE QUEUE!!");
                        $('cellFieldSourceHolder').update('');
                    }
                });

            } else {
                $('cellSettings-message').update("Please Choose Queue!");
                $('cellFieldSourceHolder').update('');
            }

        },



        viewGrid:function(params) {
            var self = this;
            if (this.API.requiresNode('qtCanvas', this)) {
                if (typeof(params.name) !== undefined) {
                    // use default fetch size
                    if (this.fetchSize == undefined) {
                        this.fetchSize = DEFAULT_FETCH_SIZE;
                    }
                    this.API.core.AJAXGetJSON('/webapi/queuetree/?method=GET&queue=' + params.name + '&size=' + this.fetchSize, function(data) {
                        // create simple list html
                        $('qtCanvas').innerHTML = '<h4>Queue "' + params.name + '"</h4><div id="qtView"></div>';

                        // leave more advanced stuff to handler function
                        self._viewGridDispatcher(data);
                    });
                }
            }
        },

        _viewGridDispatcher:function(data) {

            var entries = data.queue.response.queueElements.entry;
            var qs = this.getComplexData(data.queue, 'queueElements');
            var numResults = data.queue.header.results.totalResults;
            var firstElement = false;

            firstElement = qs[0];

            var dataType = this._detectType(firstElement);
            this._queueData = data.queue;

            // handle according to type
            if (dataType == 'object') {
                this.fieldNames = this._getPropertyNames(firstElement);
                this.drawObjectGrid();
            }
        },


        drawObjectGrid:function() {
            var self = this;
            this.gridContainerWidth = $('qtView').getWidth() - (4 * 8);

            $('qtView').innerHTML = VOIDSEARCH.VoidBase.Views.templates['gridTable'];

            var availableGridFields = $$('td.gf');
            this.maxCellSize = this.gridContainerWidth / 4;
            availableGridFields.each(function(cell) {
                cell.style.width = self.maxCellSize + 'px';

            });

            $('normalizeGraphs').observe('click', function(event) {
                var element = event.element();
                self.normalizeGraphs = element.checked;
                self.redrawAllGraphs();
            });

            var feedName = this._queueData.response.queueMetadata.name;
            this.fieldNames.each(function(field, index) {
                var queueOptions = {};
                queueOptions.fetchSize = DEFAULT_FETCH_SIZE;
                queueOptions.lineWidth = DEFAULT_LINE_WIDTH;
                queueOptions.type = DEFAULT_CHART_TYPE;
                queueOptions.cellId = index;
                self.registerNewObject(field, feedName, availableGridFields[index].id, queueOptions);
            });

            this.globalScopeMax = 0;
            this.globalScopeMin = 0;
            this.normalizeGraphs = $('normalizeGraphs').checked;

            // start grid updater, start from the first element
            this.gridUpdater(0);
        },

        //
        // GRID UPDATER
        // 

        gridUpdater:function(index) {


            var self = this;
            var timeout = 1000;
            if (self.objectRegister.activeObjects.length > 0) {
                
                // fetch field, queue and chart instance from active objects array
                var field = self.objectRegister.activeObjects[index][0];
                var queue = self.objectRegister.activeObjects[index][1];
                var objectInstance = self.objectRegister.activeObjects[index][3];
                var queueOptions = self.objectRegister.activeObjects[index][4];

                this.fetchSize = DEFAULT_FETCH_SIZE;
                
                this.updateSingleObject(field, queue, objectInstance, queueOptions);
                //console.log(this.gridContainerWidth);
                index += 1;
                if (index >= this.objectRegister.activeObjects.length) {
                    index = 0;
                }

                //calculate and apply timeout
                timeout = defaultObjectRefreshRate / this.objectRegister.activeObjects.length;
            }
            console.log(index);
            var timeoutFunc = function () {
                self.gridUpdater(index);
            };
            this.API.timers.multipleObjectRefresh = setTimeout(timeoutFunc, timeout);


        },

        updateSingleObject:function(field, queue, chartInstance, queueOptions) {
            var self = this;
            var url = '/webapi/queuetree/?method=GET&queue=' + queue + '&size=' + queueOptions.fetchSize;
            this.API.core.AJAXGetJSON(url, function(data) {
                // leave more advanced stuff to hanfler funciton
                self.updateSingleObjectHandler(field, queue, chartInstance, data);
            });
        },

        updateSingleObjectHandler:function(field, queue, chartInstance, data) {
            //fetch data
            var canvasElement = $(chartInstance.canvas.ctx.canvas.id);
            var canvasContainer = canvasElement.parentNode;
            var fieldData = [];
            if (field === false) {
                fieldData = getSimpleFieldData(data);
                chartInstance.options.yTitle = data.queue.response.queueMetadata.name;
            } else {
                //complex object queue
                fieldData = getFieldData(field, data);
            }
            var containerWidth = canvasContainer.getWidth();
            var scopeChanged = false;

            if (this.normalizeGraphs) {
                scopeChanged = this.setGlobalScope(fieldData);
            }
            //re size canvas to fit container
            canvasElement.width = containerWidth - 3;
            canvasElement.height = 150;

            if (this.normalizeGraphs) {
                chartInstance.forceScope = true;
                chartInstance.forceMaxValue = this.globalScopeMax;
                chartInstance.forceMinValue = this.globalScopeMin;
            }

            // redraw 
            if (!scopeChanged) {
                chartInstance.options.chartData = fieldData;
                chartInstance.updated = true;
                chartInstance.resetGraph();
                chartInstance.drawGraph();
            } else {
                chartInstance.options.chartData = fieldData;
                chartInstance.updated = true;
                this.redrawAllGraphs();
            }

        },

        setGlobalScope:function(data) {
            var max = data.max();
            var min = data.min();
            var scopeChanged = false;

            if (max > this.globalScopeMax) {
                this.globalScopeMax = max;
                scopeChanged = true;
            }

            if (min < this.globalScopeMin) {
                this.globalScopeMin = min;
                scopeChanged = true;
            }

            return scopeChanged;
        },

        redrawAllGraphs:function() {
            var self = this;
            this.objectRegister.activeObjects.each(function(elm) {
                var chartInstance = elm[3];
                chartInstance.forceScope = self.normalizeGraphs;
                if (self.normalizeGraphs) {
                    chartInstance.forceMaxValue = self.globalScopeMax;
                    chartInstance.forceMinValue = self.globalScopeMin;
                }
                if (chartInstance.updated) {
                    chartInstance.resetAndRedraw();
                }
            });

        },


        registerNewObject:function(field, queue, container, queueOptions) {
            console.log(container);
            if (typeof(this.objectRegister.activeContainers[queueOptions.cellId]) == 'undefined') {

                var canvasId = 'graph-canvas-' + container;
                $(container).innerHTML = '<canvas id="' + canvasId + '" width="250" height="150"></canvas>';
                //console.log("start");
                queueOptions.canvasId = canvasId;
                var instance = new ChartEngine({
                    'canvasID':canvasId,
                    'tooltip':'scatter-tooltip',
                    'type':queueOptions.type,
                    'xTitle':'time',
                    'color':queueOptions.color,
                    'lineWidth':queueOptions.lineWidth,
                    'yTitle':field
                });

                var self = this;
                $(canvasId).observe('click', function(event) {
                    var elem = event.element();
                    self.switchType(elem.id);
                });
                this.objectRegister.activeObjects.push([field,queue,container,instance,queueOptions]);
                this.objectRegister.activeContainers[queueOptions.cellId] = 1;

                self.updateSingleObject(field, queue, instance, queueOptions);

            } else {
                this.updateExistingGridCellSettings(queueOptions, field, queue);
            }
        },

        updateExistingGridCellSettings:function(queueOptions, field, queue) {
            var self = this;
            this.objectRegister.activeObjects.each(function(elm, index) {
                var cId = elm[4].cellId;
                if (queueOptions.cellId == cId) {

                    var instance = elm[3];
                    instance.options.yTitle = field;
                    instance.options.type = queueOptions.type;
                    instance.options.lineWidth = queueOptions.lineWidth;
                    instance.options.color = queueOptions.color;

                    self.objectRegister.activeObjects[index][0] = field;
                    self.objectRegister.activeObjects[index][1] = queue;

                    self.updateSingleObject(field, queue, instance, elm[4]);

                    throw $break;
                }
            });
        },

        getActiveCellInstance:function(cellId) {
            var self = this;

            var object = false;
            this.objectRegister.activeObjects.each(function(elm, index) {
                var cId = elm[4].cellId;
                if (cellId == elm[4].cellId) {
                    object={};
                    object.field=elm[0]
                    object.queue=elm[1]
                    object.instance = elm[3];
                    throw $break;
                }
            });

            return object;
        },

        switchType:function(containerId) {
            this.objectRegister.activeObjects.each(function(elm) {
                var canvasId = elm[4].canvasId;
                if (canvasId == containerId) {

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
                this.fetchSize = DEFAULT_FETCH_SIZE;
            }
            if (this.API.requiresNode('qtCanvas', this)) {
                if (typeof(params.name) !== undefined) {
                    this.API.core.AJAXGetJSON('/webapi/queuetree/?method=GET&queue=' + params.name + '&size=' + this.fetchSize, function(data) {
                        // create simple list html
                        $('qtCanvas').innerHTML = '<h4>Queue "' + params.name + '"</h4><div id="qtView"></div>';
                        self._view(data);
                    });
                }
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

            this.API.core.AJAXGetJSON(URI, function(data) {
                self._queueData = data;
                self._queueData = self._queueData.queue;
                self._drawObjectGraph(this.fieldToDraw);
            });

            var timeoutFunc = function() {
                self._repeatObjectView();
            };
            this.API.timers.refreshViewTimer = setTimeout(timeoutFunc, 2000);
        },

        _view:function(data) {
            //get the first element of queue and try to resolve type
            var qs = [];
            var numResults = data.queue.header.results.totalResults;
            var entries = data.queue.response.queueElements.entry;

            entries.each(function(entry) {
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

            // handle according to type
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
            $('qtViewSelectField').observe('change', function() {
                self._selectFieldNameHandler();
            });

            $('fetchSizeSelect').observe('change', function() {
                self._selectSizeChange();
            });

            //draw for first field
            this.fieldToDraw = this.fieldNames.first();
            this._repeatObjectView();

        },


        getComplexData:function(JSONData, container, sort) {
            var data = [];
            var entries = [];
            if(typeof(sort)=='undefined'){
                sort=false;
            }
            entries.push(JSONData.response[container].entry);
            if (JSONData.header.results.totalResults > 1) {
                entries = entries[0];
            }
            entries.each(function(entry) {
                data.push(entry.val);
            });
            if(sort){
                data.sort();
            }
            return data;
        },

        _drawObjectGraph:function() {
            $('graph-canvas').width = this.API.screenWidth - 40;
            $('graph-canvas').height = '160';

            var self = this;
            var data = [];
            var qs = this.getComplexData(this._queueData, 'queueElements');

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

        list:function() {
            if (this.API.requiresNode('qtCanvas', this)) {
                this.API.notify('queueTree:fetchQueueList');
            }
        },

        listHandler:function(data) {

            var qs = this.getComplexData(data.queue, 'queueList');

            // create simple list html
            var HTML = '<h4>Queue List</h4>';
            HTML += '<ul class="big-list">';

            //itterate array
            qs.each(function(c) {
                HTML += '<li><a href="#queueTree/view/?name=' + c + '" class="f1">' + c + '</a>  <a href="#queueTree/viewGrid/?name=' + c + '" class="f2">[VIEW GRID # ]</a> </li>';
            });
            HTML += '</ul>';
            $('qtCanvas').innerHTML = HTML;

        },

        registerListeners:function() {


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

VOIDSEARCH.VoidBase.extend(VOIDSEARCH.VoidBase.WebAPI.modules.queueTree, VOIDSEARCH.Events);
