window.setInterval(function(){

	if (typeof startRefreshOrder != undefined && startRefreshOrder == 1) {
		
		$.post("ajax/getActiveOrderSupplier", null, function(orderList) 
		{
			
			$.each(orderList, function(index, val)
			{
				refreshProgress(val.idOrder);
			});
		});
		
	}
	
}, 3000);

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
		$("#productsLink").click(productsClicked);
		$("#statLink").click(statClicked);
		$("#notifiesLink").click(notifiesClicked);
		$("#messagesLink").click(messagesClicked);
		//registerForMessages();
	    //registerForNotifies();
		registerForNews();
  
		$.datepicker.setDefaults({
			dateFormat : 'dd/mm/yy'
		});
		drawPageCallback();
	});

})(window);

var startRefreshOrder = 0;

function historyStateChanged()
{
  var History = window.History;
  var state = History.getState();
  var stateData = state.data;
  if (!stateData)
    showIndex();
  switch (stateData.action)
  {
  case 'orderSup':
    writeOrderPage(stateData.idOrd, stateData.tab);
    startRefreshOrder = 1;
    break;
  case 'productsMgmt':
	  writeSupplierPage(0);
	  startRefreshOrder = 0;
	  break;
  case 'statSupplier':
    writeStatPageSupplier();
    startRefreshOrder = 0;
    break;
  case 'notifiche':
    getMyNotifies();
    startRefreshOrder = 0;
    break;
  case 'messaggi':
    getMyMessages();
    startRefreshOrder = 0;
    break;
  default:
    writeIndexPage();
    startRefreshOrder = 0;
  }
}

function productsClicked(event)
{
  var History = window.History;
  if (History.enabled == true)
  {
    event.preventDefault();
    var state = History.getState();
    var stateData = state.data;
    if (!!stateData && !!stateData.action && stateData.action == 'productsMgmt')
      return;
    History.pushState({
      action : 'productsMgmt'
    }, null, 'productsMgmt');
  }
}

function orderClicked(event)
{
  var History = window.History;
  if (History.enabled == true)
  {
    event.preventDefault();
    var state = History.getState();
    var stateData = state.data;
    if (!!stateData && !!stateData.action && stateData.action == 'orderSup')
      return;
    History.pushState({
      action : 'orderSup',
      idOrd: 0, 
      tab:0
    }, null, 'orderSup');
  }
}

function statClicked(event) {
	var History = window.History;	
	  if (History.enabled == true) {
	    event.preventDefault();
	    var state = History.getState();
	    var stateData = state.data;
	    if (!!stateData && !!stateData.action
	        && stateData.action == 'statSupplier')
	      return;
	    History.pushState({
	      action : 'statSupplier'
	    }, null, 'statSupplier');
	  }
}

function writeOrderPage(idOrd, tab)
{
  $("#bodyTitleHeader").html("Gestione ordini");
  $(".centrale").html("   <div id='tabsOrder'>" +
				          "<ul>" +
				           "<li><a href='#tabsOrder-1'>Ordini Attivi</a></li>" +
				           "<li><a href='#tabsOrder-2'>Completati</a></li>" +
				           "<li><a href='#tabsOrder-3'>Storico Ordini</a></li>" +
				          "</ul>" +
				              "<div id='tabsOrder-1'></div>" +
				              "<div id='tabsOrder-2'></div>" +
				              "<div id='tabsOrder-3'></div>" +
				         "</div>");
	
	$('#tabsOrder-1').html("<div class='logform'>" +
	          "<table id='activeOrderList' class='log'></table>" +
	            "<div id='errorDivActiveOrder' style='display:none;'>" +
	              "<fieldset><legend id='legendErrorActiveOrder'>&nbsp;Errore&nbsp;</legend><br />" +
	               "<div id='errorsActiveOrder' style='padding-left: 40px'>" +
	                "</div>" +
	              "</fieldset>" +
	            "</div><br />" +
	        "</div>" +
	        "<div id='dialog' title='Errore: Formato date non corretto'> <p>Seleziona entrambe le date (o nel corretto ordine cronologico). </p></div>");
	
	$('#tabsOrder-2').html("<div class='logform'>" +
	          "<table id='completeOrderList' class='log'></table>" +
	            "<div id='errorDivCompleteOrder' style='display:none;'>" +
	              "<fieldset><legend id='legendErrorCompleteOrder'>&nbsp;Errore&nbsp;</legend><br />" +
	               "<div id='errorsCompleteOrder' style='padding-left: 40px'>" +
	                "</div>" +
	              "</fieldset>" +
	            "</div><br />" +
	        "</div>");
	
	$('#tabsOrder-3').html("<div class='logform'>" +
	          "<form method='post' action=''>" +
	            "<fieldset><legend>&nbsp;Opzioni di Ricerca Ordini:&nbsp;</legend><br />" +
	                "<label for='maxDate2' class='left'>Chiuso dopo il: </label>" +
	                "<input type='text' id='maxDate2' class='field' style='width: 120px'/>" +
	                "<label for='toSetShipDate' class='left'>Consegna: </label>" +
	                "<select name='toSetShipDate' id='toSetShipDate' class='field' style='width: 200px'>" +
	                    "<option value='0'>Impostata</option>" +
	                    "<option value='1'>Non Impostata  </option>" +
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
	
	$('#tabsOrder').tabs();
	$('#tabsOrder').tabs('select', tab);
	
	prepareOrderForm(idOrd, tab);
}

function prepareOrderForm(idOrd, tab){
    
    $('#tabsOrder').tabs();
    $( "#dialog" ).dialog({ autoOpen: false });

    $('#maxDate2').datepicker({ defaultDate: 0, maxDate: 0 });
    $('#maxDate2').datepicker("setDate", Date.now());
    
    $('#orderOldRequest').on("click", clickOrderOldHandler);
    
    loadActiveOrder();
    
    if(idOrd != 0 && tab == 0) {
    	idOrder = idOrd;
    	clickShowDetailsHandler();
    }
    	
    loadCompleteOrder();
    
    if(idOrd != 0 && tab == 1) {
    	idOrder = idOrd;
    	clickShowDetailsHandler();
    }
    
    $("button").button();
}

function loadActiveOrder() {
	  
    $.post("ajax/getActiveOrderSupplier", null, postActiveOrderSupplierListHandler); 
}

function postActiveOrderSupplierListHandler(orderList) {
	
	$("#activeOrderList").hide();
    $("#activeOrderList").html("");
       
    if(orderList.length > 0){
        $("#activeOrderList").append("  <tr>  <th class='top' width='15%'> Nome </th>" +
                                             "<th class='top' width='15%'> Fornitore </th>" +
                                             "<th class='top' width='15%'> Data Inizio  </th>" +
                                             "<th class='top' width='15%'> Data Chiusura  </th>" +
                                             "<th class='top' width='25%'> Progresso </th>" +
                                             "<th class='top' width='15%'> Azione  </th> </tr>");
        
        
        for(var i = 0; i < orderList.length; i++){
        	
            var order = orderList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateClose));
            
            var idProgressBar = "pbOrder_" + order.idOrder;
            var valProgress = 0;
            $.postSync("ajax/getProgressOrder", {idOrder: order.idOrder}, function(data)
            {
            	valProgress = data;
            });
                        
            $("#activeOrderList").append("<tr id='idOrder_" + order.idOrder + "'> " +
            								  "<td>" + order.orderName +"</td>" +
                                              "<td>" + order.supplier.companyName + "</td>" +
                                              "<td>" + dateOpen + "</td>" +
                                              "<td>" + dateClose + "</td>" +
                                              "<td><div id='" + idProgressBar + "'></div></td>" +
                                              "<td><button type='submit' id='showDetails_" + order.idOrder + "' data-idorder='" + order.idOrder + "'> Dettagli </button>" +
                                              "</td></tr>" +
                                         "<tr class='detailsOrder' id='TRdetailsOrderActive_" + order.idOrder + "'><td colspan='6' id='TDdetailsOrderActive_" + order.idOrder + "'></td></tr>");
            $(".detailsOrder").hide();
            $( "#" + idProgressBar ).progressbar({	value: valProgress	});
            $( "#" + idProgressBar ).css('height', '1.8em');
            $("#showDetails_" + order.idOrder).on("click", clickShowDetailsHandler);
        }
        
        $("button").button();
    
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
	
	if(typeof event != 'undefined')
		event.preventDefault();
    
    $(".detailsOrder").hide();
    
    if(typeof event != 'undefined')
    	idOrder = $(this).data('idorder');
    
    $.post("ajax/getProductListFromOrder", {idOrder: idOrder}, postShowDetailsHandler);
}

