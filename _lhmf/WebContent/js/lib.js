$(function()
{
});

(function($)
{
  $.fn.filestyle = function(options)
  {
    var settings = {
      width : 250
    };
    if (options)
    {
      $.extend(settings, options);
    }
    ;
    return this
        .each(function()
        {
          var self = this;
          var wrapper = $("<div>").css({
            "width" : settings.imagewidth + "px",
            "height" : settings.imageheight + "px",
            "background" : "url(" + settings.image + ") 0 0 no-repeat",
            "background-position" : "right",
            "display" : "inline",
            "position" : "absolute",
            "overflow" : "hidden"
          });
          var filename = $('<input class="file">').addClass(
              $(self).attr("class")).css({
            "display" : "inline",
            "width" : settings.width + "px"
          });
          $(self).before(filename);
          $(self).wrap(wrapper);
          $(self).css({
            "position" : "relative",
            "height" : settings.imageheight + "px",
            "width" : settings.width + "px",
            "display" : "inline",
            "cursor" : "pointer",
            "opacity" : "0.0"
          });
          if ($.browser.mozilla)
          {
            if (/Win/.test(navigator.platform))
            {
              $(self).css("margin-left", "-142px");
            }
            else
            {
              $(self).css("margin-left", "-168px");
            }
            ;
          }
          else
          {
            $(self).css("margin-left",
                settings.imagewidth - settings.width + "px");
          }
          ;
          $(self).bind("change", function()
          {
            filename.val($(self).val());
          });
        });
  };
})(jQuery);
$.postJSON = function(url, data, callback)
{
  return jQuery.ajax({
    'type' : 'POST',
    'url' : url,
    'contentType' : 'application/json',
    'data' : JSON.stringify(data),
    'dataType' : 'json',
    'success' : callback
  });
};

$.getSync = function(url, data, callback)
{
  return jQuery.ajax({
    'async' : false,
    'type' : 'GET',
    'url' : url,
    'data' : data,
    'success' : callback
  });
};

$.postSync = function(url, data, callback)
{
  return jQuery.ajax({
    'async' : false,
    'type' : 'POST',
    'url' : url,
    'data' : data,
    'success' : callback
  });
};

$.postSyncFormData = function(url, data, callback)
{
  return jQuery.ajax({
    'async' : false,
    'type' : 'POST',
    'url' : url,
    'data' : data,
    'success' : callback,
    'processData' : false,
    'contentType' : false

  });
};

$.postJSONsync = function(url, data, callback)
{
  return jQuery.ajax({
    'async' : false,
    'type' : 'POST',
    'url' : url,
    'contentType' : 'application/json',
    'data' : JSON.stringify(data),
    'dataType' : 'json',
    'success' : callback
  });
};

$.getJSONsync = function(url, callback)
{
  return jQuery.ajax({
    'async' : false,
    'type' : 'GET',
    'url' : url,
    'contentType' : 'application/json',
    'success' : callback
  });
};

function displayFunctionName()
{
  var functionName = arguments.callee.toString();
  functionName = functionName.substr('function '.length);
  functionName = functionName.substr(0, functionName.indexOf('('));

  return functionName;
}

function getAllSuppliers()
{
  $.getJSONsync("ajax/getsuppliers",
      function(suppliersList)
      {
        window.localStorage.setItem('suppliersList', JSON
            .stringify(suppliersList));
        console.debug("suppliersList saved in localstorage");
      });
}

function loadAllSuppliersFromLocalStorage()
{
  return JSON.parse(window.localStorage.getItem('suppliersList'));
}

function getSupplierAsTableRow(suppliersList, idSupplier)
{
  if (suppliersList == undefined || idSupplier == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return "";
  }
  for ( var supplierIndex in suppliersList)
  {
    // TODO return member resp name
    if (suppliersList[supplierIndex].idMember == idSupplier)
      return "<td>" + suppliersList[supplierIndex].name
          + suppliersList[supplierIndex].surname + "</td>";
  }
  return "";
}

function getAllResps()
{
  $.getJSONsync("ajax/getmembers", function(respsList)
  {
    window.sessionStorage.setItem('respsList', JSON.stringify(respsList));
    console.debug("respsList saved in localstorage");
  });
}

function loadAllRespsFromLocalStorage()
{
  return JSON.parse(window.sessionStorage.getItem('respsList'));
}

