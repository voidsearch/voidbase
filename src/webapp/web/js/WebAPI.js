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

    isModuleDispatchable:function(module, action) {
        var dispatchable = false;
        if (typeof(this.modules[this.app.module].API == undefined)) {
            this.modules[this.app.module]._init(this);
        }


        if (typeof(this.modules[this.app.module]) != 'undefined' && typeof(this.modules[this.app.module][this.app.action]) != 'undefined') {
            dispatchable = true;
        }
        return dispatchable;
    },

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
        try {
            var elm = $(nodeId);
            if (elm == null) {
                var timeoutFunc = function() {
                    self.modules[self._GM][self._GA]();
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
// QueueTree module
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

    _statsHandler:function() {
        $('qtStats').innerHTML = 'test test test';

    },

    view:function(params) {
        console.log(params);
    },

    list:function() {
        if (this.API.requiresNode('qtCanvas', this)) {
            $('qtCanvas').innerHTML = '<br /><p>QueTree queues list will be here ... :)</p>';
        }
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
        // /usr/bin/php /home/kodi/vast/analytics/bin/void_insertRandomData.php

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
                var tmpArray=[];
                ret.queue.response.queueElements.val.each(function(elm){
                    tmpArray.push(elm.evalJSON());
                });

                ret.queue.response.queueElements.val=tmpArray;

                ret.queue.response.queueMetadata.max=ret.queue.response.queueMetadata.max.evalJSON();
                ret.queue.response.queueMetadata.min=ret.queue.response.queueMetadata.min.evalJSON();
                ret.error=false;
               
                TIMER.message("query data evaled to JSON");
                self.drawScatter(ret);
            }
        });
    },

    drawScatter:function(apiData) {
        console.log(apiData);
        if (apiData.error != true) {
            $('debug').innerHTML = '';
            var a=new ChartEngine({
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


//
//set defaultModule to home module
//
APIModules.defaultModule = APIModules.home;