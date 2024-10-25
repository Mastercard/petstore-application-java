package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.EmployeeService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;

/**
 * The type Employee flow example.
 */

@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
public class EmployeeFlowExample {

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
    public void employeeUseCase() throws ApiException {
        //Skipping test if applications.properties isn't set
        if(basePath == null){
            return;
        }
        //Add employee
        NewEmployee newEmployee = MockDataBuilders.buildNewEmployee();
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
