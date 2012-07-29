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
                                      "<label for='minDate' class='left'>Creato dopo il: </label>" +
                                      "<input type='text' id='minDate' class='field'/>" +
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
                              "</div>" +
                              "<div id='dialog' title='Errore: Formato date non corretto'> <p>Selezionale entrambe le date (o nel corretto ordine cronologico). </p></div>");
        
        $('#tabsOrder-3').html("<div class='logform'>" +
                                "<form method='post' action=''>" +
                                  "<fieldset><legend>&nbsp;Opzioni di Ricerca Ordini Scaduti:&nbsp;</legend><br />" +
                                      "<label for='minDate2' class='left'>Creato dopo il: </label>" +
                                      "<input type='text' id='minDate2' class='field' style='width: 120px'/>" +
                                      "<label for='maxDate2' class='left'>Chiuso prima del: </label>" +
                                      "<input type='text' id='maxDate2' class='field' style='width: 120px'/>" +
                          "<br /><br /><label for='toSetShipDate' class='left'>Data di Consegna: </label>" +
                                      "<select name='toSetShipDate' id='toSetShipDate' class='field' style='width: 200px'>" +
                                          "<option value='0'>Impostata</option>" +
                                          "<option value='1'>Da Impostare  </option>" +
                                          "<option value='2'>Entrambe </option>" +
                                       "</select>" +
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
        
        $('#tabsOrder-4').html("<div class='logform'>" +
				                  "<form method='post' action=''>" +
					                "<fieldset><legend>&nbsp;Opzioni di Ricerca Ordini In Consegna:&nbsp;</legend><br />" +
					                    "<label for='minDate3' class='left'>Consegna dopo il: </label>" +
					                    "<input type='text' id='minDate3' class='field' style='width: 100px'/>" +
					                    "<label for='maxDate3' class='left'>Consegna prima del: </label>" +
					                    "<input type='text' id='maxDate3' class='field' style='width: 100px'/>" +
					                "</fieldset>" +
					                "<button type='submit' id='orderShipRequest'> Visualizza </button>" +
					              "</form>" +
					              "<table id='shipOrderList' class='log'></table>" +
					                "<div id='errorDivShipOrder' style='display:none;'>" +
					                  "<fieldset><legend id='legendErrorShipOrder'>&nbsp;Errore&nbsp;</legend><br />" +
					                   "<div id='errorsShipOrder' style='padding-left: 40px'>" +
					                    "</div>" +
					                  "</fieldset>" +
					                "</div><br />" +
					            "</div>");
       
        prepareOrderForm();
    }

    function writeIndexPage(){
        $('.centrale').html("<p>Body admin history state</p>");
    }
    
})(window);

var idOrder = 0;

function prepareOrderForm(tab){
    
    $('#tabsOrder').tabs();
    $( "#dialog" ).dialog({ autoOpen: false });
    
    $("#minDate").datepicker();
    $('#minDate').datepicker("setDate", Date.now());
    $("#minDate2").datepicker();
    $('#minDate2').datepicker("setDate", Date.now());
    $('#maxDate2').datepicker({ defaultDate: 0, maxDate: 0 });
    $('#maxDate2').datepicker("setDate", Date.now());
    
    $("#minDate3").datepicker();
    $('#minDate3').datepicker("setDate", Date.now());
    $('#maxDate3').datepicker({ defaultDate: 0, maxDate: 0 });
    $('#maxDate3').datepicker("setDate", Date.now());
    
    $('#orderRequest').on("click", clickOrderHandler);
    $('#orderActiveRequest').on("click", clickOrderActiveHandler);
    $('#orderOldRequest').on("click", clickOrderOldHandler);
    $('#orderShipRequest').on("click", clickOrderShipHandler);
    
    $("button").button();
}

function clickOrderShipHandler(event) {
	event.preventDefault();
	
	var minDateTime = $('#minDate3').datepicker("getDate").getTime();
    var maxDate = $('#maxDate3').datepicker("getDate");
    
    maxDate.setHours(23);
    maxDate.setMinutes(59);
    maxDate.setSeconds(59);
    maxDate.setMilliseconds(999);
    
    var maxDateTime = maxDate.getTime();
    
    if(minDateTime == null || maxDateTime == null || minDateTime >  maxDateTime){
        $( "#dialog" ).dialog('open');
    } else {
        
        $.post("ajax/getDeliveredOrderResp", {start: minDateTime, end: maxDateTime}, postShipOrderListHandler);
    } 
}

