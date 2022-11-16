package com.examples.empapp.service;
import java.sql.Statement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import com.examples.empapp.dao.EmpDao;
import com.examples.empapp.exception.EmployeeException;
import com.examples.empapp.model.Employee;

public class DaoServiceImpl implements EmpDao{
	
	static Connection conn = ConnectionJDBC.createConnection();
	@Override
	public boolean create(Employee employee) {
		PreparedStatement pstmt = null;
		
		try {
		      String insertQuery = "INSERT INTO employee (name, age, department, designation, country) VALUES(?, ?, ?, ?, ?);";
		      pstmt = conn.prepareStatement(insertQuery);
		      pstmt.setString(1, employee.getName());
		      pstmt.setInt(2, employee.getAge());
		      pstmt.setString(3, employee.getDepartment());
		      pstmt.setString(4, employee.getDesignation());
		      pstmt.setString(5, employee.getCountry());
		      int rowAffected =  pstmt.executeUpdate();
		      System.out.println("row affected->" + rowAffected);
		      pstmt.close();
		      return true;
		    
		}catch(SQLException e) {
			System.out.println("error while inserting" + e.getMessage());
			e.printStackTrace();
			
		}
		return false;
	}
	
	@Override
	public boolean delete(int empId) {
		PreparedStatement pstmt = null;
		try {
			String insertQuery = "delete from employee where empId = ?;";
			pstmt = conn.prepareStatement(insertQuery);
			pstmt.setInt(1, empId);
		    pstmt.execute();
		    pstmt.close();
		    return true;
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public Employee get(int id) throws EmployeeException {
		Employee employee = null;
		PreparedStatement pstmt  = null;
		ResultSet rs  = null;
		 try {
			 String query = "select * from employee where empId = ?;";
		     pstmt = conn.prepareStatement(query);
		     pstmt.setInt(1, id);
		      rs =  pstmt.executeQuery();
		     while(rs.next()) {
		    	 int empId = rs.getInt("empId");
		    	 String name = rs.getString("name");
		    	 int age = rs.getInt("age");
		    	 String department = rs.getString("department");
		    	 String designation = rs.getString("designation");
		    	 String country = rs.getString("country");
		    	 employee = new Employee(empId, name, age, department, designation, country);
		    	 
		     }
		     if(employee == null) {
		    	 throw new EmployeeException("No employee found for given id.");
		     }
		     rs.close();
		     pstmt.close();
		     return employee;
		 }
		 catch(SQLException e) {
				System.out.println("error while getting data" + e.getMessage());
				e.printStackTrace();
			}
		return employee;
		
	}

	@Override
	public List<Employee> getAll() {
		List<Employee> employees = new ArrayList<>();
		try {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM employee") ;
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				int id = rs.getInt("empId");
				String name = rs.getString("name");
				int age = rs.getInt("age");
				String designation = rs.getString("designation");
				String department = rs.getString("department");
				String country = rs.getString("country");
				Employee employee = new Employee(id , name , age,designation, department, country);
				employees.add(employee);
			}
			rs.close();
			pstmt.close();
			return (List<Employee>) employees ;
		} catch (SQLException e) {
			System.out.println("Unable to fetch all data " + e.getMessage());
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	public void statistics() {
		System.out.println("No of employee older than twenty five years: "
				+ getEmployeeCountAgeGreaterThan());
		System.out.println("List employee IDs older than twenty five years: " + getEmployeeIdsAgeGreaterThan());
		System.out.println("Employee count by Department: " + getEmployeeCountByDepartment());
		System.out.println("Employee count by Department ordered: " + getEmployeeCountByDepartmentOdered());
		
	}

	@Override
	public void export() {
		String path = ".\\output\\employee-output.txt";
		File file = new File(path);
		System.out.println("file exist"+file.exists());
		List<Employee> empList = getAll();
		String temp = "";
		for(Employee x : empList) {
			temp += x.getEmpId()+","+x.getName()+","+x.getAge()+","+x.getDesignation()+","+x.getDepartment()+","+x.getCountry()+"\n";
		}
		try(FileOutputStream fileOutputStream = new FileOutputStream(path,true)) {
			byte[] bytes = temp.getBytes();
			fileOutputStream.write(bytes);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void exit() {
		try {
			conn.close();
			System.out.println("All Connection Closed.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}


	@Override
	public boolean validate(Employee emp, String msg, java.util.function.Predicate<Employee> condition,
			java.util.function.Function<String, Boolean> operation) {
		// TODO Auto-generated method stub
		return false;
	}
	private int getEmployeeCountAgeGreaterThan(){
		
		String q = "SELECT COUNT(*) FROM employee where age > 30" ;
		
		int count ;
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(q);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				 count = rs.getInt(1);
				return count ;
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return 0 ;
	}
	public ArrayList<Integer> getEmployeeIdsAgeGreaterThan(){
		ArrayList<Integer> array = new ArrayList<>();
		String q = "SELECT empId FROM employee where age > 30 ;" ;
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(q);
			ResultSet rs = pstmt.executeQuery() ; 
			
			while(rs.next()) {
				array.add(rs.getInt("empId"));
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return array ;
	}
	
	public Map<Integer,Integer> getEmployeeCountAgeGreaterThan(int age){
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		String query = "select count(*) as count, age from employee where age > 30;";
		try {
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet result = pstmt.executeQuery();
			
			while(result.next()) {
 				int age1 = result.getInt("age");
				int count = result.getInt("count");
				
				map.put(age1, count);
			}
			result.close();
			
			
		}catch(SQLException e ) {
			e.printStackTrace();
		}
		return map;
	}
	public Map<String,Integer> getEmployeeCountByDepartment(){
		Map<String, Integer> map = new HashMap<String, Integer>();
		String q = "SELECT department,COUNT(*) as count FROM employee GROUP BY department ;" ;
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(q);
			ResultSet rs = pstmt.executeQuery() ; 
			while(rs.next()) {
				String department = rs.getString("department");
				int count = rs.getInt("count");
				
				map.put(department, count);
			}
			rs.close();
		}catch(SQLException e ) {
			e.printStackTrace();
		}
		
		return map ;
		
	}
	public Map<String,Integer> getEmployeeCountByDepartmentOdered() {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		String q = "SELECT department,COUNT(*) as count FROM employee GROUP BY department ORDER BY department ;" ;
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(q);
			ResultSet rs = pstmt.executeQuery() ; 
			while(rs.next()) {
				String department = rs.getString("department");
				int count = rs.getInt("count");
				map.put(department, count);
				
			}
			rs.close();
		}catch(SQLException e ){
			e.printStackTrace();
		}
		
		return map ;
	}

	@Override
	public boolean update(Employee emp) {
		try {
			Statement stmt = (Statement) conn.createStatement();
			String query = "UPDATE employee SET name = '" + emp.getName() + "', age = '" + emp.getAge() + "', department = '" + emp.getDepartment() + "', designation = '" + emp.getDesignation() + "', country = '" + emp.getCountry() + "' WHERE empId = " + emp.getEmpId() + ";";
			stmt.executeUpdate(query);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error Updating Employee");
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public void bulkImport() {
		System.out.format("%n%s - Import started %n", Thread.currentThread().getName());
		int counter = 0;
		
		try (Scanner in = new Scanner(new FileReader(".\\input\\employee-input.txt"))) {
			System.out.println("Implorting file...");
			while (in.hasNextLine()) {
				String emp = in.nextLine();
				System.out.println("Importing employee - " + emp);
				Employee employee = new Employee();
				StringTokenizer tokenizer = new StringTokenizer(emp, ",");

				// Emp ID
//				employee.setEmpId(Integer.parseInt(tokenizer.nextToken()));
				// Name
				employee.setName(tokenizer.nextToken());
				// Age
				employee.setAge(Integer.parseInt(tokenizer.nextToken()));
				// Designation
				employee.setDesignation(tokenizer.nextToken());
				// Department
				employee.setDepartment(tokenizer.nextToken());
				// Country
				employee.setCountry(tokenizer.nextToken());

//				employees.put(employee.getEmpId(), employee);
				this.create(employee);
				counter++;
			}
			System.out.format("%s - %d Employees are imported successfully.", Thread.currentThread().getName(),
					counter);
		} catch (Exception e) {
			System.out.println("Error occured while importing employee data. " + e.getMessage());
		}
	}


}
