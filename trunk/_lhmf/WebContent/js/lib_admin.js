var numberOfMember;

(function(window, undefined) {
	var History = window.History;
	$ = window.jQuery;
	var histEnabled = History.enabled;
	if (!histEnabled)
		console.log("HTML 5 History API is disabled!");
	else
		History.Adapter.bind(window, 'statechange', historyStateChanged);

	$(function() {
		$("#logLink").click(logClicked);
		$("#userLink").click(userClicked);
		$("#productLink").click(productClicked);
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

function historyStateChanged() {
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
  case 'messaggi':
    getMyMessages();
    break;
  case 'productsMgmtAdmin':
    writeProductsPageAdmin();
    break;
  case 'statAdmin':
    writeStatPageAdmin();
    break;
  case 'log':
    writeLogPage();
    if (!!stateData.min && !!stateData.max && !!stateData.p && !!stateData.i)
    {
      $('#logMin').datepicker("setDate", new Date(stateData.min));
      $('#logMax').datepicker("setDate", new Date(stateData.max));
      showLogs(stateData.min, stateData.max, stateData.p, stateData.i);
    }
    else
    {
      $('#logMin').datepicker("setDate", Date.now());
      $('#logMax').datepicker("setDate", Date.now());
      showLogs(Date.now(), Date.now(), 1, 10);
    }
    break;
  case 'userMgmt':
    writeUserPage();
    break;
  case 'null':
    break;
  default:
    writeIndexPage();
  }
}

function logClicked(event) {
  var History = window.History;	
  if (History.enabled == true) {
    event.preventDefault();
    var state = History.getState();
    var stateData = state.data;
    if (!!stateData && !!stateData.action && stateData.action == 'log')
      return;
    History.pushState({
      action : 'log'
    }, null, 'log');
  }
}

function userClicked(event) {
  var History = window.History;	
  if (History.enabled == true) {
    event.preventDefault();
    var state = History.getState();
    var stateData = state.data;
    if (!!stateData && !!stateData.action
        && stateData.action == 'userMgmt')
      return;
    History.pushState({
      action : 'userMgmt'
    }, null, 'userMgmt');
  }
}

function statClicked(event) {
	var History = window.History;	
	  if (History.enabled == true) {
	    event.preventDefault();
	    var state = History.getState();
	    var stateData = state.data;
	    if (!!stateData && !!stateData.action
	        && stateData.action == 'statAdmin')
	      return;
	    History.pushState({
	      action : 'statAdmin'
	    }, null, 'statAdmin');
	  }
}

function productClicked(event) {
  var History = window.History;	
  if (History.enabled == true) {
    event.preventDefault();
    var state = History.getState();
    var stateData = state.data;
    if (!!stateData && !!stateData.action
        && stateData.action == 'productsMgmtAdmin')
      return;
    History.pushState({
      action : 'productsMgmtAdmin'
    }, null, 'productsMgmtAdmin');
  }
}

function showLogs(startTime, endTime, page, itemsPerPage, scroll){
  $.getSync("ajax/getlogs", {start: startTime, end: endTime}, function(logList){
    console.log("Ricevuti log");
    $("#logs").html("");
    $("#logs").hide();
    //$("#logs").fadeOut(500, function() {
      
    //});
    var startIndex = (page - 1) * itemsPerPage;
    
    var numberOfPages = Math.ceil(logList.length/itemsPerPage);
    
    if(startIndex > logList.length)
    {
      page = numberOfPages;
      startIndex = (page - 1) * itemsPerPage;
    }

    var endBound = page * itemsPerPage;

    if (logList.length > 0)
    {
      $("#logs").append(
          "<tr>  <th class='top' width='10%'> ID </th>"
              + "<th class='top' width='20%'> Membro </th>"
              + "<th class='top' width='20%'> Timestamp  </th>"
              + "<th class='top' width='50%'> Testo  </th> </tr>");
      if (endBound > logList.length)
        endBound = logList.length;
      for ( var i = startIndex; i < endBound; i++)
      {
        var log = logList[i];
        var dateTemp = $.datepicker.formatDate('dd-mm-yy', new Date(
            log.logTimestamp));
        $("#logs").append(
            "<tr> <td>" + log.idLog + "</td>" + "<td>" + log.member.name + " "
                + log.member.surname + "</td>" + "<td>" + dateTemp + "</td>"
                + "<td>" + log.logtext + "</td></tr>");
      }
      var pages = "<option value='1' selected='selected'>1</option>";
      for (i = 2; i <= numberOfPages; i++)
        pages += "<option value='"+i+"'>"+i+"</option>";
      $("#logPage").html(pages);
      $("#logPage").val(page);
      $("#logItemsPerPage").val(itemsPerPage);
      $("#logsAmountDisplay").html("&nbsp;"+logList.length+" log trovati");
      $("#logs").fadeIn(1000);
    }
    else
    {
      $("#logs").show();
      $("#errorDivLog").hide();
      $("#legendErrorLog").html("Comunicazione");
      $("#errorsLog").append("Non ci sono Log  da visualizzare<br /><br />");
      $("#errorDivLog").show("slow");
      $("#errorDivLog").fadeIn(1000);
    }
  });
}

function writeStatPageAdmin() {
  $("#bodyTitleHeader").html("Statistiche amministratore");
	$(".centrale").html("<div id='tabs'><ul>" +
			"<li><a href='#tabs-1'>Utenti</a></li>" +
			"</ul>" +
			"<div id='tabs-1'></div>" +
	  		"</div>");
	
	var selectString = "<select name='yearS' id='yearS' class='field' onchange='refreshStatMemberReg()'>";
	for(var thisYear = new Date().getFullYear(); thisYear > 1990; thisYear--)
		selectString += "<option value='" + thisYear + "'>" + thisYear + "</option>";
	selectString += "</select>";
	
	  $('#tabs-1').html("<table id='canvAdmin-1'>" +
	  		"<tr><th></th></tr>" +
	  		"<tr><td id='tdTipologiaUtenti'><canvas id='canvasTipologiaUtenti' width='580' height='400'></canvas></td></tr>" +
	  		"<tr><th> Seleziona l'anno " + selectString +"</th></tr>" +
	  		"<tr><td id='tdIscrizioneUtenti'><canvas id='canvasIscrizioneUtenti' width='580' height='500'></canvas></td></tr><table>");
	  $('#tabs').tabs();
	  
	  writeStatistics();
}

function writeStatistics(){
	
	$('#canvAdmin-1').hide();
	
	var year = $("#yearS").val();
	
	$.post("ajax/statMemberType", null, postStatMemberTypeHandler);
	$.post("ajax/statMemberReg", {year: year}, postStatMemberRegHandler);
	
	$('#canvAdmin-1').show('slow');
}

function refreshAllStat() {
	
	refreshStatMemberType();
	refreshStatMemberReg();
	
}

function refreshStatMemberType(){
	
	
	$("#tdTipologiaUtenti").hide("slow");
	$("#tdTipologiaUtenti").html("<canvas id='canvasTipologiaUtenti' width='580' height='500'></canvas>");
	
	$.post("ajax/statMemberType", null, postStatMemberTypeHandler);
	
	$("#tdTipologiaUtenti").show("slow");
}

function refreshStatMemberReg(){
	
	var year = $("#yearS").val();
	
	$("#tdIscrizioneUtenti").hide("slow");
	$("#tdIscrizioneUtenti").html("<canvas id='canvasIscrizioneUtenti' width='580' height='500'></canvas>");
	
	$.post("ajax/statMemberReg", {year: year}, postStatMemberRegHandler);
	
	$("#tdIscrizioneUtenti").show("slow");
}

function postStatMemberRegHandler(data){
	
	new CanvasXpress("canvasIscrizioneUtenti", {
        "y": {
          "vars": [
			"Utenti",
			"Responsabili",
			"Fornitori"
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
            "Iscrizioni"
          ],
          "data": [
            [
              data[0],
              data[3],
              data[6],
              data[9],
              data[12],
              data[15],
              data[18],
              data[21],
              data[24],
              data[27],
              data[30],
              data[33]
            ],
            [
              data[1],
              data[4],
              data[7],
              data[9],
              data[13],
              data[16],
              data[19],
              data[22],
              data[25],
              data[28],
              data[31],
              data[34]
            ],
            [
              data[2],
              data[5],
              data[8],
              data[11],
              data[14],
              data[17],
              data[20],
              data[23],
              data[26],
              data[29],
              data[32],
              data[35]
            ]
          ]
        },
        "t": {
          "smps": ""
        }
      }, {
        "graphType": "Bar",
        "showAnimation": true,
        "graphOrientation": "horizontal",
        "showSmpDendrogram": true,
        "smpDendrogramPosition": "right",
        "title": "Distribuzione Iscritti",
        "smpHairlineColor": false,
        "smpTitle": "Mesi Dell'anno"
      });
	
}

function postStatMemberTypeHandler(data){
	
	new CanvasXpress("canvasTipologiaUtenti", {
        "y": {
          "vars": [
            "Utenti",
            "Responsabili",
            "Fornitori"
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
            ],
            [
              data[2]
            ]
          ]
        }
      }, {
        "graphType": "Pie",
        "title": "Divisione, in percentuale, della tipologia degli utenti iscritti",
        "pieSegmentPrecision": 1,
        "pieSegmentSeparation": 2,
        "pieSegmentLabels": "outside",
        "pieType": "solid",
      });
	
}

function writeLogPage(){
  $("#bodyTitleHeader").html("Consultazione log");
  $(".centrale").html("<div id='tabs'><ul><li><a href='#tabs-1'>Consulta Log</a></li></ul>" +
  "<div id='tabs-1'></div></div>");
  $('#tabs-1')
      .html(
          "<div class='logform'>"
              + "<form method='get' action='log'>"
              + "<fieldset><legend>&nbsp;Seleziona intervallo di date:&nbsp;</legend><br />"
              + "<label for='logMin' class='left'>Data iniziale: </label>"
              + "<input type='text' id='logMin' class='field'/>"
              + "<label for='logMax' class='left'>Data finale: </label>"
              + "<input type='text' id='logMax' class='field'/>"
              + "<label for='logPage' class='left'>Pagina: </label>"
              + "<select id='logPage' class='field'></select>"
              + "<label for='logItemsPerPage' class='left'>Elementi per pagina: </label>"
              + "<select id='logItemsPerPage' class='field'>"
              + "<option value='10' selected='selected'> 10 </option>"
              + "<option value='25'> 25 </option>"
              + "<option value='50'> 50 </option>"
              + "</select>"
              + "</fieldset>"
              + "</form>"
              + "<br/><p id='logsAmountDisplay'></p>"
              + "<table id='logs' class='log'></table>"
              + "<div id='errorDivLog' style='display:none;'>"
              + "<fieldset><legend id='legendErrorLog'>&nbsp;Errore&nbsp;</legend><br />"
              + "<div id='errorsLog' style='padding-left: 40px'>"
              + "</div>"
              + "</fieldset>"
              + "</div>"
              + "</div>"
              + "<div id='dialog' title='Errore: Formato date non corretto'> <p>Seleziona entrambe le date (o nel corretto ordine cronologico). </p></div>");
  $('#tabs').tabs();
  $( "#dialog" ).dialog({ autoOpen: false });
  prepareLogForm();
}

function writeUserPage(tab){
  $("#bodyTitleHeader").html("Gestione utenti");
  $(".centrale").html("<div id='tabs'><ul><li><a href='#tabs-1'>Aggiungi utente</a></li><li><a href='#tabs-2'>Attiva utente</a></li>" +
      "<li><a href='#tabs-3'>Lista utenti</a></li></li></ul>" +
      "<div id='tabs-1'></div><div id='tabs-2'></div><div id='tabs-3'></div>");
  
  // HTML per la registrazione utenti e fornitori
  $('#tabs-1').html("<div class='registrazioneform' style='margin: 2em 0 0 65px;'>" +
            "<form  id='regform' action='newMember' method='post'>" +
            "<fieldset><legend>&nbsp;Dati per la Registrazione&nbsp;</legend><br />" +
            "<label for='username' class='left'>Username:</label>" +
            "<input type='text' name='username' id='username' class='field' required='required'/>" +
        "<br><br><label for='firstname' class='left'>Nome: </label>" +
            "<input type='text' name='firstname' id='firstname' class='field' required='required'/>" +
          "<br><label for='lastname' class='left'>Cognome: </label>" +
            "<input type='text' name='lastname' id='lastname' class='field' required='required'/>" +
          "<br><label for='email' class='left'>Email: </label>" +
            "<input type='text' name='email' id='email' class='field' required='required'/>" +
          "<br><label for='address' class='left'>Indirizzo: </label>" +
            "<input type='text' name='address' id='address' class='field' required='required'/>" +
          "<br><label for='city' class='left'>Citt&agrave: </label>" +
            "<input type='text' name='city' id='city' class='field' required='required'/>" +
          "<br><label for='state' class='left'>Stato: </label>" +
              "<input type='text' name='state' id='state' class='field' required='required'/>" +
          "<br><label for='cap' class='left'>Cap: </label>" +
               "<input type='text' name='cap' id='cap' class='field' required='required'/>" +
          "<br><label for='phone' class='left'>Telefono: </label>" +
               "<input type='text' name='phone' id='phone' class='field'/>" +
          "<br><br><label for='mtype' class='left'>Tipo Utente: </label>" +
               "<select name='mtype' id='mtype' class='field' onchange='checkRespSelect()'>" +
                "<option value='0'>Normale</option>" +
                "<option value='1'>Responsabile  </option>" +
                "<option value='3'>Fornitore </option>" +
               "</select>" +
                "</fieldset>" +
                "<fieldset id='respFieldset' ><legend>&nbsp;Dati Fornitore&nbsp;</legend><br />" +
            "<br><label for='company' class='left'>Compagnia: </label>" +
                "<input type='text' name='company' id='company' class='field' />" +
              "<br><label for='description' class='left'>Descrizione: </label>" +
                "<input type='text' name='description' id='description' class='field' />" +
              "<br><label for='contactName' class='left'>Contatto: </label>" +
                  "<input type='text' name='contactName' id='contactName' class='field' />" +
            "<br><label for='fax' class='left'>Fax: </label>" +
                "<input type='text' name='fax' id='fax' class='field' />" +
            "<br><label for='website' class='left'>WebSite: </label>" +
                "<input type='text' name='website' id='website' class='field' />" +
             "<br><label for='payMethod' class='left'>Metodo Pagamento: </label>" +
                 "<input type='text' name='payMethod' id='payMethod' class='field' />" +
               "<br><br><label for='mtype' class='left'>Responsabile: </label>" +
                 "<select name='memberResp' id='memberResp' class='field'>" +
             "</select>" +
                "</fieldset>" +
                "<div id='errorDiv' style='display:none;'>" +
                  "<fieldset><legend id='legendError'>&nbsp;Errore&nbsp;</legend><br />" +
                   "<div id='errors' style='padding-left: 40px'>" +
                "</div>" +
                  "</fieldset>" +
                "</div>" +
                "<p><input type='submit' id='regRequest' class='button' value='Registra'/></p>" +
                "</form>" +
              "</div><p></p>");
  
  // TAB per attivazione utenti
  
  $('#tabs-2').html("<div class='logform'>" +
		              "<form method='post' action=''>" +
		                "<fieldset><legend>&nbsp;Opzioni di Ricerca:&nbsp;</legend><br />" +
		                  "<label for='memberTypeActive' class='left'>Tipo Membro: </label>" +
		                  "<select name='memberTypeActive' id='memberTypeActive' class='field' style='width: 130px; padding: 2px 2px 2px 5px;'>" +
		                    "<option value='0'> Normale </option>" +
		                    "<option value='1'> Responsabile </option>" +
		                    "<option value='3'> Fornitore </option>" +
		                    "<option value='4'> Tutti </option>" +
		               "</select>" +
		                  "<label for='pageActive' class='left'>&nbsp;&nbsp;Pagina: </label>" +
		                  "<select name='pageActive' id='pageActive'>" +
		                    "<option value='0'> ... </option>" +
		                  "</select>" +
		                  "<label for='itemsPerPageActive' class='left'>&nbsp;&nbsp;Risultati Per Pagina: </label>" +
		                  "<select name='itemsPerPageActive' id='itemsPerPageActive'>" +
		                    "<option value='10'> 10 </option>" +
		                    "<option value='25'> 25 </option>" +
		                    "<option value='50'> 50 </option>" +
		                  "</select>" +
		                "</fieldset>" +
		              "</form>" +
		              "<table id='memberList' class='log'></table>" +
		              "<div id='errorDiv2' style='display:none;'>" +
		                  "<fieldset><legend id='legendError2'>&nbsp;Errore&nbsp;</legend><br />" +
		                   "<div id='errors2' style='padding-left: 40px'></div>" +
		                  "</fieldset>" +
		              "</div><br />" +
		                "</div>");
  
  
  $('#tabs-3').html("<div class='logform'>" +
                              "<form method='post' action=''>" +
                              "<fieldset><legend>&nbsp;Opzioni di Ricerca:&nbsp;</legend><br />" +
                                "<label for='memberType2' class='left'>Tipo Membro: </label>" +
                                "<select name='memberType2' id='memberType2' class='field' style='width: 130px; padding: 2px 2px 2px 5px;'>" +
                                    "<option value='0'> Normale </option>" +
                                    "<option value='1'> Responsabile </option>" +
                                    "<option value='3'> Fornitore </option>" +
                                    "<option value='4'> Tutti </option>" +
                                 "</select>" +
                                "<label for='page2' class='left'>&nbsp;&nbsp;Pagina: </label>" +
                                "<select name='page2' id='page2'>" +
                                    "<option value='0'> ... </option>" +
                                "</select>" +
                                "<label for='itemsPerPage2' class='left'>&nbsp;&nbsp;Risultati Per Pagina: </label>" +
                                "<select name='itemsPerPage2' id='itemsPerPage2'>" +
                                    "<option value='10'> 10 </option>" +
                                    "<option value='25'> 25 </option>" +
                                    "<option value='50'> 50 </option>" +
                                "</select>" +
                              "</fieldset>" +
                            "</form>" +
                            "<table id='memberList2' class='log'></table>" +
                              "<div id='errorDiv3' style='display:none;'>" +
                                "<fieldset><legend id='legendError3'>&nbsp;Errore&nbsp;</legend><br />" +
                                 "<div id='errors3' style='padding-left: 40px'>" +
                                  "</div>" +
                                "</fieldset>" +
                              "</div><br />" +
                          "</div>");

  prepareUserForm(tab);
}

function writeIndexPage(){
    writeUserPage();
}

function getAllProductsNoLocal()
{
  var myProductsList;
  $.getJSONsync("ajax/getproducts", function(productsList)
  {
    myProductsList = productsList;
  });
  return myProductsList;
}

function writeProductsPageAdmin()
{
  $("#bodyTitleHeader").html("Gestione prodotti");
  var categoriesList = getCategoriesNoLocal();
  var categoriesString = "<option value='notSelected' selected='selected'>Seleziona...</option>";
  for ( var catIndex in categoriesList)
  {
    categoriesString += "<option value='"
        + categoriesList[catIndex].idProductCategory + "'>"
        + categoriesList[catIndex].description + "</option>";
  }

  var allProducts = getAllProductsNoLocal();
  var initialNumberOfPages = allProducts.length / 10;
  if (allProducts.length % 10 > 0)
    initialNumberOfPages += 1;

  var pagesForListino = "";
  for ( var page = 1; page <= initialNumberOfPages; page++)
    pagesForListino += "<option value='" + page + "'>" + page + "</option>";

  $(".centrale")
      .html(
          "<div class='listinoform'>"
              + "<form method='post' action=''>"
              + "<fieldset><legend>&nbsp;Opzioni di Ricerca:&nbsp;</legend><br />"
              + "<label for='productCategorySearch' class='left'>Categoria: </label>"
              + "<select name='productCategorySearch' id='productCategorySearch' class='field'>"
              + categoriesString
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
              + "</form>"
              + "<table id='productsListTable' class='list'>"
              + "</table>"
              + "<div id='errorDiv2' style='display:none;'>"
              + "<fieldset><legend id='legendError2'>&nbsp;Errore&nbsp;</legend><br />"
              + "<div id='errors2' style='padding-left: 40px'>"
              + "</div>"
              + "</fieldset>" + "</div><br />" + "</div>");

  $('#productCategorySearch').change(newProductSearch);
  $('#pageSearch').change(newProductSearch);
  $('#itemsPerPageSearch').change(newProductSearch);
  newProductSearch();
  registerForProductsUpdates();
}

function registerForProductsUpdates()
{
  var source = new EventSource('productsUpdatesAdmin');
  source.onmessage = function(event)
  {
    console.debug("Reloading products..");
    newProductSearch();
  };
}

function newProductSearch()
{
  var productCategory = $("#productCategorySearch").val();
  var page = $("#pageSearch").val();
  var itemsPerPage = $("#itemsPerPageSearch").val();
  var myProductsUnfiltered = getAllProductsNoLocal();
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
          && productCategory != myProducts[prodIndex].category.idProductCategory)
        continue;
      productsString += "<tr id='listRow" + myProducts[prodIndex].idProduct
          + "'><td><img src='" + myProducts[prodIndex].imgPath
          + "' height='60' class='thumb'></td><td>"
          + myProducts[prodIndex].name + "</td>" + "<td>"
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
    $('input:submit').button();
  }
}

