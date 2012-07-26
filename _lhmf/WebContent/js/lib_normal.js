(function(window, undefined) {
    var History = window.History;
    $ = window.jQuery;
    var histEnabled = History.enabled;
    if (!histEnabled)
        console.log("HTML 5 History API is disabled!");
    else
        History.Adapter.bind(window, 'statechange', historyStateChanged);

    $(function() 
    {
        $("#purchaseLink").click(purchaseClicked);

        $.datepicker.setDefaults({
            dateFormat : 'dd/mm/yy'
        });
        drawPageCallback();
    });

    function historyStateChanged()
    {
        var History = window.History;
        var state = History.getState();
        var stateData = state.data;
        if (!stateData)
            showIndex();
        switch (stateData.action)
        {
        case 'purchase':
            writePurchasePage();
            break;
        default:
            writeIndexPage();
        }
    }

    function purchaseClicked(event) {
        if (histEnabled == true) {
            event.preventDefault();
            var History = window.History;
            var state = History.getState();
            var stateData = state.data;
            if (!!stateData && !!stateData.action && stateData.action == 'purchase')
                return;
            History.pushState({action : 'purchase'}, null, 'purchase');
        }
    }
    
    function writePurchasePage(){
        $(".centrale").html("<div id='tabsPurchase'>" +
        		                    "<ul>" +
        		                     "<li><a href='#tabsPurchase-1'>Crea Scheda</a></li>" +
        		                     "<li><a href='#tabsPurchase-2'>Schede Attive</a></li>" +
        		                     "<li><a href='#tabsPurchase-3'>Schede Scadute</a></li>" +
        		                     "<li><a href='#tabsPurchase-4'>Schede In Consegna</a></li>" +
        		                    "</ul>" +
                                    "<div id='tabsPurchase-1'></div>" +
                                    "<div id='tabsPurchase-2'></div>" +
                                    "<div id='tabsPurchase-3'></div>" +
                                    "<div id='tabsPurchase-4'></div>" +
                               "</div>");
        
        $('#tabsPurchase-1').html("<div class='logform'>" +
                                "<form method='post' action='prder'>" +
                                  "<fieldset><legend>&nbsp;Composizione della Nuova Scheda:&nbsp;</legend><br />" +                  
                                  "<button type='submit' id='orderActiveRequest'> Visualizza Ordini Disponibili </button>" +
                                  
                                    "<button type='submit' id='purchaseRequest'> Crea Scheda </button>" +
                                  "</fieldset>" +
                                  "<div id='errorDivPurchase' style='display:none;'>" +
                                      "<fieldset><legend id='legendErrorPurchase'>&nbsp;Errore&nbsp;</legend><br />" +
                                       "<div id='errorsPurchase' style='padding-left: 40px'></div>" +
                                      "</fieldset>" +
                                  "</div>" +
                                "</form>" +
                              "</div>");
        
        $('#tabsPurchase-2').html("<div class='logform'>" +
                                "<form method='post' action=''>" +
                                  "<fieldset><legend>&nbsp;Opzioni di Ricerca Schede Attive:&nbsp;</legend><br />" +
                                      "<label for='minDate' class='left'>Data iniziale: </label>" +
                                      "<input type='text' id='minDate' class='field'/>" +
                                      "<label for='maxDate' class='left'>Data finale: </label>" +
                                      "<input type='text' id='maxDate' class='field'/>" +
                                  "</fieldset>" +
                                  "<button type='submit' id='purchaseActiveRequest'> Visualizza </button>" +
                                "</form>" +
                                "<table id='activePurchaseList' class='log'></table>" +
                                  "<div id='errorDivActivePurchase' style='display:none;'>" +
                                    "<fieldset><legend id='legendErrorActivePurchase'>&nbsp;Errore&nbsp;</legend><br />" +
                                     "<div id='errorsActivePurchase' style='padding-left: 40px'>" +
                                      "</div>" +
                                    "</fieldset>" +
                                  "</div><br />" +
                              "</div>");
        
        $('#tabsPurchase-3').html("<div class='logform'>" +
                                "<form method='post' action=''>" +
                                  "<fieldset><legend>&nbsp;Opzioni di Ricerca Schede Scadute:&nbsp;</legend><br />" +
                                      "<label for='minDate2' class='left'>Data iniziale: </label>" +
                                      "<input type='text' id='minDate2' class='field'/>" +
                                      "<label for='maxDate2' class='left'>Data finale: </label>" +
                                      "<input type='text' id='maxDate2' class='field'/>" +
                                      "<br /><label for='toSetShipDate' class='left'>Ordini con Data di Consegna da impostare: </label>" +
                                      "<input type='checkbox' id='toSetShipDate' />" +
                                  "</fieldset>" +
                                  "<button type='submit' id='purchaseOldRequest'> Visualizza </button>" +
                                "</form>" +
                                "<table id='oldPurchaseList' class='log'></table>" +
                                  "<div id='errorDivOldPurchase' style='display:none;'>" +
                                    "<fieldset><legend id='legendErrorOldPurchase'>&nbsp;Errore&nbsp;</legend><br />" +
                                     "<div id='errorsOldPurchase' style='padding-left: 40px'>" +
                                      "</div>" +
                                    "</fieldset>" +
                                  "</div><br />" +
                              "</div>");
        
        $('#tabsPurchase-4').html("Schede in fase di Consegna");
       
        preparePurchaseForm();
    }

    function writeIndexPage()
    {
        $('.centrale').html("<p>Interfaccia utente normale</p>");
    }
    
})(window);

