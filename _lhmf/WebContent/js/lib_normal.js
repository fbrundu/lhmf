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

function getPurchasesAsTableRows(purchasesList, page, itemsPerPage)
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
	    returnedTableString += "<td>" + purchasesList[purchaseIndex].idMember + "</td>";
	    returnedTableString += "<td>" + purchasesList[purchaseIndex].idOrder + "</td>";
	    returnedTableString += "</tr>";
	}
	return returnedTableString;
}