VOIDSEARCH.VoidBase.Views=function(){
    // private variables

    
        
    return{
        templates:{},
        totalTemplates:7,
        templatesLoaded:0,

        loadComplete:function(app){

            this.templatesLoaded+=1;


                var percentage=Math.round(this.templatesLoaded/this.totalTemplates*100);
                var percengageHTML='<h5>loaded '+percentage+' %</h5>';

                $(VOIDSEARCH.VoidBase.WebAPI.baseNode).update(percengageHTML);
                console.log("loaded "+percentage+" %");
            if(this.templatesLoaded==this.totalTemplates){

                app.onAllLoaded();
            }else{



            }
        },

        loadTemplates:function(app){
            var self=this;


            VOIDSEARCH.VoidBase.Core.AJAXGet('/files/html/webapi/homeTemplate.html',function(data){
                self.templates.homeTemplate=data;
                self.loadComplete(app);
            });

            VOIDSEARCH.VoidBase.Core.AJAXGet('/files/html/webapi/modulesTemplate.html',function(data){
                self.templates['modulesTemplate']=data;
                self.loadComplete(app);
            });

            VOIDSEARCH.VoidBase.Core.AJAXGet('/files/html/webapi/queueTreeDebugSplatter.html',function(data){
                self.templates['queueTreeDebugSplatter']=data;
                self.loadComplete(app);
            });

            VOIDSEARCH.VoidBase.Core.AJAXGet('/files/html/webapi/queueTreeMenu.html',function(data){
                self.templates['queueTreeMenu']=data;
                self.loadComplete(app);
            });

            VOIDSEARCH.VoidBase.Core.AJAXGet('/files/html/webapi/queueTreeStats.html',function(data){
                self.templates['queueTreeStats']=data;
                self.loadComplete(app);
            });

            VOIDSEARCH.VoidBase.Core.AJAXGet('/files/html/webapi/queuetreeTemplate.html',function(data){
                self.templates['queueTreeTemplate']=data;
                self.loadComplete(app);

            });

            VOIDSEARCH.VoidBase.Core.AJAXGet('/files/html/webapi/gridTable.html',function(data){
                self.templates['gridTable']=data;
                self.loadComplete(app);
            });

        }


    };

}();