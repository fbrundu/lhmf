window.setInterval(function(){

	
	$.post("ajax/getOrdersString", function(orderList) 
	{
		
		$.each(orderList, function(index, val)
		{
			refreshProgressBar(val.idOrder);
		});
	});
	
	
	
	$.post("ajax/getActivePurchase", function(purchaseList) {

		$.each(purchaseList, function(index, val) {
			refreshProgressBarOrder(val.idPurchase);
		});
	});
	
	
	
}, 3000);

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
        writePurchasePage(stateData.idOrd, stateData.tab);
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

function writePurchasePage(idOrd, tab)
{
  $("#bodyTitleHeader").html("Gestione schede");
    $(".centrale").html("<div id='tabsPurchase'>" +
    		                    "<ul>" +
    		                     "<li><a href='#tabsPurchase-1'>Crea Scheda</a></li>" +
    		                     "<li><a href='#tabsPurchase-2'>Schede Attive</a></li>" +
    		                     "<li><a href='#tabsPurchase-3'>Schede In Consegna</a></li>" +
    		                    "</ul>" +
                                "<div id='tabsPurchase-1'></div>" +
                                "<div id='tabsPurchase-2'></div>" +
                                "<div id='tabsPurchase-3'></div>" +
                           "</div>");
    
    $('#tabsPurchase-1').html("<div class='logform'>" +
            "<form method='post' action='purchase'>" +
              "<fieldset><legend>&nbsp;Seleziona l'ordine per creare una scheda d'acquisto:&nbsp;</legend><br />" +
              	  "<table id='TABLEorderPurchase' class='log'></table>" +
              "</fieldset>" +
              "<br />" +
              "<fieldset id='purchaseCompositor'><legend>&nbsp;Composizione Nuova Scheda D'Acquisto:&nbsp;</legend><br />" +
              "<div id='productsList'>" +
              	"<h1 class='ui-widget-header'>Prodotti in Listino</h1>" +
              	"<div id='catalog'></div>" +
              "</div>" +
              "<div id='purchaseCart'>" +
              	"<h1 class='ui-widget-header'>Scheda D'Acquisto</h1>" +
              	"<div class='ui-widget-content'>" +
              		"<ul id='products' class='list clearfix'>" +
              			"<div align='center' class='placeholder'><br />Trascina qui i prodotti da includere nella scheda<br /><br /></div>" +
              		"</ul>" +
              	"</div>" +
              "</div>" +
              "<br /><br /><br />" +
              "<div id='divTotal' data-total='0' class='price' style='text-align:center;'> Totale: <strong style='color: #3C3'>0 &euro;</strong></div>" +
              "<button type='submit' id='purchaseRequest'> Crea Scheda </button>" +
              "</fieldset>" +
              "<div id='errorDivPurchase' style='display:none;'>" +
                  "<fieldset><legend id='legendErrorPurchase'>&nbsp;Errore&nbsp;</legend><br />" +
                   "<div id='errorsPurchase' style='padding-left: 40px'></div>" +
                  "</fieldset>" +
              "</div>" +
              "<br />" +
            "</form>" +
          "</div>");
    
    $('#tabsPurchase-2').html("<div class='logform'>" +
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
      	"<p>Errore nell'inserimento dei dati. Non puoi inserire una quantit&agrave; negativa o non disponibile.</p>" +
      "</div>" +
      "<div id='dialog-internal-error' title='Errore Interno' style='display:none;'>" +
      	"<p>Errore interno. Non &egrave; stato possibile eseguire l'operazione.</p>" +
      "</div>");
    
    $('#tabsPurchase-3').html("<div class='logform'>" +
                            "<table id='oldPurchaseList' class='log'></table>" +
                            "<div id='errorDivOldPurchase' style='display:none;'>" +
                              "<fieldset><legend id='legendErrorOldPurchase'>&nbsp;Errore&nbsp;</legend><br />" +
                               "<div id='errorsOldPurchase' style='padding-left: 40px'>" +
                                "</div>" +
                              "</fieldset>" +
                            "</div><br />" +
                          "<div id='dialog-map' title='Mappa dislocamento utenti partecipanti all'ordine' style='display:none; text-align:center' align='center'>" +
                          	"<div id='map' style='width:575px; height:500px; text-align:center' align='center'> </div>" +
                          "</div>" +
                        "</div>");
    
    $('#tabsPurchase').tabs();
    $('#tabsPurchase').tabs('select', tab);
    
    preparePurchaseForm(idOrd, tab);
}

function writeStatPageNormal() {
  $("#bodyTitleHeader").html("Statistiche utente");
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
    writePurchasePage(0, 0);
}

var writePurchaseDetails = false;

