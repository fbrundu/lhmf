//////
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
  
function getPastPurchases()
{
    $.getJSONsync("ajax/getpastpurchases", function(pastPurchasesList)
    {
    	window.localStorage.setItem('pastPurchasesList', JSON.stringify(pastPurchasesList));
    	console.debug("pastPurchasesList saved in localstorage");
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

function getActiveOrdersAsRows(ordersList, respsList, suppliersList, idOrderIn)
{
	var returnedTableString = "";
	if (ordersList == undefined || idOrderIn == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
		return returnedTableString;
	}
	for ( var orderIndex in ordersList)
	{
		if (ordersList[orderIndex].idOrder == idOrderIn)
		{
			returnedTableString += "<tr>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateOpen + "</td>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateClose + "</td>";
			returnedTableString += getSupplierAsTableRow(suppliersList, ordersList[orderIndex].idMemberSupplier);
			returnedTableString += getRespAsTableRow(respsList, ordersList[orderIndex].idMemberResp);
			returnedTableString += "</tr>";
		}
	}
	return returnedTableString;
}

function getPastOrdersAsRows(ordersList, respsList, suppliersList, idOrderIn)
{
	var returnedTableString = "";
	if (ordersList == undefined || idOrderIn == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
		return returnedTableString;
	}
	for ( var orderIndex in ordersList)
	{
		if (ordersList[orderIndex].idOrder == idOrderIn)
		{
			returnedTableString += "<tr>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateOpen + "</td>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateClose + "</td>";
			returnedTableString += "<td>" + ordersList[orderIndex].dateDelivery + "</td>";
			returnedTableString += getSupplierAsTableRow(suppliersList, ordersList[orderIndex].idMemberSupplier);
			returnedTableString += getRespAsTableRow(respsList, ordersList[orderIndex].idMemberResp);
			returnedTableString += "</tr>";
		}
	}
	return returnedTableString;
}

function getPurchasesAsTableRows(purchasesList, respsList, orderList, page, itemsPerPage)
{
	var returnedTableString = "";
	if (page < 1 || (page - 1) * itemsPerPage >= purchasesList.length
		|| itemsPerPage < 1 || itemsPerPage > 100 || purchasesList == undefined
		|| page == undefined || itemsPerPage == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
		return "";
	}
	for ( var purchaseIndex = (page - 1) * itemsPerPage; purchaseIndex < purchasesList.length
      	&& purchaseIndex <= page * itemsPerPage; purchaseIndex++)
	{
	    returnedTableString += "<tr>";
	    returnedTableString += getRespAsTableRow(respsList, ordersList[orderIndex].idMemberResp);
	    returnedTableString += "<td>" + purchasesList[purchaseIndex].idMember + "</td>";
	    returnedTableString += getActiveOrdersAsRows(ordersList, purchasesList[purchaseIndex].idOrder);
	    returnedTableString += getPastOrdersAsRows(ordersList, purchasesList[purchaseIndex].idOrder);
	    returnedTableString += "</tr>";
	}
	return returnedTableString;
}