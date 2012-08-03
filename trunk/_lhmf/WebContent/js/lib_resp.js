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
                                "<form method='post' action='order'>" +
                                  "<fieldset><legend>&nbsp;Selezione del Fornitore:&nbsp;</legend><br />" +
	                                  "<label for='memberSupplier' class='left'>Seleziona Fornitore: </label>" +
	                                  "<select name='memberSupplier' id='memberSupplier' class='field' style='width: 400px'></select>" +
	                                  "<button type='submit' id='productListRequest'> Carica i Prodotti </button>" +
                                  "</fieldset>" +
                                  "<fieldset id='orderCompositor'><legend>&nbsp;Composizione Nuovo Ordine:&nbsp;</legend><br />" +
	                              "<div id='productsList'>" +
		                          	"<h1 class='ui-widget-header'>Prodotti</h1>" +
		                          	"<div id='catalog'></div>" +
		                          "</div>" +
		                          "<div id='orderCart'>" +
	                              	"<h1 class='ui-widget-header'>Ordine</h1>" +
	                              	"<div class='ui-widget-content'>" +
	                              		"<ul id='products' class='list clearfix'>" +
	                              			"<div align='center' class='placeholder'><br />Trascina qui i prodotti da includere nell'ordine<br /><br /></div>" +
	                              		"</ul>" +
	                              	"</div>" +
	                              	"<br />" +
	                               "<fieldset><legend>&nbsp;Opzioni Ordine:&nbsp;</legend><br />" +
	                               	  "<label for='orderName' class='left'>Nome Ordine: </label>" +
	                                  "<input type='text' name='orderName' id='orderName' class='field' style='width: 145px'/>" +
	                                  "<label for='closeData' class='left'>Data Chiusura: </label>" +
	                                  "<input type='text' name='closeData' id='closeData' class='field' style='width: 145px'/>" +
                                   "</fieldset>" +
	                              "</div>" +
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
    
    $('#orderActiveRequest').on("click", clickOrderActiveHandler);
    $('#orderOldRequest').on("click", clickOrderOldHandler);
    $('#orderShipRequest').on("click", clickOrderShipHandler);
    
    //Drag and drop
    loadSupplier();
    $("#orderCompositor").hide();
    $("#closeData").datepicker();
    
    $('#productListRequest').on("click", clickProductListRequest);
    $('#orderRequest').on("click", clickOrderHandler);
    
    $("button").button();
}

var addedIds = [];
var idSupplier = 0;

function clickProductListRequest(event) {
	event.preventDefault();
	
	$("#orderCompositor").hide();
	$("#productsList").html("<h1 class='ui-widget-header'>Prodotti</h1>" +
          					"<div id='catalog'></div>");
	
	idSupplier = $('#memberSupplier').val();
	
	if(idSupplier == -1) {
		$("#errorDivOrder").hide();
        $("#legendErrorOrder").html("Comunicazione");
        $("#errorsOrder").html("Non hai selezionato il Fornitore<br /><br />");
        $("#errorDivOrder").show("slow");
        $("#errorDivOrder").fadeIn(1000);
		
	} else {
		$.post("ajax/getProductFromSupplier", {idSupplier: idSupplier}, postProductListRequest);
	} 
}

