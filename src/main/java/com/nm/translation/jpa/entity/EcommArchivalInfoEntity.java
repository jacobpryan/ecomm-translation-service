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
@Table(name = "ecomm_gr_archival_info")
public class EcommArchivalInfoEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "`Source`")
    private String source;

    @Column(name = "`LastArchiveDtm`", insertable = false)
    private Date lastArchiveDtm;

    @Column(name = "`LastSendDtm`", insertable = false)
    private Date lastSendDtm;

    @Column(name = "`RowCreatedDtm`", insertable = false)
    private Date rowCreateDtm;

    @Column(name = "`LastModifieddDtm`", insertable = false)
    private Date lastModifiedDtm;
}
