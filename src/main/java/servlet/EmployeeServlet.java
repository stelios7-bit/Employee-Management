package servlet;

import com.model.Employee; // <-- CRUCIAL IMPORT STATEMENT
import com.dao.EmployeeDAO;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/employee")
@MultipartConfig
public class EmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private EmployeeDAO employeeDAO;

    public void init() {
        employeeDAO = new EmployeeDAO();
    }

    /**
     * Handles POST requests, primarily for adding a new employee.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Retrieve form data
        String name = request.getParameter("name");
        LocalDate dob = LocalDate.parse(request.getParameter("dob"));
        long mobNo = Long.parseLong(request.getParameter("mobNo"));
        
        // Handle file upload
        Part filePart = request.getPart("photo");
        InputStream photoInputStream = null;
        String photoOriginalFilename = null;
        String photoContentType = null;

        if (filePart != null) {
            photoInputStream = filePart.getInputStream();
            photoOriginalFilename = filePart.getSubmittedFileName();
            photoContentType = filePart.getContentType();
        }

        // Create a new Employee object
        Employee newEmployee = new Employee();
        newEmployee.setName(name);
        newEmployee.setDob(dob);
        newEmployee.setMobNo(mobNo);
        newEmployee.setPhotoInputStream(photoInputStream);
        newEmployee.setPhotoOriginalFilename(photoOriginalFilename);
        newEmployee.setPhotoContentType(photoContentType);

        // Add the employee to the database
        employeeDAO.addEmployee(newEmployee);

        // Redirect to the employee list page
        response.sendRedirect("employee?action=list");
    }

    /**
     * Handles GET requests for listing and deleting employees.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        // Default action is to list employees
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "delete":
                deleteEmployee(request, response);
                break;
            case "list":
            default:
                listEmployees(request, response);
                break;
        }
    }

    /**
     * Fetches the list of all employees and forwards to the JSP for display.
     */
    private void listEmployees(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Employee> employeeList = employeeDAO.getAllEmployees();
        request.setAttribute("employeeList", employeeList);
        request.getRequestDispatcher("employeeList.jsp").forward(request, response);
    }

    /**
     * Handles the deletion of an employee.
     */
    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        employeeDAO.deleteEmployee(id);
        response.sendRedirect("employee?action=list");
    }
}
