package com.nm.translation.jpa.entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "public.ecomm_attachments")
public class EcommAttachmentEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "`EcommContentId`")
    private Long ecommContentId;

    @Column(name = "`Bucket`")
    private String bucket;

    @Column(name = "`Folder`")
    private String folder;

    @Column(name = "`StoredFileName`")
    private String storedFileName;

    @Column(name = "`OriginalFileName`")
    private String originalFileName;

    @Column(name = "`ContentType`")
    private String contentType;

    @Column(name = "`Inline`")
    private Boolean inline;

    @Column(name = "`CID`")
    private String cid;

    @Column(name = "`EcommErrorsId`")
    private Long ecommErrorsId;

    @Column(name = "`RowCreatedDtm`")
    private Date rowCreatedDtm;

    @Column(name = "`LastModifiedDtm`")
    private Date lastModifiedDtm;
}
