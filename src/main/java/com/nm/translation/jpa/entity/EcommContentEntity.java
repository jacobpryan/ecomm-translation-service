package com.nm.translation.jpa.entity;


import java.util.Date;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Can delete any unused columns
 */
@Data
@Entity
@EqualsAndHashCode
@Table(name = "ecomm_content", schema="public")
public class EcommContentEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "`EmlGeneratedDtm`")
    private Date emlGeneratedDtm;

    @Column(name = "`ArchivalDtm`")
    private Date archivalDtm;

    @Column(name = "`RowCreatedDtm`", insertable = false)
    private Date rowCreatedDtm;

    @Column(name = "`LastModifiedDtm`", insertable = false)
    private Date lastModifiedDtm;

    @Column(name = "`CommunicationType`")
    private String communicationType;

    @Column(name = "`MsgId`")
    private String msgId;

    @Column(name = "`Content`")
    private String Content;

    @Column(name = "`ProcessStatusInd`")
    private Integer processStatusInd;

    @Column(name = "`Source`")
    private String source;

    @Column(name = "`SendMethod`")
    private String sendMethod;

    @Column(name = "`DoReprocess`")
    private Boolean doReprocess;

    @Column(name = "`RequestId`")
    private String requestId;
}