function postProductListRequest(productList) {

	$("#errorDivOrder").hide();
	
	if(productList.length <= 1) {
		
        $("#errorDivOrder").hide();
        $("#legendErrorOrder").html("Comunicazione");
        $("#errorsOrder").html("Non ci sono prodotti disponibili da visualizzare<br /><br />");
        $("#errorDivOrder").show("slow");
        $("#errorDivOrder").fadeIn(1000);
		
	} else {
		
		console.log("Lista prodotti ricevuta");
		var category = 0;
		var ncategory = 0;
		var divToWork = 0;
		
		for(var i = 0; i < productList.length; i++) {
            
			var product = productList[i];
			if(product.availability != true) 
				continue;
							
			if(category != product.category.idProductCategory) {
				//Nuova categoria, creare nuovo accordion
				category = product.category.idProductCategory;
				$("#catalog").append("<h3><a href='#'>" + product.category.description + "</a></h3>");
				$("#catalog").append("<div><ul id='products' class='list clearfix'></ul></div>");
				
				var divToWork = $("#catalog div ul")[ncategory];
				ncategory++;
			}
			            
			$(divToWork).append("<li class='clearfix' data-productid='" + product.idProduct + "'>" +
								   "<section class='left'>" +
								       "<img src='" + product.imgPath + "' height='60' class='thumb'>" +
								       "<h3>" + product.name + "</h3>" +
								       "<span class='meta'>" + product.description + "</span>" +
								   "</section>" +
								   "<section class='right'>" +
										"<span class='price'>&euro;" + product.unitCost + "</span>" +
										"<span class='darkview'>" +
											"Blocchi: " + product.unitBlock + " | (" + product.measureUnit + ")<br />" +
											"Pezzatura: " + product.minBuy + " - " + product.maxBuy +
										"</span>" +
									"</section>" +
									"<div class='deleteButton'><a href='#'><img src='img/delete.png' class='delButton' height='15px'></a></div>" +
								  "</li>");
								
        }
		
		$( "#catalog" ).accordion({
			autoHeight: false,
			navigation: true
		});
		$( "#catalog li" ).draggable({
			appendTo: "body",
			helper: "clone",
			cursor: "move",
			start: function(event, ui) {
				$("#errorDivOrder").hide("slow");
			}
		});
		$( "#orderCart ul" ).droppable({
			activeClass: "ui-state-default",
			hoverClass: "ui-state-hover",
			accept: ":not(.ui-sortable-helper)",
			drop: function( event, ui ) {
				
				//var idProduct = $(ui.draggable).find("input:hidden").val();
				var idProduct = $(ui.draggable).data('productid');
				
				if($.inArray(idProduct, addedIds) === -1) {
		            addedIds.push(idProduct);
		            $( "#orderCart ul" ).append($(ui.draggable).clone());
		            $( "#orderCart .delButton" ).on("click", deleteProductFromOrder);
					$( "#orderCart .deleteButton" ).show();
		        } else {
					$("#errorDivOrder").hide();
			        $("#legendErrorOrder").html("Comunicazione");
			        $("#errorsOrder").html("Questo prodotto &egrave gi&agrave presente nell'ordine<br /><br />");
			        $("#errorDivOrder").show("slow");
			        $("#errorDivOrder").fadeIn(1000);
				}
			}
		});
		
		$( ".deleteButton" ).hide();
		$( ".amount" ).hide();
		$( "#orderCompositor" ).show("slow");
		
	}
}

function deleteProductFromOrder(event) {
	event.preventDefault();
	
	$(this).parents("li").remove();
	var idProduct = $(this).parents("li").data('productid');
	addedIds = jQuery.removeFromArray(idProduct, addedIds);
}

jQuery.removeFromArray = function(value, arr) {
    return jQuery.grep(arr, function(elem, index) {
        return elem !== value;
    });
};

