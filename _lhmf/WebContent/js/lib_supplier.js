(function(window, undefined) {
	var History = window.History;
	$ = window.jQuery;
	var histEnabled = History.enabled;
	if (!histEnabled)
		console.log("HTML 5 History API is disabled!");
	else
		History.Adapter.bind(window, 'statechange', historyStateChanged);

	$(function() {
		$("#productsLink").click(productsClicked);
		$("#statLink").click(statClicked);
		$("#notifiesLink").click(notifiesClicked);
		$("#messagesLink").click(messagesClicked);
    registerForMessages();
    registerForNotifies();
  
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
  case 'productsMgmt':
	  writeSupplierPage(0);
	break;
  case 'statSupplier':
    writeStatPageSupplier();
    break;
  case 'notifiche':
    getMyNotifies();
    break;
  case 'messaggi':
    getMyMessages();
    break;
  default:
    writeIndexPage();
  }
}

function productsClicked(event) {
	  var History = window.History;	
	  if (History.enabled == true) {
	    event.preventDefault();
	    var state = History.getState();
	    var stateData = state.data;
	    if (!!stateData && !!stateData.action
	        && stateData.action == 'productsMgmt')
	      return;
	    History.pushState({
	      action : 'productsMgmt'
	    }, null, 'productsMgmt');
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

function writeStatPageSupplier() {
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
        "title": "Prodotti in Listino/Non in Listino",
        "pieSegmentPrecision": 1,
        "pieSegmentSeparation": 2,
        "pieSegmentLabels": "outside",
        "pieType": "solid",
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
        "blockSeparationFactor": 2
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
      });
}

function updateCategory(productCategory)
{
  if (productCategory == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  $.postJSONsync("ajax/updateproductcategory", productCategory, function(
      rowsAffected)
  {
    console.debug("Updated: " + rowsAffected);
  });
}

function getCategories()
{
  $.getJSONsync("ajax/getproductcategories", function(productCategoriesList)
  {
    window.localStorage.setItem('productCategoriesList', JSON
        .stringify(productCategoriesList));
    console.debug("productCategoriesList saved in localstorage");
  });
}

function loadCategoriesFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('productCategoriesList'));
}

function deleteCategory(idProductCategory)
{
  if (idProductCategory == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  $.postJSONsync("ajax/deleteproductcategory", idProductCategory, function(
      rowsAffected)
  {
    console.debug("Deleted: " + rowsAffected);
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

function getCategoriesAsTableRows(productCategoriesList, page, itemsPerPage)
{
  var returnedTableString = "";
  if (page < 1 || (page - 1) * itemsPerPage >= productCategoriesList.length
      || itemsPerPage < 1 || itemsPerPage > 100
      || productCategoriesList == undefined || page == undefined
      || itemsPerPage == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return "";
  }
  for ( var productIndex = (page - 1) * itemsPerPage; productIndex < productCategoriesList.length
      && productIndex <= page * itemsPerPage; productIndex++)
  {
    returnedTableString += "<tr>";
    returnedTableString += "<td>"
        + productCategoriesList[prodIndex].description + "</td>";
    returnedTableString += "</tr>";
  }
  return returnedTableString;
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

function getAllProducts()
{
  $.getJSONsync("ajax/getproducts", function(productsList)
  {
    window.localStorage.setItem('productsList', JSON.stringify(productsList));
    console.debug("productsList saved in localstorage");
  });
}

function loadAllProductsFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('productsList'));
}

function getMyProducts()
{
  $.getJSONsync("ajax/getmyproducts",
      function(productsList)
      {
        window.localStorage.setItem('myProductsList', JSON
            .stringify(productsList));
        console.debug("myProductsList saved in localstorage");
      });
}

function getMyProductsNoLocal()
{
  var myProductsList;
  $.getJSONsync("ajax/getmyproducts", function(productsList)
  {
    myProductsList = productsList;
  });
  return myProductsList;
}

function loadMyProductsFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('myProductsList'));
}

function deleteAllMyProducts()
{
  myProductsList = loadAllMyProductsFromLocalStorage();

  for ( var prodIndex in myProductsList)
  {
    deleteProduct(myProductsList[prodIndex].id_product);
  }
}

function getProductsAsTableRows(productsList, productCategoriesList, page,
    itemsPerPage, suppliersList)
{
  var returnedTableString = "";
  if (page < 1 || (page - 1) * itemsPerPage >= productsList.length
      || itemsPerPage < 1 || itemsPerPage > 100 || productsList == undefined
      || productCategoriesList == undefined || page == undefined
      || itemsPerPage == undefined || suppliersList == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return "";
  }
  for ( var productIndex = (page - 1) * itemsPerPage; productIndex < productsList.length
      && productIndex <= page * itemsPerPage; productIndex++)
  {
    returnedTableString += "<tr>";
    returnedTableString += "<td>" + productsList[productIndex].name + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].description
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].dimension
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].measureUnit
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].unitBlock
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].availability
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].transportCost
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].unitCost
        + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].minBuy + "</td>";
    returnedTableString += "<td>" + productsList[productIndex].maxBuy + "</td>";
    returnedTableString += "<td>"
        + getCategoryDescription(productCategoriesList,
            productsList[productIndex].idProductCategory) + "</td>";
    returnedTableString += getSupplierAsTableRow(suppliersList,
        productsList[productIndex].idMemberSupplier);
    returnedTableString += "</tr>";
  }
  return returnedTableString;
}