function preparePurchaseForm(idOrd, tab){
    
    $( "#dialog:ui-dialog" ).dialog( "destroy" );
    
    $("body").delegate(".delProductButton", "click", deleteProductFromPurchase);
    $("body").delegate(".refreshProductButton", "click", refreshProductFromPurchase);
    $("body").delegate(".addProductButton", "click", addProductFromPurchase);
    $("body").delegate(".inputAmount", "change", updateAmount);
    $("body").delegate(".mapButton", "click", drawMapOrder);
    
       
    loadOrders();
    
    if(idOrd != 0 && tab == 0)
    	productListRequest(idOrd);
    
    if(idOrd != 0 && tab == 1) {
    	idPurchase = idOrd;
	
		var History = window.History;
	    var state = History.getState();
	    var stateData = state.data;
	    idOrder = stateData.idOrd2;
	    
	    writePurchaseDetails = true;
	}
    
    loadPurchaseActive();
    
    if(idOrd != 0 && tab == 2) 
    	idToHighLight = idOrd;
    
    loadPurchaseOld();
    
    $("#purchaseCompositor").hide();
    $('#purchaseRequest').on("click", clickPurchaseHandler);
    
    $("button").button();
}

var idOrder = 0;	
var addedIds = [];
var addedPz = [];
var idToHighLight = 0;

function productListRequest(idO) 
{
	idOrder = idO;
	
	$("#purchaseCompositor").hide();
	$("#productsList").html("<h1 class='ui-widget-header'>Prodotti in Listino</h1>" +
          					"<div id='catalog'></div>");
		
	$.post("ajax/getProductFromOrder", {idOrder: idOrder}, postProductListRequest);
	
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
		var category = 0;
		var ncategory = 0;
		var divToWork = 0;
		var DispTmp = 0;
		
		for(var i = 0; i < productList.length; i++) {
            
			var product = productList[i];
							
			$.postSync("ajax/getDispOfProductOrder", {idOrder: idOrder, idProduct: product.idProduct}, function(data)
	        {
	    		if(data == -1)
	    			DispTmp = "Inf.";
	    		else
	    			DispTmp = data;
	        });
			
			if(category != product.category.idProductCategory) {
				//Nuova categoria, creare nuovo accordion
				category = product.category.idProductCategory;
				$("#catalog").append("<h3><a href='#'>" + product.category.description + "</a></h3>");
				$("#catalog").append("<div><ul id='products' class='list clearfix'></ul></div>");
				
				var divToWork = $("#catalog div ul")[ncategory];
				ncategory++;
			}
			            
			var idProgressBar = "pbProduct_" + idOrder + "_" + product.idProduct;
			var idDispDIV = "dispOrder_" + idOrder + "_" + product.idProduct;
			 
			$(divToWork).append("<li class='clearfix' data-productid='" + product.idProduct + "' data-amount='1' data-price='" + product.unitCost + "'>" +
								   "<section class='left'>" +
								       "<div ><img src='" + product.imgPath + "' height='60' class='thumb'></div>" +
								       "<h3>" + product.name + "</h3>" +
								       "<span class='meta'>" + product.description + "</span>" +
								       "<div id='" + idProgressBar + "' style='text-align: center;'><span class='textProgressBar1'></span></div>" +
								   "</section>" +
								   "<section class='right'>" +
										"<span class='amount' >" +
											"Qt. &nbsp;&nbsp;" +
											 "<input type='text' id='pz' class='inputAmount' style='width: 40px' value='1' />" +
											 "<input type='hidden' id='pzMax' value='" + DispTmp + "' />&nbsp;&nbsp;" +
											 "<strong style='color: green;'>&euro;" + product.unitCost + "</strong>" +
										"</span>" +
										"<span class='price' >&euro;" + product.unitCost + "</span>" +
										"<span class='darkview'>" +
											"Blocchi: " + product.unitBlock + " | (" + product.measureUnit + ")<br />" +
											"MinBuy: " + product.minBuy + " | Disp.: <div id='" + idDispDIV + "'>" + DispTmp + "</div>" +
										"</span>" +
									"</section>" +
									"<div class='deleteButton'><a href='#'><img alt='Rimuovi' src='img/delete.png' class='delButton' height='12px'></a></div>" +
								  "</li>");
			
			$( "#" + idProgressBar ).progressbar({	value: 0 });
			$( "#" + idProgressBar ).css('height', '1em');
			
			ClearPurchase();
								
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
				    
				    var DispTmp = 0;
				    $.postSync("ajax/getDispOfProductOrder", {idOrder: idOrder, idProduct: idProduct}, function(data)
		            {
		                if(data == -1)
		                    DispTmp = "Inf.";
		                else
		                    DispTmp = data;
		            });
                    
                    if(DispTmp != "Inf." && DispTmp == 0) {
                        
                        $("#errorDivPurchase").hide();
                        $("#legendErrorPurchase").html("Comunicazione");
                        $("#errorsPurchase").html("Questo prodotto non &egrave; disponibile<br /><br />");
                        $("#errorDivPurchase").show("slow");
                        $("#errorDivPurchase").fadeIn(1000);
                        
                    } else {
                        
                        addedIds.push(idProduct);
                        $( "#purchaseCart ul" ).append($(ui.draggable).clone());
                        $( "#purchaseCart .delButton" ).on("click", deleteProductFromOrder);
                        $( "#purchaseCart .deleteButton" ).show();
                        $( "#purchaseCart .amount" ).show();
                        $( "#purchaseCart .price" ).hide();
                        
                        //Aggiorno il totale
                        computeTotal();
                    }
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
		
		refreshProgressBar(idOrder);
		
		$( "#purchaseCompositor" ).show("slow");	
	}
}