function prepareLogForm(){
	$("#logMin").datepicker({
		defaultDate: 0,
		maxDate: 0
		});
	//$('#logMin').datepicker("setDate", Date.now());
	$('#logMax').datepicker({
		defaultDate: 0,
		maxDate: 0
		});
	//$('#logMax').datepicker("setDate", Date.now());
	
	$("#logMin").change(logRequestHandler);
  $("#logMax").change(logRequestHandler);
  $("#logPage").change(logRequestHandler);
  $("#logItemsPerPage").change(logRequestHandler);
 
  var startDate = new Date();
  startDate.setHours(0);
  startDate.setMinutes(0);
  startDate.setSeconds(0);
  startDate.setMilliseconds(0);
  var endDate = new Date();
  endDate.setHours(23);
  endDate.setMinutes(59);
  endDate.setSeconds(59);
  endDate.setMilliseconds(999);
  var logsAmount = 0;
  $.getSync("ajax/getlogsamount", {
    start : (startDate.getTime()),
    end : (endDate.getTime())
  }, function(amount)
  {
    logsAmount = amount;
  });
  var initialNumberOfPages = Math.ceil(logsAmount/10);
  var pages = "<option value='1' selected='selected'>1</option>";
  for (i = 2; i <= initialNumberOfPages; i++)
    pages += "<option value='"+i+"'>"+i+"</option>";
  $("#logPage").html(pages);
  $("#logsAmountDisplay").html("&nbsp;"+logsAmount+" log trovati");
  //logRequestHandler();
}

