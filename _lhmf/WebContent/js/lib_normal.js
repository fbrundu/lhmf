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
        registerForMessages();
        registerForNotifies();
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
                "<form method='post' action='purchase'>" +
                  "<fieldset><legend>&nbsp;Seleziona gli ordini:&nbsp;</legend><br />" +
                      "<label for='orderPurchase' class='left'>Selezione Ordine: </label>" +
                      "<select name='orderPurchase' id='orderPurchase' class='field' style='width: 400px'></select>" +
                      "<button type='submit' id='orderListRequest'> Carica i Prodotti </button>" +
                  "</fieldset>" +
                  "<fieldset id='purchaseCompositor'><legend>&nbsp;Composizione Nuova Scheda D'Acquisto:&nbsp;</legend><br />" +
                  "<div id='productsList'>" +
                  	"<h1 class='ui-widget-header'>Prodotti</h1>" +
                  	"<div id='catalog'></div>" +
                  "</div>" +
                  "<div id='purchaseCart'>" +
                  	"<h1 class='ui-widget-header'>Scheda Di Acquisto</h1>" +
                  	"<div class='ui-widget-content'>" +
                  		"<ul id='products' class='list clearfix'>" +
                  			"<div align='center' class='placeholder'><br />Trascina qui i prodotti da includere nella scheda<br /><br /></div>" +
                  		"</ul>" +
                  	"</div>" +
                  "</div>" +
                  "<button type='submit' id='purchaseRequest'> Crea Scheda </button>" +
                  "</fieldset>" +
                  "<div id='errorDivPurchase' style='display:none;'>" +
                      "<fieldset><legend id='legendErrorPurchase'>&nbsp;Errore&nbsp;</legend><br />" +
                       "<div id='errorsPurchase' stpurchasepadding-left: 40px'></div>" +
                      "</fieldset>" +
                  "</div>" +
                  
                "</form>" +
              "</div>");
        
        $('#tabsPurchase-2').html("<div class='logform'>" +
                "<form method='post' action=''>" +
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
                                  "<button type='submit' id='purchaseOldRequest'> Visualizza </button>" +
                                "</form>" +
                                "</form>" +
                                "<table id='oldPurchaseList' class='log'></table>" +
                                "<div id='errorDivOldPurchase' style='display:none;'>" +
                                  "<fieldset><legend id='legendErrorOldPurchase'>&nbsp;Errore&nbsp;</legend><br />" +
                                   "<div id='errorsOldPurchase' style='padding-left: 40px'>" +
                                    "</div>" +
                                  "</fieldset>" +
                                "</div><br />" +
                            "</div>");
        
        $('#tabsPurchase-4').html("Da implementare?");
       
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
       
    $('#purchaseActiveRequest').on("click", clickPurchaseActiveHandler);
    $('#purchaseOldRequest').on("click", clickPurchaseOldHandler);
    $('#purchaseDetailsRequest').on("click", clickPurchaseDetailsHandler);
    $("#purchaseCompositor").hide();
    
    loadOrders();
    
    $('#orderListRequest').on("click", clickProductListRequest);
    $('#purchaseRequest').on("click", clickPurchaseHandler);
    
    $("button").button();
}

var idOrder = 0;	
var addedIds = [];

function clickProductListRequest(event) {
	event.preventDefault();
	
	$("#purchaseCompositor").hide();
	$("#productsList").html("<h1 class='ui-widget-header'>Prodotti</h1>" +
          					"<div id='catalog'></div>");
	
	idOrder = $('#orderPurchase').val();
	
	if(idOrder == -1) 
	{
		$("#errorDivPurchase").hide();
		$("#legendErrorPurchase").html("Comunicazione");
		$("#errorsPurchase").html("Non hai selezionato l'Ordine<br /><br />");
		$("#errorDivPurchase").show("slow");
		$("#errorDivPurchase").fadeIn(1000);	
	}
	else
	{
		$.post("ajax/getProductFromOrder", {idOrder: idOrder}, postProductListRequest);
	}
	
}

