Visuals = function (elm) {
    // initialise
    var canvas = document.getElementById(elm);
    this.ctx = canvas.getContext('2d');

    //define some constants
    this.containerWidth = $(elm).getWidth(); //default
    this.containerHeight = $(elm).getHeight(); //default
    this.ctx.clearRect(0, 0, this.containerWidth, this.containerHeight);
    this.padding = 10;
    this.xMid = this.containerWidth / 2;
    this.yMid = this.containerHeight / 2;

    this.startX = this.xMid - 60;
    this.startY = this.yMid + 60;
    this.defaultColor = '#414141';
    this.stepX = this.xMid / 10;

    this.lineWidth = 1.0;

    this.factor = Math.round(this.stepX / 1.41421356);

}

Visuals.prototype.cutHex = function (colorHex) {
    var out = '';
    if (colorHex.charAt(0) == "#") {
        out = colorHex.substring(1, 7);
    } else {
        out = colorHex;
    }
    return out;

}

Visuals.prototype.line = function (x1, y1, x2, y2, colorHex, alpha) {

    this.ctx.lineWidth = this.getLineWidth();
    this.ctx.beginPath();

    this.ctx.lineCap = 'butt';
    if (alpha === undefined) {
        this.ctx.strokeStyle = colorHex || this.defaultColor;

    } else {
        function cutHex(colorHex) {
            return (colorHex.charAt(0) == "#") ? colorHex.substring(1, 7) : colorHex
        }

        var colorR = parseInt((cutHex(colorHex)).substring(0, 2), 16);
        var colorG = parseInt((cutHex(colorHex)).substring(2, 4), 16);
        var colorB = parseInt((cutHex(colorHex)).substring(4, 6), 16);
        this.ctx.strokeStyle = 'rgba(' + colorR + ',' + colorG + ',' + colorB + ',' + alpha + ')';
    }
    //console.debug(x1,y1,x2,y2);
    this.ctx.moveTo(x1, y1)
    this.ctx.lineTo(x2, y2);

    this.ctx.stroke();
    this.ctx.closePath();

}

Visuals.prototype.poly2d = function (vertices, colorHex, alpha) {

    //this.ctx.lineWidth=this.getLineWidth();
    this.ctx.beginPath();

    if (alpha === undefined) {
        this.ctx.strokeStyle = colorHex || this.defaultColor;

    } else {
        function cutHex(colorHex) {
            return (colorHex.charAt(0) == "#") ? colorHex.substring(1, 7) : colorHex
        }

        var colorR = parseInt((cutHex(colorHex)).substring(0, 2), 16);
        var colorG = parseInt((cutHex(colorHex)).substring(2, 4), 16);
        var colorB = parseInt((cutHex(colorHex)).substring(4, 6), 16);
        this.ctx.fillStyle = 'rgba(' + colorR + ',' + colorG + ',' + colorB + ',' + alpha + ')';
    }

    for (var i = 0; i < vertices.length; i++) {

        if (i != 0) {
            this.ctx.lineTo(vertices[i][0], vertices[i][1]);
        } else {
            this.ctx.moveTo(vertices[i][0], vertices[i][1]);
        }

    }

    this.ctx.fill();
    //this.ctx.closePath();
}

// Circle
Visuals.prototype.circle = function (x, y, radius, fill, colorHexFill, colorHexStroke, alpha) {

    this.ctx.lineWidth = this.getLineWidth();
    this.ctx.beginPath();

    if (alpha === undefined) {
        this.ctx.fillStyle = colorHexFill || this.defaultColor;
        this.ctx.strokeStyle = colorHexStroke || this.defaultColor;
    } else {

        var colorR = (colorHexFill.substring(0, 2), 16);
        var colorG = (colorHexFill.substring(2, 4), 16);
        var colorB = (colorHexFill.substring(4, 6), 16);
        this.ctx.fillStyle = 'rgba(' + colorR + ',' + colorG + ',' + colorB + ',' + alpha + ')';

        var colorRF = (colorHexStroke.substring(0, 2), 16);
        var colorGF = (colorHexStroke.substring(2, 4), 16);
        var colorBF = (colorHexStroke.substring(4, 6), 16);

        this.ctx.strokeStyle = 'rgba(' + colorRF + ',' + colorGF + ',' + colorBF + ',' + alpha + ')';
    }
    //DRAW CIRCLE
    this.ctx.arc(x, y, radius, 0, 360, false);
    //console.log('x:'+x+' y:'+y+' radius:'+radius+' alpha'+alpha);

    if (fill === true) {
        this.ctx.fill();
    }

    this.ctx.stroke();
} // Circle END
Visuals.prototype.clear = function (x, y, w, h) {
    this.ctx.clearRect(x, y, w, h);

}

Visuals.prototype.reset = function () {
    this.ctx.clearRect(0, 0, this.containerWidth, this.containerHeight);

}

Visuals.prototype.text = function (text, x, y, fontsize, colorHexStroke, alpha, angle) {
    if (typeof(angle) == 'undefined') {
        angle = 0;
    }
    set_textRenderContext(this.ctx);

    function cutHex(colorHex) {
        return (colorHex.charAt(0) == "#") ? colorHex.substring(1, 7) : colorHex
    }

    colorRF = parseInt((cutHex(colorHexStroke)).substring(0, 2), 16);
    colorGF = parseInt((cutHex(colorHexStroke)).substring(2, 4), 16);
    colorBF = parseInt((cutHex(colorHexStroke)).substring(4, 6), 16);

    var clr = 'rgba(' + colorRF + ',' + colorGF + ',' + colorBF + ',' + alpha + ')';

    if (angle == 90) {
        this.ctx.save();

        this.ctx.rotate((Math.PI / 180) * angle);
        this.ctx.translate(y+20, -110);
        console.log(x,y);

    }
    this.ctx.strokeStyle = clr;
    this.ctx.strokeText(text, x, y, fontsize);

    if (angle != 0) {
        //this.ctx.translate(x, y);
        //this.ctx.rotate((Math.PI/180)*(-1*angle));
        //this.ctx.translate(-400,0);
        this.ctx.restore();
    }
}

Visuals.prototype.getLineWidth = function () {
    return this.lineWidth;
}

Visuals.prototype.setLineWidth = function (w) {
    this.lineWidth = w;
}

Visuals.prototype.clearBlack = function () {

    this.clear(0, 0, 1100, 200);
    this.ctx.fillStyle = "rgba(0, 0, 0, 1)";
    this.ctx.fillRect(0, 0, 1150, 200);

}

Visuals.prototype.clearBlack2 = function () {

    this.clear(0, 0, 640, 480);
    this.ctx.fillStyle = "rgba(0, 0, 0, 1)";
    this.ctx.fillRect(0, 0, 640, 480);

}

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