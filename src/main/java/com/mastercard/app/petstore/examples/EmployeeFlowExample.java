package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.EmployeeService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * The type Employee flow example.
 */

@ComponentScan(basePackages = {"com.mastercard.app.petstore.utils"})
@Component("EmployeeFlowExample")
public class EmployeeFlowExample {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Employee use case. Show add a new employee, searching for their information, then removing them.
     *
     * @throws ApiException thrown whenever there is an issue sending a request
     */
    public void employeeUseCase() throws ApiException {
        //Add employee
        NewEmployee newEmployee = MockDataBuilders.buildNewEmployee();
        NewEmployeeData newEmployeeData = new NewEmployeeData().addNewEmployeesItem(newEmployee);
        Employee employee = employeeService.createEmployee(newEmployeeData).getEmployees().get(0);

        //Search for employee
        EmployeeSearch employeeSearch = new EmployeeSearch();
        employeeSearch.setSsn(employee.getSsn());
        EmployeeWrapper employeeWrapper = employeeService.searchEmployee(employeeSearch);

        //Remove employee
        employeeService.deleteEmployee(employeeWrapper.getUsername());
    }

}