function postProductListRequest(productList) 
{
	$("#errorDivPurchase").hide();
	if(productList.length < 1) 
	{
		
        $("#errorDivPurchase").hide();
        $("#legendErrorPurchase").html("Comunicazione");
        $("#errorsPurchase").html("Non ci sono prodotti disponibili da visualizzare<br /><br />");
        $("#errorDivPurchase").show("slow");
        $("#errorDivPurchase").fadeIn(1000);
		
	}
	else 
	{
		
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
										/*"<span class='amount'>" +
											"<label for='pz' class='left'>Quantit� desiderata:</label>" +
											 "<input type='text' id='pz' class='field' style='width: 40px' />" +
										"</span>" +*/
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
				$("#errorDivPurchase").hide("slow");
			}
		});
		$( "#purchaseCart ul" ).droppable({
			activeClass: "ui-state-default",
			hoverClass: "ui-state-hover",
			accept: ":not(.ui-sortable-helper)",
			drop: function( event, ui ) {
				
				var idProduct = $(ui.draggable).data('productid');
				
				if($.inArray(idProduct, addedIds) === -1) {
		            addedIds.push(idProduct);
		            $( "#purchaseCart ul" ).append($(ui.draggable).clone());
		            $( "#purchaseCart .delButton" ).on("click", deleteProductFromOrder);
					$( "#purchaseCart .deleteButton" ).show();
					//$( "#purchaseCart .amount" ).show();
		        } else {
					$("#errorDivPurchase").hide();
			        $("#legendErrorPurchase").html("Comunicazione");
			        $("#errorsPurchase").html("Questo prodotto &egrave gi&agrave presente nella scheda<br /><br />");
			        $("#errorDivPurchase").show("slow");
			        $("#errorDivPurchase").fadeIn(1000);
				}
			}
		});
		
		$( ".deleteButton" ).hide();
		$( ".amount" ).hide();
		$( "#purchaseCompositor" ).show("slow");
		
	}
}

function deleteProductFromOrder(event) 
{
	event.preventDefault();
	
	$(this).parents("li").remove();
	var idProduct = $(this).parents("li").data('productid');
	addedIds = jQuery.removeFromArray(idProduct, addedIds);
}

jQuery.removeFromArray = function(value, arr) 
{
    return jQuery.grep(arr, function(elem, index) {
        return elem !== value;
    });
};

function clickPurchaseHandler(event) 
{
    event.preventDefault();
    
    $("#errorDivPurchase").hide();
    $("#errorsPurchase").html("");

    /*var idProducts = [];
    var productsAmount = [];*/
    var fail = false;
    
    //Controllo dei campi. //addedIds
    /*var productDOMList = $("#purchaseCart ul li"); //oggetto jquery
    
    if(productDOMList.lenght == 0) {
        $("#legendErrorPurchase").html("Errore");
        $("#errorsPurchase").html("Non sono stati aggiunti prodotti all'ordine.<br /><br />");
        fail = true;
    }
    
    productDOMList.each(function(index, value) {
    	
    	var id = $(value).data('productid');
    	var amount = $(value).find("input").val();
    	
    	if(amount === undefined || isNaN(amount)) {
	        $("#legendErrorPurchase").html("Errore");
	        $("#errorsPurchase").html("Errore nei campi Quota. Compilare con un valore numerico intero.<br /><br />");
	        fail = true;
    	}
    	idProducts.push(id);
    	productsAmount.push(amount);
  	
    });
   */
    if(addedIds.length == 0)
    {
        $("#legendErrorPurchase").html("Errore");
        $("#errorsPurchase").append("Non sono stati aggiunti prodotti alla scheda.<br />");
        fail = true;
    }
    
    if(fail == true) 
    {
    	$("#errorsPurchase").append("<br />");
    	$("#errorDivPurchase").show("slow");
        $("#errorDivPurchase").fadeIn(1000);
    }
    else
    {
    	var idProducts = addedIds.join(",");
    	
    	$.post("ajax/setNewPurchase", {idOrder: idOrder, idProducts: idProducts/*, productsAmount: productsAmount*/}, postSetNewPurchaseRequest);
    }
      
}

