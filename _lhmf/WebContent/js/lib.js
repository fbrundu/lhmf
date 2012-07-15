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
