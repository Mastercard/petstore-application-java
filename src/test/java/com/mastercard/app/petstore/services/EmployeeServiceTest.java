package com.mastercard.app.petstore.services;

import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.EmployeesApi;
import org.openapitools.client.model.Employee;
import org.openapitools.client.model.EmployeeData;
import org.openapitools.client.model.EmployeeListData;
import org.openapitools.client.model.EmployeeSearch;
import org.openapitools.client.model.EmployeeWrapper;
import org.openapitools.client.model.NewEmployee;
import org.openapitools.client.model.NewEmployeeData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeServiceTest {

    private EmployeesApi employeesApi;
    private EmployeesApi employeesApiEncryptedForBody;

    @InjectMocks
    EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        employeesApi = mock(EmployeesApi.class);
        employeesApiEncryptedForBody = mock(EmployeesApi.class);
        employeeService = new EmployeeService(employeesApi, employeesApiEncryptedForBody);
    }

    @Test
    public void createEmployee_shouldCreateAnEmployee() throws ApiException {
        NewEmployee newEmployee = MockDataBuilders.buildNewEmployee();
        NewEmployeeData newEmployeeData = new NewEmployeeData().addNewEmployeesItem(newEmployee);
        Employee employee = MockDataBuilders.buildEmployee();
        EmployeeListData employeeData = new EmployeeListData().addEmployeesItem(employee);

        when(employeesApiEncryptedForBody.addEmployees(newEmployeeData)).thenReturn(employeeData);

        EmployeeListData returnEemployeeData = employeeService.createEmployee(newEmployeeData);

        assertEquals(employeeData.getEmployees().size(), returnEemployeeData.getEmployees().size());
    }

    @Test
    public void searchEmployee_shouldReturnAnEmployee() throws ApiException {
        EmployeeSearch employeeSearch = MockDataBuilders.buildEmployeeSearch();
        EmployeeWrapper employee = MockDataBuilders.buildEmployeeWrapper();

        when(employeesApiEncryptedForBody.searchEmployee(employeeSearch)).thenReturn(employee);

        EmployeeWrapper response = employeeService.searchEmployee(employeeSearch);

        assertEquals(employee, response);
    }

    @Test
    public void getEmployee_shouldReturnAnEmployee() throws ApiException {
        Employee employee = MockDataBuilders.buildEmployee();
        EmployeeData employeeData = new EmployeeData();
        employeeData.setEmployee(employee);
        when(employeesApiEncryptedForBody.getEmployee(employee.getUsername())).thenReturn(employeeData);

        EmployeeData returntedEmployeeData = employeeService.getEmployee(employee.getUsername());

        assertEquals(returntedEmployeeData.getEmployee().getUsername(), employee.getUsername());
    }

    @Test
    public void updateEmployee_shouldUpdateAnEmployee() throws ApiException {
        String etag = "33a64df551425f";
        Employee employee = MockDataBuilders.buildEmployee();

        doNothing().when(employeesApiEncryptedForBody).updateEmployee(
                eq(employee.getId().toString()),
                eq(etag),
                argThat(data -> employee.equals(data.getEmployee())));

        employeeService.updateEmployee(etag, employee);

        verify(employeesApiEncryptedForBody, times(1)).updateEmployee(
                eq(employee.getId().toString()),
                eq(etag),
                argThat(data -> employee.equals(data.getEmployee())));
    }

    @Test
    public void deleteEmployee_shouldJustRun() throws ApiException {
        String id = "Bob123";
        doNothing().when(employeesApi).removeEmployee(id);

        employeeService.deleteEmployee(id);

        verify(employeesApi, times(1)).removeEmployee(id);
    }

}
