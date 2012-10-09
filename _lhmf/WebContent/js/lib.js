$(function()
{
	//$( "#dialog-notify" ).dialog({ autoOpen: false });
	//$( "#dialog-notify:ui-dialog" ).dialog( "destroy" );
	
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

$.getJSONsyncTest = function(url, data, callback)
{
  return jQuery.ajax({
    'async' : false,
    'type' : 'GET',
    'url' : url,
    'contentType' : 'application/json',
    'data' : JSON.stringify(data),
    'dataType' : 'json',
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

function getCategoriesNoLocal()
{
  var categoriesList;
  $.getJSONsync("ajax/getproductcategories", function(productCategoriesList)
  {
    categoriesList = productCategoriesList;
  });
  return categoriesList;
}

function getMyCategoriesNoLocal()
{
  var categoriesList;
  $.getJSONsync("ajax/getmycategories", function(productCategoriesList)
  {
    categoriesList = productCategoriesList;
  });
  return categoriesList;
}

function setProductAvailableHandler(event)
{
  event.preventDefault();
  var idProduct = $(this).data('idproduct');
  if (setProductAvailable(idProduct) > 0)
  {
    $('#listHead' + idProduct).html('In listino');
    $('#listHead' + idProduct).attr('class', 'yes');
    $('#listCont' + idProduct).html(
        "<form id='prodNotAval"+idProduct+"' data-idproduct='" + idProduct + "' action=''>"
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
  $("#prodNotAval"+idProduct).submit(setProductUnavailableHandler);

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
  var idProduct = $(this).data('idproduct');
  if (setProductUnavailable(idProduct) > 0)
  {
    $('#listHead' + idProduct).html('Non in listino');
    $('#listHead' + idProduct).attr('class', 'no');
    $('#listCont' + idProduct).html(
        "<form id='prodAval"+ idProduct +"' data-idproduct='" + idProduct + "' action=''>"
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
  $("#prodAval"+idProduct).submit(setProductAvailableHandler);

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

//function clickUpdateProductHandler(event)
//{
//  event.preventDefault();
//
//  var errors = new Array();
//
//  var idProduct = $(this).attr('name');
//  var productName = $('#productNameUpd' + idProduct).val();
//  var productDescription = $('#productDescriptionUpd' + idProduct).val();
//  var productDimension = $('#productDimensionUpd' + idProduct).val();
//  var measureUnit = $('#measure_unitUpd' + idProduct).val();
//  var unitBlock = $('#unit_blockUpd' + idProduct).val();
//  var transportCost = $('#transport_costUpd' + idProduct).val();
//  var unitCost = $('#unit_costUpd' + idProduct).val();
//  var minBuy = $('#min_buyUpd' + idProduct).val();
//  var maxBuy = $('#max_buyUpd' + idProduct).val();
//  var productCategory = $('#productCategoryUpd' + idProduct).val();
//  var categoryDescription = $('#categoryDescriptionUpd' + idProduct).val();
//
//  if (productName == "" || isNumber(productName))
//  {
//    errors.push("Nome prodotto: Formato non valido");
//  }
//  if (productDescription == "")
//  {
//    errors.push("Descrizione prodotto: Formato non valido");
//  }
//  if (productDimension == "" || !isPositiveNumber(productDimension))
//  {
//    errors.push("Dimensione: Formato non valido");
//  }
//  if (measureUnit == "" || isNumber(measureUnit))
//  {
//    errors.push("Unit&agrave; di misura: Formato non valido");
//  }
//  if (unitBlock == "" || !isPositiveNumber(unitBlock))
//  {
//    errors.push("Unit&agrave; per blocco: Formato non valido");
//  }
//  if (transportCost == "" || !isPositiveNumber(transportCost))
//  {
//    errors.push("Costo di trasporto: Formato non valido");
//  }
//  if (unitCost == "" || !isPositiveNumber(unitCost))
//  {
//    errors.push("Costo per unit&agrave;: Formato non valido");
//  }
//  if (minBuy == "" || !isPositiveNumber(minBuy))
//  {
//    errors.push("Minimo unit&agrave; acquistabili: Formato non valido");
//  }
//  if (maxBuy == "" || !isPositiveNumber(maxBuy))
//  {
//    errors.push("Massimo unit&agrave; acquistabili: Formato non valido");
//  }
//  if (minBuy > maxBuy)
//  {
//    errors.push("Massimo/minimo unit&agrave; acquistabili:"
//        + "Il minimo di unit&agrave; acquistabili deve essere minore"
//        + " o uguale al massimo unit&agrave; acquistabili");
//  }
//  if (!isPositiveNumber(productCategory))
//  {
//    if (categoryDescription == "" || isNumber(categoryDescription))
//      errors.push("Categoria di prodotto: Formato non valido");
//  }
//
//  if (errors.length > 0)
//  {
//    $("#errorsUpd" + idProduct).html("");
//    $("#errorDivUpd" + idProduct).hide();
//
//    for ( var i = 0; i < errors.length; i++)
//    {
//      var error = errors[i].split(":");
//      $("#errorsUpd" + idProduct).append(
//          "<strong>" + error[0] + "</strong>: " + error[1] + "<br />");
//    }
//
//    $("#errorDivUpd" + idProduct).show("slow");
//    // $("#errorDivUpd" + idProduct).fadeIn(1000);
//  }
//  else
//  {
//    // Creazione nuova categoria
//    if (!isPositiveNumber(productCategory))
//    {
//      var idProductCategory = newCategory(categoryDescription);
//      if (idProductCategory > 0)
//      {
//        productCategory = idProductCategory;
//        $("#categoryDescriptionUpd" + idProduct).val("");
//      }
//      else
//      {
//        $("#dialog-error-update").dialog({
//          resizable : false,
//          height : 140,
//          modal : true,
//          buttons : {
//            "Ok" : function()
//            {
//              $(this).dialog('close');
//            }
//          }
//        });
//        return;
//      }
//    }
//
//    // Aggiornamento prodotto
//    updateProduct({
//      productId : idProduct,
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
//
//    $('#rowUpd' + idProduct).hide('slow', function()
//    {
//      $('#listRow' + idProduct).hide('slow', function()
//      {
//        newProductSearch();
//      });
//    });
//  }
//}

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

//function updateProduct(productParameters)
//{
//  if (productParameters == undefined)
//  {
//    console.debug("Invalid parameters in " + displayFunctionName());
//    return;
//  }
//  var returnRowsAffected = 0;
//  $.postSync("ajax/updateproduct", productParameters, function(rowsAffected)
//  {
//    if (rowsAffected > 0)
//    {
//      returnRowsAffected = rowsAffected;
//      $("#productFieldsetUpd").children("input").val("");
//    }
//    else
//      alert("Errore nella creazione di un nuovo prodotto");
//  });
//  return returnRowsAffected;
//}

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
  if ($(".contentDiv") != undefined && $(".contentDiv").scrollTop() != 0)
    var distanceFromBottom = $(".contentDiv").height()
        - $(".contentDiv").scrollTop();
  $.getSync("ajax/getmynotifies", undefined, function(notifiesList)
  {
    if (notifiesList.length < 1)
      tabellaNotifiche += "<tr><td><p>Non ci sono notifiche da visualizzare</p></td></tr>";
    for ( var notIndex in notifiesList)
    {
      tabellaNotifiche += "<tr><td";
      if (!notifiesList[notIndex].isReaded)
        tabellaNotifiche += " class='not_read_n' ";
      tabellaNotifiche += " name='" + notifiesList[notIndex].idNotify
          + "'><p>";
      switch (notifiesList[notIndex].notifyCategory)
      {
      // Nuovo prodotto
      case 1:
        tabellaNotifiche += "Nuovo prodotto: "
        	+ "<a href='' class='productNotify' " +
        	"data-idproduct='" + notifiesList[notIndex].text +"' " +
        	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        break;
      // Nuovo ordine
      case 2:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text }, function(orderName)
        {
          tabellaNotifiche += "Nuovo ordine: '" + orderName
              + "' <a href='' class='orderNotify' " +
              "data-idorder='" + notifiesList[notIndex].text + "' " +
              "data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      // Modifica disponibilita' di un prodotto in listino
      case 3:
        tabellaNotifiche += "&Egrave; cambiata la disponibilit&agrave; di un prodotto: " +
        	"<a href='' class='productNotify' " +
        	"data-idproduct='" + notifiesList[notIndex].text +"' " +
        	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        break;
      // Ordine chiuso (responsabile ordine)
      case 4:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text}, function(orderName)
        {
          tabellaNotifiche += "L'ordine '"+ orderName + "' &egrave; stato chiuso: " +
          	"<a href='' class='orderNotify' " +
          	"data-idorder='" + notifiesList[notIndex].text +"' " +
          	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      // Data di consegna settata
      case 5:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text }, function(orderName)
        {
          tabellaNotifiche += "Impostata la data di consegna per l'ordine '" + orderName + "': " +
          	"<a href='' class='orderNotify' " +
          	"data-idorder='" + notifiesList[notIndex].text +"' " +
          	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      // Nuovo utente
      case 6:
        tabellaNotifiche += "Nuovo utente: " +
        	"<a href='' class='memberNotify' " +
        	"data-idmember='" + notifiesList[notIndex].text +"' " +
          	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        break;
      // Ordine 50%
      case 7:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text }, function(orderName)
        {
          tabellaNotifiche += "L'ordine '" + orderName + "' &egrave; al 50%: " +
          	"<a href='' class='orderNotify' " +
          	"data-idorder='" + notifiesList[notIndex].text +"' " +
          	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      // Ordine 75%
      case 8:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text }, function(orderName)
        {
          tabellaNotifiche += "L'ordine '" + orderName + "' &egrave; al 75%: " +
          	"<a href='' class='orderNotify' " +
        	"data-idorder='" + notifiesList[notIndex].text +"' " +
        	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      // Ordine 90%
      case 9:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text }, function(orderName)
        {
          tabellaNotifiche += "L'ordine '" + orderName + "' &egrave; al 90%: " +
          	"<a href='' class='orderNotify' " +
        	"data-idorder='" + notifiesList[notIndex].text +"' " +
        	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      // Ordine chiuso (utenti partecipanti)
      case 10:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text }, function(orderName)
        {
          tabellaNotifiche += "L'ordine '" + orderName + "' &egrave; stato chiuso: " +
          	"<a href='' class='orderNotify' " +
        	"data-idorder='" + notifiesList[notIndex].text +"' " +
        	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      // Nuovo ordine (fornitore)
      case 11:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text }, function(orderName)
        {
          tabellaNotifiche += "Nuovo ordine: '" + orderName + "' " +
          	"<a href='' class='orderNotify' " +
        	"data-idorder='" + notifiesList[notIndex].text +"' " +
        	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      // Ordine chiuso (fornitore)
      case 12:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text }, function(orderName)
        {
          tabellaNotifiche += "L'ordine '" + orderName + "' &egrave; stato chiuso: " +
          	"<a href='' class='orderNotify' " +
        	"data-idorder='" + notifiesList[notIndex].text +"' " +
        	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      // Data di consegna settata (fornitore)
      case 13:
        $.getSync("ajax/getordername", { 'idOrder' : notifiesList[notIndex].text }, function(orderName)
        {
          tabellaNotifiche += "L'ordine '" + orderName + "' &egrave; stato assegnato per la consegna: " +
          	"<a href='' class='orderNotify' " +
        	"data-idorder='" + notifiesList[notIndex].text +"' " +
        	"data-notifycategory='" + notifiesList[notIndex].notifyCategory + "'>Visualizza dettagli</a>";
        });
        break;
      default:
        tabellaNotifiche += notifiesList[notIndex].text;
        break;
      }
      tabellaNotifiche += "</p></td></tr>";
    }
  });
  tabellaNotifiche += "</table></div>";
  $(".centrale").html(tabellaNotifiche);
  if (distanceFromBottom != undefined)
    $(".contentDiv").scrollTop(
        $(".contentDiv").height() - distanceFromBottom + 1);
  $(".not_read_n").click(setReadNotify);
  $(".productNotify").click(viewProductClick);
  $(".orderNotify").click(viewOrderClick);
  $(".memberNotify").click(viewMemberClick);
  $("#bodyTitleHeader").html("Notifiche");
  $('#notifiesCount').html("0");
  $('#notifiesCount').css("color", "");
}

function viewProductClick(event)
{
  event.preventDefault();
  $( "#dialog-notify:ui-dialog" ).dialog( "destroy" );
  idProduct = $(this).data('idproduct');
  var notifyCategory = $(this).data('notifycategory');
  viewProductDetails(idProduct, notifyCategory);
}

function viewOrderClick(event)
{
  event.preventDefault();
  idOrder = $(this).data('idorder');
  var notifyCategory = $(this).data('notifycategory');
  viewOrderDetails(idOrder, notifyCategory);
}

function viewMemberClick(event)
{
  event.preventDefault();
  idMember = $(this).data('idmember');
  var notifyCategory = $(this).data('notifycategory');
  viewMemberDetails(idMember, notifyCategory);
}

function filterProducts(myProducts, productCategory)
{
  if (productCategory == 'notSelected')
    return myProducts;
  var productsFiltered = new Array();
  for (var prodIndex = 0; prodIndex < myProducts.length; prodIndex++)
    if(myProducts[prodIndex].category.idProductCategory == productCategory)
      productsFiltered.push(myProducts[prodIndex]);
  return productsFiltered;
}

function viewProductDetails(idProduct, notifyCategory)
{
  $.postSync("ajax/viewP", { idProduct: idProduct }, function(product)
  {
    var details = "<table><tr><td class='imageTD'><img src='" + product.imgPath + "' width='250',/></td>"
     +"<td class='dataTD'><table class='productDetailsTable'>"
    + "<tr><td>Nome</td><td class='fieldTD'>" + product.name + "</td></tr>"
    + "<tr><td>Descrizione</td><td class='fieldTD'>" + product.description + "</td></tr>"
    + "<tr><td>Dimensione</td><td class='fieldTD'>" + product.dimension + "</td></tr>"
    + "<tr><td>Unit&agrave; di misura</td><td class='fieldTD'>" + product.measureUnit + "</td></tr>"
    + "<tr><td>Unit&agrave; per blocco</td><td class='fieldTD'>" + product.unitBlock + "</td></tr>"
    + "<tr><td>Disponibilit&agrave;</td><td class='fieldTD'>";
    if (product.availability)
      details += "Disponibile";
    else
      details += "Non disponibile";
    details += "</td></tr>";
    details += "<tr><td>Costo trasporto</td><td class='fieldTD'>" + product.transportCost + "</td></tr>"
    + "<tr><td>Costo per blocco</td><td class='fieldTD'>" + product.unitCost + "</td></tr>"
    + "<tr><td>Minimo blocchi acquistabili</td><td class='fieldTD'>" + product.minBuy + "</td></tr>"
    + "<tr><td>Massimo blocchi acquistabili</td><td class='fieldTD'>" + product.maxBuy + "</td></tr>";
    $.getSync("ajax/getmemberurlencoded", { 'idMember' : product.idMemberSupplier }, function(member)
    {
	      details += "<tr><td>Fornitore</td><td class='fieldTD'>" + member.name + " " + member.surname + "</td></tr>";
	      details += "<tr><td>Categoria</td><td class='fieldTD'>" + product.category.description + "</td></tr>";
	      details += "</table></td></table>";
    
	      if(notifyCategory == 1) {
	    	  $("#dialog-notify").attr('title', "Nuovo Prodotto");
	      } else {
	    	  $("#dialog-notify").attr('title', "Cambio Disponibilit&agrave; Prodotto");
	      }
	        
	   		$("#dialog-notify-container").html(details);
	    
	   		$( "#dialog-notify" ).dialog({
				resizable: false,
				height:600,
				width:800,
				modal: true,
				buttons: {
					"Crea un ordine per questo fornitore": function() {
						
						$( this ).dialog( "close" );
						
						  var History = window.History;	
						  if (History.enabled == true) {
						    History.pushState({
						      action : 'orderResp',
						      id: member.idMember,
						      idType: 0,
						      tab: 0
						    }, null, 'orderResp');
						  }
						
					},
					"Chiudi": function() {
						$( this ).dialog( "close" );
					}
				}
			});
		    
	});
  });
}

function viewOrderDetails(idOrder, notifyCategory)
{
  $.postSync("ajax/viewO", {
    'idOrder' : idOrder
  }, function(order)
  {
    
    var details = "<table align='center' style='text-align:center'><tr><td><canvas id='orderProgressCanvas' width='680' height='330'></canvas></td></tr>"
    +"<tr><td><strong>Ordine</strong> \"" + order.orderName + "\" <strong> Creato da</strong> \"" + order.memberResp.name + "\" ";
    $.getSync("ajax/getmemberurlencoded", { 'idMember' : order.supplier.idMember }, function(member)
    {
      details += "per il <strong>Fornitore</strong> \"" +  member.name + " " + member.surname + "\"<br />";
    });
    details += "<strong>Data apertura</strong> " + $.datepicker.formatDate('dd-mm-yy',new Date(order.dateOpen)) 
    + " <br /> <strong>Data chiusura</strong> "
    + $.datepicker.formatDate('dd-mm-yy', new Date(order.dateClose))
    + "<br /> <strong>Data consegna</strong> ";
    if (order.dateDelivery != "null")
      details += $.datepicker.formatDate('dd-mm-yy', new Date(order.dateDelivery));
    else
      details += "Non ancora decisa";
    details += "</td></tr></table>";

	$("#dialog-notify-container").html(details);
    
    $.postSync("ajax/getProgressOrder", { 'idOrder' : idOrder }, function(progress)
    {
    	printOrderPie(progress);
    });
	
    var memberType = 0;
    $.postSync("ajax/getMemberType", null, function(result)
    {
    	memberType = result;
    });
    
    var varAction = "";
    
    switch(notifyCategory) {
    
    case 2:
    		// 2 Nuovo ordine [Normale, Resp]
    	if (memberType == 0) 
    		varAction = "purchase";
    	else if (memberType == 1)
    		varAction = "purchaseResp";
    	
    	$("#dialog-notify").attr('title', "Notifica Nuovo Ordine");
    	
    	$( "#dialog-notify" ).dialog({
    		resizable: false, height:600, width:800, modal: true,
    		buttons: {
    			"Crea un scheda per questo ordine": function() {
					$( this ).dialog( "close" );
					  var History = window.History;	
					  if (History.enabled == true) 
					    History.pushState({ action : varAction, idOrd: idOrder, tab: 0 }, null, varAction);
				},
    			"Chiudi": function() {
    				$( this ).dialog( "close" );
    			}
    		}
    	});
    	
    	break;
    case 4:
    		//4 Chiusura Ordine con successo [Resp ordine]
    	 $("#dialog-notify").attr('title', "Notifica Ordine Chiuso Con Successo");
    	 
    	 $( "#dialog-notify" ).dialog({
     		resizable: false, height:600, width:800, modal: true,
     		buttons: {
     			"Apri la schermata per impostare la data di Consegna": function() {
 					$( this ).dialog( "close" );
 					  var History = window.History;	
 					  if (History.enabled == true) 
 					    History.pushState({ action : "orderResp", id: idOrder, idType: 1, tab: 2 }, null, "orderResp");
 				},
     			"Chiudi": function() {
     				$( this ).dialog( "close" );
     			}
     		}
     	});
    	break;
    case 5:
    		// 5 Data di consegna Settata [Normale, Resp] - solo a quelli le cui schede non sono fallite
    	if (memberType == 0) 
    		varAction = "purchase";
    	else if (memberType == 1)
    		varAction = "purchaseResp";
    	
    	 $("#dialog-notify").attr('title', "Notifica Data di Consegna Impostata");
    	 
    	 $( "#dialog-notify" ).dialog({
      		resizable: false, height:600, width:800, modal: true,
      		buttons: {
      			"Apri schermata Schede in consegna": function() {
  					$( this ).dialog( "close" );
  					  var History = window.History;	
  					  if (History.enabled == true) 
  					    History.pushState({ action : varAction, idOrd: idOrder, tab: 2 }, null, varAction);
  				},
      			"Chiudi": function() {
      				$( this ).dialog( "close" );
      			}
      		}
      	});
    	
    	break;
    case 7:
    case 8:
    case 9:
    	
    	//7 Ordine 50 % [Normale, Resp]
    	//8 Ordine 75 % [Normale, Resp]
    	//9 Ordine 90 % [Normale, Resp]
    	// Se l'utente ha una scheda per quell'ordine, rimandare alla modifica scheda
    	// Se l'utente non ha una scheda per quell'ordine, rimanadare a creazione scheda.
    	
    	if (memberType == 0) 
    		varAction = "purchase";
    	else if (memberType == 1)
    		varAction = "purchaseResp";
    	
    	var hasPurchase = "";
        $.postSync("ajax/getHasPurchaseForOrder", { idOrder: idOrder }, function(result)
        {
        	hasPurchase = result;
        });
        
        if(hasPurchase != null) {
        	
        	//L'utente ha la scheda d'acquisto. Spedirlo nella modifica scheda
        	
        	$("#dialog-notify").attr('title', "Notifica Progresso Ordine");
       	 
	       	 $( "#dialog-notify" ).dialog({
	         		resizable: false, height:600, width:800, modal: true,
	         		buttons: {
	         			"Visualizza la tua scheda": function() {
	     					$( this ).dialog( "close" );
	     					  var History = window.History;	
	     					  if (History.enabled == true) 
	     					    History.pushState({ action : varAction, idOrd: hasPurchase.idPurchase, idOrd2: idOrder, tab: 1 }, null, varAction);
	     				},
	         			"Chiudi": function() {
	         				$( this ).dialog( "close" );
	         			}
	         		}
	         	});
        	
        	
        } else {
        	
        	//L'utente non ha la scheda. Spedirlo in creazione scheda
        	
        	$("#dialog-notify").attr('title', "Notifica Progresso Ordine");
       	 
	       	 $( "#dialog-notify" ).dialog({
	         		resizable: false, height:600, width:800, modal: true,
	         		buttons: {
	         			"Crea una scheda per questo Ordine": function() {
	     					$( this ).dialog( "close" );
	     					  var History = window.History;	
	     					  if (History.enabled == true) 
	     					    History.pushState({ action : varAction, idOrd: idOrder, tab: 0 }, null, varAction);
	     				},
	         			"Chiudi": function() {
	         				$( this ).dialog( "close" );
	         			}
	         		}
	         	});
        }
    	
    	break;
    case 10:
    	
    		//10 Chiusura Ordine [Normale o Resp partecipante come normale] (sia fallito che non)
    	
    	$("#dialog-notify").attr('title', "Notifica Ordine Concluso");
    	
    		//ORdine concluso. Se non è fallito, ricavare la scheda e visualizzare se è fallita o meno.
    	
    	var hasPurchase = "";
        $.postSync("ajax/getHasPurchaseForOrder", { idOrder: idOrder }, function(result)
        {
        	hasPurchase = result;
        });
        
    	var failed = false;
		$.postSync("ajax/isPurchaseFailed", {idPurchase: hasPurchase.idPurchase}, function(data) { 
			failed = data; 
		});
    	
		if(failed) {
			$("#dialog-notify-container").append("<h3><strong>La tua scheda &egrave; <span style='color: red'> FALLITA </span></strong></h3>");
		} else {
			$("#dialog-notify-container").append("<h3><strong>La tuo acquisto ha avuto <span style='color: green'> successo </span></strong>. " +
					"</br> Riceverai una notifica quando verr&agrave; impostata la data di consegna.</h3>");
		}
		
		$( "#dialog-notify" ).dialog({
    		resizable: false, height:600, width:800, modal: true,
    		buttons: {
    			"Chiudi": function() {
    				$( this ).dialog( "close" );
    			}
    		}
    	});
		
		
    	break;
    case 11:
    		// 11 Nuovo ordine [Supplier]
    	
    	 $("#dialog-notify").attr('title', "Notifica Nuovo Ordine");
    	 
    	$( "#dialog-notify" ).dialog({
    		resizable: false, height:600, width:800, modal: true,
    		buttons: {
    			"Visualizza l'avanzamento dell'ordine in tempo reale": function() {
					$( this ).dialog( "close" );
					  var History = window.History;	
					  if (History.enabled == true) 
					    History.pushState({ action : "orderSup", idOrd: idOrder, tab: 0 }, null, "orderSup");
				},
    			"Chiudi": function() {
    				$( this ).dialog( "close" );
    			}
    		}
    	});
    	
    	break;
    case 12:
    	
    	 //12 Chiusura Ordine con successo [Supplier]
    	
    	$("#dialog-notify").attr('title', "Notifica Ordine Concluso con Successo");
    	
    	$( "#dialog-notify" ).dialog({
    		resizable: false, height:600, width:800, modal: true,
    		buttons: {
    			"Visualizza il dettaglio dell'ordine": function() {
					$( this ).dialog( "close" );
					  var History = window.History;	
					  if (History.enabled == true) 
					    History.pushState({ action : "orderSup", idOrd: idOrder, tab: 1 }, null, "orderSup");
				},
    			"Chiudi": function() {
    				$( this ).dialog( "close" );
    			}
    		}
    	});
    	
    	break;
    case 13:
    	
    		//Data di consegna settata [Supplier]
    	
    	$("#dialog-notify").attr('title', "Notifica Ordine: data di consegna impostata.");
    	
    	$( "#dialog-notify" ).dialog({
    		resizable: false, height:600, width:800, modal: true,
    		buttons: {
    			"Visualizza lo storico degli ordini": function() {
					$( this ).dialog( "close" );
					  var History = window.History;	
					  if (History.enabled == true) 
					    History.pushState({ action : "orderSup", idOrd: 0, tab: 2 }, null, "orderSup");
				},
    			"Chiudi": function() {
    				$( this ).dialog( "close" );
    			}
    		}
    	});
    	
    	break;
    
    }
    
  });
}


function viewMemberDetails(idMember, notifyCategory)
{
  $.postSync("ajax/viewM", {
    'idMember' : idMember
  }, function(member)
  {
    var details = "<table><tr><td class='imageTD'><img src='img/user.png' width='250'/></td>"
      +"<td class='dataTD'><table class='memberDetailsTable'>"
    + "<tr><td>Nome</td><td class='fieldTD'>" + member.name + "</td></tr>"
    + "<tr><td>Cognome</td><td class='fieldTD'>" + member.surname + "</td></tr>";
    if (member.username.match("https://.*") == null)
      details += "<tr><td>Username</td><td class='fieldTD'>" + member.username + "</td></tr>";
    details += "<tr><td>Data di registrazione</td><td class='fieldTD'>" + $.datepicker.formatDate('dd-mm-yy', new Date(member.regDate)) + "</td></tr>"
    + "<tr><td>Email</td><td class='fieldTD'>" + member.email + "</td></tr>"
    + "<tr><td>Indirizzo</td><td class='fieldTD'>" + member.address + "</td></tr>"
    + "<tr><td>Citt&agrave;</td><td class='fieldTD'>" + member.city + "</td></tr>"
    + "<tr><td>Stato</td><td class='fieldTD'>" + member.state + "</td></tr>"
    + "<tr><td>CAP</td><td class='fieldTD'>" + member.cap + "</td></tr>";
    if (member.tel != null)
      details += "<tr><td>Telefono</td><td class='fieldTD'>" + member.tel + "</td></tr>";
    details += "<tr><td>Tipo di utente</td><td class='fieldTD'>" + member.memberType + "</td></tr>"
    + "<tr><td>Status utente</td><td class='fieldTD'>" + member.memberStatus + "</td></tr>";
    details += "</table></td></tr></table>";
  
    $("#dialog-notify").attr('title', "Nuovo Utente");
	$("#dialog-notify-container").html(details);
    
    $( "#dialog-notify" ).dialog({
		resizable: false,
		height:450,
		width:800,
		modal: true,
		buttons: {
			"Abilita Ora": function() {
				
				$( this ).dialog( "close" );
				
				$.post("ajax/activeMember", {
				    idMember : idMember
				  }, function(result) {
					  
					  if(result == 0) 
						  $("#dialog-notify-action-container").html('Abilitazione fallita');
					  else
						  $("#dialog-notify-action-container").html('Abilitazione avvenuta con successo');
					  
					  $( "#dialog-notify-action" ).dialog({
				    		resizable: false,
				    		height: 150,
				    		width: 200, 
				    		modal: true,
				    		buttons : {
				             	 "Ok" : function() { $(this).dialog('close'); }
				           	       }
				    	});
				  });
			},
			"Abilita in un secondo momento": function() {
				$( this ).dialog( "close" );
			}
		}
	});
  
  });
}

function printOrderPie(progress)
{
  new CanvasXpress("orderProgressCanvas", {
    "y": {
      "vars": [
        "Ok",
        "No"
      ],
      "smps": [
        "Percentuale completamento"
      ],
      "data": [
        [
          progress
        ],
        [
          100-progress
        ]
      ],
    }
  }, {
    "graphType": "Pie",
    "pieSegmentPrecision": 1,
    "pieSegmentSeparation": 2,
    "pieSegmentLabels": "outside",
    "pieType": "solid",
    "showLegend": false,
    "colors": [
               "rgb(57,133,0)",
               "rgb(163,0,8)"
               ],
  });
}

function registerForNotifies()
{
  var source = new EventSource('ajax/newnotifies');
  source.onmessage = function(event)
  {
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

function registerForNews()
{
  var source = new EventSource('ajax/getnews');
  source.addEventListener('mess', function(e)
  {
    if (e.data > 0)
    {
      if (!window.History.enabled
          || window.History.getState().data.action != 'messaggi')
      {
        $('#messagesCount').html(e.data);
        $('#messagesCount').css("color", "red");
      }
      else
      {
        getMyMessages();
      }
    }
  }, false);
  source.addEventListener('nots', function(e)
  {
    if (e.data > 0)
    { 
      if (!window.History.enabled
          || window.History.getState().data.action != 'notifiche')
      {
        $('#notifiesCount').html(e.data);
        $('#notifiesCount').css("color", "red");
      }
      else
      {
        getMyNotifies();
      }
    }
  }, false);
}

function getMyMessages()
{
  var tabellaMessaggi = "<div class='contentDiv'><table class='messaggi'>";
  if ($(".contentDiv") != undefined && $(".contentDiv").scrollTop() != 0)
    var distanceFromBottom = $(".contentDiv").height()
        - $(".contentDiv").scrollTop();
  $.getSync("ajax/getmymessages", undefined, function(messagesList)
  {
    if (messagesList.length < 1)
      tabellaMessaggi += "<tr><td><p>Non ci sono messaggi da visualizzare</p></td></tr>";
    var sendersList = getUsersExceptMe();
    for ( var mesIndex in messagesList)
    {
      tabellaMessaggi += "<tr><td";
      if (!messagesList[mesIndex].isReaded)
        tabellaMessaggi += " class='not_read_m' ";
      var username = formatUsername(messagesList[mesIndex].sender);
      tabellaMessaggi += " data-idmessage='" + messagesList[mesIndex].idMessage
          + "'><h3>" + sendersList[messagesList[mesIndex].sender]
          + " (" + username + ")"
          + "</h3><p>" + messagesList[mesIndex].text
          + "</p><img src='img/reply.png' class='replyTo' data-replyusername='"
          + messagesList[mesIndex].sender + "' title='Rispondi' /></td></tr>";
    }
  });
  tabellaMessaggi += "</table></div>";
  var users = "<option value='notSelected' selected='selected'>Seleziona...</option>";
  var usersList = getUsersExceptMe();
  for ( var userIndex in usersList)
  {
    users += "<option value='" + userIndex + "'>"
        + usersList[userIndex]+ " ("+formatUsername(userIndex)+")</option>";
  }
  // var orders = "<option value='-1'
  // selected='selected'>Seleziona...</option>";
  // var ordersList = getOrders();
  // for ( var orderIndex in ordersList)
  // {
  // orders += "<option value='" + ordersList[orderIndex] + "'>"
  // + ordersList[orderIndex] + "</option>";
  // }
  // var products = "<option value='-1'
  // selected='selected'>Seleziona...</option>";
  // var productsList = getProducts();
  // for ( var prodIndex in productsList)
  // {
  // products += "<option value='" + productsList[prodIndex] + "'>"
  // + productsList[prodIndex] + "</option>";
  // }
  var formInvioMessaggio = "<div class='messageform'><form method='post' action=''>"
      + "<fieldset><legend>&nbsp;Invia un messaggio&nbsp;</legend>"
      + "<br><label for='usersSelect' class='left'>Destinatario: </label>"
      + "<select name='usersSelect' id='usersSelect' class='field'>"
      + users
      + "</select><br><br>"
      // + "<label for='ordersSelect' class='left'>Ordine (opzionale): </label>"
      // + "<select name='ordersSelect' id='ordersSelect' class='field'>"
      // + orders
      // + "</select><br><br>"
      // + "<label for='productsSelect' class='left'>Prodotto (opzionale):
      // </label>"
      // + "<select name='productsSelect' id='productsSelect' class='field'>"
      // + products
      // + "</select><br><br>"
      + "<label for='messageText' class='left'>Testo: </label>"
      + "<textarea name='messageText' id='messageText' class='field' "
      + "required='required' placeholder='Inserisci qua il tuo messaggio...' maxlength='300' /><br>"
      + "<button type='submit' id='newMessageSubmit'> Invia messaggio </button>"
      + "</fieldset></form></div>";
  $(".centrale").html(tabellaMessaggi + formInvioMessaggio);
  if (distanceFromBottom != undefined)
    $(".contentDiv").scrollTop(
        $(".contentDiv").height() - distanceFromBottom + 1);
  $("#bodyTitleHeader").html("Messaggi");
  $("button").button();
  $(".not_read_m").click(setReadMessage);
  $('#newMessageSubmit').on("click", clickNewMessageHandler);
  $('#messagesCount').html("0");
  $('#messagesCount').css("color", "");
  $('.replyTo').click(replyTo);
}

function formatUsername(username)
{
  if(username.match("https://www.google.com/accounts/.*"))
    return "Google Account";
  else if(username.match("https://me.yahoo.com/.*"))
    return "Yahoo Account";
  else if(username.match("oauth2:facebook:.*"))
    return "Facebook Account";
  else
    return username;
}

function replyTo(event)
{
	event.preventDefault();
	var replyUsername = $(this).data('replyusername');
	$('#usersSelect').val(replyUsername);
}

function getUsernames()
{
  var usersList;
  $.getJSONsync("ajax/getusernames", function(usernamesList)
  {
    usersList = usernamesList;
  });
  return usersList;
}

function getUsernamesExceptMe()
{
  var usersList;
  $.getJSONsync("ajax/getusernamesexceptme", function(usernamesList)
  {
    usersList = usernamesList;
  });
  return usersList;
}

function getUsersExceptMe()
{
  var usersList;
  $.getJSONsync("ajax/getusersexceptme", function(rusersList)
  {
    usersList = rusersList;
  });
  return usersList;
}

function getOrders()
{
  // TODO
}

function getProducts()
{
  // TODO
}

function clickNewMessageHandler(event)
{
  event.preventDefault();

  var errors = new Array();

  var username = $('#usersSelect').val();
  var messageText = $('#messageText').val();

  if (username == 'notSelected' || username == undefined || messageText == "")
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
    var idMessage = newMessage({
      dest : username,
      messageText : messageText
    });
    if (idMessage > 0)
    {
      $("#usersSelect").val("notSelected");
      $("#messageText").val("");
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

function setReadNotify()
{
  var idNotify = $(this).attr("name");
  $.postSync("ajax/setreadnotify", {
    'idNotify' : idNotify
  }, function()
  {
  });
}

function setReadMessage()
{
  var idMessage = $(this).data("idmessage");
  $.postSync("ajax/setreadmessage", {
    'idMessage' : idMessage
  }, function()
  {
  });
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
