package com.mastercard.app.petstore.flow;

import com.mastercard.app.petstore.TestMockBuilders;
import com.mastercard.app.petstore.services.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.EmployeesApi;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * The type Employee flow test.
 */
@SpringBootTest()
@RunWith(SpringRunner.class)
@ActiveProfiles({"oauth"})
@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
public class EmployeeFlowTest {

    /**
     * The Base path. Set in application.properties
     */
    @Value("${mastercard.basePath}")
    String basePath;

    @Autowired
    private EmployeeService employeeService;

    /**
     * Employee use case. Show add a new employee, searching for their information, then removing them.
     *
     * @throws ApiException the api exception
     */
    @Test
    public void employeeUseCase() throws ApiException {
        //Skipping test if applications.properties isn't set
        if(basePath == null){
            return;
        }
        //Add employee
        NewEmployee newEmployee = TestMockBuilders.buildNewEmployee();
        NewEmployeeData newEmployeeData = new NewEmployeeData().addNewEmployeesItem(newEmployee);
        Employee employee = employeeService.createEmployee(newEmployeeData).getEmployees().get(0);

        //Search for employee
        EmployeeSearch employeeSearch = new EmployeeSearch();
        employeeSearch.setSsn(employee.getSsn());
        EmployeeWrapper employeeWrapper = employeeService.searchEmployee(employeeSearch);

        //Remove employee
        employeeService.deleteEmployee(employee.getId().toString());
    }
}
