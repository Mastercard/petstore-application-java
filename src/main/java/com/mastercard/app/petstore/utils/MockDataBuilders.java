package com.mastercard.app.petstore.utils;

import org.openapitools.client.model.*;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.Random;

public class MockDataBuilders {

    public static NewCat buildNewCat(){
        NewCat newCat = new NewCat();
        newCat.setName("Cattie");
        newCat.setBreed("Tabby");
        newCat.setColor("White with black spots");
        newCat.setGender("FEMALE");
        newCat.birthdate(new Birthdate().year(2022).month(4).day(17));
        newCat.putCatProperty1Item("Intelligence", "9");
        newCat.setStatus(new PetStatus().value("AVAILABLE"));
        return newCat;
    }
    
    public static Cat buildCat(){
        Cat cat = new Cat(UUID.randomUUID(), new Date(), new Date());
        cat.setName("Cattie");
        cat.setBreed("Tabby");
        cat.setColor("White with black spots");
        cat.setGender("FEMALE");
        cat.birthdate(new Birthdate().year(2022).month(4).day(17));
        cat.putCatProperty1Item("Intelligence", "9");
        cat.setStatus(new PetStatus().value("AVAILABLE"));
        return cat;
    }

    public static NewDog buildNewDog(){
        NewDog newDog = new NewDog();
        newDog.setName("Dogtie");
        newDog.setBreed("Tabby");
        newDog.setColor("White with black spots");
        newDog.setGender("FEMALE");
        newDog.birthdate(new Birthdate().year(2022).month(4).day(17));
        newDog.putDogProperty1Item("Intelligence", "9");
        newDog.setStatus(new PetStatus().value("AVAILABLE"));
        return newDog;
    }

    public static Dog buildDog(){
        Dog dog = new Dog(UUID.randomUUID(), new Date(), new Date());
        dog.setName("Dogtie");
        dog.setBreed("Tabby");
        dog.setColor("White with black spots");
        dog.setGender("FEMALE");
        dog.birthdate(new Birthdate().year(2022).month(4).day(17));
        dog.putDogProperty1Item("Intelligence", "9");
        dog.setStatus(new PetStatus().value("AVAILABLE"));
        return dog;
    }

    public static Pet buildPet(){
        Pet pet = new Pet(UUID.randomUUID(), new Date(), new Date());
        pet.setName("Pettie");
        pet.setBreed("Tabby");
        pet.setColor("White with black spots");
        pet.setGender("FEMALE");
        pet.birthdate(new Birthdate().year(2022).month(4).day(17));
        pet.setStatus(new PetStatus().value("AVAILABLE"));
        return pet;
    }

    public static PetStatus buildPetStatus(){
        return new PetStatus().value("RESERVED");
    }

    public static NewAdoption buildNewAdoptionObject(UUID petId){
        NewAdoption newAdoption = new NewAdoption(petId);
        newAdoption.setOwner(buildOwnerObject("Bob"));
        return newAdoption;
    }

    public static NewAdoption buildNewAdoptionObject(){
        NewAdoption newAdoption = new NewAdoption(UUID.randomUUID());
        newAdoption.setOwner(buildOwnerObject("Bob"));
        return newAdoption;
    }
    
    public static Adoption buildAdoptionObject(){
        Pet pet = buildPet();
        Adoption adoption = new Adoption(UUID.randomUUID(), new Date(), new Date(), pet.getId());
        adoption.setOwner(buildOwnerObject("Bob"));
        adoption.setPet(pet);
        return adoption;
    }

    public static Owner buildOwnerObject(String firstName){
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName("Billson");
        owner.setPhoneNumber("+7509287096053");
        owner.setSsn(generateRandomSsn());
        return owner;
    }

    public static AdoptionSearch buildAdoptionSearch(){
        AdoptionSearch adoptionSearch = new AdoptionSearch(UUID.randomUUID());
        adoptionSearch.setFromDate(new Date());
        adoptionSearch.setToDate(new Date(0));
        adoptionSearch.setPetCategory("Dogs");
        adoptionSearch.setSearchResults((Collections.singletonList(buildAdoptionObject())));
        return adoptionSearch;
    }

    public static NewEmployee buildNewEmployee(){
        NewEmployee newEmployee = new NewEmployee();
        newEmployee.setFirstName(generateRandomName());
        newEmployee.setLastName(generateRandomName());
        newEmployee.setPhoneNumber("+6573437115596");
        newEmployee.setSsn(generateRandomSsn());
        return newEmployee;
    }

    public static Employee buildEmployee(){
        String firstName = generateRandomName();
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(generateRandomName());
        employee.setPhoneNumber("+6573437115596");
        employee.setSsn(generateRandomSsn());
        employee.setUsername(firstName+"123");
        return employee;
    }

    public static Payment buildPayment(){
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal(50));
        payment.setCurrency("USD");

        PaymentSource paymentSource = new PaymentSource();
        paymentSource.setName("Bob Bobson");
        paymentSource.setNumber("1234 1234 1234 1234");
        paymentSource.cvc("123");
        paymentSource.setExpMonth(1L);
        paymentSource.setExpYear("2030");
        paymentSource.setAddress(buildAddress());

        payment.setSource(paymentSource);

        return payment;
    }

    public static Address buildAddress(){
        Address address = new Address();
        address.setAddressLine1("1 South Dublin");
        address.setAddressLine2("Leopardstown");
        address.setCity("Dublin");
        address.setPostCode("124ASDF");
        address.setCountry("IE");
        return address;
    }

    public static PaymentDetails buildPaymentDetailsFromPayment(Payment payment) {
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setAmount(payment.getAmount());
        paymentDetails.setCurrency(payment.getCurrency());
        try {
            String uriString = "https://pay.petstore.com/pid_" + UUID.randomUUID();
            paymentDetails.setLink(new URI(uriString));
        } catch (URISyntaxException uriError) {
            //Do nothing
        }
        return paymentDetails;
    }

    private static String generateRandomSsn(){
        Random rand = new Random();
        String threeDigits = String.valueOf(rand.nextInt(100, 999));
        String twoDigits = String.valueOf(rand.nextInt(10, 99));
        String fourDigits = String.valueOf(rand.nextInt(1000, 9999));
        return threeDigits + "-" + twoDigits + "-" + fourDigits;
    }

    private static String generateRandomName(){
        Random rand=new Random();
        String aToZ="abcedefghidklmnopqurstuvwxz"; // 36 letter.
        StringBuilder res=new StringBuilder();
        for (int i = 0; i < 20; i++) {
            int randIndex=rand.nextInt(aToZ.length());
            res.append(aToZ.charAt(randIndex));
        }
        return res.toString();
    }
}
