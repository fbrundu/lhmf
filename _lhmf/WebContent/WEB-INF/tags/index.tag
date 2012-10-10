<%@tag description="Overall Page template" pageEncoding="UTF-8"%>

<%-- Insert fragments name below --%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="userMenu" fragment="true"%>
<%@attribute name="bodyTitle" fragment="true"%>
<%@attribute name="dialogs" fragment="true"%>
<%-- End fragments declaration --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!doctype html>
<html class="no-js">
<head>
<base href='<spring:url htmlEscape="true" value="/"/>'/>
<meta charset="utf-8" />
<meta name="copyright" content="Politecnico di Torino 2012" />
<meta name="description" content="Applicazioni Internet" />
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<link rel="stylesheet" href='<spring:url htmlEscape="true" value="/css/jquery-ui-1.8.21.custom.css"/>'/>
<link rel="stylesheet" href='<spring:url htmlEscape="true" value="/css/setup.css"/>'/>
<link rel="stylesheet" href='<spring:url htmlEscape="true" value="/css/text.css"/>'/>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/jquery-1.7.2.min.js"/>'></script>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/jquery-ui-1.8.21.custom.min.js"/>'></script>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/jquery.history.js"/>'></script>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/modernizr-2.5.3.js"/>'></script>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib.js"/>'></script>
<!--[if IE]><script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/canvasxpress/excanvas.js"/>'></script><![endif]-->
<!--[if IE]><script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/canvasxpress/extext.js"/>'></script><![endif]-->
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/canvasxpress/canvasXpress.min.js"/>'></script>
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true&language=it"></script>

<jsp:invoke fragment="scripts" />

<title>Applicazioni Internet - GAS</title>
</head>
<body>
	<jsp:invoke fragment="dialogs" />
	<div class="gasDialog" id="dialog-error-insert" title="Errore">
		<p>
			<span class="ui-icon ui-icon-alert"
				style="float: left; margin: 0 7px 20px 0;"></span> Errore
			nell'inserimento
		</p>
	</div>
	<div class="gasDialog" id="dialog-ok" title="Ok">
		<p>
			<span class="ui-icon ui-icon-check"
				style="float: left; margin: 0 7px 20px 0;"></span> Operazione andata
			a buon fine
		</p>
	</div>
	<div class="contenitore-pagina">
		<header class="header">
			<div class="header-middle"></div>
		</header>
		<nav class="header-percorso">
		<c:if test="${user != null}">
			
			<div id="menu_wrapper" class="grey">
				<ul id="menu">
					<li><a href="<spring:url htmlEscape="true" value="/logout"/>">Logout</a></li>
					<li><a id='notifiesLink' href="<spring:url htmlEscape="true" value="/notifiche"/>">Notifiche <span id='notifiesCount'> 0</span></a></li>
					<li><a href="<spring:url htmlEscape="true" value="/messaggi"/>" id='messagesLink' >Messaggi <span id='messagesCount'> 0</span></a></li>
					<li><a>Ciao ${user}</a></li>
				</ul>
			</div>

	    </c:if>
		</nav>
		<div class="main">
			<div class="navigazione">
				<div class="round-border-topright"></div>
				<h1 class="first">Menu</h1>
				<dl class="menu-navigazione">
					<dt><a href='<spring:url htmlEscape="true" value="/"/>'>Home</a></dt>
					<dt><a href='https://www.facebook.com/groups/277966958981569/' target="_blank">Gruppo Facebook
					<img style="vertical-align:middle" src='<spring:url htmlEscape="true" value="/img/facebooklogo.png"/>' width='20px' /></a></dt>
				</dl>
				<jsp:invoke fragment="userMenu" />
			</div>
			<div class="contenuto">
				<!-- <h1 class="titolo">Applicazioni Internet</h1> -->
				<h1 id='bodyTitleHeader' class="block"><jsp:invoke fragment="bodyTitle" /></h1>
				<div class="centrale"><jsp:doBody /></div>
			</div>
			<div id='dialog-notify' title='Visualizzazione Notifica' style='display:none; text-align:center' align='center'>
            	<div id='dialog-notify-container' align='center'></div>
			</div>
			<div id='dialog-notify-action' title='Risultato Azione' style='display:none; text-align:center' align='center'> 
			 	<div id='dialog-notify-action-container' align='center'></div>
			</div>
		</div>
		<footer class="footer">
			<p>Politecnico di Torino &copy; 2012</p>
		</footer>
	</div>
</body>
</html>