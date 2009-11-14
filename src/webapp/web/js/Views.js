VOIDSEARCH.VoidBase.Views=function(){

    // private variables
    var templateSources=[
        {
            url:'/files/html/webapi/homeTemplate.html',
            name:'homeTemplate'
        },
        {
            url:'/files/html/webapi/modulesTemplate.html',
            name:'modulesTemplate'
        },
        {
            url:'/files/html/webapi/queueTreeDebugSplatter.html',
            name:'queueTreeDebugSplatter'
        },
        {
            url:'/files/html/webapi/queueTreeMenu.html',
            name:'queueTreeMenu'
        },
        {
            url:'/files/html/webapi/queueTreeStats.html',
            name:'queueTreeStats'
        },
        {
            url:'/files/html/webapi/queuetreeTemplate.html',
            name:'queueTreeTemplate'
        },
        {
            url:'/files/html/webapi/gridTable.html',
            name:'gridTable'
        },
        {
            url:'/files/html/webapi/queueTreeNewGrid.html',
            name:'queueTreeNewGrid'
        }    

    ];

    //public methods
    return{
        templates:{},
        totalTemplates:templateSources.length,
        templatesLoaded:0,

        loadComplete:function(){
            this.templatesLoaded+=1;

            var percentage=Math.round(this.templatesLoaded/this.totalTemplates*100);
            var percengageHTML='<h5>loaded '+percentage+' %</h5>';

            $(VOIDSEARCH.VoidBase.WebAPI.baseNode).update(percengageHTML);
            if(this.templatesLoaded==this.totalTemplates){
                this.app.onAllLoaded();
            }
        },

        loadTemplates:function(app){
            this.app=app;
            this.load(0);
        },

        load:function(index){
            var self=this;
            var elm=templateSources[index];

            VOIDSEARCH.VoidBase.Core.AJAXGet(elm.url,function(data){
                self.templates[elm.name]=data;
                self.loadComplete();
            });

            if(index < (templateSources.length-1)){
                index+=1;
                var timeoutFunc = function () {
                    self.load(index);
                };
                this.timer = setTimeout(timeoutFunc, 40);
            }
        }
    };
}();