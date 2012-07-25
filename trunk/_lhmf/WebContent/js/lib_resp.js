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

function clickOrderActiveHandler(event) {
    event.preventDefault();
  
    var minDate = $('#minDate').datepicker("getDate");
    var maxDate = $('#maxDate').datepicker("getDate");
    
    $.post("ajax/getActiveOrder", {start: minDate, end: maxDate}, postActiveOrderListHandler);
    
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

function clickOrderHandler(event) {
    event.preventDefault();
    
    //Recuperare la lista degli ordini attivi
  
    
}

function clickOrderOldHandler(event) {
    event.preventDefault();
  
    
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