function postShipOrderListHandler(orderList) {
	
	console.log("Ricevuti Ordini In Consegna");
    
    $("#shipOrderList").html("");
    $("#shipOrderList").hide();
    //$("#logs").fadeOut(500, function() {
    
           
    //});
    if(orderList.length > 0){
        $("#shipOrderList").append(" <tr>  <th class='top' width='10%'> ID </th>" +
                                          "<th class='top' width='40%'> Fornitore </th>" +
                                          "<th class='top' width='25%'> Data Consegna  </th>" +
                                          "<th class='top' width='25%'> Azione  </th> </tr>");
        
        for(var i = 0; i < orderList.length; i++){
            var order = orderList[i];
            
            var dateDelivery = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateDelivery));
            
            $("#shipOrderList").append("<tr id='idOrderShip_" + order.idOrder + "'></tr>");
            
            $("#idOrderShip_" + order.idOrder).append(
            										  "<td>" + order.idOrder +"</td>" +
                                              		  "<td>" + order.supplier.companyName + "</td>" +
                                              		  "<td>" + dateDelivery + "</td>" +
                                              		  "<td> <form>" +
            										  		 "<input type='hidden' value='" + order.idOrder + "'/>" +
            										  	     "<button style='margin: 0px' type='submit' id='showDetailsShip_" + order.idOrder + "'> Mostra Schede </button>" +
            										  	   "</form> </td>" );
            
            $("#shipOrderList").append("<tr class='detailsOrder' id='TRdetailsOrderShip_" + order.idOrder + "'><td colspan='4' id='TDdetailsOrderShip_" + order.idOrder + "'></td></tr>");
            $(".detailsOrder").hide();
            
            $("button").button();
        }
        
        $.each(orderList, function(index, val)
        {
            $("#showDetailsShip_" + val.idOrder).on("click", clickShowDetailShipsHandler);
        });
    
        $("#shipOrderList").show("slow");
        $("#shipOrderList").fadeIn(1000);
        $("#errorDivShipOrder").hide();
    } else {
        
        $("#shipOrderList").show();
        $("#errorDivShipOrder").hide();
        $("#legendErrorShipOrder").html("Comunicazione");
        $("#errorsShipOrder").html("Non ci sono Ordini Attivi  da visualizzare<br /><br />");
        $("#errorDivShipOrder").show("slow");
        $("#errorDivShipOrder").fadeIn(1000);
    
    }
	
}

function clickShowDetailShipsHandler(event) {
	event.preventDefault();
	
	$(".detailsOrder").hide();
    var form = $(this).parents('form');
    idOrder = $('input', form).val();
    
    $.post("ajax/getPurchaseFromOrder", {idOrder: idOrder}, postShowPurchaseHandler);
	
}

function postShowPurchaseHandler(data) {
	
	//TODO visualizzazione schede d'acquisto per ogni ordine.
}


