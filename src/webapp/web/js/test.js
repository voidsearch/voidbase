var RequestTester=Class.create({

    initialize:function(){

        this.testRequests();
    },

    testRequests:function(){

        $('debugResults').innerHTML+="Sending request \n";
        var requestURL=$('resourceURL').value;
        new Ajax.Request(requestURL, {
	            method: 'get',

            onSuccess: function(transport) {
                try{
                    var ret=transport.responseText;
                }catch(err){

                    $('debugResults').innerHTML=err.description;    
                }
                
                $('debugResults').innerHTML+="Request received \n";
                $('result').innerHTML=ret;

            }
        });



        var self=this;
        var  timeoutFunc=function(){ self.testRequests(); }
        this.timer=setTimeout (timeoutFunc, 5000 );
        $('debugResults').innerHTML+="waiting 5 sec \n";
    }


});

var changeText=function(){
    document.getElementById('jsContent').innerHTML='THE JS IS WORKING BRE!';
}