function postSetNewPurchaseRequest(result) 
{
	
	if(result <= 0)
	{
		$("#legendErrorPurchase").html("Errore");
	    $("#errorsPurchase").html("Errore Interno. Non &egrave stato possibile creare la scheda di acquisto.<br /><br />");
	    $("#errorDivPurchase").show("slow");
	    $("#errorDivPurchase").fadeIn(1000);
	}
	else
	{
		$("#legendErrorPurchase").html("Comunicazione");
	    $("#errorsPurchase").html("Scheda di acquisto creata correttamente.<br /><br />");
	    $("#errorDivPurchase").show("slow");
	    $("#errorDivPurchase").fadeIn(1000);
	}
	
}

function clickPurchaseActiveHandler(event) 
{
    event.preventDefault();
    
    $.post("ajax/getActivePurchase", postActivePurchaseListHandler);
    
}



function clickPurchaseOldHandler(event) 
{
    event.preventDefault();
  
    $.post("ajax/getOldPurchase", postOldPurchaseListHandler);
    
}

function clickPurchaseDetailsHandler(event)
{
    event.preventDefault();
    
    $.post("ajax/getPurchaseDetails", postPurchaseDetailsListHandler);
	
}

function loadOrders() 
{
	
	$.post("ajax/getOrdersString", function(data) {
		
		var output = [];
		output.push('<option value="-1"> Seleziona...</option>');

		$.each(data, function(index, val)
		{
			var temp = val.split(","); 
			output.push('<option value="'+ temp[0] +'"> Date Apertura - Chiusura:'+ temp[1] /*+ ' | ' + temp[2]*/ + '</option>');
		});
		$('#orderPurchase').html(output.join(''));
	
	});
}

////////Schede Attive


function postActivePurchaseListHandler(purchaseList) 
{
    
    $("#activePurchaseList").html("");
    $("#activePurchaseList").hide();

    if(purchaseList.length > 0)
    {
        $("#activePurchaseList").append("<tr>  <th class='top' width='20%'> Numero Scheda </th>" +
        									 "<th class='top' width='30%'> Spedizione  </th>" +
        									 "<th class='top' width='15%'> Data Apertura  </th>" +
                                             "<th class='top' width='15%'> Data Chiusura  </th>" +
                                             "<th class='top' width='20%'> Dettagli ordine  </th> </tr>");
        for(var i = 0; i < purchaseList.length; i++){
            var purchase = purchaseList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateClose));
            $("#activePurchaseList").append("<tr> <td>" + purchase.idPurchase + "</td>" +
					  							  "<td>" + purchase.isShipped + "</td>" +
					  							  "<td>" + dateOpen + "</td>" +
					  							  "<td>" + dateClose + "</td>" +
					  							  "<td> <form> <input type='hidden' value='" + purchase.idPurchase + "'/>" +
					  							  "<button type='submit' id='showDetails_" + purchase.idPurchase + "'> Mostra Dettagli </button>" +
					  							  "</form></td></tr>" +
					  							  "<tr class='detailsPurchase' id='TRdetailsPurchase_" + purchase.idPurchase + "'><td colspan='5' id='TDdetailsPurchase_" + purchase.idPurchase + "'></td></tr>");
            $(".detailsPurchase").hide();
            $("button").button();
        }
        $.each(purchaseList, function(index, val)
        {
        	$("#showDetails_" + val.idPurchase).on("click", clickPurchaseDetailsHandler);
        });
            
        $("#activePurchaseList").show("slow");
        $("#activePurchaseList").fadeIn(1000);
        $("#errorDivActivePurchase").hide();
    }
    else 
    {
        
        $("#activePurchaseList").show();
        $("#errorDivActivePurchase").hide();
        $("#legendErrorActivePurchase").html("Comunicazione");
        $("#errorsActivePurchase").html("Non ci sono Schede attive da visualizzare<br /><br />");
        $("#errorDivActivePurchase").show("slow");
        $("#errorDivActivePurchase").fadeIn(1000);
    
    }
}