function clickOrderActiveHandler(event) {
    event.preventDefault();
  
    var minDateTime = $('#minDate').datepicker("getDate").getTime();

    if(minDateTime == null){
        $( "#dialog" ).dialog('open');
    } else {
        
        $.post("ajax/getActiveOrderResp", {start: minDateTime}, postActiveOrderListHandler);
    }
    
    
    
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
                                             "<th class='top' width='15%'> Data Inizio  </th>" +
                                             "<th class='top' width='15%'> Data Chiusura  </th>" +
                                             "<th class='top' width='40%'> Azione  </th> </tr>");
        for(var i = 0; i < orderList.length; i++){
            var order = orderList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateClose));
            $("#activeOrderList").append("<tr id='idOrder_" + order.idOrder + "'> <td>" + order.idOrder +"</td>" +
                                              "<td>" + order.supplier.companyName + "</td>" +
                                              "<td>" + dateOpen + "</td>" +
                                              "<td>" + dateClose + "</td>" +
                                              "<td> <form> <input type='hidden' value='" + order.idOrder + "'/>" +
                                              	   "<button type='submit' id='showDetails_" + order.idOrder + "'> Mostra Dettagli </button>" +
                                              	   "</form></td></tr>" +
                                         "<tr class='detailsOrder' id='TRdetailsOrderActive_" + order.idOrder + "'><td colspan='5' id='TDdetailsOrderActive_" + order.idOrder + "'></td></tr>");
            $(".detailsOrder").hide();
        }
        
        $.each(orderList, function(index, val)
        {
            $("#showDetails_" + val.idOrder).on("click", clickShowDetailsHandler);
        });
    
        $("#activeOrderList").show("slow");
        $("#activeOrderList").fadeIn(1000);
        $("#errorDivActiveOrder").hide();
    } else {
        
        $("#activeOrderList").show();
        $("#errorDivActiveOrder").hide();
        $("#legendErrorActiveOrder").html("Comunicazione");
        $("#errorsActiveOrder").html("Non ci sono Ordini Attivi da visualizzare<br /><br />");
        $("#errorDivActiveOrder").show("slow");
        $("#errorDivActiveOrder").fadeIn(1000);
    
    }
    
}

function clickShowDetailsHandler(event) {
    event.preventDefault();
    
    $(".detailsOrder").hide();
    var form = $(this).parents('form');
    idOrder = $('input', form).val();
    
    $.post("ajax/getProductListFromOrder", {idOrder: idOrder}, postShowDetailsHandler);
}

function postShowDetailsHandler(data) {
    
    var selectedTab = getSelectedTabIndex();
    var trControl = 0;
    var tdControl = 0;
    
    if(selectedTab == 1) {
        trControl = "#TRdetailsOrderActive_" + idOrder;
        tdControl = "#TDdetailsOrderActive_" + idOrder;
    } else if(selectedTab == 2) {
        trControl = "#TRdetailsOrderOld_" + idOrder;
        tdControl = "#TDdetailsOrderOld_" + idOrder;
    }
    
    $(tdControl).html("<div style='margin: 15px'><table id='TABLEdetailsOrder_" + idOrder + "' class='log'></table></div>");
    
    var tableControl = "#TABLEdetailsOrder_" + idOrder;
    
    $(tableControl).append("<tr>  <th class='top' width='15%'> Prodotto </th>" +
                                 "<th class='top' width='15%'> Categoria </th>" +
                                 "<th class='top' width='35%'> Descrizione  </th>" +
                                 "<th class='top' width='15%'> Costo  </th>" +
                                 "<th class='top' width='20%'> Min-Max Buy  </th> </tr>");
    
    $.each(data, function(index, val)
    {
        $(tableControl).append("<tr>    <td>" + val.name + "</td>" +
        		                       "<td>" + val.category.description + "</td>" +
        		                       "<td>" + val.description + "</td>" +
        		                       "<td>" + val.unitCost + "</td>" +
        		                       "<td>" + val.minBuy + " - " + val.maxBuy + "</td></tr>");
    });
    
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);  
}

function clickOrderHandler(event) {
    event.preventDefault();
        
}

function clickOrderOldHandler(event) {
    
 event.preventDefault();
    
    var dateDeliveryType = $("#toSetShipDate").val();
    var minDateTime = $('#minDate2').datepicker("getDate").getTime();
    var maxDate = $('#maxDate2').datepicker("getDate");
    
    maxDate.setHours(23);
    maxDate.setMinutes(59);
    maxDate.setSeconds(59);
    maxDate.setMilliseconds(999);
    
    var maxDateTime = maxDate.getTime();
    
    if(minDateTime == null || maxDateTime == null || minDateTime >  maxDateTime){
        $( "#dialog" ).dialog('open');
    } else {
        
        $.post("ajax/getOldOrderResp", {start: minDateTime, end: maxDateTime, dateDeliveryType: dateDeliveryType}, postOldOrderListHandler);
    } 
}

