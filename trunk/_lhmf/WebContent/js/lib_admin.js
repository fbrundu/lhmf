var History;

$(function(){
	$.datepicker.setDefaults({
		dateFormat: 'dd/mm/yy'
		});
	
	History = window.History;
	if(!History.enabled){
		Console.log("History API is disabled!!!");
	}
	
	History.Adapter.bind(window, 'statechange', historyStateChanged);
	
	firstPageCallback();
});

function historyStateChanged(){
	var state = History.getState();
	var stateData = state.data;
	if(!stateData)
		showIndex();
	switch(stateData.action){
		case 'log': 
					writeLogPage();
					if(!!stateData.min && !! stateData.max){
						$('#start').datepicker("setDate", new Date(stateData.min));
						$('#end').datepicker("setDate", new Date(stateData.max));
						showLogs(stateData.min, stateData.max);
					}
					else{
						$('#start').datepicker("setDate", Date.now());
						$('#end').datepicker("setDate", Date.now());
					}
					break;
		case 'null': break;
		default: writeIndexPage();
	}
}

function logClicked(event){
	event.preventDefault();
	var state = History.getState();
	var stateData = state.data;
	if(!! stateData && !! stateData.action && stateData.action == 'log')
		return;
	
	//writeLogPage();
	History.pushState({action:'log'}, null, './log');
}

function showLogs(startTime, endTime){
	$.getJSON("ajax/getlogs", {start: startTime, end: endTime}, function(logList){
		console.log("Ricevuti log");
		$("#logs").html("");
		if(logList.length > 0)
			$("#logs").append("<tr><th>ID Log<th>Membro<th>Testo<th>Timestamp</tr>");
		for(var i = 0; i < logList.length; i++){
			var log = logList[i];
			$("#logs").append("<tr><td>" + log.idLog +"<td>" + log.member.name + " " + log.member.surname + "<td>" + log.logtext + "<td>" + new Date(log.logTimestamp) + "</tr>");
//			console.log("Log id: " + log.idLog + ", log text: " + log.logtext + ", member name: " + log.member.name + 
//					", member surname: " + log.member.surname + ", log timestamp: " + new Date(log.logTimestamp));
		}
	});
}

function writeLogPage(){
	$(".centrale").html("<p>Seleziona range di date:</p><p><label for='start'>Data iniziale: </label><input type='text' id='start'><label for='end'>" +
			" Data finale: </label><input type='text' id='end'>&nbsp;<button type='button' id='logsRequest'>Seleziona</button>" +
			"<table id='logs'></table>");
	$("#start").datepicker({
		defaultDate: 0,
		maxDate: 0
		});
	$('#start').datepicker("setDate", Date.now());
	$('#end').datepicker({
		defaultDate: 0,
		maxDate: 0
		});
	$('#end').datepicker("setDate", Date.now());
	$('#logsRequest').on("click", function(event){
		var startDate = $('#start').datepicker("getDate");
		var endDate = $('#end').datepicker("getDate");
		if(startDate == null || endDate == null){
			window.alert("Selezionare entrambe le date!");
		}
		else{
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);
			endDate.setMilliseconds(999);
		
			//showLogs(startDate.getTime(), endDate.getTime());
			History.pushState({action: 'log', min: startDate.getTime(), max: endDate.getTime()}, null, "./log?min=" + startDate.getTime() + "&max=" + endDate.getTime());
			console.log("Date selezionate: " + JSON.stringify(startDate) + ", " + JSON.stringify(endDate) + ", (timestamps):" + startDate.getTime() + ", " + endDate.getTime());
		}
	});
}

function writeIndexPage(){
	$('.centrale').html("<p>Body admin history state</p>");
}