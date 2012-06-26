<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:index>
	<jsp:attribute name="scripts">
		<script type="text/javascript">
			function showLogs(event){
				if(!!window.history){
					//var historyState = {richiesta: "consultazione log"};
					//history.pushState(historyState, "Consultazione log", "log");
					//event.preventDefault();
				}
				event.preventDefault();
				$(".centrale").html("<p>Seleziona range di date:</p><p><label for='start'>Data iniziale: </label><input type='text' id='start'><label for='end'> Data finale: </label><input type='text' id='end'>&nbsp;<button type='button' id='logsRequest'>Click me!</button>");
				$("#start").datepicker({
					defaultDate: 0,
					maxDate: 0
					});
				$('#start').datepicker("setDate", Date.now());
				$('#end').datepicker({
					defaultDate: 0,
					maxDate: 0
					});
				$('#end').datepicker("setDate", Date.now());
				$('#logsRequest').on("click", function(event){
					var startDate = $('#start').datepicker("getDate");
					var endDate = $('#end').datepicker("getDate");
					endDate.setHours(23);
					endDate.setMinutes(59);
					endDate.setSeconds(59);
					endDate.setMilliseconds(999);
					if(startDate == null || endDate == null){
						window.alert("Selezionare entrambe le date!");
					}
					else{
						$.getJSON("ajax/getlogs", {start: startDate.getTime(), end: endDate.getTime()}, function(logList){
								console.log("Ricevuti log");
								for(var i = 0; i < logList.length; i++){
									var log = logList[i];
									console.log("Log id: " + log.idLog + ", log text: " + log.logtext + ", member name: " + log.member.name + 
											", member surname: " + log.member.surname + ", log timestamp: " + new Date(log.logTimestamp));
								}
							});
						console.log("Date selezionate: " + JSON.stringify(startDate) + ", " + JSON.stringify(endDate) + ", (timestamps):" + startDate.getTime() + ", " + endDate.getTime());
					}
				});
			}

			$(function(){
				$.datepicker.setDefaults({
					dateFormat: 'dd/mm/yy'
					});
				//window.addEventListener("popstate", function(event){
				//	window.alert("Ricevuto popstate event: ");
				//	if(!!event.state)
				//		console.log("Ricevuto popstate event: " + event.state.richiesta);
				//}, false);
			});
		</script>
    </jsp:attribute>
    
  <jsp:attribute name="userMenu">
      <p>Menù di amministrazione</p>
      <p>Gestione utenti</p>
      <p><a href="log" onclick="showLogs(event)">Consultazione log</a></p>
    </jsp:attribute>
  <jsp:attribute name="bodyTitle">
      Interfaccia di amministrazione
    </jsp:attribute>
  <jsp:body>
        <p>Body Admin</p>
    </jsp:body>
</t:index>