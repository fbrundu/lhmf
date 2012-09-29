(function(window, undefined) {
    var History = window.History;
    $ = window.jQuery;
    if (!(History.enabled))
        console.log("HTML 5 History API is disabled!");
    else
        History.Adapter.bind(window, 'statechange', historyStateChanged);

    $(function() 
    {
        $("#purchaseLink").click(purchaseClicked);
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

function historyStateChanged()
{
    var History = window.History;
    var state = History.getState();
    var stateData = state.data;
    if (!stateData)
        showIndex();
    switch (stateData.action)
    {
    case 'notifiche':
      getMyNotifies();
      break;
    case 'statNormal':
      writeStatPageNormal();
      break;
    case 'messaggi':
      getMyMessages();
      break;
    case 'purchase':
        writePurchasePage();
        break;
    default:
        writeIndexPage();
    }
}

function purchaseClicked(event) 
{
  var History = window.History; 
  if (History.enabled == true) {
      event.preventDefault();
        var state = History.getState();
        var stateData = state.data;
        if (!!stateData && !!stateData.action && stateData.action == 'purchase')
            return;
        History.pushState({action : 'purchase'}, null, 'purchase');
    }
}

function statClicked(event) {
	var History = window.History;	
	  if (History.enabled == true) {
	    event.preventDefault();
	    var state = History.getState();
	    var stateData = state.data;
	    if (!!stateData && !!stateData.action
	        && stateData.action == 'statNormal')
	      return;
	    History.pushState({
	      action : 'statNormal'
	    }, null, 'statNormal');
	  }
}

function writePurchasePage()
{
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
      "</div>" +
      "<div id='dialog-confirm' title='Eliminazione Scheda D'acquisto' style='display:none;'>" +
		  "<p><span class='ui-icon ui-icon-alert' style='float:left; margin:0 7px 20px 0;'></span>" +
		  "Annullando l'ordine dell'ultimo prodotto si proceder&agrave; all'eliminazione dell'intera scheda. Continuare?</p>" +
      "</div>" +
      "<div id='dialog-error' title='Errore' style='display:none;'>" +
      	"<p>Errore nell'inserimento dei dati. Non puoi inserire una quantit&agrave; negativa o non dosponibile.</p>" +
      "</div>" +
      "<div id='dialog-internal-error' title='Errore Interno' style='display:none;'>" +
      	"<p>Errore interno. Non &egrave; stato possibile eseguire l'operazione.</p>" +
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
    
    //$('#tabsPurchase-4').html("Da implementare?");
   
    preparePurchaseForm();
}

function writeStatPageNormal() {
	
	$(".centrale").html("<div id='tabs'><ul>" +
						"<li><a href='#tabs-1'>Movimenti</a></li>" +
						"<li><a href='#tabs-2'>Prodotti</a></li>" +
						"</ul>" +
					    "<div id='tabs-1'></div>" +
					    "<div id='tabs-2'></div>" +
					    "</div>");
	
	var selectString1 = "<select name='yearS1' id='yearS1' class='field' onchange='refreshStatMese()'>";
	for(var thisYear = new Date().getFullYear(); thisYear > 1990; thisYear--)
		selectString1 += "<option value='" + thisYear + "'>" + thisYear + "</option>";
	selectString1 += "</select>";
	
	  $('#tabs-1').html("<table id='canvNorm-1'>" +
	  		"<tr><th> Anno " + selectString1 +"</th></tr>" +
	  		"<tr><td id='tdNormSpesaMese'><canvas id='canvasNormSpesaMese' width='580' height='400'></canvas></td></tr><table>");
	  $('#tabs-2').html("<table id='canvNorm-2'>" +
		  		"<tr><th></th></tr>" +
		  		"<tr><td id='tdProdTopSeller'><canvas id='canvasProdTopSeller' width='580' height='400'></canvas></td></tr><table>");
	  $('#tabs').tabs();
	  
	  writeStatistics();
}

function writeStatistics() {
		
	$('#canvNorm-1').hide();
	$('#canvNorm-2').hide();
	
	var year1 = $("#yearS1").val();
	
	$.post("ajax/statNormMoneyMonth", {year: year1}, postStatNormMoneyMonthHandler);
	$.post("ajax/statProdTopProduct", null, postStatProdTopProductHandler);
	
	$('#canvNorm-1').show('slow');
	$('#canvNorm-2').show('slow');
}

function refreshAllStat() {
	
	refreshStatMese();
	refreshTopProduct();
	
}

function refreshStatMese() {
	
	var year1 = $("#yearS1").val();
	
	$("#tdNormSpesaMese").hide("slow");
	$("#tdNormSpesaMese").html("<canvas id='canvasNormSpesaMese' width='580' height='400'></canvas>");
	
	$.post("ajax/statNormMoneyMonth", {year: year1}, postStatNormMoneyMonthHandler);
	
	$("#tdNormSpesaMese").show("slow");
}

function refreshTopProduct() {
	
	
	$("#tdProdTopSeller").hide("slow");
	$("#tdProdTopSeller").html("<canvas id='canvasProdTopSeller' width='580' height='400'></canvas>");
	
	$.post("ajax/statProdTopProduct", null, postStatProdTopProductHandler);
	
	$("#tdProdTopSeller").show("slow");
}

function postStatNormMoneyMonthHandler(data) {
	
	new CanvasXpress("canvasNormSpesaMese", {
        "y": {
          "vars": [
            "Spesa Totale",
            "Spesa Media"
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
            "Spesa Totale Mensile",
            "Spesa Media Mensile"
          ],
          "data": [
            [
              data[0],
              data[2],
              data[4],
              data[6],
              data[8],
              data[10],
              data[12],
              data[14],
              data[16],
              data[18],
              data[20],
              data[22]
            ],
            [
              data[1],
              data[3],
              data[5],
              data[7],
              data[9],
              data[11],
              data[13],
              data[15],
              data[17],
              data[19],
              data[21],
              data[23]
            ]
          ]
        },
        "a": {
          "xAxis": [
            "Spesa Totale"
          ],
          "xAxis2": [
            "Spesa Media"
          ]
        }
      }, {
        "graphType": "BarLine",
        "title": "Distribuzione della spesa Totale/Media in un Anno",
        "graphOrientation": "vertical",
        //"backgroundType": "gradient",
        "legendBox": false,
        //"backgroundGradient2Color": "rgb(112,179,222)",
        //"backgroundGradient1Color": "rgb(226,236,248)",
        "colorScheme": "basic",
        "legendBackgroundColor": false,
        "lineType": "spline",
        "smpHairlineColor": "rgb(100,100,100)",
        "xAxisTickColor": "rgb(100,100,100)",
        "smpTitle": "Mesi"
      });
	
}


function postStatProdTopProductHandler(data) {
    
    var json = ' { "x": { "Response": [ ' +
                        '"Resistant",' +
                        '"Resistant",' +
                        '"Resistant",' +
                        '"Sensitive",' +
                        '"Sensitive",' +
                        '"Sensitive",' +
                        '"Sensitive",' +
                        '"Sensitive" ] }, ';

    json += ' "y": { "vars": [ "Prodotto" ], "smps": [ ';

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
    
    
    json += '], \"desc\": [ \"Spesa Totale\" ], \"data\": [ [ ';
    
    for(i = len; i < data.length; i++) {
    if (i == data.length-1)
    json += '\"' + data[i] + '\" ';
    else
    json += '\"' + data[i] + '\", ';
    }
    
    json += '] ] } }';
    
    } else {
    
    json += '""';       
    
    json += '], \"desc\": [ \"Spesa Totale\" ], \"data\": [ [ ';
    
    json += '\"0\" ';
    
    json += '] ] } }';
    
    }
    
    json2 = '{ \"graphType\": \"Bar\",' + 
    '\"colorBy\": \"Response\", ' +
    '\"title\": \"Top 10 Prodotti Venduti\", ' +
    '\"smpTitle\": \"Prodotti\", ' +
    '\"colorScheme\": \"basic\", ' +
    '\"graphOrientation\": \"vertical\", ' +
    '\"setMin\": 0, ' +
    '\"showLegend\": false }';
    
    var obj = jQuery.parseJSON(json);
    var obj2 = jQuery.parseJSON(json2);
    
    new CanvasXpress("canvasProdTopSeller", obj, obj2);
    
}






function writeIndexPage()
{
    $('.centrale').html("<p>Interfaccia utente normale</p>");
}


function preparePurchaseForm(tab){
    
    $('#tabsPurchase').tabs();
    
    $( "#dialog:ui-dialog" ).dialog( "destroy" );
    
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
var addedPz = [];

function clickProductListRequest(event) 
{
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
										"<span class='amount'>" +
											"<label for='pz' class='left'>Quantit&agrave desiderata:</label>" +
											 "<input type='text' id='pz' class='field' style='width: 40px' />" +
											 "<input type='hidden' id='pzMax' class='field' value='" + product.maxBuy + "' />" +
										"</span>" +
										"<span class='darkview'>" +
											"Blocchi: " + product.unitBlock + " | (" + product.measureUnit + ")<br />" +
											"Disponibilit&agrave: " + product.minBuy + " - " + product.maxBuy +
										"</span>" +
									"</section>" +
									"<div class='deleteButton'><a href='#'><img src='img/delete.png' class='delButton' height='12px'></a></div>" +
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
					$( "#purchaseCart .amount" ).show();
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
    return jQuery.grep(arr, function(elem, index) 
    {
        return elem !== value;
    });
};

function clickPurchaseHandler(event) 
{
    event.preventDefault();
    
    $("#errorDivPurchase").hide();
    $("#errorsPurchase").html("");

    var fail = false;
    
    addedPz = [];
    
    $("#purchaseCart ul li").each(function(index, value) 
    {
    	
        var amount = $(this).find('input').val();
        var max =  $(this).find('input:hidden').val();
      
        if(amount === "") 
        {
        	$("#legendErrorPurchase").html("Errore");
            $("#errorsPurchase").html("Errore il campo quantit&agrave &egrave vuoto. Inserire un valore.<br /><br />");
            fail = true;
        }
        else if(isNaN(amount))
        {
        	$("#legendErrorPurchase").html("Errore");
            $("#errorsPurchase").html("Errore la quantit&agrave deve essere un numero.<br /><br />");
            fail = true;
        }
        else if(parseInt(amount) <= 0) 
        {
        	$("#legendErrorPurchase").html("Errore");
            $("#errorsPurchase").html("Errore la quantit&agrave deve avere un valore positivo.<br /><br />");
            fail = true;
        }
         else if(parseInt(amount) > parseInt(max))
        {
        	$("#legendErrorPurchase").html("Errore");
            $("#errorsPurchase").html("Errore la quantit&agrave &egrave maggiore della effettiva disponibilit&agrave.<br /><br />");
            fail = true;
        }
        else if(amount === undefined) 
        {
        	$("#legendErrorPurchase").html("Errore");
            $("#errorsPurchase").html("Errore nel campo quantit&agrave. Compilare con un valore numerico intero.<br /><br />");
            fail = true;
        }
        addedPz.push(amount);
    });
       
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
    	var amountProducts = addedPz.join(",");
    	
    	$.post("ajax/setNewPurchase", {idOrder: idOrder, idProducts: idProducts, amountProducts: amountProducts}, postSetNewPurchaseRequest);
    } 
}

function postSetNewPurchaseRequest(result) 
{
	if(result <= 0)
	{
		$("#legendErrorPurchase").html("Errore");
		if(result == -2)
		{
			$("#errorsPurchase").html("Errore nel campo quantit&agrave. Compilare con un valore numerico corretto.<br /><br />");
		}
		else
		{
			$("#errorsPurchase").html("Errore Interno. Non &egrave stato possibile creare la scheda di acquisto.<br /><br />");
		}
		$("#errorDivPurchase").show("slow");
	    $("#errorDivPurchase").fadeIn(1000);
	}
	else
	{
		ClearPurchase();
		$("#legendErrorPurchase").html("Comunicazione");
	    $("#errorsPurchase").html("Scheda di acquisto creata correttamente.<br /><br />");
	    $("#errorDivPurchase").show("slow");
	    $("#errorDivPurchase").fadeIn(1000);
	}	
}

function ClearPurchase()
{
	$('#purchaseCart ul').html("<div align='center' class='placeholder'><br />Trascina qui i prodotti da includere nella scheda<br /><br /></div>");
	addedIds = [];
	addedPz = [];
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

function loadOrders() 
{
	
	$.post("ajax/getOrdersString", function(data) 
	{
		
		var output = [];
		output.push('<option value="-1"> Seleziona...</option>');

		$.each(data, function(index, val)
		{
			var temp = val.split(","); 
			output.push('<option value="'+ temp[0] +'"> Nome - Data Chiusura:'+ temp[1] /*+ ' | ' + temp[2]*/ + '</option>');
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
        $("#activePurchaseList").append("<tr> <th class='top' width='30%'> Nome Ordine </th>" +
        									 "<th class='top' width='20%'> Data Apertura  </th>" +
                                             "<th class='top' width='20%'> Data Chiusura  </th>" +
                                             "<th class='top' width='30%'> Dettagli ordine  </th></tr>");
        for(var i = 0; i < purchaseList.length; i++){
            var purchase = purchaseList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateClose));
            var idProgressBar = "progressbarOrder_" + purchase.order.idOrder;
            
            var valProgress = 0;
            $.postSync("ajax/getProgressOrder", {idOrder: purchase.order.idOrder}, function(data)
    	    {
            	valProgress = data;
    	    });
            
            
            
            $("#activePurchaseList").append("<tr class='orderPurchase_" + purchase.idPurchase + "'> <td>" + purchase.order.orderName + "</td>" +
					  							  "<td>" + dateOpen + "</td>" +
					  							  "<td>" + dateClose + "</td>" +
					  							  "<td> <form> <input type='hidden' value='" + purchase.idPurchase + "'/>" +
					  							  "<button type='submit' id='showDetails_" + purchase.idPurchase + "'> Mostra Dettagli </button>" +
					  							  "</form></td></tr>" +
				  							  "<tr class='orderPurchase_" + purchase.idPurchase + "'><td colspan='4'> <strong>Progresso dell'ordine: " + valProgress + "%</strong> </td></tr>" +
				  							  "<tr class='orderPurchase_" + purchase.idPurchase + "'><td colspan='4'> <div id='" + idProgressBar + "'></div> </td></tr>" +
				  							  "<tr class='detailsPurchase' id='TRdetailsPurchase_" + purchase.idPurchase + "'><td colspan='5' id='TDdetailsPurchase_" + purchase.idPurchase + "'></td></tr>");
            $(".detailsPurchase").hide();
            $( "#" + idProgressBar ).progressbar({	value: valProgress	});
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
        $("#oldPurchaseList").append("<tr>  <th class='top' width='20%'> Nome Ordine </th>" +
				 							"<th class='top' width='30%'> Spedizione  </th>" +
				 							"<th class='top' width='15%'> Data Apertura  </th>" +
				 							"<th class='top' width='15%'> Data Chiusura  </th>" +
                 							"<th class='top' width='20%'> Dettagli ordine  </th> </tr>");
        for(var i = 0; i < purchaseList.length; i++){
            var purchase = purchaseList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateClose));
            $("#oldPurchaseList").append("<tr> <td>" + purchase.order.orderName + "</td>" +
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
    	$("#showDetails_" + val.idPurchase).on("click", clickOldPurchaseDetailsHandler);
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

function clickOldPurchaseDetailsHandler(event) 
{
    event.preventDefault();
    
    $(".detailsPurchase").hide();
    var form = $(this).parents('form');
    idPurchase = $('input', form).val();
    
    $.postSync("ajax/getPurchaseDetails", {idPurchase: idPurchase}, postOldPurchaseDetailsListHandler);
}

function postOldPurchaseDetailsListHandler(productList) 
{
	
	var AmountTmp = 0;
    var trControl = "#TRdetailsPurchase_" + idPurchase;
    var tdControl = "#TDdetailsPurchase_" + idPurchase;
    
    $(tdControl).html("<div style='margin: 15px' id='DIVdetailsPurchase_" + idPurchase + "'><table id='TABLEdetailsPurchase_" + idPurchase + "' class='log2'></table></div>");
    
    var tableControl = "#TABLEdetailsPurchase_" + idPurchase;
    
    $(tableControl).append("<tr>  <th class='top' width='30%'> Prodotto </th>" +
                                 "<th class='top' width='35%'> Descrizione  </th>" +
                                 "<th class='top' width='25%'> Costo Unit&agrave;*Blocchi (Limiti)  </th>" +
                                 "<th class='top' width='10%'> Qt. </th>" +
                                 "<th class='top' width='10%'> Parziale </th></tr>");
    
    var tot = 0;
    var parziale = 0;
    
    $.each(productList, function(index, val)
    {
    	$.postSync("ajax/getAmountfromPurchase", {idPurchase: idPurchase, idProduct: val.idProduct}, function(data)
        {
    		AmountTmp = data;
        });
    	
    	
    	parziale += AmountTmp * (val.unitCost * val.unitBlock);
    	tot += parziale;
    	
        $(tableControl).append("<tr>    <td>" + val.name + "</td>" +
        		                       "<td>" + val.description + "</td>" +
        		                       "<td>" + val.unitCost + "&euro; * " + val.unitBlock + " (" + val.minBuy + "-" + val.maxBuy +")</td>" +
        		                       "<td>" + AmountTmp + "</td>" +
        		                       "<td>" + parziale + " &euro;</td></tr>");
    });
    
    $(tableControl).append("<tr><td colspan='4' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;</strong> </td>" +
            "<td>" + tot + " &euro;</td></tr>");
    
    
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);  
}


function clickPurchaseDetailsHandler(event) 
{
    event.preventDefault();
    
    $(".detailsPurchase").hide();
    var form = $(this).parents('form');
    idPurchase = $('input', form).val();
    
    $.postSync("ajax/getPurchaseDetails", {idPurchase: idPurchase}, postPurchaseDetailsListHandler);
}

function postPurchaseDetailsListHandler(productList) 
{
	
	var AmountTmp = 0;
	var DispTmp = 0;
    var trControl = "#TRdetailsPurchase_" + idPurchase;
    var tdControl = "#TDdetailsPurchase_" + idPurchase;
    
    $(tdControl).html("<div style='margin: 15px' id='DIVdetailsPurchase_" + idPurchase + "'><table id='TABLEdetailsPurchase_" + idPurchase + "' class='log2'></table></div>");
    
    var tableControl = "#TABLEdetailsPurchase_" + idPurchase;
    var divControl = "#DIVdetailsPurchase_" + idPurchase;
    
    $(tableControl).append("<tr>  <th class='top' width='20%'> Prodotto </th>" +
                                 "<th class='top' width='25%'> Descrizione  </th>" +
                                 "<th class='top' width='25%'> Stato Parziale  </th>" +
                                 "<th class='top' width='10%'> Costo [Blocco]<br />(Limiti)  </th>" +
                                 "<th class='top' width='5%'> Disp. </th>" +
                                 "<th class='top' width='5%'> Qt. </th>" +
                                 "<th class='top' width='5%'> Azioni </th>" +
                                 "<th class='top' width='10%'> Parziale </th> </tr>");
    
    var tot = 0;
    
    $.each(productList, function(index, val)
    {
    	var parziale = 0;
    	
    	$.postSync("ajax/getAmountfromPurchase", {idPurchase: idPurchase, idProduct: val.idProduct}, function(data)
        {
    		AmountTmp = data;
        });
    	
    	$.postSync("ajax/getDispOfProduct", {idPurchase: idPurchase, idProduct: val.idProduct}, function(data)
        {
    		if(data == -1)
    			DispTmp = "Inf.";
    		else
    			DispTmp = data;
        });
    	
    	parziale += AmountTmp * (val.unitCost);
    	tot += parziale;
    	
    	var idDisp = "disp_" + idPurchase + "_" + val.idProduct;
    	var idAmount = "amountProduct_" + idPurchase + "_" + val.idProduct;
    	
        $(tableControl).append("<tr id='tr_" + idPurchase + "_" + val.idProduct + "'>    <td>" + val.name + "</td>" +
        		                       "<td>" + val.description + "</td>" +
        		                       "<td> progressbar" + "</td>" +
        		                       "<td>" + val.unitCost + "&euro; [" + val.unitBlock + "]<br />(" + val.minBuy + "-" + val.maxBuy +")</td>" +
        		                       "<td id='" + idDisp + "' data-disp='" + DispTmp + "'>" + DispTmp + "</td>" +
        		                       "<td> <input type='text' style='width: 35px; text-align: center;' id='" + idAmount + "' data-oldamount='" + AmountTmp + "' value='" + AmountTmp + "'/></td>" +
        		                       "<td id='action_" + idPurchase + "_" + val.idProduct + "'> <img data-productid='" + val.idProduct + "' src='img/refresh.png' class='refreshProductButton' height='12px'> " +
        		                       "<img data-productid='" + val.idProduct + "' src='img/delete.png' class='delProductButton' height='12px'> </td>" +
        		                       "<td id='tdPartial_" + idPurchase +"_" + val.idProduct + "'>" + parziale + " &euro;</td></tr>");
    });
    
    $(tableControl).append("<tr id='trTotalRow_" + idPurchase +"' data-total='" + tot + "'><td colspan='7' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong></td>" +
            "<td>" + tot + " &euro;</td></tr>");
   
    $(divControl).append("<strong>Altri prodotti disponibili</strong>");
    
    $.postSync("ajax/getOtherProductsOfPurchase", {idPurchase: idPurchase}, function(productList) {
        
        var divControl = "#DIVdetailsPurchase_" + idPurchase;
        
        $(divControl).append("<table id='TABLE2detailsPurchase_" + idPurchase + "' class='log2'></table>");
        var table2Control = "#TABLE2detailsPurchase_" + idPurchase;
        
        $(table2Control).append("<tr>  <th class='top' width='20%'> Prodotto </th>" +
                                "<th class='top' width='25%'> Descrizione  </th>" +
                                "<th class='top' width='25%'> Stato Parziale  </th>" +
                                "<th class='top' width='10%'> Costo [Blocco]<br />(Limiti)  </th>" +
                                "<th class='top' width='5%'> Disp. </th>" +
                                "<th class='top' width='5%'> Qt. </th> " +
                                "<th class='top' width='10%'> Azioni </th></tr>");
        
        if(productList.length < 1) {
            
            $(table2Control).append("<tr><td colspan='7'> Non ci sono altri prodotti da aggiungere </td></tr>");
            
        } else {
            
        	var DispTmp = 0;
        	
            $.each(productList, function(index, val)
                    {
            	
            			$.postSync("ajax/getDispOfProduct", {idPurchase: idPurchase, idProduct: val.idProduct}, function(data)
            	        {
            				if(data == -1)
            	    			DispTmp = "Inf.";
            	    		else
            	    			DispTmp = data;
            	        });
                                
                        var idDisp = "disp_" + idPurchase + "_" + val.idProduct;
                        var idAmount = "amountProduct_" + idPurchase + "_" + val.idProduct;
                        
                        $(table2Control).append("<tr id='tr_" + idPurchase + "_" + val.idProduct + "'>   <td>" + val.name + "</td>" +
                                                       "<td>" + val.description + "</td>" +
                                                       "<td> progressbar" + "</td>" +
                                                       "<td>" + val.unitCost + "&euro; [" + val.unitBlock + "]<br />(" + val.minBuy + "-" + val.maxBuy +")</td>" +
                                                       "<td id='" + idDisp + "' data-disp='" + DispTmp + "'>" + DispTmp + "</td>" +
                                                       "<td> <input type='text' style='width: 35px; text-align: center;' data-oldamount='0' id='" + idAmount + "'/></td>" +
                                                       "<td id='action_" + idPurchase + "_" + val.idProduct + "'> <img data-productid='" + val.idProduct + "' src='img/add.png' class='addProductButton' height='12px'> </td></tr>");
                     });
        }
    });
    
    $( ".delProductButton" ).on("click", deleteProductFromPurchase);
    $( ".refreshProductButton" ).on("click", refreshProductFromPurchase);
    $( ".addProductButton" ).on("click", addProductFromPurchase);
    
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);  
}

function deleteProductFromPurchase(event){
    
    event.preventDefault();
    
    var tableControl = "#TABLEdetailsPurchase_" + idPurchase;
    
    var idProduct = $(this).data("productid");
    
    if($(tableControl + ' tr').length == 3) { // 3 perchè intestazione + ultimo prodotto + totale = 3
    	
    	//Si sta cancellando l'ultimo prodotto della scheda
    	$( "#dialog-confirm" ).dialog({
			resizable: false,
			height:140,
			modal: true,
			buttons: {
				"Rimuovi scheda d'acquisto": function() {
					
					removeProduct(idProduct, 1);
					$( this ).dialog( "close" );
				},
				Cancel: function() {
					$( this ).dialog( "close" );
				}
			}
		});
    } else {
    	removeProduct(idProduct, 0);
    }
}
   
function removeProduct(idProduct, lastProduct) {
	
    var result = 0;
	
	//Tolgo il prodotto alla scheda
    $.postSync("ajax/delPurchaseProduct", {idPurchase: idPurchase, idProduct: idProduct}, function(data)
    {
    	 result = data;
    });
    
    if(result < 1) {
    	
		 $("#dialog-internal-error").dialog({
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
		 
    	return -1;
	} 
    
    //mi ricavo il prodotto
	var product = null;
    $.postSync("ajax/getProductNormal", {idProduct: idProduct}, function(data)
    {
    	product = data;
    });
    
    var tableControl = "#TABLEdetailsPurchase_" + idPurchase;
    var table2Control = "#TABLE2detailsPurchase_" + idPurchase;
    var trProduct = "#tr_" + idPurchase + "_" + idProduct;
    var trTotal = "#trTotalRow_" + idPurchase;
    var inputAmount = "#amountProduct_" + idPurchase + "_" + idProduct;
  
    //Ricavo il nuovo totale
    var totale = $(trTotal).data('total');
    var oldAmount = $(inputAmount).data('oldamount');
    var oldparziale = oldAmount*(product.unitCost);
    totale -= oldparziale;
    
    //Tolgo la riga del totale
    $(trTotal).remove();    
    
    //Se non ci sono altri prodotti che si possono aggiungere scriverlo
    if($(table2Control).find('input').length == 0)
    	$(table2Control + ' tr:last').remove();
   
    //Modifico la riga aggiungendo togliendo colonna del parziale
    $(trProduct+ " td:last").remove();
    
    //Sposto la riga alla tabella in basso
    $(table2Control).append($(trProduct).remove());
    
    //Aggiorno il data-oldAmount e pulisco l'input
    $(inputAmount).data('oldamount', 0);
    $(inputAmount).val(' ');
    
    //Aggiorno disponibilità
    var idDisp = "#disp_" + idPurchase + "_" + idProduct;
    var DispTmp = 0;
    $.postSync("ajax/getDispOfProduct", {idPurchase: idPurchase, idProduct: idProduct}, function(data)
    {
    	if(data == -1)
			DispTmp = "Inf.";
		else
			DispTmp = data;
    });
    
    $(idDisp).html(DispTmp);
    
    
    //Modifico la riga cambiando i controlli delle azioni
    var actionControl = "#action_" + idPurchase + "_" + idProduct;
    $(actionControl).html("<img data-productid='" + idProduct + "' src='img/add.png' class='addProductButton' height='12px'> ");
    
    //Aggiungo la riga con il nuovo totale
    $(tableControl).append("<tr id='trTotalRow_" + idPurchase +"' data-total='" + totale + "'>" +
    					      "<td colspan='7' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong></td>" +
    					   "<td>" + totale + " &euro;</td></tr>");
    
    $( ".addProductButton" ).on("click", addProductFromPurchase);
    
    if(lastProduct == 1) {
    	
    	//Eliminazione Intera scheda
    	
    	$.postSync("ajax/delPurchase", {idPurchase: idPurchase}, function(data)
    	{
    		   result = data;
    	});
    	
    	if(result < 1) {
        	
	   		 $("#dialog-internal-error").dialog({
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
	   		 
	       	return -1;
	   	} else {
	   		
	   		// Eliminazione ordine dalla tabella
	   		var classTrOrder = ".orderPurchase_" + idPurchase;
	   		var idTrDetails = "#TRdetailsPurchase_" + idPurchase;
	   		var idTableOrder = "#activePurchaseList";
	   		
	   		$(classTrOrder).remove();
	   		$(idTrDetails).remove();
	   		
	   		//Se era l'unico ordine cancellare tutta la tabella
	   		if($(idTableOrder + ' tr').length == 1) 
	   	    	$(idTableOrder + ' tr').remove();
	   	}
    }
}

function refreshProductFromPurchase(event){
    
    event.preventDefault();
    
    var idProduct = $(this).data("productid");
    
    //Ricavo l'amount
    var inputAmount = "#amountProduct_" + idPurchase + "_" + idProduct;
    var amount = $(inputAmount).val();
    
    //Ricavo la disponibilità
    var idDisp = "#disp_" + idPurchase + "_" + idProduct;
    var disp = $(idDisp).data('disp');
    
    var oldAmount = $(inputAmount).data('oldamount');
    
    var error = 0;
    if(disp == 'Inf.') {
    	if(!isPositiveNumber(amount))
    		error = 1;
    } else if(!isPositiveNumber(amount) || parseInt(amount)-parseInt(oldAmount) > parseInt(disp))
	    {
    		error = 1;
	   	}
    
    if(error == 1) {
    	
    	$("#dialog-error").dialog({
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
    	
    	$(inputAmount).val(oldAmount);
    	return -1;
    }
    
    var result = 0;
    
    //Aggiungo il nuovo prodotto alla scheda
    $.postSync("ajax/updatePurchaseProduct", {idPurchase: idPurchase, idProduct: idProduct, amount: amount}, function(data)
    {
    	 result = data;
    });
    
    if(result != 1) {
    	
    	$("#dialog-internal-error").dialog({
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
    	
    } else {
    	
    	//aggiorno il valore del parziale e del totale
    	
    	//mi ricavo il prodotto
    	var product = null;
        $.postSync("ajax/getProductNormal", {idProduct: idProduct}, function(data)
        {
        	product = data;
        });	
        
        var tdPartial = "#tdPartial_" + idPurchase + "_" + idProduct;
        var trTotal = "#trTotalRow_" + idPurchase;
        var tableControl = "#TABLEdetailsPurchase_" + idPurchase;
        
        // mi calcolo il parziale e il totale
        var totale = $(trTotal).data('total'); 
        var oldparziale = oldAmount*(product.unitCost);
        var parziale = amount*(product.unitCost);
        totale -= oldparziale;
        totale += parziale;
    	
        //Aggiorno il data-oldAmount
        $(inputAmount).data('oldamount', amount);
        
        //Aggiorno disponibilità
        var idDisp = "#disp_" + idPurchase + "_" + idProduct;
        var DispTmp = 0;
        $.postSync("ajax/getDispOfProduct", {idPurchase: idPurchase, idProduct: idProduct}, function(data)
        {
        	if(data == -1)
    			DispTmp = "Inf.";
    		else
    			DispTmp = data;
        });
        
        $(idDisp).html(DispTmp);
        $(idDisp).data('disp', DispTmp);
        
        //aggiorno i campi
        $(tdPartial).html(parziale + " &euro;");
        $(trTotal).remove();
        //Aggiungo la riga con il nuovo totale
        $(tableControl).append("<tr id='trTotalRow_" + idPurchase +"' data-total='" + totale + "'>" +
        					      "<td colspan='7' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong></td>" +
        					   "<td>" + totale + " &euro;</td></tr>");
        
    }
    
    
}

function addProductFromPurchase(event){
    
    event.preventDefault();
    
    var idProduct = $(this).data("productid");
    
    //Ricavo l'amount
    var inputAmount = "#amountProduct_" + idPurchase + "_" + idProduct;
    var amount = $(inputAmount).val();
    
    var product = null;
    $.postSync("ajax/getProductNormal", {idProduct: idProduct}, function(data)
    {
    	product = data;
    });	
    
    //Ricavo la disponibilità
    var idDisp = "#disp_" + idPurchase + "_" + idProduct;
    var disp = $(idDisp).data('disp');

    //Controllo valore amount 
    var error = 0;
    if(disp == 'Inf.') {
    	if(!isPositiveNumber(amount))
    		error = 1;
    } else if(!isPositiveNumber(amount) || parseInt(amount) > parseInt(disp))
	    {
    		error = 1;
	   	}
    
    if(error == 1) {
    	
    	$("#dialog-error").dialog({
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
    	
    	$(inputAmount).val(' ');
    	
    	return -1;
    }
    
    var result = 0;
    
    //Aggiungo il nuovo prodotto alla scheda
    $.postSync("ajax/newPurchaseProduct", {idPurchase: idPurchase, idProduct: idProduct, amount: amount}, function(data)
    {
    	 result = data;
    });
    
    if(result < 0) {

    	$("#dialog-internal-error").dialog({
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
    	
    	$(inputAmount).val(' ');
    	
    	return -1;
	} 
		
    var tableControl = "#TABLEdetailsPurchase_" + idPurchase;
    var table2Control = "#TABLE2detailsPurchase_" + idPurchase;
    var trProduct = "#tr_" + idPurchase + "_" + idProduct;
    var trTotal = "#trTotalRow_" + idPurchase;
  
    //Ricavo il nuovo totale
    var totale = $(trTotal).data('total');
    var parziale = amount*(product.unitCost);
    totale += parziale;
    
    //Tolgo la riga del totale
    $(trTotal).remove();    
    
    //Sposto la riga del prodotto alla tabella degli ordini
    $(tableControl).append($(trProduct).remove());
    
    //Se non ci sono altri prodotti che si possono aggiungere scriverlo
    if($(table2Control + ' tr').length == 1) {
    	$(table2Control).append("<tr><td colspan='7'> Non ci sono altri prodotti da aggiungere </td></tr>");
    }
    
    //Aggiorno il data-oldAmount
    $(inputAmount).data('oldamount', amount);
    
    //Aggiorno disponibilità
    var idDisp = "#disp_" + idPurchase + "_" + idProduct;
    var DispTmp = 0;
    $.postSync("ajax/getDispOfProduct", {idPurchase: idPurchase, idProduct: idProduct}, function(data)
    {
    	if(data == -1)
			DispTmp = "Inf.";
		else
			DispTmp = data;
    });
    
    $(idDisp).html(DispTmp);
    $(idDisp).data('disp', DispTmp);
    
    
    //Modifico la riga aggiungendo la colonna del parziale
    $(trProduct).append("<td>" + parziale + " &euro;</td>");
    //Modifico la riga cambiando i controlli delle azioni
    var actionControl = "#action_" + idPurchase + "_" + idProduct;
    $(actionControl).html("<img data-productid='" + idProduct + "' src='img/refresh.png' class='refreshProductButton' height='12px'> " +
        		          "<img data-productid='" + idProduct + "' src='img/delete.png' class='delProductButton' height='12px'>");
    
    //Aggiungo la riga con il nuovo totale
    $(tableControl).append("<tr id='trTotalRow_" + idPurchase +"' data-total='" + totale + "'>" +
    					      "<td colspan='7' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong></td>" +
    					   "<td>" + totale + " &euro;</td></tr>");
    
    $( ".delProductButton" ).on("click", deleteProductFromPurchase);
    $( ".refreshProductButton" ).on("click", refreshProductFromPurchase);
    
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