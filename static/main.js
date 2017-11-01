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

var searchable_fields = [];
var data_nodes = []
$.ajax({
    url: 'http://localhost:8885/master/meta/',
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

function drawDataNodes(data){
    $("#nodes-view").empty();
    for(var a=0;a<data.length;a++){
        drawDataNode(data[a]);
    }
}

function drawDataNode(data){
    var str = '<div class="col-xs-3 node-box">'+
        '<h3>'+
        data.node_id+
        '</h3>';
    for(var a=0;a<data.shards.length;a++){
        str += '<div class="shard-box col-xs-3">'+
        data.shards[a] +
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
    var request_json = {
    "type" : "search",
    "key" : field,
    "value" : value
    };
    searchToMaster(request_json,null);
    console.log(JSON.stringify(request_json));
});

function searchToMaster(json_data, callback){
    $.ajax({
    url: 'http://localhost:8885/master/data/search/',
    type: "POST",
    datatype: "json",
    data: JSON.stringify(json_data),
    success: function(data, textStatus, jqXHR) {
        drawTable(data.result);
    }
});
}


fetch_unix_timestamp = function(date)
{
	return date.getTime()/1000;
}

timestamp = fetch_unix_timestamp(new Date());

console.log(timestamp);

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
    delete data.timestamp_offset;
    $.ajax({
    url: 'http://localhost:8885/master/data/search/',
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
    url: 'http://localhost:8885/master/data/search/',
    type: "POST",
    data: JSON.stringify(data),
    success: function(res, textStatus, jqXHR) {
    console.log(res);
         drawGraph(res.results, data);
    }
});
}



var chart_num = 0;
var dashboard_temps = [];
function drawGraph(data, req_data){
        $("#graph-preview-div").append('<canvas class="col-md-4" id="myChart'+chart_num+'"></canvas>');
        var ctx1 = document.getElementById("myChart"+chart_num).getContext('2d');

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
chart_num++;

}

function drawDashboardGraph(data, req_data){
        $("#graph-div").append('<canvas class="col-md-4" id="myChart'+chart_num+'"></canvas>');
        var ctx1 = document.getElementById("myChart"+chart_num).getContext('2d');

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
	    options: {
title: {text : req_data.label,display:true,position:"bottom"}
        }
});
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
    url: 'http://localhost:8885/master/dashboard/',
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
