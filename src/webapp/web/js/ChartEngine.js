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

var ChartEngine = Class.create({
    initialize: function (options) {
        //get options
        this.options = options;

        //this.options.numResults = this.options.chartData.length;
        this.chartPadding = 10;
        this.leftPadding = 26.5;
        this.rightPadding = 15.5;
        this.bottomPadding = 26.5;
        this.topPadding = 26;
        this.axisColor = '#cacaca';
        this.axisOpacity = 1;
        this.paddingFactor = 0.00;
        this.drawMinMaxLines = false;
        this.scatterChunkSize = 1000;

        // init canvas
        this.canvas = new Visuals(options.canvasID);
        this.canvasWidth = this.canvas.containerWidth;
        this.canvasHeight = this.canvas.containerHeight;



        //reset drawing surface
        this.canvas.reset();

        //prepare graph, and if ok draw data

        if (this.prepareGraph()) {


        }
    },

    resetGraph:function(){

        this.canvas.reload();
        this.canvasWidth = this.canvas.canvasElement.getWidth();
        this.canvasHeight = this.canvas.canvasElement.getHeight();

        this.xAxis = new xAxis(this);
        this.yAxis = new yAxis(this);

        this.drawAxes();
    },

    resetAndRedraw:function(){

        this.canvas.reset();
        this.resetGraph();
        this.drawGraph();
    },

    getPadding: function () {
        return[this.topPadding, this.rightPadding, this.bottomPadding, this.leftPadding];
    },

    getCanvas: function () {
        return this.canvas;
    },

    printError: function (text) {
        this.canvas.reset();
        this.chart.canvas.text(text.toString(), 10, 10, 10, '#414141', 0.9);

    },

    prepareGraph: function () {
        if (!this.options.type !== undefined) {
            this.xAxis = new xAxis(this);
            this.yAxis = new yAxis(this);
            this.drawAxes();
            var chartOk = true;
        } else {
            this.printError('invalid data returned!');
            var chartOk = false;
        }
        return chartOk;
    },

    drawGraph: function () {

        switch (this.options.type) {
            case 'bars':
                this.drawGenericGraph();
                break;

            case 'api-scatter':
                this.drawAPIScatterGraph();
                break;
            case 'scatter':
                this.drawScatterGraph();
                break;

            case 'line':
                this.drawGenericGraph();
                break;
        }
    },

    drawAxes: function () {
        this.xAxis.draw();
        this.yAxis.draw();
    },

    drawAPIScatterGraph: function () {
        var self = this;

        this.options.apiXMax = parseFloat(this.options.apiXMax);
        this.options.apiXMin = parseFloat(this.options.apiXMin);
        this.options.apiYMax = parseFloat(this.options.apiYMax);
        this.options.apiYMin = parseFloat(this.options.apiYMin);

        this.xMax = Math.ceil(this.options.apiXMax + ((this.options.apiXMax - this.options.apiXMin) * 0.05));
        this.yMax = Math.ceil(this.options.apiYMax + ((this.options.apiYMax - this.options.apiYMin) * 0.05));
        this.xMin = 0;
        //this.xMin=0;
        //this.yMin=0;
        this.xMin = Math.floor(this.options.apiXMin - ((this.options.apiXMax - this.options.apiXMin) * 0.05));
        this.yMin = Math.floor(this.options.apiYMin - ((this.options.apiYMax - this.options.apiYMin) * 0.05));

        this.xscope = this.xMax - this.xMin;
        this.yscope = this.yMax - this.yMin;
        // X AXIS
        this.xAxis.printTitle(this.options.xTitle);
        if (this.drawMinMaxLines) {
            this.xAxis.drawLine(this.options.apiXMax, 'text', '#6767cc', 0.7);
            this.xAxis.drawLine(this.options.apiXMin, 'text', '#6767cc', 0.7);
        }
        // Y AXIS
        this.yAxis.printTitle(this.options.yTitle);
        if (this.drawMinMaxLines) {
            this.yAxis.drawLine(this.options.apiYMax, 'text', '#6767cc', 0.7);
            this.yAxis.drawLine(this.options.apiYMin, 'text', '#6767cc', 0.7);
        }

        // set scatter dot color
        this.scatterColor = '#000000';
        //itterate
        this.options.chartData.each(function (elm, index) {
            var xScaled = self.scaleX(elm[0]);
            var yScaled = self.scaleY(elm[1]);
            //draw dots
            self.canvas.circle(xScaled, yScaled, 3, true, self.scatterColor, self.scatterColor, 0.5);
        });

        this.drawAPIScatterCurve();
        this.setScatterListeners();

    },

    drawScatterGraph: function () {
        var self = this;
        this.options.apiXMax = parseFloat(this.options.apiXMax);
        this.options.apiXMin = parseFloat(this.options.apiXMin);
        this.options.apiYMax = parseFloat(this.options.apiYMax);
        this.options.apiYMin = parseFloat(this.options.apiYMin);

        this.xMax = Math.ceil(this.options.apiXMax + ((this.options.apiXMax - this.options.apiXMin) * this.paddingFactor));
        this.xMin = 0;
        this.xMin = Math.floor(this.options.apiXMin - ((this.options.apiXMax - this.options.apiXMin) * this.paddingFactor));

        this.yMax = Math.ceil(this.options.apiYMax + ((this.options.apiYMax - this.options.apiYMin) * this.paddingFactor));
        this.yMin = Math.floor(this.options.apiYMin - ((this.options.apiYMax - this.options.apiYMin) * this.paddingFactor));

        //scope
        this.xscope = this.xMax - this.xMin;
        this.yscope = this.yMax - this.yMin;

        // X AXIS
        this.xAxis.printTitle(this.options.xTitle);

        this.xAxis.drawLine(this.options.apiXMax, 'text', '6767cc', 0.4);
        this.xAxis.drawLine(this.options.apiXMin, 'text', '6767cc', 0.4);

        // Y AXIS
        this.yAxis.printTitle(this.options.yTitle);

        this.yAxis.drawLine(this.options.apiYMax, 'text', '6767cc', 0.4);
        this.yAxis.drawLine(this.options.apiYMin, 'text', '6767cc', 0.4);

        var timeoutFunc = function () {
            self.drawScatterGraphData(0);
        }
        this.timer = setTimeout(timeoutFunc, 10);

    },

    detectNegative:function(){

        this.hasNegative=false;
        var self=this;
        this.options.chartData.each(function(element){
            if(element < 0){
                self.hasNegative=true;
                throw $break;
            }
        });

    },

    // DRAW LINE GRAPH
    // !! PLEASE !! provide array of integers in this.options.data
    drawGenericGraph:function() {
        

        this.detectNegative();
        //get scope and min/max values


        this.paddingFactor = 0;
        if(this.options.type=='line'){
            this.paddingFactor = 0.0;    
        }

        this.getDataScope();

        if(!this.hasNegative && this.options.type=='bars'){
            //we are drawing from the zero
            this.yMin=0;
            this.yscope=this.yMax-this.yMin;
        }
        
        this.availableCanvasWidth=this.scaleX(this.xMax)-this.scaleX(this.xMin);
        this.drawMinMaxLines = false;

        // X AXIS
        this.xAxis.printTitle(this.options.xTitle);
        this.xAxis.drawLine(this.xMax, 'text', '6767cc', 0.4);
        this.xAxis.drawLine(this.xMin, 'text', '6767cc', 0.4);


        // Y AXIS
        this.yAxis.printTitle(this.options.yTitle);
        this.yAxis.drawLine(this.yMax, 'max', '6767cc', 0.4);
        this.yAxis.drawLine(this.yMin, 'min', '6767cc', 0.4);

        
        if(this.options.type=='bars'){
            this.drawBars();
        }

        if(this.options.type=='line'){
            this.drawLineGraph();
        }




    },

    drawLineGraph:function(){

        var self=this;
        var len=this.options.chartData.length;
        var data=this.options.chartData.reverse(false);

        var x = 0;
        var xNext = 0;
        var y = 0;
        var yNext = 0;

        data.each(function(elm, index) {

            if(index < len-1){
                x = self.scaleX(index) + 0.5;
                y = self.scaleY(elm);

                xNext = self.scaleX(index+1) + 0.5;
                yNext = self.scaleY(data[index+1]);

                self.canvas.line(x,y,xNext,yNext , '#acac66', 0.6 );
            }
        });
    
    },


    drawBars:function(){
        var self=this;
        
        var x = 0;
        var y = 0;
        var yZero = this.scaleY(self.yMin);
        var xMaxScaled=this.scaleX(this.xMax);
        var xMinScaled=this.scaleX(this.xMin);

        var barWidth=(xMaxScaled-xMinScaled-2)/this.options.chartData.length;
        if(barWidth < 1){
            barWidth=1  ;
        }

        if(barWidth  >= 3){
            barWidth-=1.5;
        }

        this.options.chartData.reverse(false).each(function(elm, index) {

            x = self.scaleX(index) + 0.5;
            y = self.scaleY(elm);

            //console.log(x);
            //self.canvas.line(x, y, x, yZero, '#7878cc', 0.8);
            self.canvas.poly2d([[x,y],[x+barWidth,y],[x+barWidth,yZero],[x,yZero]], '#acca66', 0.7  );

        });

    },


    // GET DATA SCOPE
    getDataScope:function() {

        // get min and max values
        this.xMin = 0;
        this.yMin = this.options.chartData.min();
        this.xMax = this.options.chartData.length;
        this.yMax = this.options.chartData.max();


        if (this.yMax == this.yMin && this.yMax != 0) {
            this.yMin = 0;
        }

        this.xscope = this.xMax - this.xMin;
        this.yscope = this.yMax - this.yMin;

        this.yMax = this.yMax + ((this.yMax - this.yMin) * this.paddingFactor);
        if(this.yMin==0){
            this.paddingFactor=0;
        }
        this.yMin = Math.floor(this.yMin - ((this.yMax - this.yMin) * this.paddingFactor));


        this.yscope = this.yMax - this.yMin;

    },

    drawScatterGraphData: function (start) {
        var self = this;

        // set scatter dot color
        this.scatterColor = '000000';

        var size = this.scatterChunkSize + start;

        if (size > (this.options.numResults)) {
            size = (this.options.numResults);
        }

        //console.log(size);
        var em = [];
        for (var i = start; i < size; i++) {
            em = this.options.chartData[i];
            //console.log(em);
            var xScaled = this.scaleX(em[0]);
            var yScaled = this.scaleY(em[1]);
            //draw dots
            self.canvas.circle(xScaled, yScaled, 3, true, self.scatterColor, self.scatterColor, 0.5);
        }

        console.log(start);
        if ((size) < this.options.numResults) {
            var timeoutFunc = function () {
                self.drawScatterGraphData(i);
            }
            this.timer = setTimeout(timeoutFunc, 20);
        } else {

            TIMER.message('scatter graph done');
            TIMER.dumpTimer();
        }
        //this.setScatterListeners();
    },

    drawAPIScatterCurve: function () {
        var self = this;
        var len = this.options.curve.length;
        this.canvas.setLineWidth(1);
        this.options.curve.each(function (elm, idx) {

            if (idx < len - 1) {
                var x = elm[0];
                var y = elm[1];

                var nextX = self.options.curve[idx + 1][0];
                var nextY = self.options.curve[idx + 1][1];

                var xScaled = self.scaleX(x);
                var yScaled = self.scaleY(y);

                var nextXScaled = self.scaleX(nextX);
                var nextYScaled = self.scaleY(nextY);

                self.canvas.line(xScaled, yScaled, nextXScaled, nextYScaled, '#ff0000', 0.7);
            }
        });

    },

    scaleX: function (x) {
        return Math.round((this.canvasWidth - (this.leftPadding + this.rightPadding)) - ((this.xMax - x) / this.xscope * (this.canvasWidth - this.leftPadding - this.rightPadding)) + (this.leftPadding));
    },

    scaleY: function (y) {
        return Math.round(((this.yMax - y) / this.yscope * (this.canvasHeight - this.topPadding - this.bottomPadding)) + this.topPadding);

    },

    reverseScaleX: function (x) {

        return Math.round(x);

    },
    setScatterListeners: function () {

        var self = this;
        this.tooltip = $(this.options.tooltip);
        Event.observe($(this.options.canvasID), 'mousemove', function (e) {
            var elm = Event.element(e);
            var offset = elm.cumulativeOffset()

            var top = e.pointerY() - offset.top;
            var left = e.pointerX() - offset.left;
            self.drawScatterTooltip(top, left);
        });

        Event.observe($(this.options.canvasID), 'mouseover', function (e) {
            self.tooltip.show();

        });

        Event.observe($(this.options.canvasID), 'mouseout', function (e) {
            self.tooltip.hide();

        });

    },

    drawScatterTooltip: function (top, left) {

        this.tooltip = $(this.options.tooltip);

        this.tooltip.innerHTML = 'test test';
        this.tooltip.style.top = top + 'px';
        this.tooltip.style.left = left + 'px';
        this.tooltip.innerHTML = this.options.xTitle + ': ' + this.reverseScaleX(left) + '<br>' + this.options.yTitle + ': ' + top;
    },

    getScatterXMaxValue: function () {
        var xMax = this.options.chartData[0][0];
        this.options.chartData.each(function (val) {
            if (val[0] > xMax) {
                xMax = val[0];
            }
        });
        return xMax;
    },

    getScatterYMaxValue: function () {
        var yMax = this.options.chartData[0][1];
        this.options.chartData.each(function (val) {
            if (val[1] > yMax) {
                yMax = val[1];
            }
        });

        return yMax;
    },
    getScatterXMinValue: function () {
        var xMin = this.options.chartData[0][0];
        this.options.chartData.each(function (val) {
            if (val[0] < xMin) {
                xMin = val[0];
            }
        });
        return xMin;
    },

    getScatterYMinValue: function () {
        var yMin = this.options.chartData[0][1];
        this.options.chartData.each(function (val) {
            if (val[1] < yMin) {
                yMin = val[1];
            }
        });
        return yMin;
    },

    dbg: function () {
        console.log(this);
    }
});

