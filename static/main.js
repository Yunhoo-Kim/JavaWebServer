/**
*
*/
$(".collog-nav").click(function(){
var view = $(this).attr("view");
$(".collog-view").each(function(){
$(this).css("display","none");
});
$(".collog-nav").each(function(){
$(this).removeClass("active");
});
$(this).addClass("active");
$("#"+view).css("display","block");

});
var master_address = "";
$.ajax({
    url: '/_meta/',
    type: "GET",
    success: function(data, textStatus, jqXHR) {
    console.log(data);
        // since we are using jQuery, you don't need to parse response
        master_address = data.master_ip + ":" + data.master_port;
        console.log(master_address);
        getMeta();
    }
});

var searchable_fields = [];
var data_nodes = []
function getMeta(){
$.ajax({
    url: 'http://'+master_address+'/master/meta/',
    type: "GET",
    success: function(data, textStatus, jqXHR) {
        // since we are using jQuery, you don't need to parse response
        console.log(JSON.stringify(data));
        searchable_fields = data.searchable_fields;
        data_nodes = data.shards;
        drawDataNodes(data_nodes);
        for(var a =0;a<data.searchable_fields.length;a++){
            $("#query-table-row").append("<th>"+data.searchable_fields[a]+"</th>");
            $("#searchable-field").append('<option value="'+data.searchable_fields[a]+'">'+data.searchable_fields[a]+'</option>');
            $("#graph-searchable-field").append('<option value="'+data.searchable_fields[a]+'">'+data.searchable_fields[a]+'</option>');
        }

        for(var a=0;a<data.dashboards.length;a++){
            drawDashBoard(data.dashboards[a]);
        }
    }
});
}
function drawDataNodes(data){
    $("#nodes-view").empty();
    for(var a=0;a<data.length;a++){
        drawDataNode(data[a]);
    }
}

function drawDataNode(data){
    var str = '<div class="col-xs-2 node-box">'+
        '<h3>'+
        data.node_id+
        '</h3>';
    for(var a=0;a<data.shards.length;a++){
            str += '<div class="shard-box col-xs-3">'+
            data.shards[a] +
            '</div>'
        }

        for(var a=0;a<data.replica_shards.length;a++){
                str += '<div class="replica-shard-box col-xs-3">'+
                data.replica_shards[a] +
                '</div>'
            }

    str += '</div>';

    $("#nodes-view").append(str);
}

function drawTable(data) {
    for (var i = 0; i < data.length; i++) {
        drawRow(data[i]);
    }
}

function drawRow(rowData) {
    var row = $("<tr />")
    $("#personDataTable").append(row);
    for(var a = 0;a<searchable_fields.length;a++){
    row.append($("<td>" + rowData[searchable_fields[a]] + "</td>"));
    }
}


<!--drawTable(data);-->
$("#search-btn").click(function(){
    var field = $("#searchable-field option:selected").val();
    var value = $("#search-value").val();
    var must =[{"key" : field,

                 "value" : value}]
    var should = []

    $(".must-div").each(function(){
        var select = $(this).find("select");
        var id = select.attr("id");
        var key = $("#"+id+" option:selected").val();
        var value = $(this).find("input").val();
        var d = {"key" : key,"value" : value};
        must.push(d);
    });


    $(".should-div").each(function(){
        var select = $(this).find("select");
        var id = select.attr("id");
        var key = $("#"+id+" option:selected").val();
        var value = $(this).find("input").val();
        var d = {"key" : key,"value" : value};
        should.push(d);
    });

    var request_json = {
        "type" : "search",
        "must": must,
        "should" : should
        };
        $('#personDataTable tr:not(:first)').remove();
    searchToMaster(request_json,null);
    console.log(JSON.stringify(request_json));
});

function searchToMaster(json_data, callback){
    $.ajax({
    url: 'http://'+master_address+'/master/data/search/',
    type: "POST",
    datatype: "json",
    data: JSON.stringify(json_data),
    success: function(data, textStatus, jqXHR) {
        drawTable(data.result);
        console.log(data.result);
    },error(res,xhr,error){
    console.log("error")
    }
});
}


fetch_unix_timestamp = function(date)
{
	return date.getTime()/1000;
}

timestamp = fetch_unix_timestamp(new Date());

//console.log(timestamp);

