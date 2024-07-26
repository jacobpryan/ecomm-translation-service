package com.nm.translation.model.oc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant {
    private String displayName;

    private String corporateEmail;

    private String userType;

    public Participant () {

    }

    public Participant(String email) {
        this.displayName = email;
        this.corporateEmail = email;
    }

    public Participant(String displayName, String corporateEmail) {
        this.displayName = displayName;
        this.corporateEmail = corporateEmail;
    }

    public Participant(String displayName, String corporateEmail, String userType) {
        this.displayName = displayName;
        this.corporateEmail = corporateEmail;
        this.userType = userType;
    }
}