function loadSupplier() {
	
	$.post("ajax/getMembersSupplierString", function(data) {
		
		var output = [];
		output.push('<option value="-1"> Seleziona Il Fornitore...</option>');

		$.each(data, function(index, val)
		{
			var temp = val.split(","); 
			output.push('<option value="'+ temp[0] +'">'+ temp[1] + ' | ' + temp[2] + '</option>');
		});

		$('#memberSupplier').html(output.join(''));

		
	});
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
        $("#shipOrderList").append(" <tr>  <th class='top' width='5%'> ID </th>" +
        								  "<th class='top' width='25%'> Nome </th>" +
                                          "<th class='top' width='20%'> Fornitore </th>" +
                                          "<th class='top' width='25%'> Data Consegna  </th>" +
                                          "<th class='top' width='25%'> Azione  </th> </tr>");
        
        for(var i = 0; i < orderList.length; i++){
            var order = orderList[i];
            
            var dateDelivery = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateDelivery));
            
            $("#shipOrderList").append("<tr id='idOrderShip_" + order.idOrder + "'></tr>");
            
            $("#idOrderShip_" + order.idOrder).append(
            										  "<td>" + order.idOrder +"</td>" +
            										  "<td>" + order.orderName +"</td>" +
                                              		  "<td>" + order.supplier.companyName + "</td>" +
                                              		  "<td>" + dateDelivery + "</td>" +
                                              		  "<td> <form>" +
            										  		 "<input type='hidden' value='" + order.idOrder + "'/>" +
            										  	     "<button style='margin: 0px' type='submit' id='showDetailsShip_" + order.idOrder + "'> Mostra Schede </button>" +
            										  	   "</form> </td>" );
            
            $("#shipOrderList").append("<tr class='detailsOrder' id='TRdetailsOrderShip_" + order.idOrder + "'><td colspan='5' id='TDdetailsOrderShip_" + order.idOrder + "'></td></tr>");
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
	
	var trControl = "#TRdetailsOrderShip_" + idOrder;
	var tdControl = "#TDdetailsOrderShip_" + idOrder;
    
	$(tdControl).html("<div style='margin: 5px'><table id='TABLEdetailsOrderShip_" + idOrder + "' class='log'></table></div>");

	var tableControl = "#TABLEdetailsOrderShip_" + idOrder;
	
	$(tableControl).append("<tr> <th class='top' width='5%'> Scheda </th>" +
					            "<th class='top' width='25%'> Nome </th>" +
					            "<th class='top' width='15%'> Totale (&euro;)</th>" +
					            "<th class='top' width='30%'> Stato Consegna  </th>" +
					            "<th class='top' width='25%'> Azione  </th> </tr>");
	
	$.each(data, function(index, val)
	{
		var total = 0;
		var productTable = "<table id='TABLEdetailsPurchase_" + val.idPurchase + "' class='log'>" +
							"<tr> <th class='top' width='15%'> Immagine </th>" +
								 "<th class='top' width='15%'> Prodotto </th>" +
                                 "<th class='top' width='15%'> Categoria </th>" +
                                 "<th class='top' width='30%'> Descrizione  </th>" +
                                 "<th class='top' width='5%'> Blocchi  </th>" +
                                 "<th class='top' width='5%'> Costo Unitario (&euro;) </th>" +
                                 "<th class='top' width='5%'> N. Blocchi Acquistati </th>" +
                                 "<th class='top' width='10%'> Parziale (&euro;)</th> </tr>";
		
		var products=0;
		
		$.postSync("ajax/getPurchaseProducts", {idPurchase: val.idPurchase}, function(data) { products = data; });
		
		$.each(products, function(index, val2) {
			var amount = 0;
			$.postSync("ajax/getPurchaseAmount", {idPurchase: val.idPurchase, idProduct: val2.idProduct}, function(data) { amount = data; });
			var temp = amount * (val2.unitBlock * val2.unitCost);
			total += temp;
			productTable += "<tr>   <td> <img align='middle' height='60' src='" + val2.imgPath + "'></td> " +
								   "<td>" + val2.name + "</td>" +
			                       "<td>" + val2.category.description + "</td>" +
			                       "<td>" + val2.description + "</td>" +
			                       "<td>" + val2.unitBlock + "</td>" +
			                       "<td>" + val2.unitCost + "</td>" +
			                       "<td>" + amount + "</td>" +
			                       "<td>" + temp + "</td></tr>";
		});
		
		productTable += "</table>";
    
		if(val.isShipped == "Spedizione Effettuata") {
			
			$(tableControl).append("<tr style='background-color: #EDEDED; text-color: #CFCFCF;'> <td>" + val.idPurchase + "</td>" +
					                    "<td>" + val.member.name + "</td>" +
					                    "<td>" + total + "</td>" +
					                    "<td id='TDconsegna_" + val.idPurchase + "'style='color: green;'>Effettuata</td>" +
					                    "<td><form>" +
										  		 "<input type='hidden' value='" + val.idPurchase + "'/>" +
										  		 "<button style='margin: 0px' type='submit' id='setShip_" + val.idPurchase + "'> Set Consegna </button>" +
										  	     "<button style='margin: 0px' type='submit' id='showDetailsPurchase_" + val.idPurchase + "'> Dettagli </button>" +
										  	   "</form></td></tr>" +
								"<tr class='detailsPurchase' id='TRdetailsPurchase_" + val.idPurchase + "'><td colspan='5' id='TDdetailsPurchase_" + val.idPurchase + "'>" +
										"<div style='margin: 15px'>" + productTable + "</div></td></tr>");

		} else {
			
			$(tableControl).append("<tr> <td>" + val.idPurchase + "</td>" +
					                    "<td>" + val.member.name + "</td>" +
					                    "<td>" + total + "</td>" +
					                    "<td id='TDconsegna_" + val.idPurchase + "' style='color: red;'>Non Effettuata</td>" +
					                    "<td><form>" +
										  		 "<input type='hidden' value='" + val.idPurchase + "'/>" +
										  		 "<button style='margin: 0px' type='submit' id='setShip_" + val.idPurchase + "'> Set Consegna </button>" +
										  	     "<button style='margin: 0px' type='submit' id='showDetailsPurchase_" + val.idPurchase + "'> Dettagli </button>" +
										  	   "</form></td></tr>" +
								"<tr class='detailsPurchase' id='TRdetailsPurchase_" + val.idPurchase + "'><td colspan='5' id='TDdetailsPurchase_" + val.idPurchase + "'>" +
										"<div style='margin: 15px'>" + productTable + "</div></td></tr>");
			
		}    
    });
    
	$.each(data, function(index, val)
    {
		$("#showDetailsPurchase_" + val.idPurchase).on("click", clickShowDetailsPurchaseHandler);
		$("#setShip_" + val.idPurchase).on("click", clicksetShipPurchaseHandler);
    });
	
	$(".detailsPurchase").hide();
	$("button").button();
	
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);
}

