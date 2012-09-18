<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<t:index>
	<jsp:attribute name="scripts">
	<script type="text/javascript">
  $(function()
  {
     $("#notifiesLink").click(notifiesClicked);
     $("#messagesLink").click(messagesClicked);
     registerForMessages();
     registerForNotifies();
     getMyMessages();  
  });
	</script>
	</jsp:attribute>

	<jsp:attribute name="userMenu">
	</jsp:attribute>

	<jsp:attribute name="dialogs">
	</jsp:attribute>

	<jsp:attribute name="bodyTitle">Messaggi</jsp:attribute>

</t:index>