function updateAmount() {
	
	var amount = $(this).val();
	var max =  $(this).parents("li").find('input:hidden').val();
	
	if(max != "Inf." && parseInt(amount) > parseInt(max)) {
		$(this).val(oldamount);
		
		$("#errorDivPurchase").hide();
        $("#legendErrorPurchase").html("Errore");
        $("#errorsPurchase").html("Quantit&agrave; non disponibile<br /><br />");
        $("#errorDivPurchase").show("slow");
        $("#errorDivPurchase").fadeIn(1000);
		
		return;
	}
	if(parseInt(amount) <= 0) {
		$(this).val(oldamount);
		
		$("#errorDivPurchase").hide();
        $("#legendErrorPurchase").html("Errore");
        $("#errorsPurchase").html("Valori negativi non sono permessi. Premere la 'x' per eliminare l'ordine dalla scheda<br /><br />");
        $("#errorDivPurchase").show("slow");
        $("#errorDivPurchase").fadeIn(1000);
		
		return;
	}
	
	$(this).parents("li").data('amount', amount);
	
	//Aggiorno il totale
	computeTotal();
	
}

function deleteProductFromOrder(event) 
{
	event.preventDefault();
	
	var idProduct = $(this).parents("li").data('productid');
	addedIds = jQuery.removeFromArray(idProduct, addedIds);
	
	$(this).parents("li").remove();
	
	computeTotal();
	
	if($('#purchaseCart ul li').length == 0) {
	    
	    $('#divTotal').html("Totale: <strong style='color: #3C3'>0 &euro;</strong>");
	    $('#divTotal').data('total', 0);
	    
	}
	
}

jQuery.removeFromArray = function(value, arr) 
{
    return jQuery.grep(arr, function(elem, index) 
    {
        return elem !== value;
    });
};

function refreshProgressBar(idOrder) {
		
	var valProgress = 0;
    $.postSync("ajax/getProgressOrder", {idOrder: idOrder}, function(data)
    {
    	valProgress = data;
    });
    
    //Aggiorno progressbar ordine generale
    var idProgressBar = "#pbOrder_" + idOrder;
    $(idProgressBar).progressbar('value', valProgress);
    $(idProgressBar + " span" ).text(valProgress.toFixed(2)	+ "%");
    
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
    	
    	//Aggiorno disponibilità
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
    	$(idProgressBarProduct + " span" ).text(progress.toFixed(2)	+ "%");
    });
    
} 

