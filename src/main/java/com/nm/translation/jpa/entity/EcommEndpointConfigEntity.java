package com.nm.translation.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "ecomm_ts_endpoint_configuration")
public class EcommEndpointConfigEntity {
    @Id
    @Column(name = "config_id")
    private Long configId;

    @Column(name = "config_start_dte", insertable = false)
    private Date configStartDte;

    @Column(name = "config_modified_dte", insertable = false)
    private Date configModifiedDte;

    @Column(name = "config_end_dte", insertable = false)
    private Date configEndDte;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "token_id", referencedColumnName = "token_id")
    private EcommTokenManagementEntity tokenIdEntity;

    @Column(name = "client_credential_pair")
    private String clientCredPair;

    @Column(name = "ecomm_source_match")
    private String ecommSourceMatch;

    @Column(name = "ecomm_content_type_match")
    private String ecommContentTypeMatch;

    @Column(name = "oc_request_field_override")
    private String ocRequestBodyFieldOverride;

    @Column(name = "rate_limit_multiplier")
    private Byte rateLimitMultiplier;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "data_sub_type")
    private String dataSubType;
}