function postShowDetailsHandler(data) {
    
    var selectedTab = getSelectedTabIndex();
    var trControl = 0;
    var tdControl = 0;
    
    if(selectedTab == 0) {
        trControl = "#TRdetailsOrderActive_" + idOrder;
        tdControl = "#TDdetailsOrderActive_" + idOrder;
        trIdControl = "trActiveProduct_" + idOrder + "_";
    } else if(selectedTab == 1) {
        trControl = "#TRdetailsOrderComplete_" + idOrder;
        tdControl = "#TDdetailsOrderComplete_" + idOrder;
        trIdControl = "trCompleteProduct_" + idOrder + "_";
    } else if(selectedTab == 2) {
        trControl = "#TRdetailsOrderOld_" + idOrder;
        tdControl = "#TDdetailsOrderOld_" + idOrder;
        trIdControl = "trOldProduct_" + idOrder + "_";
    }
    
    $(tdControl).html("<div style='margin: 15px'><table id='TABLEdetailsOrder_" + idOrder + "' class='log2'></table></div>");
    
    var tableControl = "#TABLEdetailsOrder_" + idOrder;
    var DispTmp = 0;
    var TotAmount = 0;
    
    var productsid = [];
    
    $(tableControl).append("<tr>  <th class='top' width='15%'> Prodotto </th>" +
                                 "<th class='top' width='15%'> Categoria </th>" +
                                 "<th class='top' width='15%'> Descrizione  </th>" +
                                 "<th class='top' width='5%'> Costo </th>" +
                                 "<th class='top' width='10%'> Min-Max Buy </th>" +
                                 "<th class='top' width='5%'> Disp.  </th>" +
                                 "<th class='top' width='5%'> Tot. Richiesta </th>" +
                                 "<th class='top' width='25%'> Progresso  </th>" +
                                 "<th class='top' width='5%'> Parziale  </th>" +
                                 " </tr>");
    
    $.each(data, function(index, val)
    {
    	
    	productsid.push(val.idProduct);
    	
    	$.postSync("ajax/getDispOfProductOrder", {idOrder: idOrder, idProduct: val.idProduct}, function(data)
        {
    		if(data == -1)
    			DispTmp = "Inf.";
    		else
    			DispTmp = data;
        });
    	
    	var idProgressBar = "pbProduct_" + idOrder + "_" + val.idProduct;
    	var idDispDIV = "dispOrder_" + idOrder + "_" + val.idProduct;
    	var idAmountDIV = "totAmountOrder_" + idOrder + "_" + val.idProduct;
    	var idparzialeDIV = "parzialeOrder_" + idOrder + "_" + val.idProduct;
    	
        $(tableControl).append("<tr id='" + trIdControl + val.idProduct + "'>    <td>" + val.name + "</td>" +
        		                       "<td>" + val.category.description + "</td>" +
        		                       "<td>" + val.description + "</td>" +
        		                       "<td>" + val.unitCost + " &euro;</td>" +
        		                       "<td>" + val.minBuy + " - " + val.maxBuy + "</td>" +
    		                       	   "<td id='" + idDispDIV + "' class='dispClass'>" + DispTmp + "</td>" +
    		                       	   "<td id='" + idAmountDIV + "' class='totamountClass'>" + TotAmount + "</td>" +
    		                       	   "<td style='padding: 2px;'><div id='" + idProgressBar + "'></div></td>" +
    		                       	   "<td id='" + idparzialeDIV + "'> 0 &euro;</td>" +
    		                   "</tr>");
        
        $( "#" + idProgressBar ).progressbar({	value: 0 });
		$( "#" + idProgressBar ).css('height', '1em');
		
    });
    
    var stringProductsId = productsid.join(',');
    
    //aggiorno le quantit� acquistate e relativo parziale
    $.postSync("ajax/getTotBought", {idOrder: idOrder, idProducts: stringProductsId}, function(amountList)
    {
    	$.each(productsid, function(index, idProduct) {
    		var idAmountDIV = "#totAmountOrder_" + idOrder + "_" + idProduct;
    		$(idAmountDIV).html(amountList[index]);
    		
    		var idparzialeDIV = "#parzialeOrder_" + idOrder + "_" + idProduct;
    		var unitCost = 0;
    		$.postSync("ajax/getProductNormal", {idProduct: idProduct}, function(data){ unitCost = data.unitCost; });
        	var parziale = amountList[index] * unitCost;
    		
        	$(idparzialeDIV).html(parziale + '&euro;');
        	
    	});
    });
    
    //Aggiorno le progressbar dei prodotti.
    var allProgress = "";
    $.postSync("ajax/getProgressProductOfOrder", {idOrder: idOrder}, function(data)
    {
    	allProgress = data;
    });
    
    $.each(allProgress, function(index, val)
    {
    	var temp = val.split(',');
    	var idProduct = temp[0];
    	var progress = parseFloat(temp[1]);
	    
    	//Aggiorno progressbar
    	var idProgressBarProduct =  "#pbProduct_" + idOrder + "_" + idProduct;
    	$(idProgressBarProduct).progressbar('value', progress);
    	    	
    	if(progress < 100) {
    		var selectedTab = getSelectedTabIndex();
    		
			if(selectedTab == 1) {
				var idTr = "#trCompleteProduct_" + idOrder + "_" + idProduct; 	
				$(idTr).addClass("noLimitProduct");
    		}
			
			if(selectedTab == 2) {
				var idTr = "#trOldProduct_" + idOrder + "_" + idProduct;
	    		$(idTr).addClass("noLimitProduct");
			}
    	}
    	
    });
    
    //refreshProgress(idOrder);
    
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);  
}

