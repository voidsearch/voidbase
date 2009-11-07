VOIDSEARCH.VoidBase.WebAPI.templates=function(){
    // private variables
    var DEFAULT_PATTERN_IMAGE='/files/img/pattern_bg_200px.png';
    
    var _gridTable  = '<table class="gridTable">';
        _gridTable += '<tr><td id="gf_a1" class="gf bbt"></td><td id="gf_b1" class="gf bet"></td><td id="gf_bc1" class="gf bet"></td><td id="gf_bd1" class="gf bet"></td></tr>';
        _gridTable += '<tr><td id="gf_a2" class="gf bbb"></td><td id="gf_b2" class="gf beb"></td><td id="gf_bc2" class="gf beb"></td><td id="gf_bd2" class="gf beb"></td></tr>';
        _gridTable += '<tr><td id="gf_a3" class="gf bbb"></td><td id="gf_b3" class="gf beb"></td><td id="gf_bc3" class="gf beb"></td><td id="gf_bd3" class="gf beb"></td></tr>';
        _gridTable += '</table>';
        _gridTable += '<div id="gridCtrl">';
        _gridTable += '<input type="checkbox" id="normalizeGraphs"  value="true"> Normalize Graphs <br/>';
        _gridTable += '<!--<input type="button" value="&laquo;" id="gridSizeReduce"/><input type="button" value="&raquo;" id="gridSizeIncrease"/>-->';
        _gridTable += '</div>';
        
    return{
        gridTable:_gridTable
    };

}();