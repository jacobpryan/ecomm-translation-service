package com.nm.translation.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "ecomm_gr_archival_data")
public class EcommArchivalDataEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "`EcommContentId`")
    private Long ecommContentId;

    @Column(name = "`ProcessStatusInd`")
    private Integer processStatusInd;

    @Column(name = "`Source`")
    private String source;

    @Column(name = "`Sent_Date`")
    private Date sentDate;

    @Column(name = "`GR_Request_Id`")
    private String grRequestId;

    @Column(name = "`Recon_Status`")
    private Long reconStatus;

    @Column(name = "`Recon_Id`")
    private String reconId;

    @Column(name = "`RowCreatedDtm`", insertable = false)
    private Date rowCreatedDtm;

    @Column(name = "`LastModifieddDtm`", insertable = false)
    private Date lastModifiedDtm;
}
