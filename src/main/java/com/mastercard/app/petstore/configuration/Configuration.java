package com.mastercard.app.petstore.configuration;

import com.mastercard.app.petstore.services.AdoptionsService;
import com.mastercard.app.petstore.services.CatService;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.AdoptionsApi;
import org.openapitools.client.api.CatsApi;
import org.openapitools.client.api.DogsApi;
import org.openapitools.client.api.EmployeesApi;
import org.openapitools.client.api.PetsApi;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public AdoptionsApi adoptionsApiFle(ApiClient apiClientEncryption) throws Exception {
        return new AdoptionsApi(apiClientEncryption);
    }

    @Bean
    public AdoptionsApi adoptionsApiFullBody(ApiClient apiClientEncryption) throws Exception {
        return new AdoptionsApi(apiClientEncryption);
    }

    @Bean
    public CatsApi catsApi(ApiClient apiClient) {
        return new CatsApi(apiClient);
    }

    @Bean
    public DogsApi dogsApi(ApiClient apiClient) {
        return new DogsApi(apiClient);
    }

    @Bean
    public PetsApi petsApi(ApiClient apiClient) {
        return new PetsApi(apiClient);
    }

    @Bean
    public EmployeesApi employeesApi(ApiClient apiClient) {
        return new EmployeesApi(apiClient);
    }

    @Bean
    public EmployeesApi employeesApiEncryptedForFLE(ApiClient apiClientEncryption) {
        return new EmployeesApi(apiClientEncryption);
    }

    @Bean
    public EmployeesApi employeesApiEncryptedForBody(ApiClient apiClientEncryption) {
        return new EmployeesApi(apiClientEncryption);
    }
}
