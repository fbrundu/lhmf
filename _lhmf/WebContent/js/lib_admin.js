(function(window, undefined){
	var History = window.History;
		$ = window.jQuery;
	var histEnabled = History.enabled;
	if(!histEnabled)
		console.log("HTML 5 History API is disabled!");
	else
		History.Adapter.bind(window, 'statechange', historyStateChanged);

	$(function(){
		$("#logLink").click(logClicked);
		$("#userLink").click(userClicked);
		
		$.datepicker.setDefaults({
			dateFormat: 'dd/mm/yy'
			});
		drawPageCallback();
	});
	
	function historyStateChanged(){
		var History = window.History;
		var state = History.getState();
		var stateData = state.data;
		if(!stateData)
			showIndex();
		switch(stateData.action){
			case 'log': 
						writeLogPage();
						if(!!stateData.min && !! stateData.max){
							$('#min').datepicker("setDate", new Date(stateData.min));
							$('#max').datepicker("setDate", new Date(stateData.max));
							showLogs(stateData.min, stateData.max);
						}
						else{
							$('#min').datepicker("setDate", Date.now());
							$('#max').datepicker("setDate", Date.now());
						}
						break;
			case 'userMgmt':
					writeUserPage();
					break;
			case 'null': break;
			default: writeIndexPage();
		}
	}

	function logClicked(event){
		if(histEnabled == true){
			event.preventDefault();
			var History = window.History;
			var state = History.getState();
			var stateData = state.data;
			if(!! stateData && !! stateData.action && stateData.action == 'log')
				return;
			History.pushState({action:'log'}, null, 'log');
		}
	}
	
	function userClicked(event){
		if(histEnabled == true){
			event.preventDefault();
			var History = window.History;
			var state = History.getState();
			var stateData = state.data;
			if(!! stateData && !! stateData.action && stateData.action == 'userMgmt')
				return;
			History.pushState({action:'userMgmt'}, null, 'userMgmt');
		}
	}

	function showLogs(startTime, endTime){
		$.getJSON("ajax/getlogs", {start: startTime, end: endTime}, function(logList){
			console.log("Ricevuti log");
			$("#logs").html("");
			$("#logs").hide();
			//$("#logs").fadeOut(500, function() {
				
			//});
			if(logList.length > 0){
				$("#logs").append("<tr><th>ID Log<th>Membro<th>Testo<th>Timestamp</tr>");
				for(var i = 0; i < logList.length; i++){
					var log = logList[i];
					$("#logs").append("<tr><td>" + log.idLog +"<td>" + log.member.name + " " + log.member.surname + "<td>" + log.logtext + "<td>" + new Date(log.logTimestamp) + "</tr>");
		//			console.log("Log id: " + log.idLog + ", log text: " + log.logtext + ", member name: " + log.member.name + 
		//					", member surname: " + log.member.surname + ", log timestamp: " + new Date(log.logTimestamp));
				}
			}
			$("#logs").fadeIn(500);
		});
	}

	function writeLogPage(){
//		$(".centrale").html("<p>Seleziona range di date:</p><p><label for='start'>Data iniziale: </label><input type='text' id='start'><label for='end'>" +
//				" Data finale: </label><input type='text' id='end'>&nbsp;<button type='button' id='logsRequest'>Seleziona</button>" +
//				"<table id='logs'></table>");
		$(".centrale").html("<p>Seleziona range di date:</p><p><form method='get' action='log'><label for='min'>Data iniziale: </label><input type='text' id='min'/><label for='max'>" +
				" Data finale: </label><input type='text' id='max'/>&nbsp;<button type='submit' id='logsRequest'>Seleziona</button></form>" +
				"<table id='logs'></table>");
		prepareLogForm();
	}

	function writeIndexPage(){
		$('.centrale').html("<p>Body admin history state</p>");
	}
	
	function writeUserPage(){
		$(".centrale").html("<div id='tabs'><ul><li><a href='#tabs-1'>Aggiungi utente</a></li><li><a href='#tabs-2'>Utenti esistenti</a></li></ul>" +
				"<div id='tabs-1'></div><div id='tabs-2'></div></div>");
		$('#tabs-1').html("Aggiungi utente");
		$('#tabs-2').html("Utenti esistenti");
		$('#tabs').tabs();
	}
})(window);

function prepareLogForm(){
	$("#min").datepicker({
		defaultDate: 0,
		maxDate: 0
		});
	$('#min').datepicker("setDate", Date.now());
	$('#max').datepicker({
		defaultDate: 0,
		maxDate: 0
		});
	$('#max').datepicker("setDate", Date.now());
	$('#logsRequest').on("click", function(event){
		event.preventDefault();
		var startDate = $('#min').datepicker("getDate");
		var endDate = $('#max').datepicker("getDate");
		if(startDate == null || endDate == null){
			window.alert("Selezionare entrambe le date!");
		}
		else{
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);
			endDate.setMilliseconds(999);
		
			var History = window.History;
			if(History.enabled)
				History.pushState({action: 'log', min: startDate.getTime(), max: endDate.getTime()}, null, "./log?min=" + startDate.getTime() + "&max=" + endDate.getTime());
			else{
				$("form #min").val(startDate.getTime());
				$("form #max").val(endDate.getTime());
				$("form").submit();
			}
			console.log("Date selezionate: " + JSON.stringify(startDate) + ", " + JSON.stringify(endDate) + ", (timestamps):" + startDate.getTime() + ", " + endDate.getTime());
		}
	});
}

