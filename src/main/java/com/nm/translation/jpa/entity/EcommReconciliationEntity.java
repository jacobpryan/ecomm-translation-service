package com.nm.translation.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "ecomm_reconciliation")
public class EcommReconciliationEntity {
    @Id
    @Column(name = "`Id`")
    @GeneratedValue
    private Long id;

    @Column(name = "`Msg_Id`")
    private String msgId;

    @Column(name = "`Sent_Date`")
    private String sentDate;

    @Column(name = "`Content_Type`")
    private String contentType;

    @Column(name = "`Recon_Status`")
    private String reconStatus;

    @Column(name = "`RowCreatedDtm`", insertable = false)
    private Date rowCreatedDtm;

    @Column(name = "`LastModifiedDtm`", insertable = false)
    private Date lastModifiedDtm;
}