function logRequestHandler()
{
  var scroll = $("body").scrollTop();
  var startDate = $('#logMin').datepicker("getDate");
  var endDate = $('#logMax').datepicker("getDate");
  var page = $("#logPage").val();
  var itemsPerPage = $("#logItemsPerPage").val();
  if(startDate == null || endDate == null || startDate > endDate){
    // $('body').append('<div id="dialog" title="Errore nell\'input delle date">
    // <p>Selezionale entrambe le date (o nel corretto ordine cronologico).
    // </p></div>');
    $( "#dialog" ).dialog('open');
  }
  else
  {
    startDate.setHours(0);
    startDate.setMinutes(0);
    startDate.setSeconds(0);
    startDate.setMilliseconds(0);
    endDate.setHours(23);
    endDate.setMinutes(59);
    endDate.setSeconds(59);
    endDate.setMilliseconds(999);

    var History = window.History;
    if (History.enabled)
      History
          .pushState({
            action : 'log',
            min : startDate.getTime(),
            max : endDate.getTime(),
            p : page,
            i : itemsPerPage
          }, null, "./log?min=" + startDate.getTime() + "&max="
              + endDate.getTime());
    else
    {
      $("form #logMin").val(startDate.getTime());
      $("form #logMax").val(endDate.getTime());
      $("form").submit();
    }
    console.log("Date selezionate: " + JSON.stringify(startDate) + ", " + JSON.stringify(endDate) + ", (timestamps):" + startDate.getTime() + ", " + endDate.getTime());
  }
}

