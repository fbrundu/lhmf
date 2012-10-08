window.setInterval(function(){

	if (typeof startRefreshOrder != undefined && startRefreshOrder == 1) {
		
		$.post("ajax/getActiveOrderResp", null, function(orderList) 
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

var startRefresh = 0;
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
  case 'notifiche':
    getMyNotifies();
    startRefreshOrder = 0;
    startRefresh = 0;
    break;
  case 'messaggi':
    getMyMessages();
    startRefreshOrder = 0;
    startRefresh = 0;
    break;
  case 'statResp':
    writeStatPageResp();
    startRefreshOrder = 0;
    startRefresh = 0;
    break;
  case 'orderResp':
    writeOrderPage();
    startRefreshOrder = 1;
    startRefresh = 0;
    break;
  case 'purchaseResp':
    writePurchasePage();
    startRefreshOrder = 0;
    startRefresh = 1;
    break;
  default:
    writeIndexPage();
    startRefreshOrder = 1;
    startRefresh = 0;
  }
}

function orderClicked(event)
{
  var History = window.History; 
  if (History.enabled == true) {
    event.preventDefault();
    var state = History.getState();
    var stateData = state.data;
    if (!!stateData && !!stateData.action && stateData.action == 'orderResp')
      return;
    History.pushState({
      action : 'orderResp'
    }, null, 'orderResp');
  }
}

function purchaseClicked(event) 
{
  var History = window.History; 
  if (History.enabled == true) {
      event.preventDefault();
        var state = History.getState();
        var stateData = state.data;
        if (!!stateData && !!stateData.action && stateData.action == 'purchaseResp')
            return;
        History.pushState({action : 'purchaseResp'}, null, 'purchaseResp');
    }
}

function statClicked(event) {
	var History = window.History;	
	  if (History.enabled == true) {
	    event.preventDefault();
	    var state = History.getState();
	    var stateData = state.data;
	    if (!!stateData && !!stateData.action
	        && stateData.action == 'statResp')
	      return;
	    History.pushState({
	      action : 'statResp'
	    }, null, 'statResp');
	  }
}

function writeOrderPage(){
  $("#bodyTitleHeader").html("Gestione ordini");
  $(".centrale").html("   <div id='tabsOrder'>" +
                          "<ul>" +
                           "<li><a href='#tabsOrder-1'>Crea Ordine</a></li>" +
                           "<li><a href='#tabsOrder-2'>Ordini Attivi</a></li>" +
                           "<li><a href='#tabsOrder-3'>Completati</a></li>" +
                           "<li><a href='#tabsOrder-4'>In Consegna</a></li>" +
                           "<li><a href='#tabsOrder-5'>Storico Ordini</a></li>" +
                          "</ul>" +
                              "<div id='tabsOrder-1'></div>" +
                              "<div id='tabsOrder-2'></div>" +
                              "<div id='tabsOrder-3'></div>" +
                              "<div id='tabsOrder-4'></div>" +
                              "<div id='tabsOrder-5'></div>" +
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
                          "<table id='activeOrderList' class='log'></table>" +
                            "<div id='errorDivActiveOrder' style='display:none;'>" +
                              "<fieldset><legend id='legendErrorActiveOrder'>&nbsp;Errore&nbsp;</legend><br />" +
                               "<div id='errorsActiveOrder' style='padding-left: 40px'>" +
                                "</div>" +
                              "</fieldset>" +
                            "</div><br />" +
                        "</div>" +
                        "<div id='dialog' title='Errore: Formato date non corretto'> <p>Seleziona entrambe le date (o nel corretto ordine cronologico). </p></div>" +
                        "<div id='dialog-map' title='Mappa dislocamento utenti partecipanti all'ordine' style='display:none; text-align:center' align='center'>" +
                      	  "<div id='map' style='width:575px; height:500px; text-align:center' align='center'> </div>" +
                        "</div>");
  
  $('#tabsOrder-3').html("<div class='logform'>" +
                          "<table id='completeOrderList' class='log'></table>" +
                            "<div id='errorDivCompleteOrder' style='display:none;'>" +
                              "<fieldset><legend id='legendErrorCompleteOrder'>&nbsp;Errore&nbsp;</legend><br />" +
                               "<div id='errorsCompleteOrder' style='padding-left: 40px'>" +
                                "</div>" +
                              "</fieldset>" +
                            "</div><br />" +
                        "</div>");
  
  $('#tabsOrder-4').html("<div class='logform'>" +
                  "<table id='shipOrderList' class='log'></table>" +
                    "<div id='errorDivShipOrder' style='display:none;'>" +
                      "<fieldset><legend id='legendErrorShipOrder'>&nbsp;Errore&nbsp;</legend><br />" +
                       "<div id='errorsShipOrder' style='padding-left: 40px'>" +
                        "</div>" +
                      "</fieldset>" +
                    "</div><br />" +
                "</div>");
  
  $('#tabsOrder-5').html("<div class='logform'>" +
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
 
  prepareOrderForm();
}

function writeIndexPage()
{
    writeOrderPage();
    startRefreshOrder = 1;
    startRefresh = 0;
}

function writeStatPageResp() {
  $("#bodyTitleHeader").html("Statistiche responsabile");
	$(".centrale").html("<div id='tabs'><ul>" +
						"<li><a href='#tabs-1'>Fornitori</a></li>" +
						"<li><a href='#tabs-2'>Utenti</a></li>" +
						"<li><a href='#tabs-3'>Movimenti</a></li>" +
						"<li><a href='#tabs-4'>Prodotti</a></li>" +
						"</ul>" +
					    "<div id='tabs-1'></div>" +
					    "<div id='tabs-2'></div>" +
					    "<div id='tabs-3'></div>" +
					    "<div id='tabs-4'></div>" +
					    "</div>");
	
	var selectString1 = "<select name='yearS1' id='yearS1' class='field' onchange='refreshStatOrdiniMese()'>";
	for(var thisYear = new Date().getFullYear(); thisYear > 1990; thisYear--)
		selectString1 += "<option value='" + thisYear + "'>" + thisYear + "</option>";
	selectString1 += "</select>";
	
	var selectString2 = "<select name='yearS2' id='yearS2' class='field' onchange='refreshStatOrdiniAnno()'>";
	for(var thisYear = new Date().getFullYear(); thisYear > 1990; thisYear--)
		selectString2 += "<option value='" + thisYear + "'>" + thisYear + "</option>";
	selectString2 += "</select>";
	
	var selectString3 = "<select name='yearS3' id='yearS3' class='field' onchange='refreshStatMese()'>";
	for(var thisYear = new Date().getFullYear(); thisYear > 1990; thisYear--)
		selectString3 += "<option value='" + thisYear + "'>" + thisYear + "</option>";
	selectString3 += "</select>";
	
	  $('#tabs-1').html("<table id='canvResp-1'>" +
	  		"<tr><th> Fornitore <select id='SupList-1' onchange='refreshStatOrdiniMese()'></select> Anno " + selectString1 +"</th></tr>" +
	  		"<tr><td id='tdRespOrdiniMese'><canvas id='canvasRespOrdiniMese' width='580' height='400'></canvas></td></tr>" +
	  		"<tr><th> Fornitore <select id='SupList-2' onchange='refreshStatOrdiniAnno()'></select> Anno " + selectString2 + "</th></tr>" +
	  		"<tr><td id='tdOrdiniAnno'><canvas id='canvasRespOrdiniAnno' width='580' height='500'></canvas></td></tr><table>");
	  $('#tabs-2').html("<table id='canvResp-2'>" +
		  		"<tr><th></th></tr>" +
		  		"<tr><td id='tdUtentiAttivi'><canvas id='canvasUtentiAttivi' width='580' height='400'></canvas></td></tr><table>");
	  $('#tabs-3').html("<table id='canvResp-3'>" +
		  		"<tr><th> Seleziona l'anno " + selectString3 + "</th></tr>" +
		  		"<tr><td id='tdStatRespMese'><canvas id='canvasStatRespMese' width='580' height='400'></canvas></td></tr><table>");
	  $('#tabs-4').html("<table id='canvResp-4'>" +
              "<tr><th></th></tr>" +
              "<tr><td id='tdProdTopProduct'><canvas id='canvasProdTopProduct' width='580' height='400'></canvas></td></tr><table>");
	  $('#tabs').tabs();
	  
	  writeStatistics();
}

function writeStatistics() {
	
	//Ricavare i fornitori
	$.postSync("ajax/getSupplierByResp", null, postGetSupplierByRespHandler);
	
	$('#canvResp-1').hide();
	$('#canvResp-2').hide();
	$('#canvResp-3').hide();
	$('#canvResp-4').hide();
	
	var idSup1 = $("#SupList-1").val();
	var idSup2 = $("#SupList-2").val();
	var year1 = $("#yearS1").val();
	var year2 = $("#yearS2").val();
	var year3 = $("#yearS3").val();
	
	if (idSup1 != null)
	  $.post("ajax/statRespOrderMonth", {idSupplier: idSup1, year: year1}, postStatRespOrderMonthHandler);
	if (idSup2 != null)
	  $.post("ajax/statRespOrderYear", {idSupplier: idSup2, year: year2}, postStatRespOrderYearHandler);
	$.post("ajax/statRespTopUsers", null, postStatRespTopUsersHandler);
	$.post("ajax/statRespMoneyMonth", {year: year3}, postStatRespMoneyMonthHandler);
	$.post("ajax/statProdTopProduct", null, postStatProdTopProductHandler);
	
	$('#canvResp-1').show('slow');
	$('#canvResp-2').show('slow');
	$('#canvResp-3').show('slow');
	$('#canvResp-4').show('slow');
}

function postGetSupplierByRespHandler(data) {

	var opt = "";
	
	$.each(data, function(index, val)
	{
		opt += "<option value='" + val.idMember + "'>" + val.companyName + "</option>";
	});
	
	$("#SupList-1").html(opt);
	$("#SupList-2").html(opt);
	
}

function refreshAllStat() {
	
	refreshStatOrdiniMese();
	refreshStatOrdiniAnno();
	refreshStatMese();
	refreshStatTopUser();
	refreshStatTopProduct();
	
}

function refreshStatOrdiniMese() {
	
	var year1 = $("#yearS1").val();
	var idSup1 = $("#SupList-1").val();
	
	$("#tdRespOrdiniMese").hide("slow");
	$("#tdRespOrdiniMese").html("<canvas id='canvasRespOrdiniMese' width='580' height='400'></canvas>");
	
	$.post("ajax/statRespOrderMonth", {idSupplier: idSup1, year: year1}, postStatRespOrderMonthHandler);
	
	$("#tdRespOrdiniMese").show("slow");
}

function refreshStatOrdiniAnno() {
	
	var year2 = $("#yearS2").val();
	var idSup2 = $("#SupList-2").val();
	
	$("#tdRespOrdiniAnno").hide("slow");
	$("#tdRespOrdiniAnno").html("<canvas id='canvasRespOrdiniAnno' width='580' height='400'></canvas>");
	
	$.post("ajax/statRespOrderYear", {idSupplier: idSup2, year: year2}, postStatRespOrderYearHandler);
	
	$("#tdRespOrdiniAnno").show("slow");
}

function refreshStatMese() {
	
	var year3 = $("#yearS3").val();
	
	$("#tdStatRespMese").hide("slow");
	$("#tdStatRespMese").html("<canvas id='canvasStatRespMese' width='580' height='400'></canvas>");
	
	$.post("ajax/statRespMoneyMonth", {year: year3}, postStatRespMoneyMonthHandler);
	
	$("#tdStatRespMese").show("slow");
}

function refreshStatTopUser() {
	
	$("#tdUtentiAttivi").hide("slow");
	$("#tdUtentiAttivi").html("<canvas id='canvasUtentiAttivi' width='580' height='400'></canvas>");
	
	$.post("ajax/statRespTopUsers", null, postStatRespTopUsersHandler);
	
	$("#tdUtentiAttivi").show("slow");
}

function refreshStatTopProduct() {
	
	$("#tdProdTopProduct").hide("slow");
	$("#tdProdTopProduct").html("<canvas id='canvasProdTopProduct' width='580' height='400'></canvas>");
	
	$.post("ajax/statRespMoneyMonth", {year: year3}, postStatRespMoneyMonthHandler);
	
	$("#tdProdTopProduct").show("slow");
}

function postStatRespOrderMonthHandler(data) {
	
	new CanvasXpress("canvasRespOrdiniMese", {
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
        "blockSeparationFactor": 2
      });
	
}

function postStatRespOrderYearHandler(data) {
	
	new CanvasXpress("canvasRespOrdiniAnno", {
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
      });
	
}

function postStatRespTopUsersHandler(data) {
	
	var json = ' { "x": { "Response": [ ' +
         			'"Resistant",' +
         			'"Resistant",' +
         			'"Resistant",' +
                    '"Sensitive",' +
                    '"Sensitive",' +
                    '"Sensitive",' +
                    '"Sensitive",' +
                    '"Sensitive" ] }, ';
	
    json += ' "y": { "vars": [ "Utente" ], "smps": [ ';
	
	var len = data.length/2;
	var i = 0;
	var json2 = "";
	
	if(data[0] != "errNoUser") {
		
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
			'\"title\": \"Top 10 Utenti attivi\", ' +
			'\"smpTitle\": \"Utenti\", ' +
			'\"colorScheme\": \"basic\", ' +
			'\"graphOrientation\": \"horizontal\", ' +
			'\"setMin\": 0, ' +
			'\"showLegend\": false }';
    
    var obj = jQuery.parseJSON(json);
    var obj2 = jQuery.parseJSON(json2);
    
    new CanvasXpress("canvasUtentiAttivi", obj, obj2);
	
}

function postStatRespMoneyMonthHandler(data) {
	
	new CanvasXpress("canvasStatRespMese", {
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
    
    new CanvasXpress("canvasProdTopProduct", obj, obj2);
    
}


var idOrder = 0;

function prepareOrderForm(tab){
    
    $('#tabsOrder').tabs();
    $( "#dialog" ).dialog({ autoOpen: false });

    $("body").delegate(".shipButton", "click", clickSetShipPurchaseHandler);
    $("body").delegate(".detailsButton", "click", clickShowDetailsPurchaseHandler);
    $("body").delegate(".detailsButton2", "click", clickShowDetailsHandler);
    $("body").delegate(".mapButton", "click", drawMapOrder);
    
    $('#maxDate2').datepicker({ defaultDate: 0, maxDate: 0 });
    $('#maxDate2').datepicker("setDate", Date.now());
    
    $('#orderOldRequest').on("click", clickOrderOldHandler);
    
    //Drag and drop
    loadSupplier();
    loadActiveOrder();
    loadCompleteOrder();
    loadShipOrder();
    
    
    $("#orderCompositor").hide();
    var tomorrow = new Date(); 
    tomorrow.setFullYear(tomorrow.getFullYear(),tomorrow.getMonth(),tomorrow.getDate()+1);
    $("#closeData").datepicker({ minDate: tomorrow });
    
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
	
	if(productList.length < 1) {
		
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

function loadShipOrder() {
	
    $.post("ajax/getDeliveredOrderResp", null, postShipOrderListHandler);
}

function postShipOrderListHandler(orderList) {
	
	console.log("Ricevuti Ordini In Consegna");
    
    $("#shipOrderList").html("");
    $("#shipOrderList").hide();
    
    if(orderList.length > 0){
        $("#shipOrderList").append(" <tr>  <th class='top' width='5%'> ID </th>" +
        								  "<th class='top' width='25%'> Nome </th>" +
                                          "<th class='top' width='20%'> Fornitore </th>" +
                                          "<th class='top' width='20%'> Data Consegna  </th>" +
                                          "<th class='top' width='20%'> Azione  </th> " +
                                          "<th class='top' width='10%'> Mappa  </th></tr>");
        
        for(var i = 0; i < orderList.length; i++){
            var order = orderList[i];
            
            var dateDelivery = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateDelivery));
            
            $("#shipOrderList").append("<tr id='idOrderShip_" + order.idOrder + "' data-shipcount='0'>" +
					            		"<td>" + order.idOrder +"</td>" +
										  "<td>" + order.orderName +"</td>" +
					            		  "<td>" + order.supplier.companyName + "</td>" +
					            		  "<td>" + dateDelivery + "</td>" +
					            		  "<td><button style='margin: 0px' type='submit' id='showDetailsShip_" + order.idOrder + "' data-idorder='" + order.idOrder + "'> Mostra Schede </button>" +
										  "</td>" +
										  "<td><img src='img/map.png' class='mapButton' height='12px' data-idorder='" + order.idOrder + "'></td>" +
					            		"</tr>");

            $("#shipOrderList").append("<tr class='detailsOrder' id='TRdetailsOrderShip_" + order.idOrder + "'><td colspan='6' id='TDdetailsOrderShip_" + order.idOrder + "'></td></tr>");
            $(".detailsOrder").hide();
            $("#showDetailsShip_" + order.idOrder).on("click", clickShowDetailShipsHandler);
           
        }
        
        $("button").button();
    
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
	idOrder = $(this).data('idorder');
    
    $.post("ajax/getPurchaseFromOrder", {idOrder: idOrder}, postShowPurchaseHandler);
	
}

function postShowPurchaseHandler(data) {
	
	var trControl = "#TRdetailsOrderShip_" + idOrder;
	var tdControl = "#TDdetailsOrderShip_" + idOrder;
    
	$(tdControl).html("<div style='margin: 10px'><table id='TABLEdetailsOrderShip_" + idOrder + "' class='log2'></table></div>");

	var tableControl = "#TABLEdetailsOrderShip_" + idOrder;
	
	$(tableControl).append("<tr> <th class='top' width='5%'> Scheda </th>" +
					            "<th class='top' width='25%'> Nome </th>" +
					            "<th class='top' width='15%'> Totale (&euro;)</th>" +
					            "<th class='top' width='30%'> Stato Ritiro  </th>" +
					            "<th class='top' width='25%'> Azione  </th> </tr>");
	
	var countShip = 0;
	
	$.each(data, function(index, val)
	{
		countShip++;
		
		var failed = false;
		$.postSync("ajax/isPurchaseFailed", {idPurchase: val.idPurchase}, function(data) { 
			failed = data; 
		});
		
		var tdRitiro = "";
		var imgs = "";
		if(failed) {
			countShip--;
			tdRitiro = "<td style='color: gray;'>Scheda Fallita</td>";
			var imgs = "<img src='img/details.png' class='detailsButton' height='12px' data-idpurchase='" + val.idPurchase + "'>";
		} else if(val.isShipped == "Spedizione Effettuata") {
			countShip--;
			tdRitiro = "<td style='color: green;'>Ok</td>";
			var imgs = "<img src='img/details.png' class='detailsButton' height='12px' data-idpurchase='" + val.idPurchase + "'>";
		} else {
			tdRitiro = "<td id='TDconsegna_" + val.idPurchase + "' style='color: red;'>Non Ritirato</td>";
			var imgs = "<img src='img/ship.png' class='shipButton' height='13px' data-idpurchase='" + val.idPurchase + "'>" +
            		   "<img src='img/details.png' class='detailsButton' height='12px' data-idpurchase='" + val.idPurchase + "'>";
		}
		
		var totPurchase = 0;
		$.postSync("ajax/getTotPurchaseCost", {idPurchase: val.idPurchase}, function(data) { totPurchase = data; });
		
		$(tableControl).append("<tr style='background-color: #EDEDED; text-color: #CFCFCF;'> <td>" + val.idPurchase + "</td>" +
                "<td>" + val.member.name + "</td>" +
                "<td>" + totPurchase + "</td>" + tdRitiro +
                "<td>" + imgs + "</td></tr>" +
              "<tr class='detailsPurchase' id='TRdetailsPurchase_" + val.idPurchase + "'>" +
				"<td colspan='5' id='TDdetailsPurchase_" + val.idPurchase + "'></td></tr>");
			
	});
	
	var idShipCount = "#idOrderShip_" + idOrder;
	$(idShipCount).data('shipcount', countShip);

	$(".detailsPurchase").hide();
	$("button").button();
	
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);
	
}
 
var idPurchase;

function clickSetShipPurchaseHandler() {
		
	$(".detailsPurchase").hide();
    idPurchase = $(this).data('idpurchase');
    var result = 0;
    
    $.postSync("ajax/setShip", {idPurchase: idPurchase}, function(data) {result = data;});
    
    var tdControl = "#TDconsegna_" + idPurchase;
    
    if(result == 0){
		$(tdControl).css('color','red');
		$(tdControl).html("Non Effettuata");
	} else {
		$(tdControl).css('color','green');
		$(tdControl).html("Effettuata");
		
		//aggiorno la quantit� degli ordini ancora da consegnare
		var idShipCount = "#idOrderShip_" + idOrder;
		var countShip = $(idShipCount).data('shipcount');
		countShip--;
		
		//Controllo se son state consegnate tutte le schede, in caso affermativo ricarico gli ordini in consegna
		
		if(countShip == 0) {
			loadShipOrder();
		} else {
			$(idShipCount).data('shipcount', countShip);
		}
		
	}
    
}
 
function clickShowDetailsPurchaseHandler()  {
	
	$(".detailsPurchase").hide();
    idPurchase = $(this).data('idpurchase');
    
    trControl = "#TRdetailsPurchase_" + idPurchase;
    tdControl = "#TDdetailsPurchase_" + idPurchase;
    trIdControl = "trShipProduct_" + idOrder + "_";
    
    $(tdControl).html("<div style='margin: 15px'><table id='TABLEdetailsPurchaseShip_" + idPurchase + "' class='log2'></table></div>");
    
    var tableControl = "#TABLEdetailsPurchaseShip_" + idPurchase;
    
    var productsid = [];
    
    $(tableControl).append("<tr>  <th class='top' width='15%'> Prodotto </th>" +
                                 "<th class='top' width='15%'> Categoria </th>" +
                                 "<th class='top' width='15%'> Descrizione  </th>" +
                                 "<th class='top' width='5%'> Costo </th>" +
                                 "<th class='top' width='10%'> Min-Max Buy </th>" +
                                 "<th class='top' width='5%'> Richiesta </th>" +
                                 "<th class='top' width='25%'> Progresso  </th>" +
                                 "<th class='top' width='5%'> Parziale  </th>" +
                                 " </tr>");
    
    var products = 0;
    $.postSync("ajax/getProductListFromPurchase", {idPurchase: idPurchase}, function(data){ products = data; });
    
    $.each(products, function(index, val)
    {
    	
    	productsid.push(val.idProduct);
    	
    	var amount = 0;
    	$.postSync("ajax/getAmountfromPurchaseNorm", {idPurchase: idPurchase, idProduct: val.idProduct}, function(data){ amount = data; });
    	
    	var idProgressBar = "pbProduct_" + idOrder + "_" + idPurchase + "_" + val.idProduct;
    	var parziale = amount * val.unitCost;
    	
        $(tableControl).append("<tr id='" + trIdControl + val.idProduct + "'><td>" + val.name + "</td>" +
        		                       "<td>" + val.category.description + "</td>" +
        		                       "<td>" + val.description + "</td>" +
        		                       "<td>" + val.unitCost + " &euro;</td>" +
        		                       "<td>" + val.minBuy + " - " + val.maxBuy + "</td>" +
    		                       	   "<td>" + amount + "</td>" +
    		                       	   "<td style='padding: 2px;'><div id='" + idProgressBar + "' style='text-align: center;'><span class='textProgressBar1order'></span></div></td>" +
    		                       	   "<td> " + parziale + " &euro;</td>" +
    		                   "</tr>");
        
        $( "#" + idProgressBar ).progressbar({	value: 0 });
        $( "#" + idProgressBar + " span" ).progressbar("00.00%");
		$( "#" + idProgressBar ).css('height', '1em');
		
    });
    
    //Aggiorno le progressbar dei prodotti.
    var allProgress = "";
    $.postSync("ajax/getProgressProductOfOrder", {idOrder: idOrder}, function(data)
    {
    	allProgress = data;
    });
    
	//Aggiorno progressbar
    $.each(allProgress, function(index, val)
    {
    	var temp = val.split(',');
    	var idProduct = temp[0];
    	var progress = parseFloat(temp[1]);
	   
    	var idProgressBarProduct =  "#pbProduct_" + idOrder + "_" + idPurchase + "_" + idProduct;
    	$(idProgressBarProduct).progressbar('value', progress);
    	$(idProgressBarProduct + " span").text(progress.toFixed(2) + "%");
    	
    	if(progress < 100) {
	    	//Aggiungo effetto grigio per prodotti non attivi
			var idTr = "#trShipProduct_" + idOrder + "_" + idProduct;
			$(idTr).addClass("noLimitProduct");
    	}
    	
    });
    
    $(trControl).show("slow");    
    $(tdControl).fadeIn(1000);
}

function loadActiveOrder() {
  
    $.post("ajax/getActiveOrderResp", null, postActiveOrderListHandler); 
}

function postActiveOrderListHandler(orderList) {
      
	$("#activeOrderList").hide();
    $("#activeOrderList").html("");
       
    if(orderList.length > 0){
        $("#activeOrderList").append("  <tr>  <th class='top' width='10%'> Nome </th>" +
                                             "<th class='top' width='15%'> Fornitore </th>" +
                                             "<th class='top' width='15%'> Data Inizio  </th>" +
                                             "<th class='top' width='15%'> Data Chiusura  </th>" +
                                             "<th class='top' width='25%'> Progresso </th>" +
                                             "<th class='top' width='10%'> Azione  </th>" +
                                             "<th class='top' width='10%'> Mappa  </th></tr>");
        
        
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
                                              "<td><div id='" + idProgressBar + "' style='text-align: center;'><span class='textProgressBar2order'></span></div></td>" +
                                              "<td><img src='img/details.png' class='detailsButton2' height='12px' data-idorder='" + order.idOrder + "'></td>" +
                                              "<td><img src='img/map.png' class='mapButton' height='12px' data-idorder='" + order.idOrder + "'></td>" +
                                              "</tr>" +
                                         "<tr class='detailsOrder' id='TRdetailsOrderActive_" + order.idOrder + "'><td colspan='7' id='TDdetailsOrderActive_" + order.idOrder + "'></td></tr>");
            $(".detailsOrder").hide();
            $( "#" + idProgressBar ).progressbar({	value: valProgress	});
            $( "#" + idProgressBar ).css('height', '1.8em');
            $( "#" + idProgressBar + " span" ).text(valProgress.toFixed(2) + "%");
        }
            
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
    idOrder = $(this).data('idorder');
    
    $.post("ajax/getProductListFromOrder", {idOrder: idOrder}, postShowDetailsHandler);
}

function postShowDetailsHandler(data) {
    
    var selectedTab = getSelectedTabIndex();
    var trControl = 0;
    var tdControl = 0;
    
    if(selectedTab == 1) {
        trControl = "#TRdetailsOrderActive_" + idOrder;
        tdControl = "#TDdetailsOrderActive_" + idOrder;
        trIdControl = "trActiveProduct_" + idOrder + "_";
    } else if(selectedTab == 2) {
        trControl = "#TRdetailsOrderComplete_" + idOrder;
        tdControl = "#TDdetailsOrderComplete_" + idOrder;
        trIdControl = "trCompleteProduct_" + idOrder + "_";
    } else if(selectedTab == 4) {
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
    		                       	   "<td style='padding: 2px;'><div id='" + idProgressBar + "' style='text-align: center;'><span class='textProgressBar1order'></span></div></td>" +
    		                       	   "<td id='" + idparzialeDIV + "'> 0 &euro;</td>" +
    		                   "</tr>");
        
        $( "#" + idProgressBar ).progressbar({	value: 0 });
        $( "#" + idProgressBar + " span").text("00.00%");
		$( "#" + idProgressBar ).css('height', '1em');
		
    });
    
    var stringProductsId = productsid.join(',');
    
    //aggiorno le quantit� acquistate e relativo parziale
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
    	$(idProgressBarProduct + " span").text(progress.toFixed(2) + "%");
    	
    	if(progress < 100) {
    		var selectedTab = getSelectedTabIndex();
    		
			if(selectedTab == 2) {
				var idTr = "#trCompleteProduct_" + idOrder + "_" + idProduct; 	
				$(idTr).addClass("noLimitProduct");
    		}
			
			if(selectedTab == 3) {
				var idTr = "#trShipProduct_" + idOrder + "_" + idProduct; 	
				$(idTr).addClass("noLimitProduct");
    		}
			
			if(selectedTab == 4) {
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
    $(idProgressBar + " span" ).text(valProgress.toFixed(2)	+ "%");
    
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
    	$(idProgressBarProduct + " span" ).text(progress.toFixed(2)	+ "%");
    	
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
	    
	    $("#orderCompositor").hide();
	    
	    //Riaggiorno la pagina degli ordini attivi
	    loadActiveOrder();
	}
	
}

function ClearOrder() {
	$('#orderCart ul').html("<div align='center' class='placeholder'><br />Trascina qui i prodotti da includere nell'ordine<br /><br /></div>");
	addedIds = [];
	$('#orderName').val("");
}

function loadCompleteOrder() {
    
    $.post("ajax/getCompleteOrderResp", null, postCompleteOrderListHandler);
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

function postCompleteOrderListHandler(orderList) {
	
	$("#completeOrderList").html("");
    $("#completeOrderList").hide();
    
    if(orderList.length > 0){
    	
    	$("#completeOrderList").append(" <tr><th class='top' width='10%'> Nome </th>" +
									        "<th class='top' width='20%'> Fornitore </th>" +
									        "<th class='top' width='15%'> Data Inizio  </th>" +
									        "<th class='top' width='15%'> Data Chiusura  </th>" +
									        "<th class='top' width='15%'> Data Consegna  </th>" +
									        "<th class='top' width='20%'> Azione  </th> " +
									        "<th class='top' width='5%'> Mappa  </th></tr>");
    	
    	
    	for(var i = 0; i < orderList.length; i++) {
    		
    		 var order = orderList[i];
             var dateOpen = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateOpen));
             var dateClose = $.datepicker.formatDate('dd-mm-yy', new Date(order.dateClose));
             
             $("#completeOrderList").append("<tr id='idOrder_" + order.idOrder + "'>" +
						            		 "<td>" + order.orderName +"</td>" +
						             		  "<td>" + order.supplier.companyName + "</td>" +
						             		  "<td>" + dateOpen + "</td>" +
						             		  "<td>" + dateClose + "</td>" +
						             		  "<td> <input type='text' id='dateDelivery_" + order.idOrder + "' style='width: 80px' onchange='dataDeliveryChange(" + order.idOrder + ")'/> </td>" +   	
											  "<td> <button style='margin: 0px' type='submit' id='setDateDelivery_" + order.idOrder + "' data-idorder='" + order.idOrder + "'> Set Consegna </button>" +
											  	   "<button style='margin: 0px' type='submit' id='showDetailsComplete_" + order.idOrder + "' data-idorder='" + order.idOrder + "'> Dettagli </button>" +
											  "</td>" +
											  "<td><img src='img/map.png' class='mapButton' height='12px' data-idorder='" + order.idOrder + "'></td>" +
											  "</tr>");
    		
             $("#completeOrderList").append("<tr class='detailsOrder' id='TRdetailsOrderComplete_" + order.idOrder + "'><td colspan='7' id='TDdetailsOrderComplete_" + order.idOrder + "'></td></tr>");
     		
             $("#dateDelivery_"+ order.idOrder).datepicker();
             
             $("#showDetailsComplete_" + order.idOrder).on("click", clickShowDetailsHandler);
             $("#setDateDelivery_" + order.idOrder).on("click", clickSetDateDeliveryHandler);
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
        
        $("#oldOrderList").show();
        $("#errorDivOldOrder").hide();
        $("#legendErrorOldOrder").html("Comunicazione");
        $("#errorsOldOrder").html("Non ci sono Ordini da visualizzare<br /><br />");
        $("#errorDivOldOrder").show("slow");
        $("#errorDivOldOrder").fadeIn(1000);
    
    }
    
}

function clickSetDateDeliveryHandler(event) {
    event.preventDefault();
   
    idOrder = $(this).data('idorder');
    var dateDelivery = $('#dateDelivery_' + idOrder).datepicker("getDate").getTime();

    var result = 0;
    $.postSync("ajax/setDeliveryDate", {idOrder: idOrder, dateDelivery: dateDelivery}, function(data){
    	result = data;
    });
    
    if(result == 0) {
        //Errore nell'attivazione
    	$("#dateDelivery_" + idOrder).css('background','#FFBFA8');
    } else {

    	//riaggiorno completati e in consegna
    	loadCompleteOrder();
    	loadShipOrder();
    }
}

function getSelectedTabIndex() { 
    return  $('#tabsOrder').tabs('option', 'selected');
}

function dataDeliveryChange(idOrder) {
	
	$("#dateDelivery_" + idOrder).css('background','#FFFFFF');
	
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