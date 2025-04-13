package com.smartpos.common.service;

import com.smartpos.common.entity.Employee;
import java.util.List;

public interface EmployeeService {
    Employee findById(Long id);
    List<Employee> findAll();
    List<Employee> findByRole(String role);
    Employee save(Employee employee);
    Employee updateEmployee(Employee employee);
    void deleteById(Long id);
    Employee findByEmail(String email);
}