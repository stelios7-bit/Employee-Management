package com.dao;
import com.model.Employee;
import com.util.DBConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.model.Employee;

public class EmployeeDAO {

    // Define a path to store uploaded images. 
    // IMPORTANT: Make sure this directory exists and your application has write permissions.
    // You might need to change this path depending on your server environment.
    private static final String UPLOAD_DIRECTORY = "C:/employee_photos";

    /**
     * Adds a new employee to the database and saves their photo to the file system.
     * @param employee The Employee object to be added.
     * @return true if the employee was added successfully, false otherwise.
     */
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (name, DOB, MobNo, photo_filename, photo_original_filename, photo_content_type) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        // 1. Save the photo to the file system
        String uniqueFilename = savePhotoToFileSystem(employee.getPhotoInputStream(), employee.getPhotoOriginalFilename());
        if (uniqueFilename == null) {
            System.err.println("Failed to save photo to file system.");
            return false;
        }
        employee.setPhotoFilename(uniqueFilename);


        // 2. Save employee details to the database
        try {
            conn = DBConnection.getConnection(); // Assuming you have a DBConnection class
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, employee.getName());
            pstmt.setDate(2, Date.valueOf(employee.getDob()));
            pstmt.setLong(3, employee.getMobNo());
            pstmt.setString(4, employee.getPhotoFilename());
            pstmt.setString(5, employee.getPhotoOriginalFilename());
            pstmt.setString(6, employee.getPhotoContentType());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            // Consider deleting the saved file if DB insert fails
            deletePhotoFromFileSystem(uniqueFilename);
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("Error closing resources: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Retrieves all employees from the database.
     * @return A list of all employees.
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        // --- FIX: Add ORDER BY id ASC to ensure consistent sorting ---
        String sql = "SELECT * FROM employees ORDER BY id ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getInt("id"));
                emp.setName(rs.getString("name"));
                
                Date dobDate = rs.getDate("DOB");
                if (dobDate != null) {
                    emp.setDob(dobDate.toLocalDate());
                } else {
                    emp.setDob(null); 
                }

                emp.setMobNo(rs.getLong("MobNo"));
                emp.setPhotoFilename(rs.getString("photo_filename"));
                emp.setPhotoOriginalFilename(rs.getString("photo_original_filename"));
                emp.setPhotoContentType(rs.getString("photo_content_type"));
                employees.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                 System.err.println("Error closing resources: " + ex.getMessage());
            }
        }
        return employees;
    }

    /**
     * Deletes an employee from the database by their ID.
     * Also deletes the associated photo from the file system.
     * @param employeeId The ID of the employee to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteEmployee(int employeeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        String photoFilename = getPhotoFilenameById(employeeId);
        if (photoFilename == null) {
            System.err.println("Could not find employee or photo filename for ID: " + employeeId);
        }

        String sql = "DELETE FROM employees WHERE id = ?";

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);

            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                if (photoFilename != null) {
                    deletePhotoFromFileSystem(photoFilename);
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("Error closing resources: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Helper method to get a photo filename from the database based on employee ID.
     * @param employeeId The ID of the employee.
     * @return The filename of the photo, or null if not found.
     */
    private String getPhotoFilenameById(int employeeId) {
        String sql = "SELECT photo_filename FROM employees WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String filename = null;

        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                filename = rs.getString("photo_filename");
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        } finally {
             try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                 System.err.println("Error closing resources: " + ex.getMessage());
            }
        }
        return filename;
    }


    /**
     * Helper method to save an uploaded photo InputStream to a file.
     * @param inputStream The input stream of the photo.
     * @param originalFilename The original name of the file.
     * @return The unique filename under which the file was saved, or null on failure.
     */
    private String savePhotoToFileSystem(InputStream inputStream, String originalFilename) {
        if (inputStream == null || originalFilename == null || originalFilename.isEmpty()) {
            return null;
        }
        
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        File uploadDir = new File(UPLOAD_DIRECTORY);
        
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File file = new File(UPLOAD_DIRECTORY, uniqueFilename);

        try (OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return uniqueFilename;
        } catch (IOException e) {
            System.err.println("File I/O Error: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Helper method to delete a photo from the file system.
     * @param filename The name of the file to delete.
     */
    private void deletePhotoFromFileSystem(String filename) {
        if (filename == null || filename.isEmpty()) {
            return;
        }
        try {
            File fileToDelete = new File(UPLOAD_DIRECTORY, filename);
            if (fileToDelete.exists()) {
                if(fileToDelete.delete()){
                    System.out.println("File deleted successfully: " + filename);
                } else {
                    System.err.println("Failed to delete file: " + filename);
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
    }
}
