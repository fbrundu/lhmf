//CRUD on Order
function newOrder(order)
{
  $.postJSON("ajax/neworder", order, function(idOrder)
  {
    console.debug("Inserted: " + idOrder);
  });
}

function getPastOrders()
{
  $.getJSON("ajax/getpastorders", function(orderList)
  {
    window.localStorage.setItem('orderList', JSON.stringify(orderList));
    console.debug("orderList saved in localstorage");
  });
}
 
function getActiveOrders()
{
  $.getJSON("ajax/getactiveorders", function(orderList)
  {
    window.localStorage.setItem('orderList', JSON.stringify(orderList));
    console.debug("orderList saved in localstorage");
  });
}