function preparePurchaseForm(tab){
    
    $('#tabsPurchase').tabs();
    
    $("#minDate").datepicker({ defaultDate: 0, maxDate: 0 });
    $('#minDate').datepicker("setDate", Date.now());
    $('#maxDate').datepicker({ defaultDate: 0, maxDate: 0 });
    $('#maxDate').datepicker("setDate", Date.now());
    $("#minDate2").datepicker({ defaultDate: 0, maxDate: 0 });
    $('#minDate2').datepicker("setDate", Date.now());
    $('#maxDate2').datepicker({ defaultDate: 0, maxDate: 0 });
    $('#maxDate2').datepicker("setDate", Date.now());
    
    $('#orderActiveRequest').on("click", clickOrderActiveHandler);
    $('#purchaseRequest').on("click", clickPurchaseHandler);
    $('#purchaseActiveRequest').on("click", clickPurchaseActiveHandler);
    $('#purchaseOldRequest').on("click", clickPurchaseOldHandler);
}

function clickPurchaseHandler(event) {
    event.preventDefault();
    
    
    //Creazione schede
    
    
    
    
    
}

function clickOrderActiveHandler(event) {
    event.preventDefault();
    
    
    //Creazione schede
    
    
    
    
    
}

function clickPurchaseActiveHandler(event) {
    event.preventDefault();
  
    var minDate = $('#minDate').datepicker("getDate");
    var maxDate = $('#maxDate').datepicker("getDate");
    
    $.postSync("ajax/getActiveOrderNormal", {start: minDate, end: maxDate}, postActiveOrderListHandler);
    
}

function postActiveOrderListHandler(orderList) {
    
    console.log("Ricevuti Ordini Attivi");
    
    $("#activeOrderList").html("");
    $("#activeOrderList").hide();
    //$("#logs").fadeOut(500, function() {
    
           
    //});
    if(orderList.length > 0){
        $("#activeOrderList").append("  <tr>  <th class='top' width='10%'> ID </th>" +
                                             "<th class='top' width='20%'> Fornitore </th>" +
                                             "<th class='top' width='20%'> Data Inizio  </th>" +
                                             "<th class='top' width='50%'> Data Chiusura  </th>" +
                                             "<th class='top' width='50%'> Azione  </th> </tr>");
        for(var i = 0; i < orderList.length; i++){
            var order = orderList[i];
            var supplier=0;
            $.get("ajax/getsupplier", {idSupplier: order.idMemberSupplier}, function(result) {supplier = result;});
            
            $("#activeOrderList").append("<tr> <td>" + order.idOrder +"</td>" +
                                              "<td>" + supplier.companyName + "</td>" +
                                              "<td>" + new Date(order.dateOpen) + "</td>" +
                                              "<td>" + new Date(order.dateClose) + "</td>" +
                                              "<td>" + "Da fare" + "</td></tr>");
        }
    
        $("#activeOrderList").fadeIn(1000);
    } else {
        
        $("#errorDivActiveOrder").hide();
        $("#legendErrorActiveOrder").html("Comunicazione");
        $("#errorsActiveOrder").html("Non ci sono Ordini Attivi  da visualizzare<br /><br />");
        $("#errorDivActiveOrder").show("slow");
        $("#errorDivActiveOrder").fadeIn(1000);
    
    }
    
}

