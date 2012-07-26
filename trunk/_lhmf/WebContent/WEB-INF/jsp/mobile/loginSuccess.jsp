<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE HTML>
<html>
<head>
<base href='<spring:url htmlEscape="true" value="/"/>'/>
<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0;">
<meta charset="utf-8">
<title>GasProject.net</title>
<link href='<spring:url htmlEscape="true" value="/css/style-mobile.css"/>' rel="stylesheet" type="text/css"  />
<script src='<spring:url htmlEscape="true" value="/js/jquery-1.7.2.min.js"/>' type="text/javascript"></script>
</head>
<body>
    <div class="header" align="center">
        <div class="logo">
            <a href="<spring:url htmlEscape='true' value='/'></spring:url>"><img src="<spring:url htmlEscape='true' value='/img/logo.png'/>" alt="GasProject.it"/></a>
        </div>
        <!-- <div align="center"><button id="show">Menu <span>+</span> <span style="display:none;">-</span></button></div> -->
        <div class="clear"></div>
    </div>
    <!-- <div class="nav">
        <ul>
            <li><a href="<spring:url value='/'></spring:url>">Home</a></li>
            <li><a href="">Menu Item 1</a></li>
            <li><a href="">Menu Item 2</a></li>
            <li><a href="">Menu Item 3</a></li>
        </ul>
    </div> -->
    <div class="content">
        <div class="loginform" align="center">
        	<p>
        	Login avvenuto con successo!
        	</p>
        </div>
    </div>
<div class="header" align="center">
   Politecnico di Torino &copy; 2012
</div>
<!-- <script type="text/javascript">
	$('.nav').hide();
	$('#show').click(function (){
		$(".nav").toggle();
		$("span").toggle();
	});
</script> --->

</body>
</html>
