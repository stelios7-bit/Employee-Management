package servlet;

import com.model.Employee; 
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        String name = request.getParameter("name");
        LocalDate dob = LocalDate.parse(request.getParameter("dob"));
        long mobNo = Long.parseLong(request.getParameter("mobNo"));
       
        Part filePart = request.getPart("photo");
        InputStream photoInputStream = null;
        String photoOriginalFilename = null;
        String photoContentType = null;

        if (filePart != null) {
            photoInputStream = filePart.getInputStream();
            photoOriginalFilename = filePart.getSubmittedFileName();
            photoContentType = filePart.getContentType();
        }

        Employee newEmployee = new Employee();
        newEmployee.setName(name);
        newEmployee.setDob(dob);
        newEmployee.setMobNo(mobNo);
        newEmployee.setPhotoInputStream(photoInputStream);
        newEmployee.setPhotoOriginalFilename(photoOriginalFilename);
        newEmployee.setPhotoContentType(photoContentType);

        employeeDAO.addEmployee(newEmployee);

        response.sendRedirect("employee?action=list");
    }
  
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
       
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

    private void listEmployees(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Employee> employeeList = employeeDAO.getAllEmployees();
        request.setAttribute("employeeList", employeeList);
        request.getRequestDispatcher("employeeList.jsp").forward(request, response);
    }
    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        employeeDAO.deleteEmployee(id);
        response.sendRedirect("employee?action=list");
    }
}
