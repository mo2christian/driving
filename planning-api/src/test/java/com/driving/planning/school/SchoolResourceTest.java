package com.driving.planning.school;

import com.driving.planning.Generator;
import com.driving.planning.account.AccountService;
import com.driving.planning.account.dto.AccountDto;
import com.driving.planning.common.DatePattern;
import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.school.dto.SchoolDto;
import com.driving.planning.school.dto.SchoolRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.*;

@QuarkusTest
class SchoolResourceTest {

    @InjectMock
    SchoolService schoolService;

    @InjectMock
    AccountService accountService;

    @Test
    void getSchoolEndpoint(){
        var dto = Generator.school();
        when(schoolService.list()).thenReturn(Collections.singletonList(dto));
        var formatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
        var hourly = dto.getWorkDays().stream().findFirst().orElse(null);
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schools")
                .then()
                .statusCode(200)
                .body("schools[0].name", Matchers.is(dto.getName()))
                .body("schools[0].pseudo", Matchers.is(dto.getPseudo()))
                .body("schools[0].phoneNumber", Matchers.is(dto.getPhoneNumber()))
                .body("schools[0].address.path", Matchers.is(dto.getAddress().getPath()))
                .body("schools[0].address.postalCode", Matchers.is(dto.getAddress().getPostalCode()))
                .body("schools[0].address.town", Matchers.is(dto.getAddress().getTown()))
                .body("schools.size()", Matchers.is(1))
                .body("schools[0].workDays.size()", Matchers.is(1))
                .body("schools[0].workDays[0].day", Matchers.is(hourly.getDay().getValue()))
                .body("schools[0].workDays[0].begin", Matchers.is(formatter.format(hourly.getBegin())))
                .body("schools[0].workDays[0].end", Matchers.is(formatter.format(hourly.getEnd())));
        verify(schoolService, times(1)).list();
    }

    @Test
    void getSchoolByID(){
        var dto = Generator.school();
        when(schoolService.get(anyString())).thenReturn(Optional.of(dto));
        var formatter = DateTimeFormatter.ofPattern(DatePattern.TIME);
        var hourly = dto.getWorkDays().stream().findFirst().orElse(null);
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schools/{id}", dto.getPseudo())
                .then()
                .statusCode(200)
                .body("name", Matchers.is(dto.getName()))
                .body("pseudo", Matchers.is(dto.getPseudo()))
                .body("phoneNumber", Matchers.is(dto.getPhoneNumber()))
                .body("address.path", Matchers.is(dto.getAddress().getPath()))
                .body("address.postalCode", Matchers.is(dto.getAddress().getPostalCode()))
                .body("address.town", Matchers.is(dto.getAddress().getTown()))
                .body("workDays.size()", Matchers.is(1))
                .body("workDays[0].day", Matchers.is(hourly.getDay().getValue()))
                .body("workDays[0].begin", Matchers.is(formatter.format(hourly.getBegin())))
                .body("workDays[0].end", Matchers.is(formatter.format(hourly.getEnd())));
        verify(schoolService, times(1)).get(dto.getPseudo());
    }

    @Test
    void getSchoolByID_NotFound(){
        when(schoolService.get(anyString())).thenReturn(Optional.empty());
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schools/{id}", "empty")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .body("message", Matchers.containsStringIgnoringCase("not found"));
    }

    @Test
    void handlePlanningException(){
        var msg = "Error";
        doThrow(new PlanningException(Response.Status.BAD_REQUEST, msg)).when(schoolService).list();
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schools")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("message", Matchers.equalToIgnoringCase(msg));
    }

    @Test
    void handleException(){
        when(schoolService.list()).then(invocation -> {
            throw new Exception();
        });
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/schools")
                .then()
                .statusCode(500)
                .body("message", Matchers.equalToIgnoringCase("Internal error"));
    }

    @Test
    void createSchool(){
        var schoolDto = Generator.school();
        var request = new SchoolRequest();
        request.setSchool(schoolDto);
        request.setAccount(generateAccount());
        when(schoolService.isNameUsed(schoolDto.getName())).thenReturn(false);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/schools")
                .then()
                    .statusCode(204);
        verify(schoolService, times(1)).createSchool(any(SchoolDto.class));
        verify(accountService, times(1)).createAccount(anyString(), any());
    }

    @Test
    void update(){
        var schoolDto = Generator.school();
        schoolDto.setPseudo("pseudo");
        when(schoolService.isNameUsed(schoolDto.getName())).thenReturn(false);
        when(schoolService.get(schoolDto.getPseudo())).thenReturn(Optional.of(schoolDto));
        given()
                .contentType(ContentType.JSON)
                .body(schoolDto)
                .post("/api/v1/schools/{id}", schoolDto.getPseudo())
                .then()
                .statusCode(204);
        verify(schoolService, times(1)).update(any(SchoolDto.class));
    }