function refreshProgress(idOrder) {
	
	
	var valProgress = 0;
    $.postSync("ajax/getProgressOrder", {idOrder: idOrder}, function(data)
    {
    	valProgress = data;
    });
    
    //Aggiorno progressbar ordine generale
    var idProgressBar = "#pbOrder_" + idOrder;
    $(idProgressBar).progressbar('value', valProgress);
    
    //Aggiorno le progressbar dei prodotti.
    var allProgress = "";
    $.postSync("ajax/getProgressProductOfOrder", {idOrder: idOrder}, function(data)
    {
    	allProgress = data;
    });
    
    var productsid = [];
    
    $.each(allProgress, function(index, val)
    {
    	var temp = val.split(',');
    	var idProduct = temp[0];
    	productsid.push(idProduct);
    	var progress = parseFloat(temp[1]);
    	
    	//Aggiorno disponibilit�
    	var idDispDIV = "#dispOrder_" + idOrder + "_" + idProduct;
    	var DispTmp = 0;
	    $.postSync("ajax/getDispOfProductOrder", {idOrder: idOrder, idProduct: idProduct}, function(data)
        {
            if(data == -1)
                DispTmp = "Inf.";
            else
                DispTmp = data;
        });
	    
	    $(idDispDIV).html(DispTmp);
	    
    	//Aggiorno progressbar
    	var idProgressBarProduct =  "#pbProduct_" + idOrder + "_" + idProduct;
    	$(idProgressBarProduct).progressbar('value', progress);
    	
    });	
    
    	var stringProductsId = productsid.join(',');
    
	    //aggiorno le quantit� acquistate
	    $.post("ajax/getTotBought", {idOrder: idOrder, idProducts: stringProductsId}, function(amountList)
	    {
	    	$.each(productsid, function(index, idProduct) {
	    		var idAmountDIV = "#totAmountOrder_" + idOrder + "_" + idProduct;
	    		$(idAmountDIV).html(amountList[index]);
	    		
	    		var idparzialeDIV = "#parzialeOrder_" + idOrder + "_" + idProduct;
	    		var unitCost = 0;
	    		$.postSync("ajax/getProductNormal", {idProduct: idProduct}, function(data){ unitCost = data.unitCost; });
	        	var parziale = amountList[index] * unitCost;
	    		
	        	$(idparzialeDIV).html(parziale + '&euro;');
	    		
	    	});
	    });
	
}

function loadCompleteOrder() {
    
    $.post("ajax/getCompleteOrderSupplier", null, postCompleteOrderSupplierListHandler);
}

function postCompleteOrderSupplierListHandler(orderList) {
	
	$("#completeOrderList").html("");
    $("#completeOrderList").hide();
    
    if(orderList.length > 0){
    	
    	$("#completeOrderList").append(" <tr><th class='top' width='10%'> Nome </th>" +
									        "<th class='top' width='25%'> Fornitore </th>" +
									        "<th class='top' width='15%'> Data Inizio  </th>" +
									        "<th class='top' width='15%'> Data Chiusura  </th>" +
									        "<th class='top' width='15%'> Data Consegna  </th>" +
									        "<th class='top' width='20%'> Azione  </th> </tr>");
    	
    	
    	for(var i = 0; i < orderList.length; i++) {
    		
    		 var order = orderList[i];
             var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateOpen));
             var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateClose));
             var dateDelivery = "Non Definita";
             if(order.dateDelivery === 'true')
            	 dateDelivery = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateDelivery));

             
             $("#completeOrderList").append("<tr id='idOrder_" + order.idOrder + "'>" +
						            		 "<td>" + order.orderName +"</td>" +
						             		  "<td>" + order.supplier.companyName + "</td>" +
						             		  "<td>" + dateOpen + "</td>" +
						             		  "<td>" + dateClose + "</td>" +
						             		  "<td> " + dateDelivery + "</td>" +   	
											  "<td><button style='margin: 0px' type='submit' id='showDetailsComplete_" + order.idOrder + "' data-idorder='" + order.idOrder + "'> Dettagli </button>" +
											  "</td></tr>");
    		
             $("#completeOrderList").append("<tr class='detailsOrder' id='TRdetailsOrderComplete_" + order.idOrder + "'><td colspan='6' id='TDdetailsOrderComplete_" + order.idOrder + "'></td></tr>");
             
             $("#showDetailsComplete_" + order.idOrder).on("click", clickShowDetailsHandler);
             $(".detailsOrder").hide();
             
    		
    	}
    	
    	$("button").button();
        $("#completeOrderList").show("slow");
        $("#completeOrderList").fadeIn(1000);
        $("#errorDivCompleteOrder").hide();
    	
    } else {
    	
    	 $("#completeOrderList").show();
         $("#errorDivCompleteOrder").hide();
         $("#legendErrorCompleteOrder").html("Comunicazione");
         $("#errorsCompleteOrder").html("Non ci sono Ordini Completati da visualizzare<br /><br />");
         $("#errorDivCompleteOrder").show("slow");
         $("#errorDivCompleteOrder").fadeIn(1000);
    	
    }
	
}

function clickOrderOldHandler(event) {
    
	 event.preventDefault();
	    
    var dateDeliveryType = $("#toSetShipDate").val();

    var maxDate = $('#maxDate2').datepicker("getDate");
    maxDate.setHours(23);
    maxDate.setMinutes(59);
    maxDate.setSeconds(59);
    maxDate.setMilliseconds(999);
    var maxDateTime = maxDate.getTime();
    
    if(maxDateTime == null){
        $( "#dialog" ).dialog('open');
    } else {
        
        $.post("ajax/getOldOrderResp", {end: maxDateTime, dateDeliveryType: dateDeliveryType}, postOldOrderListHandler);
    } 
}

