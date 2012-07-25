var numberOfMember;

(function(window, undefined) {
	var History = window.History;
	$ = window.jQuery;
	var histEnabled = History.enabled;
	if (!histEnabled)
		console.log("HTML 5 History API is disabled!");
	else
		History.Adapter.bind(window, 'statechange', historyStateChanged);

	$(function() {
		$("#logLink").click(logClicked);
		$("#userLink").click(userClicked);

		$.datepicker.setDefaults({
			dateFormat : 'dd/mm/yy'
		});
		drawPageCallback();
	});

	function historyStateChanged() {
		var History = window.History;
		var state = History.getState();
		var stateData = state.data;
		if (!stateData)
			showIndex();
		switch (stateData.action) {
		case 'log':
			writeLogPage();
			if (!!stateData.min && !!stateData.max) {
				$('#min').datepicker("setDate", new Date(stateData.min));
				$('#max').datepicker("setDate", new Date(stateData.max));
				showLogs(stateData.min, stateData.max);
			} else {
				$('#min').datepicker("setDate", Date.now());
				$('#max').datepicker("setDate", Date.now());
			}
			break;
		case 'userMgmt':
			switch (stateData.tab) {
			case 1:
				// Tab registrazione

				writeUserPage(1);

				/*
				 * if(!!stateData.username) {
				 * 
				 * doRegistration(stateData);
				 *  }
				 */

				break;
			case 2:
				// Tab 2
				writeUserPage(2);

				break;
			case 3:
				// Tab 3
				writeUserPage(3);

				break;
			case 4:
				writeUserPage(4);
				break;

			}
			writeUserPage();
			break;
		case 'null':
			break;
		default:
			writeIndexPage();
		}
	}

	function logClicked(event) {
		if (histEnabled == true) {
			event.preventDefault();
			var History = window.History;
			var state = History.getState();
			var stateData = state.data;
			if (!!stateData && !!stateData.action && stateData.action == 'log')
				return;
			History.pushState({
				action : 'log'
			}, null, 'log');
		}
	}

	function userClicked(event) {
		if (histEnabled == true) {
			event.preventDefault();
			var History = window.History;
			var state = History.getState();
			var stateData = state.data;
			if (!!stateData && !!stateData.action
					&& stateData.action == 'userMgmt')
				return;
			History.pushState({
				action : 'userMgmt'
			}, null, 'userMgmt');
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
					$("#logs").append("<tr> <td>" + log.idLog +"</td>" +
							               "<td>" + log.member.name + " " + log.member.surname + "</td>" +
							               "<td>" + new Date(log.logTimestamp) + "</td>" +
							               "<td>" + log.logtext + "</td></tr>");
				}
			
				$("#logs").fadeIn(1000);
			} else {
			    
			    $("#errorDivLog").hide();
			    $("#legendErrorLog").html("Comunicazione");
			    $("#errorsLog").append("Non ci sono Log  da visualizzare<br /><br />");
			    $("#errorDivLog").show("slow");
			    $("#errorDivLog").fadeIn(1000);
			
			}
			
		});
	}
	
	function writeLogPage(){
		$(".centrale").html("<div id='tabs'><ul><li><a href='#tabs-1'>Consulta Log</a></li></ul>" +
		"<div id='tabs-1'></div></div>");
		$('#tabs-1').html("<div class='logform'>" +
            		        "<div id='errorDivLog' style='display:none;'>" +
                                "<fieldset><legend id='legendErrorLog'>&nbsp;Errore&nbsp;</legend><br />" +
                                 "<div id='errorsLog' style='padding-left: 40px'>" +
                                  "</div>" +
                                "</fieldset>" +
                            "</div><br />" +
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
				"<li><a href='#tabs-3'>Lista utenti</a></li></ul>" +
				"<div id='tabs-1'></div><div id='tabs-2'></div><div id='tabs-3'></div></div>");
		
		// HTML per la registrazione utenti e fornitori
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
					         "<select name='memberResp' id='memberResp' class='field'>" +
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
		
		// TAB per attivazione utenti
		
		$('#tabs-2').html("<div class='logform'>" +
						    "<form method='post' action=''>" +
						      "<fieldset><legend>&nbsp;Opzioni di Ricerca:&nbsp;</legend><br />" +
						        "<label for='memberType' class='left'>Tipo Membro: </label>" +
						        "<select name='memberType' id='memberType' class='field'>" +
						        	"<option value='0'> Normale </option>" +
						        	"<option value='1'> Responsabile </option>" +
						        	"<option value='3'> Fornitore </option>" +
								 "</select>" +
						        "<label for='page' class='left'>&nbsp;&nbsp;&nbsp;Pagina: </label>" +
						        "<select name='page' id='page' class='field'>" +
					        		"<option value='0'> ... </option>" +
					        	"</select>" +
					        	"<label for='itemsPerPage' class='left'>&nbsp;&nbsp;&nbsp;Risultati Per Pagina: </label>" +
						        "<select name='itemsPerPage' id='itemsPerPage' class='field'>" +
					        		"<option value='10'> 10 </option>" +
					        		"<option value='25'> 25 </option>" +
					        		"<option value='50'> 50 </option>" +
					        	"</select>" +
						      "</fieldset>" +
						      "<button type='submit' id='memberToActiveRequest'> Visualizza </button>" +
						    "</form>" +
						    "<table id='memberList' class='log'></table>" +
							  "<div id='errorDiv2' style='display:none;'>" +
					        	"<fieldset><legend id='legendError2'>&nbsp;Errore&nbsp;</legend><br />" +
						         "<div id='errors2' style='padding-left: 40px'>" +
								  "</div>" +
						        "</fieldset>" +
					          "</div><br />" +
				          "</div>");
		
		
		$('#tabs-3').html("<div class='logform'>" +
                                "<form method='post' action=''>" +
                                "<fieldset><legend>&nbsp;Opzioni di Ricerca:&nbsp;</legend><br />" +
                                  "<label for='memberType2' class='left'>Tipo Membro: </label>" +
                                  "<select name='memberType2' id='memberType2' class='field'>" +
                                      "<option value='0'> Normale </option>" +
                                      "<option value='1'> Responsabile </option>" +
                                      "<option value='3'> Fornitore </option>" +
                                   "</select>" +
                                  "<label for='page2' class='left'>&nbsp;&nbsp;&nbsp;Pagina: </label>" +
                                  "<select name='page2' id='page2' class='field'>" +
                                      "<option value='0'> ... </option>" +
                                  "</select>" +
                                  "<label for='itemsPerPage2' class='left'>&nbsp;&nbsp;&nbsp;Risultati Per Pagina: </label>" +
                                  "<select name='itemsPerPage2' id='itemsPerPage2' class='field'>" +
                                      "<option value='10'> 10 </option>" +
                                      "<option value='25'> 25 </option>" +
                                      "<option value='50'> 50 </option>" +
                                  "</select>" +
                                "</fieldset>" +
                                "<button type='submit' id='getList'> Visualizza </button>" +
                              "</form>" +
                              "<table id='memberList2' class='log'></table>" +
                                "<div id='errorDiv3' style='display:none;'>" +
                                  "<fieldset><legend id='legendError3'>&nbsp;Errore&nbsp;</legend><br />" +
                                   "<div id='errors3' style='padding-left: 40px'>" +
                                    "</div>" +
                                  "</fieldset>" +
                                "</div><br />" +
                            "</div>");
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
	
	$('#regRequest').on("click", clickRegHandler);
	
	$('#memberToActiveRequest').on("click", clickActMemberHandler);
	
	$('#getList').on("click", clickGetMemberHandler);
}

