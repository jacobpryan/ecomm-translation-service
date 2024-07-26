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
@Table(name = "ecomm_ts_token_management")
public class EcommTokenManagementEntity {
    @Id
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "token_start_dte", insertable = false)
    private Date configStartDte;

    @Column(name = "token_modified_dte", insertable = false)
    private Date configModifiedDte;

    @Column(name = "token_end_dte", insertable = false)
    private Date configEndDte;

    @Column(name = "endpoint_version")
    private Byte endpointVersion;

    @Column(name = "endpoint_path")
    private String endpointPath;

    @Column(name = "client_registration_name")
    private String clientRegName;

    @Column(name = "auth_grant_type")
    private String grantType;

    @Column(name = "auth_token_uri")
    private String tokenUri;

    @Column(name = "resource_server_url")
    private String serverUrl;

    @Column(name = "rate_limit_threshold")
    private Integer rateLimitThreshold;
}
