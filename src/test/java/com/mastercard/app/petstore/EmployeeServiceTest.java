package com.mastercard.app.petstore;

import com.mastercard.app.petstore.services.EmployeeService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.EmployeesApi;
import org.openapitools.client.model.Employee;
import org.openapitools.client.model.EmployeeData;
import org.openapitools.client.model.EmployeeListData;
import org.openapitools.client.model.NewEmployee;
import org.openapitools.client.model.NewEmployeeData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    private EmployeesApi employeesApi;
    private EmployeesApi employeesApiEncryptedForBody;
    private EmployeesApi employeesApiEncryptedForFLE;

    @InjectMocks
    EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        employeesApi = mock(EmployeesApi.class);
        employeesApiEncryptedForBody = mock(EmployeesApi.class);
        employeesApiEncryptedForFLE = mock(EmployeesApi.class);
        employeeService = new EmployeeService(employeesApi, employeesApiEncryptedForFLE, employeesApiEncryptedForBody);
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
    public void returnEmployee_shouldReturnAnEmployee() throws ApiException {
        Employee employee = MockDataBuilders.buildEmployee();
        EmployeeData employeeData = new EmployeeData();
        employeeData.setEmployee(employee);
        when(employeesApiEncryptedForFLE.getEmployee(employee.getUsername())).thenReturn(employeeData);

        EmployeeData returntedEmployeeData = employeeService.getEmployee(employee.getUsername());

        assertEquals(returntedEmployeeData.getEmployee().getUsername(), employee.getUsername());
    }

    @Test
    public void deleteEmployee_shouldJustRun() throws ApiException {
        String id = "Bob123";
        doNothing().when(employeesApi).removeEmployee(id);

        employeeService.deleteEmployee(id);

        verify(employeesApi, times(1)).removeEmployee(id);
    }

}