function clickGetMemberHandler(event) {
    event.preventDefault();
    var memberType = $('#memberType2').val();
    var page = $('#page2').val();
    var itemsPerPage = $('#itemsPerPage2').val();
    
    //normale o responsabile
    $.post("ajax/getMembersList", {   memberType: memberType,
                                      page: page,
                                      itemsPerPage: itemsPerPage }, postMemberListHandler);

}


function clickActMemberHandler(event){
	event.preventDefault();
	var memberType = $('#memberType').val();
	var page = $('#page').val();
	var itemsPerPage = $('#itemsPerPage').val();
	
	//normale o responsabile
	$.post("ajax/getMembersToActivate", {	memberType: memberType,
											page: page,
											itemsPerPage: itemsPerPage }, postMemberToActivateHandler);

}

function clickMemberActivationHandler(event){
	event.preventDefault();
	
	var form = $(this).parents('form');
	var tmp = $('input', form).val().split(',');
	var idMember = tmp[0];
	var isSupplier = tmp[1];
	
	if(isSupplier != 'true') {
		//member
		$.post("ajax/activeMember", {idMember: idMember}, postMemberActivationHandler);
	} else {
		//supplier
		$.post("ajax/activeSupplier", {idMember: idMember}, postMemberActivationHandler);
	}
	
	
	
}

function postMemberActivationHandler(result) {
	
	if(result == 0) {
		
		//Errore nell'attivazione
		
	} else {
		var trControl = "#ActMember_" + result;
		
		$(trControl).hide("slow");
	}
}