var xAxis = Class.create(ChartEngine, {
    initialize: function (self) {
        this.chart = self;
    },
    //draw x axis
    draw: function () {
        this.chart.canvas.line(this.chart.leftPadding - 10, (this.chart.canvasHeight - this.chart.bottomPadding), this.chart.canvasWidth - this.chart.rightPadding, (this.chart.canvasHeight - this.chart.bottomPadding), this.chart.axisColor, this.chart.axisOpacity);
    },
    //print x axis max
    printMax: function (val) {
        this.chart.canvas.text(val.toString(), this.chart.canvasWidth - this.chart.rightPadding - 20, (this.chart.canvasHeight - this.chart.bottomPadding + 10), 8, '#414141', 0.9);
    },
    // print x axis min
    printMin: function (val) {
        this.chart.canvas.text(val.toString(), this.chart.leftPadding, (this.chart.canvasHeight - this.chart.bottomPadding + 10), 8, '#414141', 0.9);
    },
    // draw x axis title
    printTitle: function (title) {
        this.chart.canvas.text(title.toString(), (this.chart.canvasWidth / 2) - this.chart.rightPadding - 20, (this.chart.canvasHeight - this.chart.bottomPadding + 8), 8, '#414141', 0.9);
    },
    drawLine: function (h, text, color, alpha) {
        //scale coordinates
        var y = this.chart.canvasHeight - this.chart.bottomPadding;
        var xScaled = this.chart.scaleX(h) + 0.5;
        //draw line
        if (this.chart.drawMinMaxLines) {
            this.chart.canvas.line(xScaled, y, xScaled, this.chart.topPadding, color, alpha);
        }
        var textWidth = get_textWidth(h.toString(), 8);
        this.chart.canvas.text(h.toString(), xScaled - (textWidth / 2) , this.chart.canvasHeight - 16, 8, color, alpha);
    }
});

