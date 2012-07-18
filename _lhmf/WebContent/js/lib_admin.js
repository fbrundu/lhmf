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
						if(!!stateData.min && !!stateData.max){
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
				switch(stateData.tab) {
					case 1:
						// Tab registrazione
						
						writeUserPage(1);
						
						/*if(!!stateData.username) {
							
							doRegistration(stateData);
							
						}*/
						
						break;
					case 2:
						// Tab 2
						writeUserPage(2);
						
						break;
					case 3:
						//Tab 3
						writeUserPage(3);
						
						break;
					case 4:
						writeUserPage(4);
						break;
				
				}
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
				$("#logs").append("<tr>  <th class='top' width='10%'> ID </th>" +
										"<th class='top' width='20%'> Membro </th>" +
										"<th class='top' width='20%'> Timestamp  </th>" +
										"<th class='top' width='50%'> Testo  </th> </tr>");
				for(var i = 0; i < logList.length; i++){
					var log = logList[i];
					$("#logs").append("<tr><td>" + log.idLog +"<td>" + log.member.name + " " + log.member.surname + "<td>" + new Date(log.logTimestamp) + "<td>" + log.logtext + "</tr>");
		//			console.log("Log id: " + log.idLog + ", log text: " + log.logtext + ", member name: " + log.member.name + 
		//					", member surname: " + log.member.surname + ", log timestamp: " + new Date(log.logTimestamp));
				}
			}
			$("#logs").fadeIn(1000);
		});
	}
	
	function writeLogPage(){
		$(".centrale").html("<div id='tabs'><ul><li><a href='#tabs-1'>Consulta Log</a></li></ul>" +
		"<div id='tabs-1'></div></div>");
		$('#tabs-1').html("<div class='logform'>" +
						    "<form method='get' action='log'>" +
						      "<fieldset><legend>&nbsp;Seleziona range di date:&nbsp;</legend><br />" +
						        "<label for='min' class='left'>Data iniziale: </label>" +
						        "<input type='text' id='min' class='field'/>" +
						        "<label for='max' class='left'>Data finale: </label>" +
						        "<input type='text' id='max' class='field'/>" +
						      "</fieldset>" +
						      "<button type='submit' id='logsRequest'> Visualizza </button>" +
						    "</form>" +
						    "<table id='logs' class='log'></table>" +
						  "</div>" +
						  "<div id='dialog' title='Errore: Formato date non corretto'> <p>Selezionale entrambe le date (o nel corretto ordine cronologico). </p></div>");
		$('#tabs').tabs();
		$( "#dialog" ).dialog({ autoOpen: false });
		prepareLogForm();
	}

	function writeIndexPage(){
		$('.centrale').html("<p>Body admin history state</p>");
	}
	
	function writeUserPage(tab){
		$(".centrale").html("<div id='tabs'><ul><li><a href='#tabs-1'>Aggiungi utente</a></li><li><a href='#tabs-2'>Attiva utente</a></li>" +
				"<li><a href='#tabs-3'>Modifica utente</a></li><li><a href='#tabs-4'>Lista utenti</a></li></ul>" +
				"<div id='tabs-1'></div><div id='tabs-2'></div><div id='tabs-3'></div><div id='tabs-4'></div></div>");
		$('#tabs-1').html("<div class='registrazioneform' style='margin: 2em 0 0 65px;'>" +
							"<form  id='regform' action='newMember' method='post'>" +
							"<fieldset><legend>&nbsp;Dati per la Registrazione&nbsp;</legend><br />" +
							"<label for='username' class='left'>Username:</label>" +
							"<input type='text' name='username' id='username' class='field' required='required'/>" +
					"<br><br><label for='firstname' class='left'>Nome: </label>" +
							"<input type='text' name='firstname' id='firstname' class='field' required='required'/>" +
						"<br><label for='lastname' class='left'>Cognome: </label>" +
							"<input type='text' name='lastname' id='lastname' class='field' required='required'/>" +
						"<br><label for='email' class='left'>Email: </label>" +
							"<input type='text' name='email' id='email' class='field' required='required'/>" +
						"<br><label for='address' class='left'>Indirizzo: </label>" +
							"<input type='text' name='address' id='address' class='field' required='required'/>" +
						"<br><label for='city' class='left'>Citt&agrave: </label>" +
							"<input type='text' name='city' id='city' class='field' required='required'/>" +
						"<br><label for='state' class='left'>Stato: </label>" +
						    "<input type='text' name='state' id='state' class='field' required='required'/>" +
						"<br><label for='cap' class='left'>Cap: </label>" +
						     "<input type='text' name='cap' id='cap' class='field' required='required'/>" +
						"<br><label for='phone' class='left'>Telefono: </label>" +
						     "<input type='text' name='phone' id='phone' class='field'/>" +
				    "<br><br><label for='mtype' class='left'>Tipo Utente: </label>" +
						     "<select name='mtype' id='mtype' class='field' onchange='checkRespSelect()'>" +
						     	"<option value='0'>Normale</option>" +
						     	"<option value='1'>Responsabile  </option>" +
						     	"<option value='3'>Fornitore </option>" +
						     "</select>" +
					        "</fieldset>" +
					        "<fieldset id='respFieldset' ><legend>&nbsp;Dati Fornitore&nbsp;</legend><br />" +
					    "<br><label for='company' class='left'>Compagnia: </label>" +
				        	"<input type='text' name='company' id='company' class='field' />" +
				        "<br><label for='description' class='left'>Descrizione: </label>" +
				        	"<input type='text' name='description' id='description' class='field' />" +
				        "<br><label for='contactName' class='left'>Contatto: </label>" +
				          	"<input type='text' name='contactName' id='contactName' class='field' />" +
					    "<br><label for='fax' class='left'>Fax: </label>" +
					        "<input type='text' name='fax' id='fax' class='field' />" +
					    "<br><label for='website' class='left'>WebSite: </label>" +
					        "<input type='text' name='website' id='website' class='field' />" +
					     "<br><label for='payMethod' class='left'>Metodo Pagamento: </label>" +
					         "<input type='text' name='payMethod' id='payMethod' class='field' />" +
				         "<br><br><label for='mtype' class='left'>Responsabile: </label>" +
					         "<select name='memberResp' id='mtype' class='field'>" +
							 "</select>" +
					        "</fieldset>" +
					        "<div id='errorDiv' style='display:none;'>" +
					        	"<fieldset><legend id='legendError'>&nbsp;Errore&nbsp;</legend><br />" +
						         "<div id='errors' style='padding-left: 40px'>" +
								  "</div>" +
						        "</fieldset>" +
					        "</div>" +
					        "<p><input type='submit' id='regRequest' class='button' value='Registra'/></p>" +
					        "</form>" +
					      "</div><p></p>");
		$('#tabs-2').html("Attiva utente");
		$('#tabs-3').html("Modifica utente");
		$('#tabs-4').html("Lista Utenti");
		prepareUserForm(tab);
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
		if(startDate == null || endDate == null || startDate > endDate){
			//$('body').append('<div id="dialog" title="Errore nell\'input delle date"> <p>Selezionale entrambe le date (o nel corretto ordine cronologico). </p></div>');
			$( "#dialog" ).dialog('open');
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

function prepareUserForm(tab){
	$('#tabs').tabs({
		  selected: tab 
	});
	
	//disabilitare fieldset resp
	
	$('#respFieldset').hide();
	$('#respFieldset').children().attr("disabled", "disabled");
	
	//Recuperare
	
	$('#regRequest').on("click", function(event){
		
		event.preventDefault();
		
		var errors = new Array();
		
		var username = $('#username').val();
		var firstname = $('#firstname').val();
		var lastname = $('#lastname').val();
		var email = $('#email').val();
		var address = $('#address').val();
		var city = $('#city').val();
		var state = $('#state').val();
		var cap = $('#cap').val();
		var tel = $('#phone').val();
		
		if(username == "") {
			errors.push("Username: Formato non valido");
		}
		if(firstname == "") {
			errors.push("Nome: Formato non valido");
		}
		if(lastname == "" || isNumber(lastname)) {
			errors.push("Cognome: Formato non valido");
		}
		if(email == "" || !isValidMail(email)) {
			errors.push("Email: Formato non valido");
		}
		if(address == "" || isNumber(address)) {
			errors.push("Indirizzo: Formato non valido");
		}
		if(city == "" || isNumber(city)) {
			errors.push("Citt&agrave: Formato non valido");
		}
		if(state == "" || isNumber(state)) {
			errors.push("Stato: Formato non valido");
		}
		if(cap == "" || !isNumber(cap)) {
			errors.push("Cap: Formato non valido");
		}
		if(tel != "")
			if(!isNumber(tel)) {
			errors.push("Telefono: Formato non valido");
		}
		

		if(errors.length > 0){
			$("#errors").html("");
			$("#errorDiv").hide();
			
			
			for(var i = 0; i < errors.length; i++){
				var error = errors[i].split(":");
				$("#errors").append("<strong>" + error[0] +"</strong>: " + error[1] + "<br />");
			}
			
			$("#errorDiv").show("slow");
			//$("#errorDiv").fadeIn(1000);
		}
		else{
					
			$.post("ajax/newMember", {	username: username,
				firstname: firstname,
				lastname: lastname,
				email: email,
				address: address,
				city: city,
				state: state,
				cap: cap,
				tel: tel}, function(regResult) {
					
				console.log("Ricevuto risultato registrazione");
				
				$("#errorDiv").hide();
				$("#errors").html("");
				
				var errors = regResult;
				
				if(errors.length <= 0) {
				
				$("#legendError").html("");
				$("#legendError").append("Registrazione Riuscita");
				
				$("#errors").append("La registrazione del nuovo utente &egrave avvenuta con successo.<br />" +
								"&Egrave; stata inviata una mail per verificarne l'autenticit&agrave.<br />" +
								"Una volta autenticata l'email l'attivazione sar&agrave automatica.");
				} else {
				
					for(var i = 0; i < errors.length; i++){
					var error = errors[i].split(":");
					$("#errors").append("<strong>" + error[0] +"</strong>: " + error[1] + "<br />");
					}
				}

				$("#errorDiv").show("slow");
				$("#errorDiv").fadeIn(1000);
				});
			
		}
	});
}

function checkRespSelect() {
	
	var selected = $('#mtype').val();
	
	if(selected == 3) {
		//Utente fornitore selezionato
		
		$('#respFieldset').show('slow');
		$('#respFieldset').children().attr("disabled", false);
		
	} else {
		//Fornitore non selezionato
		
		$('#respFieldset').hide('slow');
		$('#respFieldset').children().attr("disabled", true);
	}
	
	
	
}

function isNumber(n) {
	  return !isNaN(parseFloat(n)) && isFinite(n);
	}

function isValidMail(email) {
	 
	   var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	   if(reg.test(email) == false) {
	      return false;
	   }
	   return true;
	}


