package com.mastercard.app.petstore.configuration;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.AdoptionsApi;
import org.openapitools.client.api.CatsApi;
import org.openapitools.client.api.DogsApi;
import org.openapitools.client.api.EmployeesApi;
import org.openapitools.client.api.PetsApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@org.springframework.context.annotation.Configuration
@ComponentScan("com.mastercard.app.petstore")
public class Configuration {

    @Bean
    public AdoptionsApi adoptionsApiFle(ApiClient apiClientEncryptionAdoptionFle) {
        return new AdoptionsApi(apiClientEncryptionAdoptionFle);
    }

    @Bean
    public AdoptionsApi adoptionsApiFullBody(ApiClient apiClientEncryption) {
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
    public EmployeesApi employeesApiEncryptedForBody(ApiClient apiClientEncryption) {
        return new EmployeesApi(apiClientEncryption);
    }
}