function clickPurchaseHandler(event) 
{
    event.preventDefault();
    
    $("#errorDivPurchase").hide();
    $("#errorsPurchase").html("");

    var fail = false;
    
    addedPz = [];
    
    $("#purchaseCart ul li").each(function(index, value) 
    {
    	
        var amount = $(this).find('input:text').val();
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
         else if(max != "Inf." && parseInt(amount) > parseInt(max))
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
		
		//Eliminare riga dell'ordine 
		$("#order_" + idOrder).remove();
		
		//Se non ci sono altri ordini scriverlo
		if($('#TABLEorderPurchase tr').length == 1) {
	    	$('#TABLEorderPurchase').append("<tr><td colspan='6'> Non ci sono altri ordini. </td></tr>");
	    }
		
		
		//Chiudere composizione scheda
		$( "#purchaseCompositor" ).hide("slow");
		
		$("#legendErrorPurchase").html("Comunicazione");
	    $("#errorsPurchase").html("Scheda di acquisto creata correttamente.<br /><br />");
	    $("#errorDivPurchase").show("slow");
	    $("#errorDivPurchase").fadeIn(1000);
	    
	    //Ricaricare pagina schede attive
	    loadPurchaseActive();
	}	
}

function ClearPurchase()
{
	$('#purchaseCart ul').html("<div align='center' class='placeholder'><br />Trascina qui i prodotti da includere nella scheda<br /><br /></div>");
	$('#divTotal').html("Totale: <strong style='color: #3C3'>0 &euro;</strong>");
	$('#divTotal').data('total', 0);
	addedIds = [];
	addedPz = [];
}

function computeTotal(){
    
    var total = 0;
    
    $("#purchaseCart ul li").each(function(index, value) 
    {
        var amount = $(this).find('input:text').val();
        var price = $(this).data('price');
        if (isNumber(amount))
    	{
        	total += amount*price;
    	}
    });
    
    $('#divTotal').html("Totale: <strong style='color: #3C3'>" + total  + " &euro;</strong>");
    $('#divTotal').data('total', total);
    
}

function loadPurchaseActive() 
{
    
    $.post("ajax/getActivePurchase", postActivePurchaseListHandler);  
}



function loadPurchaseOld() 
{  
    $.post("ajax/getShipPurchase", postShipPurchaseListHandler);
    
}

function loadOrders() 
{
	
	$.post("ajax/getOrdersString", function(data) 
	{
		
		$('#TABLEorderPurchase').html("<tr> <th class='top' width='20%'> Nome Ordine </th>" +
											 "<th class='top' width='15%'> Responsabile </th>" +
											 "<th class='top' width='15%'> Fornitore </th>" +
											 "<th class='top' width='15%'> Data Apertura </th>" +
				 							 "<th class='top' width='15%'> Data Chiusura </th>" +
				 							 "<th class='top' width='20%'> Progresso </th></tr>");

		if(data.length == 0)
			$('#TABLEorderPurchase').append("<tr><td colspan='6'> Non ci sono ordini da selezionare. </td></tr>");
		
		$.each(data, function(index, val)
		{
			var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(val.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(val.dateClose));
            
            var idProgressBar = "pbOrder_" + val.idOrder;
            
            var valProgress = 0;
            $.postSync("ajax/getProgressOrder", {idOrder: val.idOrder}, function(data)
    	    {
            	valProgress = data;
    	    });
            
			$('#TABLEorderPurchase').append("<tr id='order_" + val.idOrder + "' onclick='productListRequest(" + val.idOrder + ")'> " +
												"<td>" + val.orderName + "</td>" +
												"<td>" + val.memberResp.name + " " + val.memberResp.surname + "</td>" +
												"<td>" + val.supplier.companyName + "</td>" +
												"<td>" + dateOpen + "</td>" +
												"<td>" + dateClose + "</td>" +
												"<td style='padding: 5px' ><div id='" + idProgressBar + "' style='text-align: center;'><span class='textProgressBar1h'></span></div></td>" +
											"</tr>");
			
			$( "#" + idProgressBar ).progressbar({	value: valProgress	});
			$( "#" + idProgressBar + " span" ).text(valProgress.toFixed(2)	+ "%");
			$( "#" + idProgressBar ).css('height', '1.5em');
			
		});
		
		
		
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
                                             "<th class='top' width='20%'> Dettagli ordine  </th>" +
                                             "<th class='top' width='10%'> Mappa  </th></tr>");
        for(var i = 0; i < purchaseList.length; i++){
            var purchase = purchaseList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateClose));
            var idProgressBar = "progressbarOrder_" + purchase.idPurchase;
            
            var valProgress = 0;
            $.postSync("ajax/getProgressOrder", {idOrder: purchase.order.idOrder}, function(data)
    	    {
            	valProgress = data;
    	    });
            
            $("#activePurchaseList").append("<tr class='orderPurchase_" + purchase.idPurchase + "'> <td>" + purchase.order.orderName + "</td>" +
					  							  "<td>" + dateOpen + "</td>" +
					  							  "<td>" + dateClose + "</td>" +
					  							  "<td><button type='submit' id='showDetails_" + purchase.idPurchase + "' data-idpurchase='" + purchase.idPurchase + "'> Dettagli </button>" +
					  							  "</td>" +
					  							 "<td><img alt='Mappa dislocazione utenti' src='img/map.png' class='mapButton' height='12px' data-idorder='" + purchase.order.idOrder + "'></td>" +
					  							  "</tr>" +
				  							  "<tr class='orderPurchase_" + purchase.idPurchase + "'><td colspan='5'> <strong>Progresso dell'ordine.</td></tr>" +
				  							  "<tr class='orderPurchase_" + purchase.idPurchase + "'><td colspan='5' style='padding: 5px'> <div id='" + idProgressBar + "' style='text-align: center;'><span class='textProgressBar2'></span></div> </td></tr>" +
				  							  "<tr class='detailsPurchase' id='TRdetailsPurchase_" + purchase.idPurchase + "' data-idorder=' " + purchase.order.idOrder + "'><td colspan='5' id='TDdetailsPurchase_" + purchase.idPurchase + "'></td></tr>");
            
            $("#showDetails_" + purchase.idPurchase).on("click", clickPurchaseDetailsHandler);
            $(".detailsPurchase").hide();
            $( "#" + idProgressBar ).progressbar({	value: valProgress	});
            $( "#" + idProgressBar + " span").text(valProgress.toFixed(2) + "%");
            $("button").button();
        }
            
        $("#activePurchaseList").show("slow");
        $("#activePurchaseList").fadeIn(1000);
        
        if(writePurchaseDetails) {
        	writePurchaseDetails = false;
        	clickPurchaseDetailsHandler();
        }
        
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

function postShipPurchaseListHandler(purchaseList) 
{
    
    $("#oldPurchaseList").html("");
    $("#oldPurchaseList").hide();

    if(purchaseList.length > 0){
        $("#oldPurchaseList").append("<tr>  <th class='top' width='20%'> Nome Ordine </th>" +
				 							"<th class='top' width='15%'> Data Apertura  </th>" +
				 							"<th class='top' width='15%'> Data Chiusura  </th>" +
				 							"<th class='top' width='20%'> Data Consegna  </th>" +
                 							"<th class='top' width='20%'> Dettagli ordine  </th>" +
                 							"<th class='top' width='10%'> Mappa  </th> </tr>");
        for(var i = 0; i < purchaseList.length; i++){
            
        	var purchase = purchaseList[i];
            var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateOpen));
            var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateClose));
            var dateDelivery = $.datepicker.formatDate('dd-mm-yy', new Date(purchase.order.dateDelivery));
            
            var style = "";
            if(idToHighLight != 0 && purchase.order.idOrder == idToHighLight) {
           	 style = "style='background-color: #BABAC2'";
           	 idToHighLight = 0;
            }
            
            $("#oldPurchaseList").append("<tr " + style + "> <td>" + purchase.order.orderName + "</td>" +
					  							  "<td>" + dateOpen + "</td>" +
					  							  "<td>" + dateClose + "</td>" +
					  							  "<td>" + dateDelivery + "</td>" +
					  							  "<td><button type='submit' data-idpurchase='" + purchase.idPurchase + "' data-idorder='" + purchase.order.idOrder + "' id='showDetails_" + purchase.idPurchase + "'>Dettagli</button>" +
					  							  "</td>" +
					  							  "<td><img alt='Mappa dislocazione utenti' src='img/map.png' class='mapButton' height='12px' data-idorder='" + purchase.order.idOrder + "'></td>" +
					  							  "</tr>" +
					  							  "<tr class='detailsPurchase' id='TRdetailsPurchase_" + purchase.idPurchase + "'><td colspan='6' id='TDdetailsPurchase_" + purchase.idPurchase + "'></td></tr>");   
            $("#showDetails_" + purchase.idPurchase).on("click", clickShipPurchaseDetailsHandler);
            $(".detailsPurchase").hide();
            $("button").button();
    }
    
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

