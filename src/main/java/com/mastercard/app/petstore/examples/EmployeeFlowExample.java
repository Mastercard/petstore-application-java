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
@Component("EmployeeFlowExample")
public class EmployeeFlowExample {

    private EmployeeService employeeService;

    /**
     * Constructs a new {@code EmployeeFlowExample} with the required service dependency.
     * <p>
     * This constructor is annotated with {@link org.springframework.beans.factory.annotation.Autowired},
     * allowing Spring to automatically inject the {@link EmployeeService} bean at runtime.
     * The service is then used to perform operations related to employees within the flow example.
     * </p>
     *
     * @param employeeService the service responsible for managing employee-related operations
     */
    @Autowired
    public EmployeeFlowExample(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

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
