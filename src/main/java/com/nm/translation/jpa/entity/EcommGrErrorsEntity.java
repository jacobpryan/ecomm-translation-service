package com.nm.translation.jpa.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "ecomm_gr_errors")
public class EcommGrErrorsEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "`EcommContentId`")
    private Long ecommConetentId;

    @Column(name = "`Source`")
    private String source;

    @Column(name = "`ErrorMessage`")
    private String errorMessage;

    @Column(name = "`RowCreatedDtm`", insertable = false)
    private Date rowCreatedDtm;

    @Column(name = "`LastModifiedDtm`", insertable = false)
    private Date lastModifiedDtm;
}
