<%@tag description="Overall Page template" pageEncoding="UTF-8"%>

<%-- Insert fragments name below --%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="userMenu" fragment="true"%>
<%@attribute name="bodyTitle" fragment="true"%>
<%-- End fragments declaration --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!doctype html>
<html class="no-js">
<head>
<meta charset="utf-8" />
<meta name="copyright" content="Politecnico di Torino 2012" />
<meta name="description" content="Applicazioni Internet" />
<link rel="stylesheet" href='<spring:url htmlEscape="true" value="/css/jquery-ui-1.8.21.custom.css"/>'/>
<link rel="stylesheet" href='<spring:url htmlEscape="true" value="/css/setup.css"/>'/>
<link rel="stylesheet" href='<spring:url htmlEscape="true" value="/css/text.css"/>'/>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/jquery-1.7.2.min.js"/>'></script>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/jquery-ui-1.8.21.custom.min.js"/>'></script>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/jquery.history.js"/>'></script>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/modernizr-2.5.3.js"/>'></script>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/lib.js"/>'></script>

<jsp:invoke fragment="scripts" />

<title>Applicazioni Internet - GAS</title>
</head>
<body>
	<div class="contenitore-pagina">
		<header class="header">
			<div class="header-middle"></div>
		</header>
		<nav class="header-percorso">
		<c:if test="${user != null}">
			<ul>
		    	<li>Ciao ${user} | <a href='<spring:url htmlEscape="true" value="/logout"/>'>Logout</a>
		    </ul>
	    </c:if>
		<ul>
	      <li><a href='<spring:url htmlEscape="true" value="/notifiche"/>'>Notifiche</a></li>
	      <li>NN</li>
	    </ul>
	    <ul>
	      <li><a href='<spring:url htmlEscape="true" value="/messaggi"/>'>Messaggi</a></li>
	      <li>NN</li>
	    </ul>
		</nav>
		<div class="main">
			<div class="navigazione">
				<div class="round-border-topright"></div>
				<h1 class="first">Menu</h1>
				<dl class="menu-navigazione">
					<dt><a href='<spring:url htmlEscape="true" value="/"/>'>Home</a></dt>
					<dt><a href="relazione.php">Relazione</a></dt>
					<dd><a href="presentazione.php">Struttura Web</a></dd>
					<dd><a href="db.php">Il DataBase</a></dd>
					<dd><a href="auth.php">L'autenticazione</a></dd>
					<dd><a href="reg.php">La Registrazione</a></dd>
				</dl>
				<br /><br />
				<jsp:invoke fragment="userMenu" />
			</div>
			<div class="contenuto">
				<!-- <h1 class="titolo">Applicazioni Internet</h1> -->
				<h1 class="block"><jsp:invoke fragment="bodyTitle" /></h1>
				<div class="centrale"><jsp:doBody /></div>
			</div>
		</div>
		<footer class="footer">
			<p>Politecnico di Torino &copy; 2012</p>
			<p class="credits">
				Powered by <a href="http://validator.w3.org/check?uri=referer"
					title="Validate XHTML code">W3C XHTML 1.0</a> | <a
					href="http://jigsaw.w3.org/css-validator/"
					title="Validate CSS code">W3C CSS 2.0</a>
			</p>
		</footer>
	</div>
</body>
</html>