function clicksetShipPurchaseHandler(event) {
	event.preventDefault();
	
	$(".detailsPurchase").hide();
    var form = $(this).parents('form');
    var idPurchase = $('input', form).val();
    var result = 0;
    
    $.postSync("ajax/setShip", {idPurchase: idPurchase}, function(data) {result = data;});
    
    var tdControl = "#TDconsegna_" + idPurchase;
    
    if(result == 0){
		$(tdControl).css('color','red');
		$(tdControl).html("Non Effettuata");
	} else {
		$(tdControl).css('color','green');
		$(tdControl).html("Effettuata");
	}
    
}

function clickShowDetailsPurchaseHandler(event)  {
	event.preventDefault();
	
	$(".detailsPurchase").hide();
    var form = $(this).parents('form');
    var idPurchase = $('input', form).val();
    
    var trControl = "#TRdetailsPurchase_" + idPurchase;
    var tdControl = "#TDdetailsPurchase_" + idPurchase;
    
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);
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
                                              	   "<button type='submit' id='showDetails_" + order.idOrder + "'> Dettagli </button>" +
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
    
    $("#errorDivOrder").hide();
    $("#errorsOrder").html("");
    var fail = false;
    var dataClose = $("#closeData").datepicker("getDate");
    var orderName = $("#orderName").val();
    
    if(orderName == "") {
    	$("#legendErrorOrder").html("Errore");
        $("#errorsOrder").append("Nome Ordine non inserito.<br />");
        fail = true;
    }
    
    if(dataClose == "" || dataClose == null || dataClose.getTime() <= new Date().getTime()) {
    	 $("#legendErrorOrder").html("Errore");
         $("#errorsOrder").append("Data di chiusura non corretta.<br />");
         fail = true;
    } 
    
    if(addedIds.length == 0) {
        $("#legendErrorOrder").html("Errore");
        $("#errorsOrder").append("Non sono stati aggiunti prodotti all'ordine.<br />");
        fail = true;
    }
   
    if(fail == true) {
    	$("#errorsOrder").append("<br />");
    	$("#errorDivOrder").show("slow");
        $("#errorDivOrder").fadeIn(1000);
    } else {
    	
    	var dataCloseTime = 0;
    	
    	if(dataClose != null) {
        	dataClose.setHours(23);
            dataClose.setMinutes(59);
            dataClose.setSeconds(59);
            dataClose.setMilliseconds(999);
            
            dataCloseTime = dataClose.getTime();
        }
    	var idString = addedIds.join(",");
    	
    	$.post("ajax/setNewOrder", {idSupplier: idSupplier, orderName: orderName, idString: idString, dataCloseTime: dataCloseTime}, postSetNewOrderRequest);
    }
      
}

function postSetNewOrderRequest(result) {
	
	if(result <= 0) {
		//errore
		$("#legendErrorOrder").html("Errore");
	    $("#errorsOrder").html("Errore Interno. Non &egrave stato possibile creare l'ordine.<br /><br />");
	    $("#errorDivOrder").show("slow");
	    $("#errorDivOrder").fadeIn(1000);
	} else {
		//tutto ok
		ClearOrder();
		$("#legendErrorOrder").html("Comunicazione");
	    $("#errorsOrder").html("Ordine Creato Correttamente.<br /><br />");
	    $("#errorDivOrder").show("slow");
	    $("#errorDivOrder").fadeIn(1000);
	}
	
}

function ClearOrder() {
	$('#orderCart ul').html("<div align='center' class='placeholder'><br />Trascina qui i prodotti da includere nell'ordine<br /><br /></div>");
	addedIds = [];
	$('#orderName').val("");
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
            										  	     "<button style='margin: 0px' type='submit' id='showDetails_" + order.idOrder + "'> Dettagli </button>" +
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