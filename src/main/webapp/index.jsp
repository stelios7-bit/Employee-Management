<%--
  This index page will automatically redirect the user to the main employee list.
  Place this file in the root of your web application content directory (e.g., WebContent or webapp).
--%>
<%
    response.sendRedirect("employee?action=list");
%>
