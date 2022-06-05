package com.driving.planning.student.otp;

import com.driving.planning.common.exception.PlanningException;
import com.driving.planning.common.mail.EmailService;
import com.driving.planning.common.mail.Mail;
import com.driving.planning.common.mail.MailTemplate;
import com.driving.planning.common.sms.SMSService;
import com.driving.planning.student.dto.StudentDto;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Traced
@ApplicationScoped
public class OTPService {

    private final OTPRepository otpRepository;

    private final EmailService emailService;

    private final SMSService smsService;

    private final Logger logger;

    private final Random rand;

    @ConfigProperty(name = "app.otp.length")
    int otpLength;

    @ConfigProperty(name = "app.otp.validity")
    int validPeriod;

    @Inject
    public OTPService(OTPRepository otpRepository, EmailService emailService, SMSService smsService, Logger logger){
        this.otpRepository = otpRepository;
        this.logger = logger;
        this.emailService = emailService;
        this.smsService = smsService;
        try{
            rand = SecureRandom.getInstanceStrong();
        }
        catch(NoSuchAlgorithmException ex){
            throw new PlanningException(Response.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    public void sendOTPByMail(StudentDto studentDto){
        logger.infof("Send OTP by mail to %s", studentDto.getEmail());
        var email = studentDto.getEmail();
        if (email == null || email.isBlank()){
            throw new PlanningException(Response.Status.BAD_REQUEST, "Email not found");
        }
        var otp = new OTP();
        otp.setStudentId(studentDto.getId());
        otp.setContent(generateOTP());
        otpRepository.persist(otp);
        var mail = new Mail(MailTemplate.OTP, studentDto.getEmail());
        mail.addProperty("otp", otp.getContent());
        emailService.sendEmail(mail);
    }

    public void sendOTPBySMS(StudentDto studentDto){
        logger.infof("Send OTP by SMS to %s", studentDto.getPhoneNumber());
        var otp = new OTP();
        otp.setStudentId(studentDto.getId());
        otp.setContent(generateOTP());
        otpRepository.persist(otp);
        var sms = String.format("Votre code de vérification est : %s", otp.getContent());
        smsService.sendSMS(studentDto.getPhoneNumber(), sms);
    }

    public List<OTP> findValidOTP(String studentId){
        logger.infof("Get valid OTP for %s", studentId);
        final var timeLimit = LocalDateTime.now().minusMinutes(validPeriod);
        return otpRepository.findByStudent(studentId)
                .stream()
                .filter(otp -> timeLimit.isBefore(otp.getCreatedDate()))
                .collect(Collectors.toList());
    }

    private String generateOTP(){
        logger.info("Generate OTP");
        final var numbers = "0123456789";
        char[] otp = new char[otpLength];
        for (int i = 0; i < otpLength; i++){
            otp[i] = numbers.charAt(rand.nextInt(numbers.length()));
        }
        return new String(otp);
    }

}