    @Test
    void updateOnError(){
        var schoolDto = Generator.school();
        schoolDto.setPseudo("pseudo");
        when(schoolService.get(schoolDto.getPseudo())).thenReturn(Optional.empty());
        given()
                .contentType(ContentType.JSON)
                .body(schoolDto)
                .post("/api/v1/schools/{id}", schoolDto.getPseudo())
                .then()
                .statusCode(404);
        verify(schoolService, never()).update(any(SchoolDto.class));

        when(schoolService.isNameUsed(schoolDto.getName(), schoolDto.getPseudo())).thenReturn(true);
        when(schoolService.get(schoolDto.getPseudo())).thenReturn(Optional.of(schoolDto));
        given()
                .contentType(ContentType.JSON)
                .body(schoolDto)
                .post("/api/v1/schools/{id}", schoolDto.getPseudo())
                .then()
                .statusCode(400);
        verify(schoolService, never()).update(any(SchoolDto.class));
    }

    @ParameterizedTest
    @MethodSource({"requestParams"})
    void createSchoolInvalidParamRequest(String field) throws NoSuchFieldException, IllegalAccessException {
        var account = generateAccount();
        var request = new SchoolRequest();
        request.setAccount(account);
        request.setSchool(Generator.school());
        setField(account, field, null);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/schools")
                .then()
                .statusCode(400)
                .body("details[0]", Matchers.containsStringIgnoringCase("createSchool.request.account." + field));
    }

    @ParameterizedTest
    @MethodSource({"schoolParams"})
    void createSchoolInvalidParamSchool(String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        var schoolDto = Generator.school();
        setField(schoolDto, field, value);
        var request = new SchoolRequest();
        request.setAccount(generateAccount());
        request.setSchool(schoolDto);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/schools")
                .then()
                .statusCode(400)
                .body("details[0]", Matchers.containsStringIgnoringCase("createSchool.request.school." + field));
    }

    @ParameterizedTest
    @ValueSource(strings = {"path", "town", "postalCode"})
    void createSchoolInvalidParamAddress(String field) throws NoSuchFieldException, IllegalAccessException {
        var schoolDto = Generator.school();
        setField(schoolDto.getAddress(), field, null);
        var request = new SchoolRequest();
        request.setAccount(generateAccount());
        request.setSchool(schoolDto);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/schools")
                .then()
                .statusCode(400)
                .body("details[0]", Matchers.containsStringIgnoringCase("createSchool.request.school.address." + field));
    }

    @Test
    void createSchoolsAlreadyExist(){
        var schoolDto = Generator.school();
        SchoolRequest request = new SchoolRequest();
        request.setSchool(schoolDto);
        request.setAccount(generateAccount());
        when(schoolService.isNameUsed(schoolDto.getName())).thenReturn(true);
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/api/v1/schools")
                .then()
                .statusCode(400);
        verify(schoolService, never()).createSchool(any(SchoolDto.class));
        verify(accountService, never()).createAccount(anyString(), any());
    }

    @Test
    void deleteSchool(){
        var pseudo = "test";
        var school = new SchoolDto();
        school.setPseudo(pseudo);
        when(schoolService.get(pseudo)).thenReturn(Optional.of(school));
        given()
                .pathParam("id", pseudo)
                .delete("/api/v1/schools/{id}")
                .then()
                .statusCode(204);
        verify(schoolService, times(1)).delete(anyString());
    }

    @Test
    void deleteSchoolNotfound(){
        var pseudo = "test";
        when(schoolService.get(pseudo)).thenReturn(Optional.empty());
        given()
                .pathParam("id", pseudo)
                .delete("/api/v1/schools/{id}")
                .then()
                .statusCode(404);
        verify(schoolService, never()).delete(anyString());
    }

    private AccountDto generateAccount(){
        var account = new AccountDto();
        account.setEmail("test@test.com");
        account.setPassword("pwd");
        return account;
    }

    private void setField(Object target, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static Stream<Arguments> requestParams(){
        return Stream.of(
                Arguments.of("email", null),
                Arguments.of("email", ""),
                Arguments.of("password", null),
                Arguments.of("password", ""),
                Arguments.of("email", "toto@"),
                Arguments.of("email", "toto")
        );
    }

    private static Stream<Arguments> schoolParams(){
        return Stream.of(
                Arguments.of("name", null),
                Arguments.of("name", ""),
                Arguments.of("phoneNumber", null),
                Arguments.of("phoneNumber", "toto@"),
                Arguments.of("phoneNumber", "25874126"),
                Arguments.of("phoneNumber", "25874126698")
        );
    }

}