function clickPurchaseOldHandler(event) {
    event.preventDefault();
  
    //Schede passate
    
}

function newPurchase(purchase)
{
	if (purchase == undefined)
	{
	    console.debug("Invalid parameters in " + displayFunctionName());
	    return;
	}
	$.postJSONsync("ajax/newpurchase", purchase, function(idPurchase)
	{
		console.debug("Inserted: " + idPurchase);
	});
}
  
function getPastPurchases()
{
    $.getJSONsync("ajax/getpastpurchases", function(pastPurchasesList)
    {
    	window.localStorage.setItem('pastPurchasesList', JSON.stringify(pastPurchasesList));
    	console.debug("pastPurchasesList saved in localstorage");
    });
}

function loadAllPastPurchasesFromLocalStorage()
{
	return JSON.parse(window.localStorage.getItem('pastPurchasesList'));
}
   
function getActivePurchases()
{
    $.getJSONsync("ajax/getactivepurchases", function(activePurchasesList)
    {
      window.localStorage.setItem('activePurchasesList', JSON.stringify(activePurchasesList));
      console.debug("activePurchasesList saved in localstorage");
    });
}

function loadAllActivePurchasesFromLocalStorage()
{
	return JSON.parse(window.localStorage.getItem('activePurchasesList'));
}

function getMyPurchase()
{
  $.getJSONsync("ajax/getmypurchases", function(purchasesList)
      {
        window.localStorage.setItem('myPurchasesList', JSON.stringify(purchasesList));
        console.debug("myPurchasesList saved in localstorage");
      });
}

function loadMyPurchasesFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('myPurchasesList'));
}

function getActiveOrdersAsRows(ordersList, respsList, suppliersList, idOrderIn)
{
	var returnedTableString = "";
	if (ordersList == undefined || idOrderIn == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
		return returnedTableString;
	}
	for ( var orderIndex in ordersList)
	{
		if (ordersList[orderIndex].idOrder == idOrderIn)
		{
			returnedTableString += "<tr>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateOpen + "</td>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateClose + "</td>";
			returnedTableString += getSupplierAsTableRow(suppliersList, ordersList[orderIndex].idMemberSupplier);
			returnedTableString += getRespAsTableRow(respsList, ordersList[orderIndex].idMemberResp);
			returnedTableString += "</tr>";
		}
	}
	return returnedTableString;
}

function getPastOrdersAsRows(ordersList, respsList, suppliersList, idOrderIn)
{
	var returnedTableString = "";
	if (ordersList == undefined || idOrderIn == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
		return returnedTableString;
	}
	for ( var orderIndex in ordersList)
	{
		if (ordersList[orderIndex].idOrder == idOrderIn)
		{
			returnedTableString += "<tr>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateOpen + "</td>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateClose + "</td>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateDelivery + "</td>";
			returnedTableString += getSupplierAsTableRow(suppliersList, ordersList[orderIndex].idMemberSupplier);
			returnedTableString += getRespAsTableRow(respsList, ordersList[orderIndex].idMemberResp);
			returnedTableString += "</tr>";
		}
	}
	return returnedTableString;
}

function getPurchasesAsTableRows(purchasesList, respsList, orderList, page, itemsPerPage)
{
	var returnedTableString = "";
	if (page < 1 || (page - 1) * itemsPerPage >= purchasesList.length
		|| itemsPerPage < 1 || itemsPerPage > 100 || purchasesList == undefined
		|| page == undefined || itemsPerPage == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
		return "";
	}
	for ( var purchaseIndex = (page - 1) * itemsPerPage; purchaseIndex < purchasesList.length
      	&& purchaseIndex <= page * itemsPerPage; purchaseIndex++)
	{
	    returnedTableString += "<tr>";
	    returnedTableString += getRespAsTableRow(respsList, ordersList[orderIndex].idMemberResp);
	    returnedTableString += "<td>" + purchasesList[purchaseIndex].idMember + "</td>";
	    returnedTableString += getActiveOrdersAsRows(ordersList, purchasesList[purchaseIndex].idOrder);
	    returnedTableString += getPastOrdersAsRows(ordersList, purchasesList[purchaseIndex].idOrder);
	    returnedTableString += "</tr>";
	}
	return returnedTableString;
}