package com.driving.planning.student.otp;

import com.driving.planning.config.database.DatabaseResolverInitializer;
import com.driving.planning.config.database.Tenant;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;

@QuarkusTest
class OTPRepositoryTest {

    @Inject
    OTPRepository otpRepository;

    @BeforeAll
    static void before(){
        var tenant = new Tenant("base");
        DatabaseResolverInitializer.alterAnnotation(OTP.class, tenant);
    }

    @Test
    void insert(){
        var content = "87459";
        var studentId = "id";
        var otp = new OTP();
        Assertions.assertThat(otp.getCreatedDate())
                .isNotNull()
                .isBetween(LocalDateTime.now().minusMinutes(2), LocalDateTime.now());
        otp.setContent(content);
        otp.setStudentId(studentId);
        otpRepository.persist(otp);

        Assertions.assertThat(otpRepository.findByStudent(studentId))
                .hasSize(1)
                .element(0)
                .extracting(OTP::getContent, OTP::getStudentId)
                .containsExactly(content, studentId);
    }

}
