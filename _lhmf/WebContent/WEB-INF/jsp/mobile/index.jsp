<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE HTML>
<html>
<head>
<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;">
<meta charset="utf-8">
<title>GasProject.net</title>
<link href='<spring:url htmlEscape="true" value="/css/style-mobile.css"/>' rel="stylesheet" type="text/css"  />
<script src='<spring:url htmlEscape="true" value="/js/jquery-1.7.2.min.js"/>' type="text/javascript"></script>
<link type="text/css" rel="stylesheet" href='<spring:url htmlEscape="true" value="/js/openid-selector/css/openid-shadow.css"/>'/>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/openid-selector/js/openid-jquery.js"/>'></script>
<script type="text/javascript" src='<spring:url htmlEscape="true" value="/js/openid-selector/js/openid-it.js"/>'></script>
<script type="text/javascript">
$(document).ready(function() {
	openid.init('openid_identifier');
});

function facebook_click() {
	$('#facebook_form').submit();
}
</script>
</head>
<body>
    <div class="header" align="center">
        <div class="logo">
            <a href="<spring:url value='/'></spring:url>"><img src="img/logo.png" alt="GasProject.it" /></a>
        </div>
        <div align="center"><button id="show">Menu <span>+</span> <span style="display:none;">-</span></button></div>
        <div class="clear"></div>
    </div>
    <div class="nav">
        <ul>
            <li><a href="<spring:url value='/'></spring:url>">Home</a></li>
            <li><a href="">Menu Item 1</a></li>
            <li><a href="">Menu Item 2</a></li>
            <li><a href="">Menu Item 3</a></li>
        </ul>
    </div>
    <div class="content">
        <div class="loginform" align="center">
        
            <c:if test="${param.form_error != null}">
                <fieldset><legend>&nbsp;Errore&nbsp;</legend><br />
                   <span class="actionError">
                       <strong>Il tentativo di accesso al sito non è andato a buon fine.</strong><br/><br/>
                       Causa: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.<br/>
                  </span>
                </fieldset>
            </c:if>
        
           <form id="loginform" method = "POST" action='<spring:url value="/j_spring_security_check"></spring:url>'>
                <fieldset><legend>&nbsp;Effettua il Login&nbsp;</legend><br />
                    <p>	<label for="j_username" class="top">Username</label><br />
                        <input type="text" name="j_username" id="j_username" tabindex="1" class="field" value="" /></p>
                    <p>	<label for="j_password" class="top">Password</label><br />
                        <input type="password" name="j_password" id="j_password" tabindex="2" class="field" value="" /></p><br />
                    <p>	<input type="submit" name="action" class="button" value="LOGIN"  /></p>
                </fieldset>
            </form>
        </div>
        
        <div class="loginBox" align="center">
	        <c:if test="${param.openid_error != null || param.fb_error != null}">
	        <fieldset><legend>&nbsp;Errore&nbsp;</legend><br />
			   <span class="actionError">
				   <strong>Il tentativo di accesso al sito non è andato a buon fine.</strong><br/><br/>
				   Causa: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.<br/>
			  </span>
			 </fieldset>
			</c:if>
	        <h2 align="center">Accedi con il tuo account preferito</h2>
			<p class="center"></p>
			<form id="openid_form_mobile" class="loginform" name="oidf" action='<spring:url htmlEscape="true" value="/j_spring_openid_security_check"/>' method="POST">
				<div id="openid_choice" >
					<div id="openid_btns"></div>
				</div>
				<div id="openid_input_area">
					<input id="openid_identifier" name="openid_identifier" type="text" value="http://" required="required"/>
					<input id="openid_submit" type="submit" value="Sign-In"/>
				</div>
			</form>
			<form style="display:none" id="facebook_form" class="loginform" name="f" action='<spring:url htmlEscape="true" value="/j_spring_oauth_security_check"/>' method="POST">
				<input type="submit" />
			</form>
		</div>
    </div>
<div class="header" align="center">
   Politecnico di Torino &copy; 2012
</div>
<script type="text/javascript">
	$('.nav').hide();
	$('#show').click(function (){
		$(".nav").toggle();
		$("span").toggle();
	});
</script>

</body>
</html>