function prepareUserForm(tab){
	$('#tabs').tabs({
		  selected: tab 
	});
	
	//disabilitare fieldset resp
	
	$('#respFieldset').hide();
	$('#respFieldset').children().attr("disabled", "disabled");
	
	$('#regRequest').on("click", clickRegHandler);	
	
	$('#memberTypeActive').change(clickActMemberHandler);
	$('#pageActive').change(clickActMemberHandler);
	$('#itemsPerPageActive').change(clickActMemberHandler);
	
	$('#memberType2').change(clickGetMemberHandler);
  $('#page2').change(clickGetMemberHandler);
  $('#itemsPerPage2').change(clickGetMemberHandler);
  
	activeUserSearch();
	userSearch();
}

function clickGetMemberHandler(event)
{
  event.preventDefault();
  userSearch();
}

function userSearch()
{
  var memberType = $('#memberType2').val();
  var page = $('#page2').val();
  var itemsPerPage = $('#itemsPerPage2').val();

  // normale o responsabile
  $.post("ajax/getMembersList", {
    memberType : memberType,
    page : page,
    itemsPerPage : itemsPerPage
  }, postMemberListHandler);
}

function clickActMemberHandler(event){
	event.preventDefault();
	
	activeUserSearch();
}

function activeUserSearch()
{
  var memberType = $('#memberTypeActive').val();
  var page = $('#pageActive').val();
  var itemsPerPage = $('#itemsPerPageActive').val();
  
  //normale o responsabile
  $.post("ajax/getMembersToActivate", { memberType: memberType,
                      page: page,
                      itemsPerPage: itemsPerPage }, postMemberToActivateHandler);
}