function postOldOrderListHandler(orderList) {
    
console.log("Ricevuti Ordini Vecchi");
    
    $("#oldOrderList").html("");
    $("#oldOrderList").hide();
    //$("#logs").fadeOut(500, function() {
    
           
    //});
    if(orderList.length > 0){
        $("#oldOrderList").append("  <tr>  <th class='top' width='5%'> ID </th>" +
                                          "<th class='top' width='25%'> Fornitore </th>" +
                                          "<th class='top' width='15%'> Data Inizio  </th>" +
                                          "<th class='top' width='15%'> Data Chiusura  </th>" +
                                          "<th class='top' width='15%'> Data Consegna  </th>" +
                                          "<th class='top' width='25%'> Azione  </th> </tr>");
        
        for(var i = 0; i < orderList.length; i++){
            var order = orderList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateClose));
            var dateDelivery = 0;
            
            if(order.dateDelivery == "null") {
                dateDelivery = "null";
            } else {
                dateDelivery = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateDelivery)); 
            }
            
            $("#oldOrderList").append("<tr id='idOrder_" + order.idOrder + "'></tr>");
            
            $("#idOrder_" + order.idOrder).append(
            										  "<td>" + order.idOrder +"</td>" +
                                              		  "<td>" + order.supplier.companyName + "</td>" +
                                              		  "<td>" + dateOpen + "</td>" +
                                              		  "<td>" + dateClose + "</td>" +
                                              		  "<td> <input type='text' id='dateDelivery_" + order.idOrder + "' style='width: 80px' onchange='dataDeliveryChange(" + order.idOrder + ")'/> </td>" +   	
            										  "<td> <form>" +
            										  		 "<input type='hidden' value='" + order.idOrder + "'/>" +
            										  		 "<button style='margin: 0px' type='submit' id='setDateDelivery_" + order.idOrder + "'> Set Consegna </button>" +
            										  	     "<button style='margin: 0px' type='submit' id='showDetails_" + order.idOrder + "'> Mostra Dettagli </button>" +
            										  	   "</form> </td>" );
            
            $("#oldOrderList").append("<tr class='detailsOrder' id='TRdetailsOrderOld_" + order.idOrder + "'><td colspan='6' id='TDdetailsOrderOld_" + order.idOrder + "'></td></tr>");
            		
            $("#dateDelivery_"+ order.idOrder).datepicker();
            
            if(dateDelivery != "null") {
            	$("#dateDelivery_"+ order.idOrder).datepicker("setDate", new Date(dateDelivery));
            	$("#dateDelivery_" + order.idOrder).css('background','#C7FFA8');
            }
                

            $("#setDateDelivery_" + order.idOrder).on("click", clickSetDateDeliveryHandler);
            
            $(".detailsOrder").hide();
            $("button").button();
        }
        
        $.each(orderList, function(index, val)
        {
            $("#showDetails_" + val.idOrder).on("click", clickShowDetailsHandler);
        });
    
        $("#oldOrderList").show("slow");
        $("#oldOrderList").fadeIn(1000);
        $("#errorDivOldOrder").hide();
    } else {
        
        $("#oldOrderList").show();
        $("#errorDivOldOrder").hide();
        $("#legendErrorOldOrder").html("Comunicazione");
        $("#errorsOldOrder").html("Non ci sono Ordini Scaduti da visualizzare<br /><br />");
        $("#errorDivOldOrder").show("slow");
        $("#errorDivOldOrder").fadeIn(1000);
    
    }
    
}

function clickSetDateDeliveryHandler(event) {
    event.preventDefault();
    
    var form = $(this).parents('form');
    idOrder = $('input', form).val();
    
    var dateDelivery = $('#dateDelivery_' + idOrder).datepicker("getDate").getTime();

    $.post("ajax/setDeliveryDate", {idOrder: idOrder, dateDelivery: dateDelivery}, postSetDeliveryDateHandler);

}

function postSetDeliveryDateHandler(result) {
    
    if(result == 0) {
        //Errore nell'attivazione
    	$("#dateDelivery_" + idOrder).css('background','#FFBFA8');
    } else {
        //Modificare html
    	$("#dateDelivery_" + idOrder).css('background','#C7FFA8');
    }
}

function getSelectedTabIndex() { 
    return  $('#tabsOrder').tabs('option', 'selected');
}

function dataDeliveryChange(idOrder) {
	
	$("#dateDelivery_" + idOrder).css('background','#FFFFFF');
	
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