var j_data = {
    key : "status",
    type : "count",
    label : "asdfa",
    graph_type : "bar",
    time1 : "1308910110.075",
    time2 : "1508910492.398"
};

function drawDashBoard(data){

    var timestamp = fetch_unix_timestamp(new Date());
    var time1 = timestamp - data.timestamp_offset;
    var time2 = timestamp;
    data.time1 = time1;
    data.time2 = time2;
//    delete data.timestamp_offset;
    $.ajax({
    url: 'http://'+master_address+'/master/data/search/',
    type: "POST",
    data: JSON.stringify(data),
    success: function(res, textStatus, jqXHR) {
         drawDashboardGraph(res.results, data);
    }
});
}

function getGraphData(data){
    var timestamp = fetch_unix_timestamp(new Date());
    var time1 = timestamp - data.timestamp_offset;
    var time2 = timestamp;
    data.time1 = time1;
    data.time2 = time2;
    delete data.timestamp_offset;
$.ajax({
    url: 'http://'+master_address+'/master/data/search/',
    type: "POST",
    data: JSON.stringify(data),
    success: function(res, textStatus, jqXHR) {
//    console.log(res);
         drawGraph(res.results, data);
    }
});
}



var chart_num = 0;
var dashboard_temps = [];
function drawGraph(data, req_data){
        var req_id = req_data.id.toString().replace(".","_");

        $("#graph-preview-div").append('<canvas class="col-md-4" id="myChart'+req_id+'"></canvas>');
        var ctx1 = document.getElementById("myChart"+req_id).getContext('2d');

        var myDoughnutChart = new Chart(ctx1, {
	    type: req_data.graph_type,
	    data: {
		    datasets: [
		    {
		    label: req_data.label,
		    backgroundColor: dynamicColors(Object.values(data).length),
            data: Object.values(data)
		    }
		    ],
		    labels:Object.keys(data)

	    },
	    options: {
title: {text : req_data.label,display:true,position:"bottom"}
        }


});
        $("#myChart"+req_id).prop("data",req_data);

chart_num++;

}

function drawDashboardGraph(data, req_data){
        var req_id = req_data.id.toString().replace(".","_");
        if($("#myChart"+req_id).length != 0){
        var ctx = document.getElementById("myChart"+req_id);
var LineGraph = new Chart(ctx, {
        type: req_data.graph_type,
        data: Object.values(data)});
        LineGraph.destroy();

            }
            else{
            $("#graph-div").append('<canvas class="col-md-4 update-modal-open" id="myChart'+req_id+'"></canvas>');
            }
//        }
//        else{
//        }
        var ctx1 = document.getElementById("myChart"+req_id).getContext('2d');

        var myDoughnutChart = new Chart(ctx1, {
	    type: req_data.graph_type,
	    data: {
		    datasets: [
		    {
		    backgroundColor: dynamicColors(Object.values(data).length),

            data: Object.values(data)
		    }
		    ],
		    labels:Object.keys(data)

	    },
	     responsive : true,
	    options: {
title: {text : req_data.label,display:true,position:"bottom"}
        }
});
        $("#myChart"+req_id).prop("data",req_data);

chart_num++;

}

function dynamicColors(len) {
var dynamic_colors = [];
for(var a = 0;a<len;a++){
    var r = Math.floor(Math.random() * 255);
    var g = Math.floor(Math.random() * 255);
    var b = Math.floor(Math.random() * 255);
    dynamic_colors.push("rgb(" + r + "," + g + "," + b + ")");
    }
    return dynamic_colors;
}
$("#draw-graph-btn").click(function(){
    var key = $("#graph-searchable-field option:selected").val();
    var type = $("#graph-type option:selected").val();
    var time_offset = $("#duration option:selected").val();
    time_offset = time_offset * 3600 * 24;
    var time1 = timestamp - time_offset;
    var time2 = timestamp;
    var label = $("#label-value").val();
    var data = {
        id : fetch_unix_timestamp(new Date()),
        key: key,
        type: "count",
        label: label,
        graph_type: type,
        timestamp_offset: time_offset
    }
    dashboard_temps.push(data);
    getGraphData( JSON.parse(JSON.stringify(data)));
});

