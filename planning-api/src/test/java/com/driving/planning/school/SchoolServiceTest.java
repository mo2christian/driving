package com.driving.planning.school;

import com.driving.planning.Generator;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.common.hourly.Day;
import com.driving.planning.common.hourly.Hourly;
import com.driving.planning.school.domain.Address;
import com.driving.planning.school.domain.School;
import com.driving.planning.school.dto.AddressDto;
import com.driving.planning.school.dto.SchoolDto;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
 class SchoolServiceTest {

    final String name = "name";
    final String phoneNumber = "0101010101";
    final String pseudo = "pseudo";
    final String town = "town";
    final String path = "1 rue de la mort";
    final String postalCode = "87459";

    @Inject
    SchoolService schoolService;

    @InjectMock
    SchoolRepository repository;

    @Test
    void createSchool(){
        ArgumentCaptor<School> schoolCaptor = ArgumentCaptor.forClass(School.class);
        var schoolDto = Generator.school();
        schoolService.createSchool(schoolDto);
        verify(repository, atMostOnce()).persist(schoolCaptor.capture());
        Assertions.assertThat(schoolCaptor.getValue())
                .isNotNull()
                .extracting(School::getPseudo, School::getName, School::getPhoneNumber, School::getWorkDays)
                .containsExactly(schoolDto.getPseudo(), schoolDto.getName(),schoolDto.getPhoneNumber(), schoolDto.getWorkDays());

        Assertions.assertThat(schoolCaptor.getValue().getAddress())
                .isNotNull()
                .extracting(Address::getPath, Address::getPostalCode, Address::getTown)
                .containsExactly(schoolDto.getAddress().getPath(), schoolDto.getAddress().getPostalCode(), schoolDto.getAddress().getTown());
    }

    @Test
    void update(){
        var schoolDto = Generator.school();
        when(repository.findByPseudo(schoolDto.getPseudo())).thenReturn(Optional.of(generateSchool()));
        schoolService.update(schoolDto);
        ArgumentCaptor<School> schoolCaptor = ArgumentCaptor.forClass(School.class);
        verify(repository, atMostOnce()).update(schoolCaptor.capture());
        Assertions.assertThat(schoolCaptor.getValue())
                .isNotNull()
                .extracting(School::getPseudo, School::getName, School::getPhoneNumber, School::getWorkDays)
                .containsExactly(schoolDto.getPseudo(), schoolDto.getName(),schoolDto.getPhoneNumber(), schoolDto.getWorkDays());

        Assertions.assertThat(schoolCaptor.getValue().getAddress())
                .isNotNull()
                .extracting(Address::getPath, Address::getPostalCode, Address::getTown)
                .containsExactly(schoolDto.getAddress().getPath(), schoolDto.getAddress().getPostalCode(), schoolDto.getAddress().getTown());
    }

    @Test
    void updateNotFound(){
        var schoolDto = Generator.school();
        when(repository.findByPseudo(schoolDto.getPseudo())).thenReturn(Optional.empty());
        Assertions.assertThatExceptionOfType(PlanningException.class)
                .isThrownBy(() -> schoolService.update(schoolDto));
    }

    @Test
    void list(){
        when(repository.listAll()).thenReturn(Collections.singletonList(generateSchool()));
        List<SchoolDto> schoolDtos = schoolService.list();
        Assertions.assertThat(schoolDtos)
                .element(0)
                .extracting(SchoolDto::getName, SchoolDto::getPseudo, SchoolDto::getPhoneNumber)
                .containsExactly(name, pseudo, phoneNumber);

        Assertions.assertThat(schoolDtos.get(0).getAddress())
                .extracting(AddressDto::getPath, AddressDto::getPostalCode, AddressDto::getTown)
                .containsExactly(path, postalCode, town);
    }

    @Test
    void get(){
        when(repository.findByPseudo(pseudo)).thenReturn(Optional.of(generateSchool()));
        var schoolDto = schoolService.get("pseudo");
        Assertions.assertThat(schoolDto)
                .isNotEmpty()
                .get()
                .extracting(SchoolDto::getName, SchoolDto::getPseudo, SchoolDto::getPhoneNumber)
                .containsExactly(name, pseudo, phoneNumber);

        Assertions.assertThat(schoolDto.get().getAddress())
                .extracting(AddressDto::getPath, AddressDto::getPostalCode, AddressDto::getTown)
                .containsExactly(path, postalCode, town);
    }

    @Test
    void nameExist(){
        when(repository.findByName(name)).thenReturn(Optional.of(generateSchool()));
        Assertions.assertThat(schoolService.isNameUsed(name)).isTrue();

        when(repository.findByName(name)).thenReturn(Optional.of(generateSchool()));
        Assertions.assertThat(schoolService.isNameUsed(name, "notValidPseudo")).isTrue();
    }

    @Test
    void delete(){
        when(repository.findByPseudo(pseudo)).thenReturn(Optional.of(generateSchool()));
        schoolService.delete(pseudo);
        ArgumentCaptor<String> pseudoCaptor = ArgumentCaptor.forClass(String.class);
        verify(repository, atMostOnce()).delete(pseudoCaptor.capture());
        Assertions.assertThat(pseudoCaptor.getValue()).isEqualTo(pseudo);
    }

    @Test
    void deleteInvalid(){
        when(repository.findByPseudo("notFound")).thenReturn(Optional.empty());
        Assertions.assertThatExceptionOfType(PlanningException.class)
            .isThrownBy(() -> schoolService.delete("notFound"));
        verify(repository, never()).delete(anyString());
    }

    @Test
    void nameNotExist(){
        when(repository.findByName(name)).thenReturn(Optional.empty());
        Assertions.assertThat(schoolService.isNameUsed(name)).isFalse();

        var school = generateSchool();
        when(repository.findByName(name)).thenReturn(Optional.of(school));
        Assertions.assertThat(schoolService.isNameUsed(name, school.getPseudo())).isFalse();
    }

    @Test
    void isOpen(){
        var school = generateSchool();
        when(repository.findByPseudo(name)).thenReturn(Optional.of(school));
        var dateTime = LocalDateTime.of(LocalDate.of(2022, Month.MAY, 9), LocalTime.of(10, 0));
        Assertions.assertThat(schoolService.isSchoolOpened(name, dateTime)).isTrue();
    }

    @Test
    void isClose(){
        var school = generateSchool();
        when(repository.findByPseudo(name)).thenReturn(Optional.of(school));
        var dateTime = LocalDateTime.of(LocalDate.of(2022, Month.MAY, 9), LocalTime.of(14,15));
        Assertions.assertThat(schoolService.isSchoolOpened(name, dateTime)).isFalse();
    }

    public School generateSchool(){
        var school = new School();
        school.setPseudo(pseudo);
        school.setName(name);
        school.setPhoneNumber(phoneNumber);
        var address = new Address();
        address.setPath(path);
        address.setPostalCode(postalCode);
        address.setTown(town);
        school.setAddress(address);
        var hourly = new Hourly();
        hourly.setDay(Day.MONDAY);
        hourly.setBegin(LocalTime.of(8, 0));
        hourly.setEnd(LocalTime.of(12, 0));
        school.setWorkDays(Collections.singleton(hourly));
        return school;
    }

}
