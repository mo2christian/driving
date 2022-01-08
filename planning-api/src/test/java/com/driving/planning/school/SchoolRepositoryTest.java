package com.driving.planning.school;

import com.driving.planning.MongodbTestResource;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.school.domain.Address;
import com.driving.planning.school.domain.School;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.Collections;

@QuarkusTest
@QuarkusTestResource(MongodbTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SchoolRepositoryTest {

    final String pseudo = "pseudo";
    final String name = "name";
    final String phoneNumber = "0101010101";
    final String path = "1 rue test";
    final String postalCode = "25252";
    final String town = "town";
    final Address address;
    final Hourly hourly;

    public SchoolRepositoryTest() {
        address = new Address();
        address.setPath(path);
        address.setPostalCode(postalCode);
        address.setTown(town);
        hourly = new Hourly();
        hourly.setDay(Day.MONDAY);
        hourly.setBegin(LocalTime.now());
        hourly.setEnd(LocalTime.now().plusHours(5));
    }

    @Inject
    SchoolRepository schoolRepository;

    @Test
    @Order(1)
    void list(){
        assertThat(schoolRepository.list()).isEmpty();
    }

    @Test
    @Order(2)
    void create(){
        School school = new School();
        school.setPseudo(pseudo);
        school.setName(name);
        school.setPhoneNumber(phoneNumber);
        school.setAddress(address);
        school.setWorkDays(Collections.singleton(hourly));
        schoolRepository.createSchool(school);
        assertThat(schoolRepository.list()).hasSize(1)
                .element(0)
                .extracting(School::getName, School::getPseudo, School::getPhoneNumber, School::getAddress, School::getWorkDays)
                .containsExactly(name, pseudo, phoneNumber, address, school.getWorkDays());
    }

    @Test
    @Order(3)
    void findByName(){
        assertThat(schoolRepository.findByName(name))
                .isNotEmpty()
                .get()
                .extracting(School::getName, School::getPseudo, School::getPhoneNumber, School::getAddress, School::getWorkDays)
                .containsExactly(name, pseudo, phoneNumber, address, Collections.singleton(hourly));
    }

    @Test
    @Order(3)
    void findByPseudo(){
        assertThat(schoolRepository.findByPseudo(pseudo))
                .isNotEmpty()
                .get()
                .extracting(School::getName, School::getPseudo, School::getPhoneNumber, School::getAddress, School::getWorkDays)
                .containsExactly(name, pseudo, phoneNumber, address, Collections.singleton(hourly));
    }

    @Test
    @Order(3)
    void findByNameNotFound(){
        assertThat(schoolRepository.findByName("toto"))
                .isEmpty();
    }

    @Test
    @Order(3)
    void findByPseudoNotFound(){
        assertThat(schoolRepository.findByPseudo("toto"))
                .isEmpty();
    }

    @Order(4)
    @Test
    void update(){
        var schoolOptional = schoolRepository.findByPseudo(pseudo);
        var school = schoolOptional.orElseThrow();
        var updateName = "test update";
        school.setName(updateName);
        var updatePath = "encore";
        school.getAddress().setPath(updatePath);
        var time= new Hourly();
        time.setDay(Day.SUNDAY);
        time.setBegin(hourly.getBegin());
        time.setEnd(hourly.getEnd());
        school.setWorkDays(Collections.singleton(time));
        schoolRepository.update(school);
        assertThat(schoolRepository.list()).hasSize(1)
                .element(0)
                .extracting(School::getName, School::getPseudo, School::getPhoneNumber, School::getAddress, School::getWorkDays)
                .containsExactly(updateName, pseudo, phoneNumber, school.getAddress(), school.getWorkDays());
    }

    @Test
    @Order(5)
    void deleteSchool(){
        schoolRepository.delete(pseudo);
        assertThat(schoolRepository.list()).isEmpty();
    }

}