function writeIndexPage()
{
  $('.centrale').html("<p>Questa è l'interfaccia fornitore</p>");
}

function writeSupplierPage(tab)
{
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
  var categoriesString = "<option value='notSelected' selected='selected'>Tutte</option>";
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

function updateProductHandler(event)
{
  event.preventDefault();
  $("#dialog:ui-dialog").dialog("destroy");

  var idProduct = $(this).attr('name');

  if ($('#rowUpd' + idProduct).is(':visible'))
  {
    $('#rowUpd' + idProduct).hide('slow');
    return;
  }

  $('.rowUpdClass').hide('slow');

  var prod = getProduct(idProduct);
  if (prod == undefined)
  {
    $("#dialog-error-update").dialog({
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
  $('#divUpd' + idProduct)
      .html(
          "<div class='modificaProdottoForm' style='margin: 2em 0 0 65px;'>"
              + "<form id='updateProdForm"
              + idProduct
              + "' action='' method='post'>"
              + "<fieldset id='productFieldsetUpd"
              + idProduct
              + "'>"
              + "<legend>&nbsp;Dati per la modifica prodotto&nbsp;</legend>"
              + "<br /><label for='productNameUpd"
              + idProduct
              + "' class='left'>Nome: </label>"
              + "<input type='text' name='productNameUpd"
              + idProduct
              + "'"
              + " id='productNameUpd"
              + idProduct
              + "' class='field'"
              + "required='required' value='"
              + prod.name
              + "'/><br><label"
              + " for='productDescriptionUpd"
              + idProduct
              + "' class='left'>"
              + "Descrizione: </label><input type='text' "
              + "name='productDescriptionUpd"
              + idProduct
              + "' id='productDescriptionUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.description
              + "' />"
              + "<br><label for='productDimensionUpd"
              + idProduct
              + "' "
              + "class='left'>Dimensione: </label>"
              + "<input type='text' name='productDimensionUpd"
              + idProduct
              + "' "
              + "id='productDimensionUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.dimension
              + "' />"
              + "<br><label for='measure_unitUpd"
              + idProduct
              + "' "
              + "class='left'>Unità di misura: </label>"
              + "<input type='text' name='measure_unitUpd"
              + idProduct
              + "' "
              + "id='measure_unitUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.measureUnit
              + "' />"
              + "<br><label for='unit_blockUpd"
              + idProduct
              + "' "
              + "class='left'>Unità per blocco: </label>"
              + "<input type='text' name='unit_blockUpd"
              + idProduct
              + "' "
              + "id='unit_blockUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.unitBlock
              + "' />"
              + "<br><label for='transport_costUpd"
              + idProduct
              + "' "
              + "class='left'>Costo trasporto: </label>"
              + "<input type='text' name='transport_costUpd"
              + idProduct
              + "'"
              + " id='transport_costUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.transportCost
              + "' />"
              + "<br><label for='unit_costUpd"
              + idProduct
              + "' "
              + "class='left'>Costo unità: </label>"
              + "<input type='text' name='unit_costUpd"
              + idProduct
              + "' id='unit_costUpd"
              + idProduct
              + "'"
              + "class='field' required='required' value='"
              + prod.unitCost
              + "' />"
              + "<br><label for='min_buyUpd"
              + idProduct
              + "' class='left'>Minimo unità acquistabili: </label>"
              + "<input type='text' name='min_buyUpd"
              + idProduct
              + "' id='min_buyUpd"
              + idProduct
              + "' class='field'  value='"
              + prod.minBuy
              + "'/>"
              + "<br><label for='max_buyUpd"
              + idProduct
              + "' class='left'>Massimo unità acquistabili: </label>"
              + "<input type='text' name='max_buyUpd"
              + idProduct
              + "' id='max_buyUpd"
              + idProduct
              + "'"
              + "class='field' value='"
              + prod.maxBuy
              + "' />"
              + "<br><br><label for='productCategoryUpd"
              + idProduct
              + "' class='left'>Categoria: </label>"
              + "<select name='productCategoryUpd"
              + idProduct
              + "' id='productCategoryUpd"
              + idProduct
              + "' class='field' onchange='checkCategorySelectUpd("
              + idProduct
              + ")'>"
              + "</select></fieldset>"
              + "<fieldset id='categoryFieldsetUpd"
              + idProduct
              + "' ><legend>&nbsp;Inserisci nuova categoria&nbsp;</legend><br />"
              + "<br><label for='categoryDescriptionUpd"
              + idProduct
              + "' class='left'>Descrizione: </label>"
              + "<input type='text' name='categoryDescriptionUpd"
              + idProduct
              + "' id='categoryDescriptionUpd"
              + idProduct
              + "' class='field' />"
              + "</fieldset>"
              + "<div id='errorDivUpd"
              + idProduct
              + "' style='display: none;'>"
              + "<fieldset><legend id='legendError'>&nbsp;Errore&nbsp;</legend><br />"
              + "<div id='errorsUpd"
              + idProduct
              + "' style='padding-left: 40px'>"
              + "</div></fieldset></div><p>"
              + "<input type='submit' class='button' value='Aggiorna prodotto' id='updateProductSubmit' name='"
              + idProduct + "' />" + "</p></form></div>");
  var categoriesList = getCategoriesNoLocal();
  var categoriesString = "";
  for ( var catIndex in categoriesList)
  {
    categoriesString += "<option";
    if (prod.category.idProductCategory == categoriesList[catIndex].idProductCategory)
      categoriesString += " selected='selected'";
    categoriesString += " value='"
        + categoriesList[catIndex].idProductCategory + "'>"
        + categoriesList[catIndex].description + "</option>";
  }
  categoriesString += "<option value='nuova'>Nuova categoria...</option>";

  $('#productCategoryUpd' + idProduct).html(categoriesString);
  // disabilitare fieldset category
  $('#categoryFieldsetUpd' + idProduct).hide();
  $('#categoryFieldsetUpd' + idProduct).children().attr("disabled", "disabled");

  $('#updateProductSubmit').on("click", clickUpdateProductHandler);

  $('#rowUpd' + idProduct).show('slow');

  prepareProductsForm(1);

  return false; // don't post it automatically
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
            + "'><form id='prodAval' name='" + myProducts[prodIndex].idProduct
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
            + "'><form id='prodNotAval' name='"
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