function postOldOrderListHandler(orderList) {
    
    $("#oldOrderList").html("");
    $("#oldOrderList").hide();
           
    if(orderList.length > 0){
        $("#oldOrderList").append("  <tr>  <th class='top' width='10%'> Nome </th>" +
                                          "<th class='top' width='25%'> Fornitore </th>" +
                                          "<th class='top' width='15%'> Data Inizio  </th>" +
                                          "<th class='top' width='15%'> Data Chiusura  </th>" +
                                          "<th class='top' width='15%'> Data Consegna  </th>" +
                                          "<th class='top' width='20%'> Azione  </th> </tr>");
        
        for(var i = 0; i < orderList.length; i++){
        	
            var order = orderList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateClose));
            var dateDelivery = 0;
            
            if(order.dateDelivery == "null") {
                dateDelivery = "Non Impostata";
            } else {
                dateDelivery = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateDelivery)); 
            }
            
            $("#oldOrderList").append("<tr id='idOrder_" + order.idOrder + "'>" +
					            		"<td>" + order.orderName +"</td>" +
					            		  "<td>" + order.supplier.companyName + "</td>" +
					            		  "<td>" + dateOpen + "</td>" +
					            		  "<td>" + dateClose + "</td>" +
					            		  "<td>" + dateDelivery + "</td>" +   	
										  "<td><button style='margin: 0px' type='submit' id='showDetails_" + order.idOrder + "' data-idorder='" + order.idOrder + "'> Dettagli </button>" +
										  "</td>" +
					            		"</tr>");
            
            $("#oldOrderList").append("<tr class='detailsOrder' id='TRdetailsOrderOld_" + order.idOrder + "'><td colspan='6' id='TDdetailsOrderOld_" + order.idOrder + "'></td></tr>");
                
            $("#showDetails_" + order.idOrder).on("click", clickShowDetailsHandler);
            
            $(".detailsOrder").hide();
            
        }
        $("button").button();
        $("#oldOrderList").show("slow");
        $("#oldOrderList").fadeIn(1000);
        $("#errorDivOldOrder").hide();
    } 
    else 
    {
        
        $("#oldOrderList").hide();
        $("#errorDivOldOrder").hide();
        $("#legendErrorOldOrder").html("Comunicazione");
        $("#errorsOldOrder").html("Non ci sono Ordini da visualizzare<br /><br />");
        $("#errorDivOldOrder").show("slow");
        $("#errorDivOldOrder").fadeIn(1000);
    
    }
    
}

function getSelectedTabIndex() { 
    return  $('#tabsOrder').tabs('option', 'selected');
}

function writeStatPageSupplier() {
  $("#bodyTitleHeader").html("Statistiche fornitore");
	$(".centrale").html("<div id='tabs'><ul>" +
			"<li><a href='#tabs-1'>Incasso</a></li>" +
			"<li><a href='#tabs-2'>Prodotti</a></li>" +
			"<li><a href='#tabs-3'>Ordini</a></li>" +
			"</ul>" +
		    "<div id='tabs-1'></div>" +
		    "<div id='tabs-2'></div>" +
		    "<div id='tabs-3'></div>" +
		    "</div>");
	
	var selectString1 = "<select name='yearS1' id='yearS1' class='field' onchange='refreshStatIncasso()'>";
	for(var thisYear = new Date().getFullYear(); thisYear > 1990; thisYear--)
		selectString1 += "<option value='" + thisYear + "'>" + thisYear + "</option>";
	selectString1 += "</select>";
	
	var selectString2 = "<select name='yearS2' id='yearS2' class='field' onchange='refreshStatOrdiniMese()'>";
	for(var thisYear = new Date().getFullYear(); thisYear > 1990; thisYear--)
		selectString2 += "<option value='" + thisYear + "'>" + thisYear + "</option>";
	selectString2 += "</select>";
	
	var selectString3 = "<select name='yearS3' id='yearS3' class='field' onchange='refreshStatOrdiniAnno()'>";
	for(var thisYear = new Date().getFullYear(); thisYear > 1990; thisYear--)
		selectString3 += "<option value='" + thisYear + "'>" + thisYear + "</option>";
	selectString3 += "</select>";
	
	  $('#tabs-1').html("<table id='canvSupplier-1'>" +
	  		"<tr><th> Seleziona l'anno " + selectString1 +"</th></tr>" +
	  		"<tr><td id='tdIncassoMensile'><canvas id='canvasIncassoMensile' width='580' height='400'></canvas></td></tr>" +
	  		"<tr><th></th></tr>" +
	  		"<tr><td id='tdIncassoProdotto'><canvas id='canvasIncassoProdotto' width='580' height='500'></canvas></td></tr><table>");
	  $('#tabs-2').html("<table id='canvSupplier-2'>" +
		  		"<tr><th></th></tr>" +
		  		"<tr><td id='tdProdottiListino'><canvas id='canvasProdottiListino' width='580' height='400'></canvas></td></tr><table>");
	  $('#tabs-3').html("<table id='canvSupplier-3'>" +
		  		"<tr><th> Seleziona l'anno " + selectString2 + "</th></tr>" +
		  		"<tr><td id='tdOrdiniMese'><canvas id='canvasOrdiniMese' width='580' height='400'></canvas></td></tr>" +
		  		"<tr><th> Selezione l'anno " + selectString3 +"</th></tr>" +
		  		"<tr><td id='tdOrdiniAnno'><canvas id='canvasOrdiniAnno' width='580' height='500'></canvas></td></tr><table>");
	  $('#tabs').tabs();
	  
	  writeStatistics();
}

function writeStatistics() {
	 
	$('#canvSupplier-1').hide();
	$('#canvSupplier-2').hide();
	$('#canvSupplier-3').hide();
	
	var year1 = $("#yearS1").val();
	var year2 = $("#yearS2").val();
	var year3 = $("#yearS3").val();
	
	$.post("ajax/statSupplierMoneyMonth", {year: year1}, postStatSupplierMoneyMonthHandler);
	$.post("ajax/statSupplierMoneyProduct", null, postStatSupplierMoneyProductHandler);
	$.post("ajax/statSupplierProductList", null, postStatSupplierProductListHandler);
	$.post("ajax/statSupplierOrderMonth", {year: year2}, postStatSupplierOrderMonthHandler);
	$.post("ajax/statSupplierOrderYear", {year: year3}, postStatSupplierOrderYearHandler);
	
	
	$('#canvSupplier-1').show('slow');
	$('#canvSupplier-2').show('slow');
	$('#canvSupplier-3').show('slow');
}

function refreshAllStat() {
	
	refreshStatIncasso();
	refreshStatOrdiniMese();
	refreshStatOrdiniAnno();
	refreshStatIncassoProdotto();
	refreshStatProdottiListino();
	
}

function refreshStatIncasso() {
	
	var year1 = $("#yearS1").val();
	
	$("#tdIncassoMensile").hide("slow");
	$("#tdIncassoMensile").html("<canvas id='canvasIncassoMensile' width='580' height='400'></canvas>");
	
	$.post("ajax/statSupplierMoneyMonth", {year: year1}, postStatSupplierMoneyMonthHandler);
	
	$("#tdIncassoMensile").show("slow");
}