function getRespAsTableRow(respsList, idResp)
{
  if (respsList == undefined || idResp == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return "";
  }
  for ( var respIndex in respsList)
  {
    if (respsList[respIndex].idMember == idResp)
      return "<td>" + respsList[respIndex].name + respsList[respIndex].surname
          + /* respsList[respIndex].idMember + */"</td>";
  }
  return "";
}

function getCategoriesNoLocal()
{
  var categoriesList;
  $.getJSONsync("ajax/getproductcategories", function(productCategoriesList)
  {
    categoriesList = productCategoriesList;
  });
  return categoriesList;
}

function setProductAvailableHandler(event)
{
  event.preventDefault();
  var idProduct = $(this).attr('name');
  if (setProductAvailable(idProduct) > 0)
  {
    $('#listHead' + idProduct).html('In listino');
    $('#listHead' + idProduct).attr('class', 'yes');
    $('#listCont' + idProduct).html(
        "<form id='prodNotAval' name='" + idProduct + "' action=''>"
            + "<input id='prodNotAval" + idProduct
            + "' type='submit' class='button'"
            + " value='Rimuovi da listino' />" + "</form>");
    $('input:submit').button();
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

  $('form').filter(function()
  {
    return this.id.match(/prodNotAval/);
  }).bind('submit', setProductUnavailableHandler);

  return false; // don't post it automatically
}

function getProduct(idProduct)
{
  if (idProduct == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var product = undefined;
  $.getSync("ajax/getproduct", {
    idProduct : idProduct
  }, function(prod)
  {
    product = prod;
  });
  return product;
}

function setProductUnavailableHandler(event)
{
  event.preventDefault();
  var idProduct = $(this).attr('name');
  if (setProductUnavailable(idProduct) > 0)
  {
    $('#listHead' + idProduct).html('Non in listino');
    $('#listHead' + idProduct).attr('class', 'no');
    $('#listCont' + idProduct).html(
        "<form id='prodAval' name='" + idProduct + "' action=''>"
            + "<input type='submit' class='button'"
            + " value='Inserisci in listino' />" + "</form>");
    $('input:submit').button();
  }
  else
  {
    $("#dialog-error-remove").dialog({
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
  $('form').filter(function()
  {
    return this.id.match(/prodAval/);
  }).bind('submit', setProductAvailableHandler);

  return false; // don't post it automatically
}

function setProductAvailable(idProduct)
{
  if (idProduct == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var returnedRowsAffected = undefined;
  $.getSync("ajax/setproductavailable", {
    idProduct : idProduct
  }, function(rowsAffected)
  {
    returnedRowsAffected = rowsAffected;
    if (rowsAffected > 0)
      console.debug("Product inserted in list: " + idProduct);
  });
  return returnedRowsAffected;
}

function setProductUnavailable(idProduct)
{
  if (idProduct == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var returnedRowsAffected = undefined;
  $.getSync("ajax/setproductunavailable", {
    idProduct : idProduct
  }, function(rowsAffected)
  {
    returnedRowsAffected = rowsAffected;
    if (rowsAffected > 0)
      console.debug("Product removed from list: " + idProduct);
  });
  return returnedRowsAffected;
}

function clickUpdateProductHandler(event)
{
  event.preventDefault();

  var errors = new Array();

  var idProduct = $(this).attr('name');
  var productName = $('#productNameUpd' + idProduct).val();
  var productDescription = $('#productDescriptionUpd' + idProduct).val();
  var productDimension = $('#productDimensionUpd' + idProduct).val();
  var measureUnit = $('#measure_unitUpd' + idProduct).val();
  var unitBlock = $('#unit_blockUpd' + idProduct).val();
  var transportCost = $('#transport_costUpd' + idProduct).val();
  var unitCost = $('#unit_costUpd' + idProduct).val();
  var minBuy = $('#min_buyUpd' + idProduct).val();
  var maxBuy = $('#max_buyUpd' + idProduct).val();
  var productCategory = $('#productCategoryUpd' + idProduct).val();
  var categoryDescription = $('#categoryDescriptionUpd' + idProduct).val();

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
  if (minBuy == "" || !isPositiveNumber(minBuy))
  {
    errors.push("Minimo unit&agrave; acquistabili: Formato non valido");
  }
  if (maxBuy == "" || !isPositiveNumber(maxBuy))
  {
    errors.push("Massimo unit&agrave; acquistabili: Formato non valido");
  }
  if (minBuy > maxBuy)
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
    $("#errorsUpd" + idProduct).html("");
    $("#errorDivUpd" + idProduct).hide();

    for ( var i = 0; i < errors.length; i++)
    {
      var error = errors[i].split(":");
      $("#errorsUpd" + idProduct).append(
          "<strong>" + error[0] + "</strong>: " + error[1] + "<br />");
    }

    $("#errorDivUpd" + idProduct).show("slow");
    // $("#errorDivUpd" + idProduct).fadeIn(1000);
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
        $("#categoryDescriptionUpd" + idProduct).val("");
      }
      else
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
    }

    // Creazione nuovo prodotto
    updateProduct({
      productId : idProduct,
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

    $('#rowUpd' + idProduct).hide('slow', function()
    {
      $('#listRow' + idProduct).hide('slow', function()
      {
        newProductSearch();
      });
    });
  }
}

function deleteProductHandler(event)
{
  event.preventDefault();
  $("#dialog:ui-dialog").dialog("destroy");

  var idProduct = $(this).attr('name');

  $("#dialog-confirm").dialog({
    resizable : false,
    height : 140,
    modal : true,
    buttons : {
      "Elimina" : function()
      {
        $(this).dialog("close");
        if (deleteProduct(idProduct) > 0)
        {
          $("#listRow" + idProduct).hide('slow');
        }
        else
        {
          $("#dialog-error-remove").dialog({
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

      },
      "Annulla" : function()
      {
        $(this).dialog("close");
      }
    }
  });
  return false; // don't post it automatically
}

function updateProduct(productParameters)
{
  if (productParameters == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var returnRowsAffected = 0;
  $.postSync("ajax/updateproduct", productParameters, function(rowsAffected)
  {
    if (rowsAffected > 0)
    {
      returnRowsAffected = rowsAffected;
      $("#productFieldsetUpd").children("input").val("");
    }
    else
      alert("Errore nella creazione di un nuovo prodotto");
  });
  return returnRowsAffected;
}

function checkCategorySelectUpd(idProduct)
{
  var selected = $('#productCategoryUpd' + idProduct).val();
  $("#errorDivUpd" + idProduct).hide('slow');

  if (selected == 'nuova')
  {
    $('#categoryFieldsetUpd' + idProduct).show('slow');
    $('#categoryFieldsetUpd' + idProduct).children().attr("disabled", false);
  }
  else
  {
    // Nuova categoria non selezionata
    $('#categoryFieldsetUpd' + idProduct).hide('slow');
    $('#categoryFieldsetUpd' + idProduct).children().attr("disabled", true);
  }
}

function newCategory(pcDescription)
{
  if (pcDescription == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var idCategory = undefined;
  $.postSync("ajax/newproductcategory", {
    description : pcDescription
  }, function(idProductCategory)
  {
    idCategory = idProductCategory;
  });
  return idCategory;
}

function deleteProduct(idProduct)
{
  if (idProduct == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var returnRowsAffected = undefined;
  $.postSync("ajax/deleteproduct", {
    idProduct : idProduct
  }, function(rowsAffected)
  {
    returnRowsAffected = rowsAffected;
  });
  return returnRowsAffected;
}

function isNumber(n)
{
  return !isNaN(parseFloat(n)) && isFinite(n);
}

function isPositiveNumber(n)
{
  return !isNaN(parseFloat(n)) && isFinite(n) && n > 0;
}

function getMyNotifies()
{
  var tabellaNotifiche = "<div class='contentDiv'><table class='notifiche'>";
  $.getSync("ajax/getmynotifies", undefined, function(notifiesList)
  {
    for ( var notIndex in notifiesList)
    {
      tabellaNotifiche += "<tr><td";
      if (!notifiesList[notIndex].isReaded)
        tabellaNotifiche += " class='not_read' ";
      tabellaNotifiche += "><p>" + notifiesList[notIndex].text
          + "</p></td></tr>";
    }
  });
  tabellaNotifiche += "</table></div>";
  $(".centrale").html(tabellaNotifiche);
  $('#notifiesCount').html("0");
  $('#notifiesCount').css("color", "");
}

function registerForNotifies()
{
  var source = new EventSource('ajax/newnotifies');
  source.onmessage = function(event)
  {
    console.debug("New notifies..");
    $('#notifiesCount').html(event.data);
    $('#notifiesCount').css("color", "red");
  };
}

function getMyMessages()
{
  var tabellaMessaggi = "<div class='contentDiv'><table class='messaggi'>";
  $.getSync("ajax/getmymessages", undefined, function(messagesList)
  {
    for ( var mesIndex in messagesList)
    {
      tabellaMessaggi += "<tr><td";
      if (!messagesList[mesIndex].isReaded)
        tabellaMessaggi += " class='not_read' ";
      tabellaMessaggi += "><h3>From: " + messagesList[mesIndex].sender
          + "</h3><p>" + messagesList[mesIndex].text + "</p></td></tr>";
    }
  });
  tabellaMessaggi += "</table></div>";
  var users = "<option value='notSelected' selected='selected'>Seleziona...</option>";
  var usersList = getUsers();
  for ( var userIndex in usersList)
  {
    users += "<option value='" + usersList[userIndex] + "'>" 
      + usersList[userIndex] + "</option>";
  }
  var formInvioMessaggio = "<div class='messageform'><form method='post' action=''>"
      + "<fieldset><legend>&nbsp;Invia un messaggio&nbsp;</legend>"
      + "<label for='usersSelect' class='left'>&nbsp;&nbsp;&nbsp;Destinatario: </label>"
      + "<select name='usersSelect' id='usersSelect' class='field'>"
      + users
      + "</select><br>"
      + "<label for='messageText' class='left'>Testo: </label>"
      + "<textarea name='messageText' id='messageText' class='field' "
      + "required='required' /><br>" 
      + "<button type='submit' id='newMessageSubmit'> Crea prodotto </button>"
      + "</fieldset></form></div>";
  $(".centrale").html(tabellaMessaggi + formInvioMessaggio);
  $("button").button();
  $('#newMessageSubmit').on("click", clickNewMessageHandler);
  $('#messagesCount').html("0");
  $('#messagesCount').css("color", "");
}

function getUsers()
{
  var usersList;
  $.getJSONsync("ajax/getusernames", function(usernamesList)
  {
    usersList = usernamesList;
  });
  return usersList;
}

function clickNewMessageHandler()
{
  event.preventDefault();

  var errors = new Array();

  var username = $('#usersSelect').val();
  var messageText = $('#messageText').val();

  if(username == 'notSelected' || username == undefined || messageText == "")
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
  else
  {
    // TODO Creazione nuovo messaggio
//    var idProduct = newProduct({
//      picture : picture,
//      productName : productName,
//      productDescription : productDescription,
//      productDimension : productDimension,
//      measureUnit : measureUnit,
//      unitBlock : unitBlock,
//      transportCost : transportCost,
//      unitCost : unitCost,
//      minBuy : minBuy,
//      maxBuy : maxBuy,
//      productCategory : productCategory,
//    });
//    if (idProduct > 0)
//    {
//      $("#productFieldset").children("input").val("");
//      $("#dialog-ok").dialog({
//        resizable : false,
//        height : 140,
//        modal : true,
//        buttons : {
//          "Ok" : function()
//          {
//            $(this).dialog('close');
//          }
//        }
//      });
//    }
//    else
//    {
//      $("#dialog-error-insert").dialog({
//        resizable : false,
//        height : 140,
//        modal : true,
//        buttons : {
//          "Ok" : function()
//          {
//            $(this).dialog('close');
//          }
//        }
//      });
//    }
  }
}

function registerForMessages()
{
  var source = new EventSource('ajax/newmessages');
  source.onmessage = function(event)
  {
    console.debug("New messages..");
    $('#messagesCount').html(event.data);
    $('#messagesCount').css("color", "red");
  };
}

function messagesClicked(event)
{
  var History = window.History;
  if (History.enabled == true)
  {
    event.preventDefault();
    var state = History.getState();
    var stateData = state.data;
    if (!!stateData && !!stateData.action && stateData.action == 'messaggi')
      return;
    History.pushState({
      action : 'messaggi'
    }, null, 'messaggi');
  }
}

function notifiesClicked(event)
{
  var History = window.History;
  if (History.enabled == true)
  {
    event.preventDefault();
    var state = History.getState();
    var stateData = state.data;
    if (!!stateData && !!stateData.action && stateData.action == 'notifiche')
      return;
    History.pushState({
      action : 'notifiche'
    }, null, 'notifiche');
  }
}

//Parte utente normale

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
										"<span class='amount'>" +
											"<label for='pz' class='left'>Quantit� desiderata:</label>" +
											 "<input type='text' id='pz' class='field' style='width: 40px' />" +
										"</span>" +
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
