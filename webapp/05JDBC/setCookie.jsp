<%@page import="common.CookieManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String val1=request.getParameter("val1name");
String del = request.getParameter("");

if(val1 !=null ) {
	Cookie cookie = new Cookie("Name", val1);
	cookie.setPath(request.getContextPath());
	cookie.setMaxAge(60*2);
	response.addCookie(cookie);
} else {
	CookieManager.deleteCookie(response, "Name");
}

String savedName = "";
Cookie[] cookies = request.getCookies();
if (cookies != null) {
    for (Cookie c : cookies) {
            savedName = c.getValue();
    }
}

if (!savedName.equals("")) {
    out.println("저장된 쿠키값: " + savedName );
} else {
    out.println("저장된 쿠키가 없습니다.");
}


%>