function postMemberListHandler(result) {
    
    console.log("Ricevuto risultato lista membri/suppliers");
    
    $("#errorDiv3").hide();
    $("#errors3").html("");
    
    var data = result;
    
    if(data.length <= 0) {
        
        $("#legendError3").html("");
        $("#legendError3").append("Comunicazione");
        
        var memberType = $('#memberType2').val();
        
        if(memberType == 3) 
            $("#errors3").append("Non ci sono Fornitori da visualizzare<br /><br />");
        if(memberType == 1) 
            $("#errors3").append("Non ci sono Responsabili  da visualizzare<br /><br />");
        if(memberType == 0) 
            $("#errors3").append("Non ci sono Membri  da visualizzare<br /><br />");
        
        
        $("#errorDiv3").show("slow");
        $("#errorDiv3").fadeIn(1000);
    } else {
        
        $("#memberList2").hide();
        
        //Costruire le option delle pagine
        var mtype = $('#memberType2').val();
        $.postSync("ajax/getNumberItems", {memberType: mtype}, function(numberItems){ numberOfMember = numberItems; });
        
        //qui c'è il numero di pagine. Generare le options del pageSelect
        var out = [];
        var itemsPerPage = $('#itemsPerPage2').val();
        var npagine = Math.ceil(numberOfMember / itemsPerPage);
        
        for(var i = 0; i < npagine;)
            out.push('<option value="'+ i +'"> ' + (++i) + '</option>');
        
        $('#page2').html(out.join(''));
        
        //Costruzione tabella con utenti
        var output = [];
        
        if (typeof data[0].active === "undefined") {
            //member
            output.push("   <tr>  <th class='top' width='10%'> ID </th>" +
                                 "<th class='top' width='25%'> Membro </th>" +
                                 "<th class='top' width='20%'> Data Iscrizione </th>" +
                                 "<th class='top' width='20%'> Email  </th>" +
                                 "<th class='top' width='25%'> Address  </th>" +
                                 "<th class='top' width='10%'> Tel  </th> </tr>");
        } else  {
            //supplier
            output.push("   <tr>  <th class='top' width='5%'> ID </th>" +
                                 "<th class='top' width='15%'> Compagnia  </th>" +
                                 "<th class='top' width='15%'> Email  </th>" +
                                 "<th class='top' width='20%'> Address  </th>" +
                                 "<th class='top' width='10%'> Tel  </th>" +
                                 "<th class='top' width='10%'> Contatto  </th>" +
                                 "<th class='top' width='10%'> Fax  </th>" +
                                 "<th class='top' width='15%'> WebSite  </th>" +
                                 " </tr>");
        }
        
        
        $.each(data, function(index, val)
        {
            if (typeof val.active === "undefined") {
                //member
                output.push("<tr> <td>" + val.idMember +"</td>" +
                		         "<td>" + val.name + " " + val.surname + "</td>" +
                				 "<td>" + new Date(val.regDate) + "</td>" +
                				 "<td>" + val.email + "</td>" +
                				 "<td>" + val.address + " " + val.cap+ ", " + val.city + " " + val.state + "</td>" +
                        		 "<td>" + val.tel + "</td></tr>");
            } else  {
                //supplier
                output.push("<tr> <td>" + val.idMember +"</td>" +
                                 "<td>" + val.companyName + "</td>" +
                                 "<td>" + val.email + "</td>" +
                                 "<td>" + val.address + " " + val.cap+ ", " + val.city + " " + val.state + "</td>" +
                                 "<td>" + val.tel + "</td>" +
                                 "<td>" + val.contactName + "</td>" +
                                 "<td>" + val.fax + "</td>" +
                                 "<td>" + val.website + "</td></tr>");
            }
        });

        $('#memberList2').html(output.join(''));
        $("#memberList2").fadeIn(1000);
    }
    
}
function postMemberToActivateHandler(result) {
	
	console.log("Ricevuto risultato lista membri/suppliers da attivare");
	
	$("#errorDiv2").hide();
	$("#errors2").html("");
	
	var data = result;
	
	if(data.length <= 0) {
	
		$("#legendError2").html("");
		$("#legendError2").append("Comunicazione");
		
		var memberType = $('#memberType').val();
		
		if(memberType == 3) 
			$("#errors2").append("Non ci sono Fornitori da visualizzare<br /><br />");
		if(memberType == 1) 
			$("#errors2").append("Non ci sono Responsabili  da visualizzare<br /><br />");
		if(memberType == 0) 
			$("#errors2").append("Non ci sono Membri  da visualizzare<br /><br />");
		
		
		$("#errorDiv2").show("slow");
		$("#errorDiv2").fadeIn(1000);
	} else {
		
		$("#memberList").hide();
		
		//Costruire le option delle pagine
		var mtype = $('#memberType').val();
		$.postSync("ajax/getNumberItemsToActivate", {memberType: mtype}, function(numberItems){ numberOfMember = numberItems; });
		
		//qui c'è il numero di pagine. Generare le options del pageSelect
		var out = [];
		var itemsPerPage = $('#itemsPerPage').val();
		var npagine = Math.ceil(numberOfMember / itemsPerPage);
		
		for(var i = 0; i < npagine;)
			out.push('<option value="'+ i +'"> ' + (++i) + '</option>');
		
		$('#page').html(out.join(''));
		
		//Costruzione tabella con utenti
		var output = [];
		output.push("	<tr>  <th class='top' width='10%'> ID </th>" +
							 "<th class='top' width='40%'> Membro </th>" +
							 "<th class='top' width='30%'> Tipo  </th>" +
							 "<th class='top' width='20%'> Attiva  </th> </tr>");
		
		
		
		
		$.each(data, function(index, val)
		{
			if (typeof val.active === "undefined") {
				//member
				output.push("<tr id='ActMember_" + val.idMember + "'><td>" + val.idMember +"</td><td>" + val.name + " " + val.surname + "</td><td>" +
						val.memberType + "</td><td>" +
						"<form method='post'><input type='hidden' value='" + val.idMember + ",false'/>" +
						"<button type='submit' id='memberActivation_" + val.idMember + "'> Attiva </button></form></td></tr>");
			} else  {
				//supplier
				output.push("<tr id='ActMember_" + val.idMember + "'><td>" + val.idMember +"</td><td>" + val.name + " " + val.surname + "</td><td> Fornitore </td><td>" +
						"<form method='post'><input type='hidden' value='" + val.idMember + ",true'/>" +
						"<button type='submit' id='memberActivation_" + val.idMember + "'> Attiva </button></form></td></tr>");
			}
		});

		
		
		$('#memberList').html(output.join(''));
		
		$.each(data, function(index, val)
		{
			$("#memberActivation_" + val.idMember).on("click", clickMemberActivationHandler);
		});
		$("#memberList").fadeIn(1000);
	}
}