$("#graph-save").click(function(){
    var data = {
        dashboards : dashboard_temps
    }
    console.log(JSON.stringify(data));
    $.ajax({
    url: 'http://'+master_address+'/master/dashboard/',
    type: "POST",
    data: JSON.stringify(data),
    success: function(res, textStatus, jqXHR) {
    <!--console.log(res);-->
         <!--drawGraph(res.results, data);-->
         for(var a=0;a<dashboard_temps.length;a++){
            drawDashBoard(dashboard_temps[a]);
         }
         $("#clean-preview-graph").trigger("click");
    }
});

});

$("#clean-preview-graph").click(function(){
    dashboard_temps = [];
    $("#graph-preview-div").empty();
});

$("body").on("click",".update-modal-open", function(){
    var data = $(this).prop("data");
    $("#update-graph-modal").modal("show");
    $("#update-graph-modal").prop("data",data);
    console.log(JSON.stringify(data));
});

$("#update-btn").click(function(){
    disBlkContent($("#update-div"));
    disNoneContent($("#update-text-div"));
    var data = $("#update-graph-modal").prop("data");
    var type = data.graph_type;
    var label = data.label;
    var time_offset = data.timestamp_offset;
    time_offset = parseInt(time_offset/(3600*24));
    console.log(time_offset);
    $("#update-duration").val(time_offset.toString());
    $("#update-graph-type").val(type);
    $("#update-label-value").val(label);
});

function disBlkContent($a){
    $a.removeClass("dis-none");
}
function disNoneContent($a){
    $a.addClass("dis-none");
}

$("#update-complete-btn").click(function(){
    var data = $("#update-graph-modal").prop("data");
    var type = $("#update-graph-type option:selected").val();
    var time_offset = $("#update-duration option:selected").val();
    var label = $("#update-label-value").val();

    delete data.time1;
    delete data.time2;
    data.graph_type = type;
    data.timestamp_offset = parseInt(time_offset) * 3600 * 24;
    data.label = label;
    console.log(JSON.stringify(data));


//    drawDashBoard(data);

    updateDashboard(data);
});

function updateDashboard(data){
// var data = {
//        dashboards : dashboard_temps
//    }
//    console.log(JSON.stringify(data));

    $.ajax({
    url: 'http://'+master_address+'/master/dashboard/update/',
    type: "POST",
    data: JSON.stringify(data),
    success: function(res, textStatus, jqXHR) {
//         for(var a=0;a<dashboard_temps.length;a++){
//            drawDashBoard(dashboard_temps[a]);
//         }
//         $("#clean-preview-graph").trigger("click");
        drawDashBoard(data);

    disNoneContent($("#update-div"));
    disBlkContent($("#update-text-div"));
    $("#update-graph-modal").modal("hide");
//        $("#")
    }
});
}

function removeDashbord(data){
    $.ajax({
    url: 'http://'+master_address+'/master/dashboard/remove/',
    type: "POST",
    data: JSON.stringify(data),
    success: function(res, textStatus, jqXHR) {
//         for(var a=0;a<dashboard_temps.length;a++){
//            drawDashBoard(dashboard_temps[a]);
//         }
//         $("#clean-preview-graph").trigger("click");
//        drawDashBoard(data);
        var _id = data.id.toString().replace(".","_");

    $("#myChart"+_id).remove();
    disNoneContent($("#update-div"));
    disBlkContent($("#update-text-div"));
    $("#update-graph-modal").modal("hide");
//        $("#")
    }
});
}

$("#remove-btn").click(function(){
     var data = $("#update-graph-modal").prop("data");
    removeDashbord(data);
});
var must_order = 0;
var should_order = 0;
$("#add-must").click(function(){

        var str = '<div class="must-div">'+
            '<select id="must-div-select'+must_order+'">'+
            '</select>'+
            '<input type="text" id="must-div-input">'+
        '</div>';
        $("#must-query-div").append(str);
        for(var a =0;a<searchable_fields.length;a++){
            $("#must-div-select"+must_order).append('<option value="'+searchable_fields[a]+'">'+searchable_fields[a]+'</option>');
        }
        must_order += 1;
});

$("#add-should").click(function(){
var str = '<div class="should-div">'+
            '<select id="should-div-select'+should_order+'">'+
            '</select>'+
            '<input type="text" id="should-div-input'+should_order+'">'+
        '</div>';
        $("#should-query-div").append(str);
        for(var a =0;a<searchable_fields.length;a++){
            $("#should-div-select"+should_order).append('<option value="'+searchable_fields[a]+'">'+searchable_fields[a]+'</option>');
        }
        should_order += 1;
});