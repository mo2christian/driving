package com.driving.planning.account;

import com.driving.planning.account.dto.AccountDto;
import com.driving.planning.common.exception.PlanningException;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Traced
@ApplicationScoped
public class AccountService {

    private final AccountRepository repository;

    private final AccountMapper mapper;

    private final Logger logger;

    @Inject
    public AccountService(Logger logger, AccountRepository repository, AccountMapper mapper) {
        this.logger = logger;
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<AccountDto> list(){
        return repository.listAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public boolean isValidAccount(@Valid final AccountDto accountDto){
        var account = repository.findByEmail(accountDto.getEmail())
                .orElse(null);
        return account != null && account.getPassword().equals(hash(accountDto.getPassword()));
    }

    public void createAccount(@NotNull String schema, @Valid AccountDto accountDto){
        var hashPwd = hash(accountDto.getPassword());
        var account = mapper.toEntity(accountDto);
        account.setPassword(hashPwd);
        repository.createInSchema(schema, account);
    }

    public String hash(String value) {
        try{
            var md = MessageDigest.getInstance("SHA-512");
            md.update(value.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
        }
        catch (NoSuchAlgorithmException ex){
            logger.error("Error while hashing password", ex);
            throw new PlanningException(Response.Status.INTERNAL_SERVER_ERROR, "Error while hashing password", ex);
        }
    }

}
