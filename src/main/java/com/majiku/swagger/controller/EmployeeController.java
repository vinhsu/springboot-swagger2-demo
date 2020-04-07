package com.majiku.swagger.controller;

import com.majiku.swagger.exception.ResourceNotFoundException;
import com.majiku.swagger.model.Employee;
import com.majiku.swagger.repository.EmployeeRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Api(value = "Employee Management System", description = "Operations pertaining to employee in Employee Management System")
public class EmployeeController {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @ApiOperation(value = "View a list of available employees", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @ApiOperation(value = "Get an employee by Id")
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @ApiParam(value = "Employee id from which employee object will retrieve", required = true)
            @PathVariable("id") Long employeeId)
            throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
        new ResourceNotFoundException("Employee not found for this id :: " + employeeId));
        return ResponseEntity.ok(employee);
    }

    @ApiOperation(value = "Add an employee")
    @PostMapping("/employees")
    public Employee  creteEmployee(
            @ApiParam(value = "Employee object store in database table", required = true)
            @Valid @RequestBody Employee employee){
        return employeeRepository.save(employee);
        //another way to return
        //return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update an employee")
    @PutMapping("employees/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @ApiParam(value = "Employee Id to update employee object", required = true)
            @PathVariable("id") Long employeeId,
            @ApiParam(value = "Update employee object", required = true)
            @Valid @RequestBody Employee employeeDetails)
            throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new ResourceNotFoundException("Employee not found for this id :: " + employeeId));

        employee.setEmailId(employeeDetails.getEmailId());
        employee.setLastName(employeeDetails.getLastName());
        employee.setFirstName(employeeDetails.getFirstName());
        final Employee updateEmployee = employeeRepository.save(employee);
        return ResponseEntity.ok(updateEmployee);
    }

    @ApiOperation(value = "Delete an employee")
    @DeleteMapping("employees/{id}")
    public Map<String, Boolean> deleteEmployee(
            @ApiParam(value = "Employee Id from which employee object will delete from database table", required = true)
            @PathVariable("id") Long employeeId) throws ResourceNotFoundException {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new ResourceNotFoundException("Employee not found for this id :: " + employeeId));

        employeeRepository.delete(employee);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