function clickShipPurchaseDetailsHandler(event) 
{
    event.preventDefault();
    
    $(".detailsPurchase").hide();
    idPurchase = $(this).data('idpurchase');
    idOrder = $(this).data('idorder');

    $.postSync("ajax/getPurchaseDetails", {idPurchase: idPurchase}, postShipPurchaseDetailsListHandler);
}

function postShipPurchaseDetailsListHandler(productList) 
{
	
	var AmountTmp = 0;
    var trControl = "#TRdetailsPurchase_" + idPurchase;
    var tdControl = "#TDdetailsPurchase_" + idPurchase;
    var trIdControl = "trShipProductNormal_" + idOrder + "_";
    
    $(tdControl).html("<div style='margin: 15px' id='DIVdetailsPurchase_" + idPurchase + "'><table id='TABLEdetailsPurchase_" + idPurchase + "' class='log2'></table></div>");
    
    var tableControl = "#TABLEdetailsPurchase_" + idPurchase;
    
    $(tableControl).append("<tr>     <th class='top' width='15%'> Prodotto </th>" +
						            "<th class='top' width='15%'> Categoria </th>" +
						            "<th class='top' width='15%'> Descrizione  </th>" +
						            "<th class='top' width='5%'> Costo </th>" +
						            "<th class='top' width='5%'> Richiesta </th>" +
						            "<th class='top' width='5%'> Parziale  </th>" +
						            													" </tr>");
    
    
    var productsid = [];
    $.each(productList, function(index, val)
    {
    	productsid.push(val.idProduct);
    	var parziale = 0;
    	
    	$.postSync("ajax/getAmountfromPurchase", {idPurchase: idPurchase, idProduct: val.idProduct}, function(data)
        {
    		AmountTmp = data;
        });
    	
    	
    	parziale = AmountTmp * val.unitCost;
    	
        $(tableControl).append("<tr id='" + trIdControl + val.idProduct + "'>    " +
        							   "<td>" + val.name + "</td>" +
        							   "<td>" + val.category.description + "</td>" +
        		                       "<td>" + val.description + "</td>" +
        		                       "<td>" + val.unitCost + "&euro; [" + val.unitBlock + "]</td>" +
        		                       "<td>" + AmountTmp + "</td>" +
        		                       "<td>" + parziale + " &euro;</td></tr>");
    });
    
    var totPurchase = 0;
	$.postSync("ajax/getTotPurchaseCost", {idPurchase: idPurchase}, function(data) { totPurchase = data; });
    
    $(tableControl).append("<tr><td colspan='5' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;</strong> </td>" +
            "<td>" + totPurchase + " &euro;</td></tr>");
    
    //Disattivo graficamente i prodotti falliti
    $.postSync("ajax/getProgressProductOfOrder", {idOrder: idOrder}, function(allProgress)
    {
    	//Aggiorno progressbar
        $.each(allProgress, function(index, val)
        {
        	var temp = val.split(',');
        	var idProduct = temp[0];
        	var progress = parseFloat(temp[1]);
        	
        	if(progress < 100) {
    	    	//Aggiungo effetto grigio per ordini attivi
    			var idTr = "#trShipProductNormal_" + idOrder + "_" + idProduct;
    			$(idTr).addClass("noLimitProduct");
        	}
        	
        });
    });
    
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);  
}


