(function(window, undefined) {
    var History = window.History;
    $ = window.jQuery;
    var histEnabled = History.enabled;
    if (!histEnabled)
        console.log("HTML 5 History API is disabled!");
    else
        History.Adapter.bind(window, 'statechange', historyStateChanged);

    $(function() {
        $("#orderLink").click(orderClicked);

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
        case 'order':
            writeOrderPage();
            break;
        default:
            writeIndexPage();
        }
    }

    function orderClicked(event) {
        if (histEnabled == true) {
            event.preventDefault();
            var History = window.History;
            var state = History.getState();
            var stateData = state.data;
            if (!!stateData && !!stateData.action && stateData.action == 'order')
                return;
            History.pushState({
                action : 'order'
            }, null, 'order');
        }
    }
    
    function writeOrderPage(){
        $(".centrale").html("   <div id='tabsOrder'>" +
        		                    "<ul>" +
        		                     "<li><a href='#tabsOrder-1'>Crea Ordine</a></li>" +
        		                     "<li><a href='#tabsOrder-2'>Ordini Attivi</a></li>" +
        		                     "<li><a href='#tabsOrder-3'>Ordini Scaduti</a></li>" +
        		                     "<li><a href='#tabsOrder-4'>Ordini In Consegna</a></li>" +
        		                    "</ul>" +
                                    "<div id='tabsOrder-1'></div>" +
                                    "<div id='tabsOrder-2'></div>" +
                                    "<div id='tabsOrder-3'></div>" +
                                    "<div id='tabsOrder-4'></div>" +
                               "</div>");
        
        $('#tabsOrder-1').html("<div class='logform'>" +
                                "<form method='post' action='prder'>" +
                                  "<fieldset><legend>&nbsp;Composizione del Nuovo Ordine:&nbsp;</legend><br />" +
                                    " Fare qui roba drag and drop" +
                                    "<button type='submit' id='orderRequest'> Crea Ordine </button>" +
                                  "</fieldset>" +
                                  "<div id='errorDivOrder' style='display:none;'>" +
                                      "<fieldset><legend id='legendErrorOrder'>&nbsp;Errore&nbsp;</legend><br />" +
                                       "<div id='errorsOrder' style='padding-left: 40px'></div>" +
                                      "</fieldset>" +
                                  "</div>" +
                                  
                                "</form>" +
                              "</div>");
        
        $('#tabsOrder-2').html("<div class='logform'>" +
                                "<form method='post' action=''>" +
                                  "<fieldset><legend>&nbsp;Opzioni di Ricerca Ordini Attivi:&nbsp;</legend><br />" +
                                      "<label for='minDate' class='left'>Data iniziale: </label>" +
                                      "<input type='text' id='minDate' class='field'/>" +
                                      "<label for='maxDate' class='left'>Data finale: </label>" +
                                      "<input type='text' id='maxDate' class='field'/>" +
                                  "</fieldset>" +
                                  "<button type='submit' id='orderActiveRequest'> Visualizza </button>" +
                                "</form>" +
                                "<table id='activeOrderList' class='log'></table>" +
                                  "<div id='errorDivActiveOrder' style='display:none;'>" +
                                    "<fieldset><legend id='legendErrorActiveOrder'>&nbsp;Errore&nbsp;</legend><br />" +
                                     "<div id='errorsActiveOrder' style='padding-left: 40px'>" +
                                      "</div>" +
                                    "</fieldset>" +
                                  "</div><br />" +
                              "</div>");
        
        $('#tabsOrder-3').html("<div class='logform'>" +
                                "<form method='post' action=''>" +
                                  "<fieldset><legend>&nbsp;Opzioni di Ricerca Ordini Scaduti:&nbsp;</legend><br />" +
                                      "<label for='minDate2' class='left'>Data iniziale: </label>" +
                                      "<input type='text' id='minDate2' class='field'/>" +
                                      "<label for='maxDate2' class='left'>Data finale: </label>" +
                                      "<input type='text' id='maxDate2' class='field'/>" +
                                      "<br /><label for='toSetShipDate' class='left'>Ordini con Data di Consegna da impostare: </label>" +
                                      "<input type='checkbox' id='toSetShipDate' />" +
                                  "</fieldset>" +
                                  "<button type='submit' id='orderOldRequest'> Visualizza </button>" +
                                "</form>" +
                                "<table id='oldOrderList' class='log'></table>" +
                                  "<div id='errorDivOldOrder' style='display:none;'>" +
                                    "<fieldset><legend id='legendErrorOldOrder'>&nbsp;Errore&nbsp;</legend><br />" +
                                     "<div id='errorsOldOrder' style='padding-left: 40px'>" +
                                      "</div>" +
                                    "</fieldset>" +
                                  "</div><br />" +
                              "</div>");
        
        $('#tabsOrder-4').html("Ordini in fase di Consegna");
       
        prepareOrderForm();
    }

    function writeIndexPage(){
        $('.centrale').html("<p>Body admin history state</p>");
    }
    
})(window);