function refreshStatOrdiniMese() {
	
	var year2 = $("#yearS2").val();
	
	$("#tdOrdiniMese").hide("slow");
	$("#tdOrdiniMese").html("<canvas id='canvasOrdiniMese' width='580' height='400'></canvas>");
	
	$.post("ajax/statSupplierOrderMonth", {year: year2}, postStatSupplierOrderMonthHandler);
	
	$("#tdOrdiniMese").show("slow");
}

function refreshStatOrdiniAnno() {
	
	var year3 = $("#yearS3").val();
	
	$("#tdOrdiniAnno").hide("slow");
	$("#tdOrdiniAnno").html("<canvas id='canvasOrdiniAnno' width='580' height='400'></canvas>");
	
	$.post("ajax/statSupplierOrderYear", {year: year3}, postStatSupplierOrderYearHandler);
	
	$("#tdOrdiniAnno").show("slow");
}

function refreshStatIncassoProdotto() {
	
	$("#tdIncassoProdotto").hide("slow");
	$("#tdIncassoProdotto").html("<canvas id='canvasIncassoProdotto' width='580' height='400'></canvas>");
	
	$.post("ajax/statSupplierMoneyProduct", null, postStatSupplierMoneyProductHandler);
	
	$("#tdIncassoProdotto").show("slow");
}

function refreshStatProdottiListino() {
	
	
	$("#tdProdottiListino").hide("slow");
	$("#tdProdottiListino").html("<canvas id='canvasProdottiListino' width='580' height='400'></canvas>");
	
	$.post("ajax/statSupplierProductList", null, postStatSupplierProductListHandler);
	
	$("#tdProdottiListino").show("slow");
}

function postStatSupplierMoneyMonthHandler(data){
	
	new CanvasXpress("canvasIncassoMensile", {
        "y": {
          "vars": [
            "Incasso"
          ],
          "smps": [
			"Gennaio",
			"Febbraio",
			"Marzo",
			"Aprile",
			"Maggio",
			"Giugno",
			"Luglio",
			"Agosto",
			"Settembre",
			"Ottobre",
			"Novembre",
			"Dicembre"
          ],
          "desc": [
            "Incasso"
          ],
          "data": [
            [
              data[0],
              data[1],
              data[2],
              data[3],
              data[4],
              data[5],
              data[6],
              data[7],
              data[8],
              data[9],
              data[10],
              data[11]
            ]
          ]
        }
      }, {
        "graphType": "Bar",
        "colorBy": "Response",
        "title": "Incasso Mensile",
        "smpTitle": "Mesi Dell'anno",
        "colorScheme": "basic",
        "graphOrientation": "vertical",
        "showLegend": false
      });
	
}

function postStatSupplierMoneyProductHandler(data) {
	
	var json = '{ \"y\": { \"vars\": [ \"Incasso" ], \"smps\": [ ';
	var len = data.length/2;
	var i = 0;
	var json2 = "";
	
	if(data[0] != "errNoProduct") {
		
		for(i = 0; i < len; i++) {
			if (i == len-1)
				json += '"' + data[i] + '" ';
			else
				json += '"' + data[i] + '", ';
		}
			
			
		json += '], \"desc\": [ \"Incasso\" ], \"data\": [ [ ';
		
		for(i = len; i < data.length; i++) {
			if (i == data.length-1)
				json += '\"' + data[i] + '\" ';
			else
				json += '\"' + data[i] + '\", ';
		}
		
		json += '] ] } }';
	    
		json2 = '{ \"graphType\": \"Bar\",' + 
	    			'\"colorBy\": \"Response\", ' +
	    			'\"title\": \"Incasso Totale per Prodotto\", ' +
	    			'\"smpTitle\": \"Prodotti\", ' +
	    			'\"colorScheme\": \"basic\", ' +
	    			'\"graphOrientation\": \"horizontal\", ' +
	    			'\"showLegend\": false }';
	} else {
		
		json += '""';		
			
		json += '], \"desc\": [ \"Incasso\" ], \"data\": [ [ ';
		
		json += '\"0\" ';

		json += '] ] } }';
	    
		json2 = '{ \"graphType\": \"Bar\",' + 
	    			'\"colorBy\": \"Response\", ' +
	    			'\"title\": \"Incasso Totale per Prodotto\", ' +
	    			'\"subtitle\": \"Non ci sono prodotti da visualizzare\", ' +
	    			'\"smpTitle\": \"Prodotti\", ' +
	    			'\"colorScheme\": \"basic\", ' +
	    			'\"graphOrientation\": \"horizontal\", ' +
	    			'\"showLegend\": false }';
		
	}
	
    
    var obj = jQuery.parseJSON(json);
    var obj2 = jQuery.parseJSON(json2);
    
    new CanvasXpress("canvasIncassoProdotto", obj, obj2);

}

function postStatSupplierProductListHandler(data){
	
	new CanvasXpress("canvasProdottiListino", {
        "y": {
          "vars": [
            "In Listino",
            "Non Disponibili",
          ],
          "smps": [
            "Tipologia Prodotti"
          ],
          "data": [
            [
              data[0]
            ],
            [
              data[1]
            ]
          ]
        }
      }, {
        "graphType": "Pie",
        "title": "Prodotti in Listino/Non in Listino",
        "pieSegmentPrecision": 1,
        "pieSegmentSeparation": 2,
        "pieSegmentLabels": "outside",
        "pieType": "solid",
        "colors": [
                   "rgb(57,133,0)",
                   "rgb(163,0,8)"
                   ],
      });
	
}


function postStatSupplierOrderMonthHandler(data){
	
	new CanvasXpress("canvasOrdiniMese", {
        "y": {
          "vars": [
            "Conclusi",
            "Falliti"
          ],
          "smps": [
			"Gennaio",
			"Febbraio",
			"Marzo",
			"Aprile",
			"Maggio",
			"Giugno",
			"Luglio",
			"Agosto",
			"Settembre",
			"Ottobre",
			"Novembre",
			"Dicembre"
          ],
          "desc": [
            "Numero Ordini"
          ],
          "data": [
            [
              data[0],
              data[1],
              data[2],
              data[3],
              data[4],
              data[5],
              data[6],
              data[7],
              data[8],
              data[9],
              data[10],
              data[11]
            ],
            [
              data[12],
              data[13],
              data[14],
              data[15],
              data[16],
              data[17],
              data[18],
              data[19],
              data[20],
              data[21],
              data[22],
              data[23]
            ]
          ]
        }
      }, {
        "graphType": "Stacked",
        "graphOrientation": "vertical",
        "title": "Distribuzione Ordini Conclusi/Falliti Per Mese",
        "colorScheme": "basic",
        "legendBackgroundColor": false,
        "fontSize": 10,
        "maxTextSize": 12,
        "blockSeparationFactor": 2,
        "colors": [
                   "rgb(57,133,0)",
                   "rgb(163,0,8)"
                   ],
      });
	
}

