package com.mastercard.app.petstore.services;

import org.openapitools.client.ApiException;
import org.openapitools.client.api.EmployeesApi;
import org.openapitools.client.model.Employee;
import org.openapitools.client.model.EmployeeData;
import org.openapitools.client.model.EmployeeListData;
import org.openapitools.client.model.EmployeeSearch;
import org.openapitools.client.model.EmployeeWrapper;
import org.openapitools.client.model.NewEmployeeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The type Employee service.
 */
@Service
public class EmployeeService {
    private final EmployeesApi employeesApi;
    private final EmployeesApi employeesApiEncryptedForFLE;
    private final EmployeesApi employeesApiEncryptedForBody;

    /**
     * Instantiates a new Employee service.
     *
     * @param employeesApi                 the employees api for unencrypted calls
     * @param employeesApiEncryptedForFLE  the employees api encrypted for field level encryption. Only certain will be encrypted
     * @param employeesApiEncryptedForBody the employees api encrypted for full payload encryption
     */
    @Autowired
    public EmployeeService(EmployeesApi employeesApi, EmployeesApi employeesApiEncryptedForFLE, EmployeesApi employeesApiEncryptedForBody) {
        this.employeesApi = employeesApi;
        this.employeesApiEncryptedForBody = employeesApiEncryptedForBody;
        this.employeesApiEncryptedForFLE = employeesApiEncryptedForFLE;
    }

    /**
     * Add new employees. Uses encryption on certain fields
     *
     * @param newEmployees the new employees
     * @return the employee list data
     * @throws ApiException the api exception
     */
    public EmployeeListData createEmployee(NewEmployeeData newEmployees) throws ApiException {
        return employeesApiEncryptedForBody.addEmployees(newEmployees);
    }

    /**
     * Search employee data. Uses encryption on certain fields
     *
     * @param employeeSearch the employee search
     * @return the employee list wrapper
     * @throws ApiException the api exception
     */
    public EmployeeWrapper searchEmployee(EmployeeSearch employeeSearch) throws ApiException {
        return employeesApiEncryptedForFLE.searchEmployee(employeeSearch);
    }

    /**
     * Gets information on a single employee.
     *
     * @param username the username
     * @return the employee
     * @throws ApiException the api exception
     */
    public EmployeeData getEmployee(String username) throws ApiException {
        return employeesApiEncryptedForFLE.getEmployee(username);
    }

    /**
     * Update employee.
     *
     * @param etag     the etag. Can be found in getEmployee
     * @param employee the employee
     * @throws ApiException the api exception
     */
    public void updateEmployee(String etag, Employee employee) throws ApiException {
        EmployeeData employeeData = new EmployeeData();
        employeeData.setEmployee(employee);
        employeesApiEncryptedForBody.updateEmployee(employee.getId().toString(), etag, employeeData);
    }

    /**
     * Delete employee.
     *
     * @param id the id
     * @throws ApiException the api exception
     */
    public void deleteEmployee(String id) throws ApiException {
        employeesApi.removeEmployee(id);
    }


}