function clickPurchaseDetailsHandler(event) 
{
	if(typeof event != 'undefined')
		event.preventDefault();
    
    $(".detailsPurchase").hide();
    
    if(typeof event != 'undefined')
    	idPurchase = $(this).data('idpurchase');
    
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
    
    $(tableControl).append("<tr>  <th class='top' width='5%'> Immagine </th>" +
    		                     "<th class='top' width='20%'> Prodotto </th>" +
                                 "<th class='top' width='20%'> Descrizione  </th>" +
                                 "<th class='top' width='20%'> Stato Parziale  </th>" +
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
    	var idProgressBarProduct = "progressbarProduct_" + idPurchase + "_" + val.idProduct;
    	
        $(tableControl).append("<tr id='tr_" + idPurchase + "_" + val.idProduct + "'>  " +
        		                       "<td><img src='" + val.imgPath + "' height='30' class='thumb'></td>" +
        		                       "<td>" + val.name + "</td>" +
        		                       "<td>" + val.description + "</td>" +
        		                       "<td style='padding: 3px;'><div id='" + idProgressBarProduct + "' style='text-align: center;'><span class='textProgressBar2product'></span></div></td>" +
        		                       "<td>" + val.unitCost + "&euro; [" + val.unitBlock + "]<br />(" + val.minBuy + "-" + val.maxBuy +")</td>" +
        		                       "<td id='" + idDisp + "' data-disp='" + DispTmp + "'>" + DispTmp + "</td>" +
        		                       "<td> <input type='text' style='width: 35px; text-align: center;' id='" + idAmount + "' data-oldamount='" + AmountTmp + "' value='" + AmountTmp + "'/></td>" +
        		                       "<td id='action_" + idPurchase + "_" + val.idProduct + "'> <img alt='Aggiorna' data-productid='" + val.idProduct + "' src='img/refresh.png' class='refreshProductButton' height='12px'> " +
        		                       "<img alt='Rimuovi' data-productid='" + val.idProduct + "' src='img/delete.png' class='delProductButton' height='12px'> </td>" +
        		                       "<td id='tdPartial_" + idPurchase +"_" + val.idProduct + "'>" + parziale + " &euro;</td></tr>");
        
        $( "#" + idProgressBarProduct ).progressbar({	value: 0	});
        $( "#" + idProgressBarProduct + " span").text("00.00%");
    });
    
    $(tableControl).append("<tr id='trTotalRow_" + idPurchase +"' data-total='" + tot + "'><td colspan='8' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong></td>" +
            "<td>" + tot + " &euro;</td></tr>");
   
    $(divControl).append("<strong>Altri prodotti disponibili</strong>");
    
    $.postSync("ajax/getOtherProductsOfPurchase", {idPurchase: idPurchase}, function(productList) {
        
        var divControl = "#DIVdetailsPurchase_" + idPurchase;
        
        $(divControl).append("<table id='TABLE2detailsPurchase_" + idPurchase + "' class='log2'></table>");
        var table2Control = "#TABLE2detailsPurchase_" + idPurchase;
        
        $(table2Control).append("<tr> <th class='top' width='5%'> Immagine </th> " +
        		                "<th class='top' width='15%'> Prodotto </th>" +
                                "<th class='top' width='25%'> Descrizione  </th>" +
                                "<th class='top' width='25%'> Stato Parziale  </th>" +
                                "<th class='top' width='10%'> Costo [Blocco]<br />(Limiti)  </th>" +
                                "<th class='top' width='5%'> Disp. </th>" +
                                "<th class='top' width='5%'> Qt. </th> " +
                                "<th class='top' width='10%'> Azioni </th></tr>");
        
        if(productList.length < 1) {
            
            $(table2Control).append("<tr><td colspan='8'> Non ci sono altri prodotti da aggiungere </td></tr>");
            
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
                var idProgressBarProduct = "progressbarProduct_" + idPurchase + "_" + val.idProduct;
                
                $(table2Control).append("<tr id='tr_" + idPurchase + "_" + val.idProduct + "'>   " +
                                               "<td><img src='" + val.imgPath + "' height='30' class='thumb'></td>" +
                                               "<td>" + val.name + "</td>" +
                                               "<td>" + val.description + "</td>" +
                                               "<td style='padding: 3px;'><div id='" + idProgressBarProduct + "' style='text-align: center;'><span class='textProgressBar2productDisp'></span></div></td>" +
                                               "<td>" + val.unitCost + "&euro; [" + val.unitBlock + "]<br />(" + val.minBuy + "-" + val.maxBuy +")</td>" +
                                               "<td id='" + idDisp + "' data-disp='" + DispTmp + "'>" + DispTmp + "</td>" +
                                               "<td> <input type='text' style='width: 35px; text-align: center;' data-oldamount='0' id='" + idAmount + "'/></td>" +
                                               "<td id='action_" + idPurchase + "_" + val.idProduct + "'> <img alt='Aggiungi' data-productid='" + val.idProduct + "' src='img/add.png' class='addProductButton' height='12px'> </td></tr>");
                
                $( "#" + idProgressBarProduct ).progressbar({	value: 0	});
                $( "#" + idProgressBarProduct + " span").text("00.00%");
             });
        }
    });
    
    refreshProgressBarOrder(idPurchase);
    
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);  

    
}