function clickMemberActivationHandler(event)
{
  event.preventDefault();

  var form = $(this).parents('form');
  var idMember = $('input', form).val();

  $.post("ajax/activeMember", {
    idMember : idMember
  }, postMemberActivationHandler);

}

function postMemberActivationHandler(result) {
	
	if(result == 0) {
		
        $("#errorDiv2").hide();
        $("#legendError2").html("Errore");
        $("#errors2").html("Attivazione utente fallita<br /><br />");
        $("#errorDiv2").show("slow");
        $("#errorDiv2").fadeIn(1000);
        
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
        
        //qui c'� il numero di pagine. Generare le options del pageSelect
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
                var dateTemp = $.datepicker.formatDate('dd-mm-yy', new Date(val.regDate));
                output.push("<tr> <td>" + val.idMember +"</td>" +
                		         "<td>" + val.name + " " + val.surname + "</td>" +
                				 "<td>" + dateTemp + "</td>" +
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
		
		var memberType = $('#memberTypeActive').val();
		
		if(memberType == 4) 
			$("#errors2").append("Non ci sono Utenti da visualizzare<br /><br />");
		if(memberType == 3) 
			$("#errors2").append("Non ci sono Fornitori da visualizzare<br /><br />");
		if(memberType == 1) 
			$("#errors2").append("Non ci sono Responsabili  da visualizzare<br /><br />");
		if(memberType == 0) 
			$("#errors2").append("Non ci sono Membri Normali da visualizzare<br /><br />");
		
		
		$("#errorDiv2").show("slow");
		$("#errorDiv2").fadeIn(1000);
	} else {
		
		$("#memberList").hide();
		
		//Costruire le option delle pagine
		var mtype = $('#memberTypeActive').val();
		$.postSync("ajax/getNumberItemsToActivate", {memberType: mtype}, function(numberItems){ numberOfMember = numberItems; });
		
		//qui c'� il numero di pagine. Generare le options del pageSelect
		var out = [];
		var itemsPerPage = $('#itemsPerPageActive').val();
		var npagine = Math.ceil(numberOfMember / itemsPerPage);
		
		for(var i = 0; i < npagine;)
			out.push('<option value="'+ i +'"> ' + (++i) + '</option>');
		
		$('#pageActive').html(out.join(''));
		
		//Costruzione tabella con utenti
		var output = [];
		output.push("	<tr>  <th class='top' width='5%'> ID </th>" +
							 "<th class='top' width='30%'> Membro </th>" +
							 "<th class='top' width='25%'> Tipo  </th>" +
							 "<th class='top' width='25%'> Stato  </th>" +
							 "<th class='top' width='25%'> Attiva  </th> </tr>");
		
		
		
		
		$.each(data, function(index, val)
		{
				output.push("<tr id='ActMember_" + val.idMember + "'><td>" + val.idMember +"</td><td>" + val.name + " " + val.surname + "</td><td>" +
						val.memberType + "</td><td>" + val.memberStatus + "</td><td>" + 
						"<form method='post'><input type='hidden' value='" + val.idMember + "'/>" +
						"<button type='submit' id='memberActivation_" + val.idMember + "'> Attiva </button></form></td></tr>");
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
	var phone = $('#phone').val();
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
			$.post("ajax/newMember", {	username: username,
										firstname: firstname,
										lastname: lastname,
										email: email,
										address: address,
										city: city,
										state: state,
										cap: cap,
										phone: phone,
										mType: mType}, postRegHandler);
			
		} else {
			
			// Registrazione Fornitore
			
			$.post("ajax/newSupplier", {	username: username,
											firstname: firstname,
											lastname: lastname,
											email: email,
											address: address,
											city: city,
											state: state,
											cap: cap,
											phone: phone,
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

function isValidMail(email)
{
  var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
  if (reg.test(email) == false)
  {
    return false;
  }
  return true;
}