function prepareOrderForm(tab){
    
    $('#tabsOrder').tabs();
    
    $("#minDate").datepicker({ defaultDate: 0, maxDate: 0 });
    $('#minDate').datepicker("setDate", Date.now());
    $('#maxDate').datepicker({ defaultDate: 0, maxDate: 0 });
    $('#maxDate').datepicker("setDate", Date.now());
    $("#minDate2").datepicker({ defaultDate: 0, maxDate: 0 });
    $('#minDate2').datepicker("setDate", Date.now());
    $('#maxDate2').datepicker({ defaultDate: 0, maxDate: 0 });
    $('#maxDate2').datepicker("setDate", Date.now());
    
    $('#orderRequest').on("click", clickOrderHandler);
    $('#orderActiveRequest').on("click", clickOrderActiveHandler);
    $('#orderOldRequest').on("click", clickOrderOldHandler);
}

function clickOrderHandler(event) {
    event.preventDefault();
  
    
}

function clickOrderActiveHandler(event) {
    event.preventDefault();
    
    //Recuperare la lista degli ordini attivi
  
    
}

function clickOrderOldHandler(event) {
    event.preventDefault();
  
    
}

function clickActMemberHandler(event){
    event.preventDefault();
    var memberType = $('#memberType').val();
    var page = $('#page').val();
    var itemsPerPage = $('#itemsPerPage').val();
    
    if(memberType == 3) {
        //supplier
        $.post("ajax/getSuppliersToActivate", { page: page,
                                                itemsPerPage: itemsPerPage }, postMemberToActivateHandler);
        
    } else {
        //normale o responsabile
        $.post("ajax/getMembersToActivate", {   memberType: memberType,
                                                page: page,
                                                itemsPerPage: itemsPerPage }, postMemberToActivateHandler);
    }
    
    
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
        output.push("   <tr>  <th class='top' width='10%'> ID </th>" +
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
            $.post("ajax/newMember", {  username: username,
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
            
            $.post("ajax/newSupplier", {    username: username,
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



function newOrder(order)
{
	if (order == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
	    return;
	}
	$.postJSONsync("ajax/neworder", order, function(idOrder)
    {
    	console.debug("Inserted: " + idOrder);
    });
}

function getPastOrders()
{
	$.getJSONsync("ajax/getpastorders", function(pastOrdersList)
	{
		window.localStorage.setItem('pastOrdersList', JSON.stringify(pastOrdersList));
		console.debug("pastOrdersList saved in localstorage");
	});
}

function loadAllPastOrdersFromLocalStorage()
{
	return JSON.parse(window.localStorage.getItem('pastOrdersList'));
}

function getActiveOrders()
{
	$.getJSONsync("ajax/getactiveorders", function(activeOrdersList)
	{
		window.localStorage.setItem('activeOrdersList', JSON.stringify(activeOrdersList));
		console.debug("activeOrdersList saved in localstorage");
	});
}

function loadAllActiveOrdersFromLocalStorage()
{
	return JSON.parse(window.localStorage.getItem('activeOrdersList'));
}

function getActiveOrdersAsTableRows(ordersList, respsList, suppliersList, page, itemsPerPage)
{
	var returnedTableString = "";
	if (page < 1 || (page - 1) * itemsPerPage >= ordersList.length
		|| itemsPerPage < 1 || itemsPerPage > 100 || ordersList == undefined
		|| page == undefined || itemsPerPage == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
		return "";
	}
	for ( var orderIndex = (page - 1) * itemsPerPage; orderIndex < ordersList.length
      	&& orderIndex <= page * itemsPerPage; orderIndex++)
	{
	    returnedTableString += "<tr>";
	    returnedTableString += "<td>" + ordersList[orderIndex].dateOpen + "</td>";
	    returnedTableString += "<td>" + ordersList[orderIndex].dateClose + "</td>";
	    returnedTableString += getRespAsTableRow(respsList, productsList[productIndex].idMemberResp);
	    returnedTableString += getSupplierAsTableRow(suppliersList, productsList[productIndex].idMemberSupplier);
	    returnedTableString += "</tr>";
	}
	return returnedTableString;
}

function getPastOrdersAsTableRows(ordersList, respsList, suppliersList, page, itemsPerPage)
{
	var returnedTableString = "";
	if (page < 1 || (page - 1) * itemsPerPage >= ordersList.length
		|| itemsPerPage < 1 || itemsPerPage > 100 || ordersList == undefined
		|| page == undefined || itemsPerPage == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
		return "";
	}
	for ( var orderIndex = (page - 1) * itemsPerPage; orderIndex < ordersList.length
      	&& orderIndex <= page * itemsPerPage; orderIndex++)
	{
	    returnedTableString += "<tr>";
	    returnedTableString += "<td>" + ordersList[orderIndex].dateOpen + "</td>";
	    returnedTableString += "<td>" + ordersList[orderIndex].dateClose + "</td>";
	    returnedTableString += "<td>" + ordersList[orderIndex].dateDelivery + "</td>";
	    returnedTableString += getRespAsTableRow(respsList, productsList[productIndex].idMemberResp);
	    returnedTableString += getSupplierAsTableRow(suppliersList, productsList[productIndex].idMemberSupplier);
	    returnedTableString += "</tr>";
	}
	return returnedTableString;
}