function deleteProductFromPurchase(event){
    
    event.preventDefault();
    
    var tableControl = "#TABLEdetailsPurchase_" + idPurchase;
    
    var idProduct = $(this).data("productid");
    
    if($(tableControl + ' tr').length == 3) { // 3 perchï¿½ intestazione + ultimo prodotto + totale = 3
    	
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
   
    //Modifico la riga togliendo colonna del parziale
    $(trProduct+ " td:last").remove();
    
    //Sposto la riga alla tabella in basso
    $(table2Control).append($(trProduct).remove());
    
    
    //Ricreo la progressbar
    var idProgressBarProduct =  "#progressbarProduct_" + idPurchase + "_" + idProduct;
	$(idProgressBarProduct).progressbar( { value: 0 });
	//Cambio la classe dello span della progressbar
	$(idProgressBarProduct + " span").removeClass().addClass('textProgressBar2productDisp');
    
    //Aggiorno il data-oldAmount e pulisco l'input
    $(inputAmount).data('oldamount', 0);
    $(inputAmount).val(' ');
    
    
    //Modifico la riga cambiando i controlli delle azioni
    var actionControl = "#action_" + idPurchase + "_" + idProduct;
    $(actionControl).html("<img alt='Aggiungi' data-productid='" + idProduct + "' src='img/add.png' class='addProductButton' height='12px'> ");
    
    //Aggiungo la riga con il nuovo totale
    $(tableControl).append("<tr id='trTotalRow_" + idPurchase +"' data-total='" + totale + "'>" +
    					      "<td colspan='8' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong></td>" +
    					   "<td>" + totale + " &euro;</td></tr>");
    
    //aggiorno progressbar ordine
    refreshProgressBarOrder(idPurchase);
    
    
    if(lastProduct == 1) {
	   		
   		// Eliminazione ordine dalla tabella
   		var classTrOrder = ".orderPurchase_" + idPurchase;
   		var idTrDetails = "#TRdetailsPurchase_" + idPurchase;
   		var idTableOrder = "#activePurchaseList";
   		
   		$(classTrOrder).remove();
   		$(idTrDetails).remove();
   		
   		//Se era l'unico ordine cancellare tutta la tabella e mostrare messaggio
   		if($(idTableOrder + ' tr').length == 1) {
   		    
   		     $(idTableOrder + ' tr').remove();
   		 
	   		 $("#errorDivActivePurchase").hide();
	         $("#legendErrorActivePurchase").html("Comunicazione");
	         $("#errorsActivePurchase").html("Non ci sono Schede attive da visualizzare<br /><br />");
	         $("#errorDivActivePurchase").show("slow");
	         $("#errorDivActivePurchase").fadeIn(1000);
   		}
   		
   		//aggiornare tabella ordini in creazione scheda
   		loadOrders();
    } else {
        
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
    }
}

function refreshProductFromPurchase(event){
    
    event.preventDefault();
    
    var idProduct = $(this).data("productid");
    
    //Ricavo l'amount
    var inputAmount = "#amountProduct_" + idPurchase + "_" + idProduct;
    var amount = $(inputAmount).val();
    
    //Ricavo la disponibilitï¿½
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
        
        //Aggiorno disponibilitï¿½
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
        					      "<td colspan='8' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong></td>" +
        					   "<td>" + totale + " &euro;</td></tr>");
        
        //aggiorno progressbar ordine
        refreshProgressBarOrder(idPurchase);
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
    
    //Ricavo la disponibilitï¿½
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
    
    //Ricreo la progressbar
    var idProgressBarProduct =  "#progressbarProduct_" + idPurchase + "_" + idProduct;
	$(idProgressBarProduct).progressbar( { value: 0 });
	//Cambio la classe dello span della progressbar
	$(idProgressBarProduct + " span").removeClass().addClass('textProgressBar2product');
	
    //Se non ci sono altri prodotti che si possono aggiungere scriverlo
    if($(table2Control + ' tr').length == 1) {
    	$(table2Control).append("<tr><td colspan='8'> Non ci sono altri prodotti da aggiungere </td></tr>");
    }
    
    //Aggiorno il data-oldAmount
    $(inputAmount).data('oldamount', amount);
    
    //Aggiorno disponibilitï¿½
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
    
    var idPartial = "tdPartial_" + idPurchase + "_" + idProduct;
    
    //Modifico la riga aggiungendo la colonna del parziale
    $(trProduct).append("<td id='" + idPartial + "'>" + parziale + " &euro;</td>");
    //Modifico la riga cambiando i controlli delle azioni
    var actionControl = "#action_" + idPurchase + "_" + idProduct;
    $(actionControl).html("<img alt='Aggiorna' data-productid='" + idProduct + "' src='img/refresh.png' class='refreshProductButton' height='12px'> " +
        		          "<img alt='Rimuovi' data-productid='" + idProduct + "' src='img/delete.png' class='delProductButton' height='12px'>");
    
    //aggiorno progressbar
    refreshProgressBarOrder(idPurchase);
    
    //Aggiungo la riga con il nuovo totale
    $(tableControl).append("<tr id='trTotalRow_" + idPurchase +"' data-total='" + totale + "'>" +
    					      "<td colspan='8' style='text-align: right;'> <strong> Totale: &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong></td>" +
    					   "<td>" + totale + " &euro;</td></tr>");
    
}

