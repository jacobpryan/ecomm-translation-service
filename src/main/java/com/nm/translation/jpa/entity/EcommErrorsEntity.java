package com.nm.translation.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "ecomm_errors")
public class EcommErrorsEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "`Source`")
    private String source;

    @Column(name = "`Content`")
    private String Content;

    @Column(name = "`CommunicationType`")
    private String communicationType;

    @Column(name = "`DoReprocess`")
    private Boolean doReprocess;

    @Column(name = "`ErrorMessage`")
    private String errorMessage;

    @Column(name = "`RowCreatedDtm`", insertable = false)
    private Date rowCreatedDtm;

    @Column(name = "`LastModifiedDtm`", insertable = false)
    private Date lastModifiedDtm;
}
