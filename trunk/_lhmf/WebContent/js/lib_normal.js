//CRUD on Order
function newPurchase(purchase)
{
  $.postJSON("ajax/newpurchase", purchase, function(
      idPurchase)
  {
    console.debug("Inserted: " + idPurchase);
  });
  
  function getPastPurchases()
  {
    $.getJSON("ajax/getpastpurchases", function(orderList)
    {
      window.localStorage.setItem('orderList', JSON.stringify(orderList));
      console.debug("orderList saved in localstorage");
    });
  }
   
  function getActivePurchases()
  {
    $.getJSON("ajax/getactivepurchases", function(orderList)
    {
      window.localStorage.setItem('orderList', JSON.stringify(orderList));
      console.debug("orderList saved in localstorage");
    });
  }
}