function clickRegHandler(event) {
	
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
	var mType = $('#mtype').val();
	var company = "";
	var description = "";
	var contactName = "";
	var fax = "";
	var website = "";
	var payMethod = "";
	var idResp = "";
	
	if(username == "") {
		errors.push("Username: Formato non valido");
	}
	if(firstname == "" || isNumber(firstname)) {
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
	
	if(mType == 3) {
		
		//Recuperare variabili fornitore
		
		company = $('#company').val();
		description = $('#description').val();
		contactName = $('#contactName').val();
		fax = $('#fax').val();
		website = $('#website').val();
		payMethod = $('#payMethod').val();
		idResp = $('#memberResp').val();
		
		if(company == "" || isNumber(company)) {
			errors.push("Compagnia: Formato non valido");
		}
		if(description == "" || isNumber(description)) {
			errors.push("Descrizione: Formato non valido");
		}
		if(contactName == "" || isNumber(contactName)) {
			errors.push("Contatto: Formato non valido");
		}
		if(fax == "" || !isNumber(fax)) {
			errors.push("Fax: Formato non valido");
		}
		if(website == "" || isNumber(website)) {
			errors.push("Web Site: Formato non valido");
		}
		if(payMethod == "" || isNumber(payMethod)) {
			errors.push("Metodo di Pagamento: Formato non valido");
		}
		if(idResp == -1 ) {
			errors.push("Responsabile: Non hai selezionato un responsabile");
		}
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
		
		//Chiamata ajax
		
		if(mType != 3) {
			
			// Registrazione membro normale o responsabile
			$.post("ajax/newMember", {	username: username,
										firstname: firstname,
										lastname: lastname,
										email: email,
										address: address,
										city: city,
										state: state,
										cap: cap,
										tel: tel,
										mType: mType}, postRegHandler);
			
		} else {
			
			// Registrazione Fornitore
			
			$.post("ajax/newSupplier", {	username: username,
											firstname: firstname,
											lastname: lastname,
											email: email,
											address: address,
											city: city,
											state: state,
											cap: cap,
											tel: tel,
											mType: mType,
											company: company,
											description: description,
											contactName: contactName,
											fax: fax,
											website: website,
											payMethod: payMethod,
											idResp: idResp}, postRegHandler);
			
		}	
	}
}

function postRegHandler(regResult) {
	
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
}

function checkRespSelect() {
	
	var selected = $('#mtype').val();
	$("#errorDiv").hide('slow');
	
	if(selected == 3) {
		//Utente fornitore selezionato
		
		$.post("ajax/getMembersRespString", function(data) {
			
			var output = [];
			output.push('<option value="-1"> Seleziona Responsabile...</option>');

			$.each(data, function(index, val)
			{
				var temp = val.split(","); 
				output.push('<option value="'+ temp[0] +'">'+ temp[1] +'</option>');
			});

			$('#memberResp').html(output.join(''));

			
		}).error(function() { alert("error"); });
		
		
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


