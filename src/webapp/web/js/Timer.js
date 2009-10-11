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