function postOldPurchaseListHandler(purchaseList) 
{
    
    $("#oldPurchaseList").html("");
    $("#oldPurchaseList").hide();

    if(purchaseList.length > 0){
        $("#oldPurchaseList").append("<tr>  <th class='top' width='20%'> Numero Scheda </th>" +
				 							"<th class='top' width='30%'> Spedizione  </th>" +
				 							"<th class='top' width='15%'> Data Apertura  </th>" +
				 							"<th class='top' width='15%'> Data Chiusura  </th>" +
                 							"<th class='top' width='20%'> Dettagli ordine  </th> </tr>");
        for(var i = 0; i < purchaseList.length; i++){
            var purchase = purchaseList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateClose));
            $("#oldPurchaseList").append("<tr> <td>" + purchase.idPurchase + "</td>" +
					  							  "<td>" + purchase.isShipped + "</td>" +
					  							  "<td>" + dateOpen + "</td>" +
					  							  "<td>" + dateClose + "</td>" +
					  							  "<td> <form> <input type='hidden' value='" + purchase.idPurchase + "'/>" +
					  							  "<button type='submit' id='showDetails_" + purchase.idPurchase + "'> Mostra Dettagli </button>" +
					  							  "</form></td></tr>" +
					  							  "<tr class='detailsPurchase' id='TRdetailsPurchase_" + purchase.idPurchase + "'><td colspan='5' id='TDdetailsPurchase_" + purchase.idPurchase + "'></td></tr>");        $(".detailsPurchase").hide();
        $("button").button();
    }
    $.each(purchaseList, function(index, val)
    {
    	$("#showDetails_" + val.idPurchase).on("click", clickPurchaseDetailsHandler);
    });
    
        $("#oldPurchaseList").show("slow");
        $("#oldPurchaseList").fadeIn(1000);
        $("#errorDivOldPurchase").hide();
    } else {
        
        $("#oldPurchaseList").show();
        $("#errorDivOldPurchase").hide();
        $("#legendErrorOldPurchase").html("Comunicazione");
        $("#errorsOldPurchase").html("Non ci sono Schede scadute da visualizzare<br /><br />");
        $("#errorDivOldPurchase").show("slow");
        $("#errorDivOldPurchase").fadeIn(1000);
    
    }
}


var idPurchase = 0;

function clickPurchaseDetailsHandler(event) 
{
    event.preventDefault();
    
    $(".detailsPurchase").hide();
    var form = $(this).parents('form');
    idPurchase = $('input', form).val();
    
    $.post("ajax/getPurchaseDetails", {idPurchase: idPurchase}, postPurchaseDetailsListHandler);
}

function postPurchaseDetailsListHandler(productList) 
{

    var trControl = "#TRdetailsPurchase_" + idPurchase;
    var tdControl = "#TDdetailsPurchase_" + idPurchase;
    
    $(tdControl).html("<div style='margin: 15px'><table id='TABLEdetailsPurchase_" + idPurchase + "' class='log'></table></div>");
    
    var tableControl = "#TABLEdetailsPurchase_" + idPurchase;
    
    $(tableControl).append("<tr>  <th class='top' width='25%'> Prodotto </th>" +
                                 "<th class='top' width='35%'> Descrizione  </th>" +
                                 "<th class='top' width='20%'> Costo  </th>" +
                                 "<th class='top' width='20%'> Min-Max Buy  </th> </tr>");
    
    $.each(productList, function(index, val)
    {
        $(tableControl).append("<tr>    <td>" + val.name + "</td>" +
        		                       "<td>" + val.description + "</td>" +
        		                       "<td>" + val.unitCost + "</td>" +
        		                       "<td>" + val.minBuy + " - " + val.maxBuy + "</td></tr>");
    });
    
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);  
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