<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach var="u" items="${users}">       
 <div id="${u.uid}" class="sU w3-container w3-card w3-text w3-round w3-margin w3-animate-opacity"><br>
   <img src="ProfileImages/${u.uid}.png" alt="Avatar" class="w3-left w3-circle w3-margin-right" style="width:60px; margin-bottom: 10px" onerror="javascript:this.src='ProfileImages/default.png'">
   <h4 class="uVw" style="text-decoration:none" onmouseover="style='text-decoration:underline; cursor:pointer'" onmouseout="style='text-decoration:none'"> ${u.username} </h4>
   <div>
   <c:if test="${u.uid != user.uid}">
   <c:if test="${u.isFollowed}">
			<button type="button" class="uU w3-button w3-right w3-red w3-margin-bottom w3-round-medium"><i class="fa fa-minus-circle"></i>&nbsp;Unfollow</button>
	</c:if>
				 
	<c:if test="${!u.isFollowed}">
			<button type="button" class="fU w3-button w3-right w3-green w3-margin-bottom w3-round-medium"><i class="fa fa-plus-circle"></i>&nbsp;Follow</button>
	</c:if>
	</c:if>
	</div>
 </div>
</c:forEach>