function refreshProgressBarOrder(idPurchase) {
	
	var idTrDetails = "#TRdetailsPurchase_" + idPurchase;
	var idOrder = $(idTrDetails).data('idorder');
	
	var valProgress = 0;
    $.postSync("ajax/getProgressOrder", {idOrder: idOrder}, function(data)
    {
    	valProgress = data;
    });
    
    //aggiorno progressbar generale dell'ordine    
    var idProgressBar = "#progressbarOrder_" + idPurchase;
    var idTextProgressBar = "#Text_progressbarOrder_" + idPurchase;
	
    $(idProgressBar).progressbar('value', valProgress);
    $(idProgressBar + " span").text(valProgress.toFixed(2) + "%");
    
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
    	
    	 //Aggiorno disponibilitï¿½
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
        
        //aggiorno progressbar
    	var idProgressBarProduct =  "#progressbarProduct_" + idPurchase + "_" + idProduct;
    	$(idProgressBarProduct).progressbar('value', progress);
    	$(idProgressBarProduct + " span").text(progress.toFixed(2) + "%");
    });
    
}

function drawMapOrder()
{
	$( "#dialog-map:ui-dialog" ).dialog( "destroy" );
	
	$( "#dialog-map" ).dialog({
		resizable: false,
		height: 600,
		width: 605, 
		modal: true,
		buttons : {
         	 "Ok" : function() { $(this).dialog('close'); }
       	       }
	});
	
	idOrder = $(this).data('idorder');
	initialize(idOrder);
	
	
}

function initialize(idOrder)
{
	var geocoder = new google.maps.Geocoder();
	var latlng = new google.maps.LatLng(45.0875198, 7.985248);
	var myOptions = 
	{
	    zoom: 6,
	    center: latlng,
	    mapTypeId: google.maps.MapTypeId.ROADMAP
	};

	var map = new google.maps.Map(document.getElementById("map"), myOptions);
	$.postSync("ajax/getNormalForMap", {idOrder: idOrder}, function(membersList)
	{
		$.each(membersList, function(index, val)
		{
			var address = "" + val.address + " - " + val.city + "";
			var text = "" + val.name + " " + val.surname + "";
			geocoder.geocode( { 'address': address}, function(results, status) {
			      if (status == google.maps.GeocoderStatus.OK) {
			        //map.setCenter(results[0].geometry.location);
			        var marker = new google.maps.Marker({
			            map: map,
			            title: text,
			            position: results[0].geometry.location
			        });
			        var tooltip = "<div id='tooltip'>"+
					"<p><strong>Utente normale: " + val. name + " " + val.surname + "</strong><br>"+
					val.address + "<br>" +
					"provincia: " + val.city + "<br>" +
					"nazione: " + val.state + "</p>" +
					"</div>";
					var infowindow = new google.maps.InfoWindow({
						content: tooltip
					});
					google.maps.event.addListener(marker, 'click', function() {
						infowindow.open(map,marker);
					});
			      }
			      else 
			      {
			        alert("Geocode was not successful for the following reason: " + status);
			      }
			    });		
		});
	});
	
	$.postSync("ajax/getRespForMap", {idOrder: idOrder}, function(data)
	{
		var address = "" + data.address + " - " + data.city + "";
		var text = "" + data.name + " " + data.surname + "";
		geocoder.geocode( { 'address': address}, function(results, status) {
		      if (status == google.maps.GeocoderStatus.OK) {
		        map.setCenter(results[0].geometry.location);
		        var marker = new google.maps.Marker({
		            map: map,
		            title: text,
		            icon: 'http://google-maps-icons.googlecode.com/files/country.png',
		            position: results[0].geometry.location
		        });
		        var tooltip = "<div id='tooltip'>"+
				"<p><strong>Utente responsabile: " + data. name + " " + data.surname + "</strong><br>"+
				data.address + "<br>" +
				"provincia: " + data.city + "<br>" +
				"nazione: " + data.state + "</p>" +
				"</div>";
				var infowindow = new google.maps.InfoWindow({
					content: tooltip
				});
				google.maps.event.addListener(marker, 'click', function() {
					infowindow.open(map,marker);
				});
		      }
		      else 
		      {
		        alert("Geocode was not successful for the following reason: " + status);
		      }
		    });	
	});
}

function isNumber(o)
{
	return ! isNaN(o-0) && o != null;
}
