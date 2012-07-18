function newOrder(order)
{
	if (order == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
	    return;
	}
	$.postJSONsync("ajax/neworder", order, function(idOrder)
    {
    	console.debug("Inserted: " + idOrder);
    });
}

function getPastOrders()
{
	$.getJSONsync("ajax/getpastorders", function(pastOrdersList)
	{
		window.localStorage.setItem('pastOrdersList', JSON.stringify(pastOrdersList));
		console.debug("pastOrdersList saved in localstorage");
	});
}

function loadAllPastOrdersFromLocalStorage()
{
	return JSON.parse(window.localStorage.getItem('pastOrdersList'));
}

function getActiveOrders()
{
	$.getJSONsync("ajax/getactiveorders", function(activeOrdersList)
	{
		window.localStorage.setItem('activeOrdersList', JSON.stringify(activeOrdersList));
		console.debug("activeOrdersList saved in localstorage");
	});
}

function loadAllActiveOrdersFromLocalStorage()
{
	return JSON.parse(window.localStorage.getItem('activeOrdersList'));
}

function getOrdersAsTableRows(ordersList, page, itemsPerPage)
{
	var returnedTableString = "";
	if (page < 1 || (page - 1) * itemsPerPage >= ordersList.length
		|| itemsPerPage < 1 || itemsPerPage > 100 || ordersList == undefined
		|| page == undefined || itemsPerPage == undefined)
	{
		console.debug("Invalid parameters in " + displayFunctionName());
		return "";
	}
	for ( var orderIndex = (page - 1) * itemsPerPage; orderIndex < ordersList.length
      	&& orderIndex <= page * itemsPerPage; orderIndex++)
	{
	    returnedTableString += "<tr>";
	    returnedTableString += "<td>" + ordersList[orderIndex].dateOpen + "</td>";
	    returnedTableString += "<td>" + ordersList[orderIndex].dateClose + "</td>";
	    returnedTableString += "<td>" + ordersList[orderIndex].dateDelivery + "</td>";
	    //TODO: vedere come ritornare il membro responsabile
	    returnedTableString += "<td>" + ordersList[orderIndex].idMemberResp + "</td>";
	    returnedTableString += getSupplierAsTableRow(suppliersList, productsList[productIndex].idMemberSupplier);
	    returnedTableString += "</tr>";
	}
	return returnedTableString;
}