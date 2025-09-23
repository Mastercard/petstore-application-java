package com.mastercard.app.petstore.examples;

import com.mastercard.app.petstore.services.EmployeeService;
import com.mastercard.app.petstore.utils.MockDataBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.*;

import static org.mockito.Mockito.*;

class EmployeeFlowExampleTest {

    @InjectMocks
    private EmployeeFlowExample employeeFlowExample;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEmployeeUseCase() throws ApiException {
        NewEmployee newEmployee = new NewEmployee().ssn("123-45-6789");
        Employee employee = new Employee().ssn("123-45-6789");
        EmployeeWrapper employeeWrapper = new EmployeeWrapper().username("jdoe");

        NewEmployeeData newEmployeeData = new NewEmployeeData().addNewEmployeesItem(newEmployee);

        EmployeeListData employeeData = new EmployeeListData().addEmployeesItem(employee);

        when(employeeService.createEmployee(any(NewEmployeeData.class))).thenReturn(employeeData);
        when(employeeService.searchEmployee(any(EmployeeSearch.class))).thenReturn(employeeWrapper);

        try (MockedStatic<MockDataBuilders> mockBuilders = mockStatic(MockDataBuilders.class)) {
            mockBuilders.when(MockDataBuilders::buildNewEmployee).thenReturn(newEmployee);

            employeeFlowExample.employeeUseCase();

            verify(employeeService).createEmployee(newEmployeeData);
            verify(employeeService).searchEmployee(argThat(search -> "123-45-6789".equals(search.getSsn())));
            verify(employeeService).deleteEmployee("jdoe");
        }
    }

    @Test
    void testEmployeeUseCaseThrowsApiException() throws ApiException {
        NewEmployee newEmployee = new NewEmployee().ssn("123-45-6789");

        when(employeeService.createEmployee(any(NewEmployeeData.class))).thenThrow(new ApiException("API error"));

        try (MockedStatic<MockDataBuilders> mockBuilders = mockStatic(MockDataBuilders.class)) {
            mockBuilders.when(MockDataBuilders::buildNewEmployee).thenReturn(newEmployee);

            org.junit.jupiter.api.Assertions.assertThrows(ApiException.class, () -> {
                employeeFlowExample.employeeUseCase();
            });
        }
    }
}
