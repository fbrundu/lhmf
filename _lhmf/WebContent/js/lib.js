$.postJSON = function(url, data, callback) {
    return jQuery.ajax({
        'type': 'POST',
        'url': url,
        'contentType': 'application/json',
        'data': JSON.stringify(data),
        'dataType': 'json',
        'success': callback
    });
};

$.postSync = function(url, data, callback) {
    return jQuery.ajax({
    	'async': false,
        'type': 'POST',
        'url': url,
        'data': data,
        'success': callback
    });
};

$.postJSONsync = function(url, data, callback) {
  return jQuery.ajax({
      'async': false,
      'type': 'POST',
      'url': url,
      'contentType': 'application/json',
      'data': JSON.stringify(data),
      'dataType': 'json',
      'success': callback
  });
};

$.getJSONsync = function(url, callback) {
  return jQuery.ajax({
      'async': false,
      'type': 'GET',
      'url': url,
      'contentType': 'application/json',
      'success': callback
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
			return "<td>" + respsList[respIndex].name + respsList[respIndex].surname + /*respsList[respIndex].idMember +*/ "</td>";
  }
  return "";
}