function postStatSupplierOrderYearHandler(data){
	
	new CanvasXpress("canvasOrdiniAnno", {
        "y": {
          "vars": [
            "Conclusi",
            "Falliti",
          ],
          "smps": [
            "Tipologia Utenti"
          ],
          "data": [
            [
              data[0]
            ],
            [
              data[1]
            ]
          ]
        }
      }, {
        "graphType": "Pie",
        "title": "Prodotti Conclusi/Falliti per Anno",
        "subtitle": "Se non compare il grafico non ci sono ordini da visualizzare",
        "pieSegmentPrecision": 1,
        "pieSegmentSeparation": 2,
        "pieSegmentLabels": "outside",
        "pieType": "solid",
        "colors": [
                   "rgb(57,133,0)",
                   "rgb(163,0,8)"
                   ],
      });
}

function getCategoryDescription(productCategoriesList, idProductCategory)
{
  if (productCategoriesList == undefined || idProductCategory == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return "";
  }
  for ( var categoryIndex in productCategoriesList)
  {
    if (productCategoriesList[categoryIndex].idProductCategory == idProductCategory)
      return productCategoriesList[categoryIndex].description;
  }
  return "";
}

function newProduct(productParameters)
{
  if (productParameters == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var returnedIdProduct = -1;
  if(!!window.FormData && productParameters.picture !== ""){
	  var formData = new FormData();
	  formData.append("picture", document.getElementById("picture").files[0]);
	  for(var prop in productParameters){
		  if(prop != "picture")
			  formData.append(prop, productParameters[prop]);
	  }
	  $.postSyncFormData("ajax/newproductwithpicture", formData, function(idProduct){
		  returnedIdProduct = idProduct;
		  if (idProduct > 0)
			  console.debug("Inserted product: " + idProduct);
		  $("#picture").val("");
	  });
  }
  else{
	  $.postSync("ajax/newproduct", productParameters, function(idProduct)
	  {
	    returnedIdProduct = idProduct;
	    if (idProduct > 0)
	      console.debug("Inserted product: " + idProduct);
	  });
  }
  return returnedIdProduct;
}

//function getAllProducts()
//{
//  $.getJSONsync("ajax/getproducts", function(productsList)
//  {
//    window.localStorage.setItem('productsList', JSON.stringify(productsList));
//    console.debug("productsList saved in localstorage");
//  });
//}

function getMyProductsNoLocal()
{
  var myProductsList;
  $.getJSONsync("ajax/getmyproducts", function(productsList)
  {
    myProductsList = productsList;
  });
  return myProductsList;
}

function writeIndexPage()
{
    writeSupplierPage(0);
}

function writeSupplierPage(tab)
{
  $("#bodyTitleHeader").html("Gestione prodotti");
  $(".centrale").html(
      "<div id='tabs'><ul><li><a href='#tabs-1'>Aggiungi prodotto</a></li>"
          + "<li><a href='#tabs-2'>Gestione listino</a></li></ul>"
          + "<div id='tabs-1'></div><div id='tabs-2'></div>");

  var newProductForm = "<div class='creazioneProdottoForm' style='margin: 2em 0 0 65px;'>"
      + "<form id='newProdForm' action='' method='post'>"
      + "<fieldset id='productFieldset'><legend>&nbsp;Dati per la creazione prodotto&nbsp;</legend>"
      + "<br />";
  
  if(!!window.FormData)
      newProductForm +=	"<label for='picture' class='left'>Immagine: </label>"	  
      + "<input type='file' name='picture' id='picture' class='field'/>";
	  
  newProductForm += "<label for='productName' class='left'>Nome: </label>"
      + "<input type='text' name='productName' id='productName' class='field'"
      + "required='required' /><br><label for='productDescription' class='left'>"
      + "Descrizione: </label><input type='text' name='productDescription' id='productDescription'"
      + "class='field' required='required' />"
      + "<br><label for='productDimension' class='left'>Dimensione: </label>"
      + "<input type='text' name='productDimension' id='productDimension'"
      + "class='field' required='required' />"
      + "<br><label for='measure_unit' class='left'>Unità di misura: </label>"
      + "<input type='text' name='measure_unit' id='measure_unit'"
      + "class='field' required='required' />"
      + "<br><label for='unit_block' class='left'>Unità per blocco: </label>"
      + "<input type='text' name='unit_block' id='unit_block'"
      + "class='field' required='required' />"
      + "<br><label for='transport_cost' class='left'>Costo trasporto: </label>"
      + "<input type='text' name='transport_cost' id='transport_cost'"
      + "class='field' required='required' />"
      + "<br><label for='unit_cost' class='left'>Costo unit&agrave;: </label>"
      + "<input type='text' name='unit_cost' id='unit_cost'"
      + "class='field' required='required' />"
      + "<br><label for='min_buy' class='left'>Minimo blocchi acquistabili: </label>"
      + "<input type='text' name='min_buy' id='min_buy' class='field' />"
      + "<br><label for='max_buy' class='left'>Massimo blocchi acquistabili: </label>"
      + "<input type='text' name='max_buy' id='max_buy'"
      + "class='field' />"
      + "<br><br><label for='productCategory' class='left'>Categoria: </label>"
      + "<select name='productCategory' id='productCategory' class='field' onchange='checkCategorySelect()'>"
      + "</select><br /><br /><br /><br /><br /><br /><br />"
      + "<fieldset id='categoryFieldset' style='padding: 5px'><legend>&nbsp;Inserisci nuova categoria&nbsp;</legend><br />"
      + "<br><label for='categoryDescription' class='left'>Descrizione: </label>"
      + "<input type='text' name='categoryDescription' id='categoryDescription' class='field' />"
      + "</fieldset>"
      + "<button type='submit' id='newProductSubmit'> Crea prodotto </button>"
      + "</fieldset>"
      + "<div id='errorDiv' style='display: none;'>"
      + "<fieldset><legend id='legendError'>&nbsp;Errore&nbsp;</legend><br />"
      + "<div id='errors' style='padding-left: 40px'>"
      + "</div></fieldset></div><p>"
      + "</p></form></div>";
  
  $('#tabs-1')
      .html(newProductForm);
  
  var categoriesList = getCategoriesNoLocal();
  var categoriesString = "<option value='notSelected' selected='selected'>Seleziona..</option>";
  for ( var catIndex in categoriesList)
  {
    categoriesString += "<option value='"
        + categoriesList[catIndex].idProductCategory + "'>"
        + categoriesList[catIndex].description + "</option>";
  }
  categoriesString += "<option value='nuova'>Nuova categoria...</option>";

  categoriesList = getMyCategoriesNoLocal();
  var categoriesForListino = "<option value='notSelected' selected='selected'>Tutte</option>";
  for ( var catIndex in categoriesList)
  {
    categoriesForListino += "<option value='"
        + categoriesList[catIndex].idProductCategory + "'>"
        + categoriesList[catIndex].description + "</option>";
  }
  
  $('#productCategory').html(categoriesString);

  // disabilitare fieldset category
  $('#categoryFieldset').hide();
  $('#categoryFieldset').children().attr("disabled", "disabled");

  $('#newProductSubmit').on("click", clickNewProductHandler);

  var myProducts = getMyProductsNoLocal();
  var initialNumberOfPages = myProducts.length / 10;
  if (myProducts.length % 10 > 0)
    initialNumberOfPages += 1;

  var pagesForListino = "";
  for ( var page = 1; page <= initialNumberOfPages; page++)
    pagesForListino += "<option value='" + page + "'>" + page + "</option>";

  $("#picture").filestyle({ 
	     image: "img/browse.png",
	     imageheight : 22,
	     imagewidth : 34,
	     width : 136
	 });
  $("button").button();

  $('#tabs-2')
      .html(
          "<div class='listinoform'>"
              + "<form method='post' action=''>"
              + "<fieldset><legend>&nbsp;Opzioni di Ricerca:&nbsp;</legend><br />"
              + "<label for='productCategorySearch' class='left'>Categoria: </label>"
              + "<select name='productCategorySearch' id='productCategorySearch' class='field'>"
              + categoriesForListino
              + "</select>"
              + "<label for='pageSearch' class='left'>&nbsp;&nbsp;&nbsp;Pagina: </label>"
              + "<select name='pageSearch' id='pageSearch' class='field'>"
              + pagesForListino
              + "</select>"
              + "<label for='itemsPerPageSearch' class='left'>&nbsp;&nbsp;&nbsp;Risultati Per Pagina: </label>"
              + "<select name='itemsPerPageSearch' id='itemsPerPageSearch' class='field'>"
              + "<option value='10'> 10 </option>"
              + "<option value='25'> 25 </option>"
              + "<option value='50'> 50 </option>"
              + "</select>"
              + "</fieldset>"
              // + "<p><input type='submit' class='button' value='Visualizza'
              // id='productListRequest' /></p>"
              + "</form>"
              + "<table id='productsListTable' class='list'></table>"
              + "<div id='errorDiv2' style='display:none;'>"
              + "<fieldset><legend id='legendError2'>&nbsp;Errore&nbsp;</legend><br />"
              + "<div id='errors2' style='padding-left: 40px'>"
              + "</div>"
              + "</fieldset>" + "</div><br />" + "</div>");

  $('#productCategorySearch').change(newProductSearch);
  $('#pageSearch').change(newProductSearch);
  $('#itemsPerPageSearch').change(newProductSearch);
  prepareProductsForm(tab);
  newProductSearch(tab);
  // $('#productListRequest').on("click", clickNewProductSearchHandler);
}

function checkCategorySelect()
{
  var selected = $('#productCategory').val();
  $("#errorDiv").hide('slow');

  if (selected == 'nuova')
  {
    $('#categoryFieldset').show('slow');
    $('#categoryFieldset').children().attr("disabled", false);
  }
  else
  {
    // Nuova categoria non selezionata
    $('#categoryFieldset').hide('slow');
    $('#categoryFieldset').children().attr("disabled", true);
  }
}

function prepareProductsForm(tab)
{
  $('#tabs').tabs({
    selected : tab
  });
  $('input:submit').button();
}

function newProductSearch(tab)
{
  var productCategory = $("#productCategorySearch").val();
  var page = $("#pageSearch").val();
  var itemsPerPage = $("#itemsPerPageSearch").val();
  var myProductsUnfiltered = getMyProductsNoLocal();
  var myProducts = filterProducts(myProductsUnfiltered, productCategory);
  if(myProducts.length < page * itemsPerPage + 1)
  {
    page = Math.ceil(myProducts.length / itemsPerPage);
    var pagesForListino = "";
    for ( var i = 1; i <= page; i++)
      pagesForListino += "<option value='" + i + "'>" + i + "</option>";
  }
  else
  {
    var p = Math.ceil(myProducts.length / itemsPerPage);
    var pagesForListino = "";
    for ( var i = 1; i <= p; i++)
      pagesForListino += "<option value='" + i + "'>" + i + "</option>";
  }
  $("#pageSearch").html(pagesForListino);
  $("#pageSearch").val(page);

  var productsString = "<tr><th class='top' width='15%'></th>"
    + "<th class='top' width='15%'> Nome </th>"
    + "<th class='top' width='25%'> Descrizione </th>"
    + "<th class='top' width='25%'> Disponibilit&agrave; </th>"
    + "<th class='top' width='20%'> Modifica disponibilit&agrave; </th></tr>";
  if (myProducts.length > 0)
  {
    for ( var prodIndex = (page - 1) * itemsPerPage; prodIndex < myProducts.length
        && prodIndex < (page * itemsPerPage); prodIndex++)
    {
      if (productCategory != "notSelected"
          && productCategory != myProducts[prodIndex].category.idProductCategory
          || myProducts[prodIndex] == undefined)
        continue;
      productsString += "<tr id='listRow" + myProducts[prodIndex].idProduct
          + "'><td><img src='" + myProducts[prodIndex].imgPath
          + "' height='60' class='thumb'></td><td>" + myProducts[prodIndex].name + "</td>" + "<td>"
          + myProducts[prodIndex].description + "</td>";
      if (myProducts[prodIndex].availability == 0)
      {
        productsString += "<td id='listHead" + myProducts[prodIndex].idProduct
            + "' class='no'>Non in listino</td>";
        productsString += "<td id='listCont" + myProducts[prodIndex].idProduct
            + "'><form id='prodAval"+myProducts[prodIndex].idProduct+"' data-idproduct='" + myProducts[prodIndex].idProduct
            + "' action=''>";
        productsString += "<input type='submit' class='button' value='Inserisci in listino' />";
        productsString += "</form></td>";
        /*
        productsString += "<td id='listUpd"
            + myProducts[prodIndex].idProduct + "'><form id='prodUpd' name='"
            + myProducts[prodIndex].idProduct + "' action=''>";
        productsString += "<input type='submit' class='button' value='Modifica' />";
        productsString += "</form></td><td id='listDel"
            + myProducts[prodIndex].idProduct + "'><form id='prodDel' name='"
            + myProducts[prodIndex].idProduct + "' action=''>";
        productsString += "<input type='submit' class='button' value='Cancella' />";
        productsString += "</form></td>";
        */
      }
      else
      {
        productsString += "<td id='listHead" + myProducts[prodIndex].idProduct
            + "' class='yes'>In listino</td>";
        productsString += "<td id='listCont" + myProducts[prodIndex].idProduct
            + "'><form id='prodNotAval"+myProducts[prodIndex].idProduct+"' data-idproduct='"
            + myProducts[prodIndex].idProduct + "' action=''>";
        productsString += "<input type='submit' class='button' value='Rimuovi da listino' />";
        productsString += "</form></td>";
        /*
        productsString += "<td id='listUpd"
            + myProducts[prodIndex].idProduct + "'><form id='prodUpd' name='"
            + myProducts[prodIndex].idProduct + "' action=''>";
        productsString += "<input type='submit' class='button' value='Modifica' />";
        productsString += "</form></td><td id='listDel"
            + myProducts[prodIndex].idProduct + "'><form id='prodDel' name='"
            + myProducts[prodIndex].idProduct + "' action=''>";
        productsString += "<input type='submit' class='button' value='Cancella' />";
        productsString += "</form></td>";
        */
      }
      /*
      productsString += "</tr><tr class='rowUpdClass' id='rowUpd"
          + myProducts[prodIndex].idProduct + "'><td id='divUpd"
          + myProducts[prodIndex].idProduct + "' name='"
          + myProducts[prodIndex].idProduct + "' colspan='6'></td></tr>";
      */
    }
    $('#productsListTable').html(productsString).show('slow');
    $('form').filter(function()
    {
      return this.id.match(/prodAval/);
    }).bind('submit', setProductAvailableHandler);
    $('form').filter(function()
    {
      return this.id.match(/prodNotAval/);
    }).bind('submit', setProductUnavailableHandler);
    /*
    $('form').filter(function()
    {
      return this.id.match(/prodDel/);
    }).bind('submit', deleteProductHandler);
    $('form').filter(function()
    {
      return this.id.match(/prodUpd/);
    }).bind('submit', updateProductHandler);
    $('.rowUpdClass').hide();
    */
    prepareProductsForm(tab);
  }
}

function clickNewProductSearchHandler(event)
{
  event.preventDefault();
  newProductSearch();
}

function clickNewProductHandler(event)
{
  event.preventDefault();

  var errors = new Array();

  var picture = "";
  if(!!window.FormData)
	  picture = $('#picture').val();
  
  var productName = $('#productName').val();
  var productDescription = $('#productDescription').val();
  var productDimension = $('#productDimension').val();
  var measureUnit = $('#measure_unit').val();
  var unitBlock = $('#unit_block').val();
  var transportCost = $('#transport_cost').val();
  var unitCost = $('#unit_cost').val();
  var minBuy = $('#min_buy').val();
  var maxBuy = $('#max_buy').val();
  var productCategory = $('#productCategory').val();
  var categoryDescription = $('#categoryDescription').val();

  if (productName == "" || isNumber(productName))
  {
    errors.push("Nome prodotto: Formato non valido");
  }
  if (productDescription == "")
  {
    errors.push("Descrizione prodotto: Formato non valido");
  }
  if (productDimension == "" || !isPositiveNumber(productDimension))
  {
    errors.push("Dimensione: Formato non valido");
  }
  if (measureUnit == "" || isNumber(measureUnit))
  {
    errors.push("Unit&agrave; di misura: Formato non valido");
  }
  if (unitBlock == "" || !isPositiveNumber(unitBlock))
  {
    errors.push("Unit&agrave; per blocco: Formato non valido");
  }
  if (transportCost == "" || !isPositiveNumber(transportCost))
  {
    errors.push("Costo di trasporto: Formato non valido");
  }
  if (unitCost == "" || !isPositiveNumber(unitCost))
  {
    errors.push("Costo per unit&agrave;: Formato non valido");
  }
  if (minBuy != "" && !isPositiveNumber(minBuy))
  {
    errors.push("Minimo unit&agrave; acquistabili: Formato non valido");
  }
  if (maxBuy != "" && !isPositiveNumber(maxBuy))
  {
    errors.push("Massimo unit&agrave; acquistabili: Formato non valido");
  }
  if (minBuy != "" && maxBuy != "" && parseInt(minBuy) > parseInt(maxBuy))
  {
    errors.push("Massimo/minimo unit&agrave; acquistabili:"
        + "Il minimo di unit&agrave; acquistabili deve essere minore"
        + " o uguale al massimo unit&agrave; acquistabili");
  }
  if (!isPositiveNumber(productCategory))
  {
    if (categoryDescription == "" || isNumber(categoryDescription))
      errors.push("Categoria di prodotto: Formato non valido");
  }

  if (errors.length > 0)
  {
    $("#errors").html("");
    $("#errorDiv").hide();

    for ( var i = 0; i < errors.length; i++)
    {
      var error = errors[i].split(":");
      $("#errors").append(
          "<strong>" + error[0] + "</strong>: " + error[1] + "<br />");
    }

    $("#errorDiv").show("slow");
    // $("#errorDiv").fadeIn(1000);
  }
  else
  {
    // Creazione nuova categoria
    if (!isPositiveNumber(productCategory))
    {
      var idProductCategory = newCategory(categoryDescription);
      if (idProductCategory > 0)
      {
        productCategory = idProductCategory;
        $("#categoryDescription").val("");
        var categoriesList = getCategoriesNoLocal();
        var categoriesString = "<option value='notSelected' selected='selected'>Seleziona...</option>";
        for ( var catIndex in categoriesList)
        {
          categoriesString += "<option value='"
              + categoriesList[catIndex].idProductCategory + "'>"
              + categoriesList[catIndex].description + "</option>";
        }
        $('#productCategorySearch').html(categoriesString);
        $('#productCategory').html(categoriesString);
      }
      else
      {
        $("#dialog-error-insert").dialog({
          resizable : false,
          height : 140,
          modal : true,
          buttons : {
            "Ok" : function()
            {
              $(this).dialog('close');
            }
          }
        });
        return;
      }

    }

    // Creazione nuovo prodotto
    var idProduct = newProduct({
      picture : picture,
      productName : productName,
      productDescription : productDescription,
      productDimension : productDimension,
      measureUnit : measureUnit,
      unitBlock : unitBlock,
      transportCost : transportCost,
      unitCost : unitCost,
      minBuy : minBuy,
      maxBuy : maxBuy,
      productCategory : productCategory,
    });
    if (idProduct > 0)
    {
      newProductSearch(0);
      $("#dialog-ok").dialog({
        resizable : false,
        height : 140,
        modal : true,
        buttons : {
          "Ok" : function()
          {
            $(this).dialog('close');
          }
        }
      });
      $("#productFieldset").children("input").val("");
    }
    else
    {
      $("#dialog-error-insert").dialog({
        resizable : false,
        height : 140,
        modal : true,
        buttons : {
          "Ok" : function()
          {
            $(this).dialog('close');
          }
        }
      });
    }
  }
}
