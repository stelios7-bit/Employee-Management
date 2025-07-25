<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Employee Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            padding: 20px;
        }
        .employee-photo {
            max-width: 100px;
            max-height: 100px;
            border-radius: 5px;
        }
    </style>
</head>
<body>

    <div class="container">
        <h1 class="mb-4">Employee List</h1>

        <p>
            <a href="addEmployee.jsp" class="btn btn-primary">Add New Employee</a>
        </p>

        <table class="table table-bordered table-striped">
            <thead class="table-dark">
                <tr>
                    <th>S.No.</th>
                    <th>Name</th>
                    <th>Date of Birth</th>
                    <th>Mobile No.</th>
                    <th>Photo</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%-- Use varStatus to get a loop counter for sequential numbering --%>
                <c:forEach var="employee" items="${employeeList}" varStatus="loop">
                    <tr>
                        <%-- Display the loop count (starts at 1) instead of the database ID --%>
                        <td><c:out value="${loop.count}" /></td>
                        <td><c:out value="${employee.name}" /></td>
                        <td><c:out value="${employee.dob}" /></td>
                        <td><c:out value="${employee.mobNo}" /></td>
                        <td>
                            <c:if test="${not empty employee.photoFilename}">
                                <img src="images/${employee.photoFilename}" alt="Employee Photo" class="employee-photo"/>
                            </c:if>
                            <c:if test="${empty employee.photoFilename}">
                                <span>No Image</span>
                            </c:if>
                        </td>
                        <td>
                            <%-- The delete link still correctly uses the actual employee.id --%>
                            <a href="employee?action=delete&id=<c:out value='${employee.id}'/>" 
                               class="btn btn-danger btn-sm"
                               onclick="return confirm('Are you sure you want to delete this employee?')">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

</body>
</html>
