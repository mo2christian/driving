package com.driving.planning.school.dto;

import com.driving.planning.account.dto.AccountDto;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@RegisterForReflection
public class SchoolRequest implements Serializable {

    @NotNull
    @Valid
    private SchoolDto school;

    @NotNull
    @Valid
    private AccountDto account;

    public SchoolDto getSchool() {
        return school;
    }

    public void setSchool(SchoolDto school) {
        this.school = school;
    }

    public AccountDto getAccount() {
        return account;
    }

    public void setAccount(AccountDto account) {
        this.account = account;
    }
}
