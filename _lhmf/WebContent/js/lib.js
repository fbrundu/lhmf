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
  $("#bodyTitleHeader").html("Notifiche");
  $('#notifiesCount').html("0");
  $('#notifiesCount').css("color", "");
}

function registerForNotifies()
{
  var source = new EventSource('ajax/newnotifies');
  source.onmessage = function(event)
  {
    console.debug("New notifies..");
    if (!window.History.enabled
        || window.History.getState().data.action != 'notifiche')
    {
      $('#notifiesCount').html(event.data);
      $('#notifiesCount').css("color", "red");
    }
    else
    {
      getMyNotifies();
    }
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
  var usersList = getUsersExceptMe();
  for ( var userIndex in usersList)
  {
    users += "<option value='" + usersList[userIndex] + "'>" 
      + usersList[userIndex] + "</option>";
  }
//  var orders = "<option value='-1' selected='selected'>Seleziona...</option>";
//  var ordersList = getOrders();
//  for ( var orderIndex in ordersList)
//  {
//    orders += "<option value='" + ordersList[orderIndex] + "'>" 
//      + ordersList[orderIndex] + "</option>";
//  }
//  var products = "<option value='-1' selected='selected'>Seleziona...</option>";
//  var productsList = getProducts();
//  for ( var prodIndex in productsList)
//  {
//    products += "<option value='" + productsList[prodIndex] + "'>" 
//      + productsList[prodIndex] + "</option>";
//  }
  var formInvioMessaggio = "<div class='messageform'><form method='post' action=''>"
      + "<fieldset><legend>&nbsp;Invia un messaggio&nbsp;</legend>"
      + "<br><label for='usersSelect' class='left'>Destinatario: </label>"
      + "<select name='usersSelect' id='usersSelect' class='field'>"
      + users
      + "</select><br><br>"
//      + "<label for='ordersSelect' class='left'>Ordine (opzionale): </label>"
//      + "<select name='ordersSelect' id='ordersSelect' class='field'>"
//      + orders
//      + "</select><br><br>"
//      + "<label for='productsSelect' class='left'>Prodotto (opzionale): </label>"
//      + "<select name='productsSelect' id='productsSelect' class='field'>"
//      + products
//      + "</select><br><br>"
      + "<label for='messageText' class='left'>Testo: </label>"
      + "<textarea name='messageText' id='messageText' class='field' "
      + "required='required' /><br>" 
      + "<button type='submit' id='newMessageSubmit'> Invia messaggio </button>"
      + "</fieldset></form></div>";
  $(".centrale").html(tabellaMessaggi + formInvioMessaggio);
  $("#bodyTitleHeader").html("Messaggi");
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

function getUsersExceptMe()
{
  var usersList;
  $.getJSONsync("ajax/getusernamesexceptme", function(usernamesList)
  {
    usersList = usernamesList;
  });
  return usersList;
}

function getOrders()
{
  //TODO
}

function getProducts()
{
  //TODO
}

function clickNewMessageHandler(event)
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
    // TODO idOrder e idProduct devono essere scelti tramite il form
    var idMessage = newMessage({
      dest : username,
      messageText : messageText,
      idOrder: -1,
      idProduct: -1,
    });
    if (idMessage > 0)
    {
      $("#productFieldset").children("input").val("");
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

function registerForMessages()
{
  var source = new EventSource('ajax/newmessages');
  source.onmessage = function(event)
  {
    if (!window.History.enabled
        || window.History.getState().data.action != 'messaggi')
    {
      console.debug("New messages..");
      $('#messagesCount').html(event.data);
      $('#messagesCount').css("color", "red");
    }
    else
    {
      getMyMessages();
    }
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

function newMessage(messageParameters)
{
  if (messageParameters == undefined)
  {
    console.debug("Invalid parameters in " + displayFunctionName());
    return;
  }
  var returnedIdMessage = -1;
    $.postSync("ajax/newmessage", messageParameters, function(idMessage)
    {
      returnedIdMessage = idMessage;
      if (idMessage > 0)
        console.debug("Inserted message: " + idMessage);
    });
  return returnedIdMessage;
}