//
// Y AXIS
//
var yAxis = Class.create(ChartEngine, {
    initialize: function (self) {
        this.chart = self;
    },

    scaleX: function ($super, x) {

        return $super(x);
    },

    scaleY: function ($super, y) {

        return $super(y);
    },
    //draw y axis
    draw: function () {
        this.chart.canvas.line(this.chart.leftPadding, 15, this.chart.leftPadding, this.chart.canvasHeight - this.chart.bottomPadding + 10, this.chart.axisColor, this.chart.axisOpacity);
    },

    printMax: function (val) {
        this.chart.canvas.text(val.toString(), 5, 5, 8, '#414141', 0.9);
    },

    printMin: function (val) {
        this.chart.canvas.text(val.toString(), 5, (this.chart.canvasHeight - this.chart.bottomPadding) - 15, 8, '#414141', 0.9);
    },
    //write vertical title
    printTitle: function (val) {
        this.chart.canvas.text(val.toString(), 5, ((this.chart.canvasHeight / 2) - this.chart.bottomPadding) - 15, 8, '#414141', 0.9, 90);
    },

    drawLine: function (h, type, color, alpha) {

        var hOffset=0;

        if(type=='max'){
            hOffset=+21;
        }

        if(type=='min'){
            hOffset=-1;
        }


        //scale
        var x = this.chart.leftPadding;
        var yScaled = this.chart.scaleY(h) + 0.5;
        //draw line
        if (this.chart.drawMinMaxLines) {
            this.chart.canvas.line(x, yScaled, (this.chart.canvasWidth - this.chart.rightPadding), yScaled, color, alpha);
        }
        this.chart.canvas.text(h.toString(), 4, yScaled - hOffset, 8, color, alpha);

    }
});