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
