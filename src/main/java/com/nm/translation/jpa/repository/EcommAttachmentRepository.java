package com.nm.translation.jpa.repository;

import com.nm.translation.jpa.entity.EcommAttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface EcommAttachmentRepository extends JpaRepository<EcommAttachmentEntity, Long> {

    List<EcommAttachmentEntity> findByEcommContentId(@Param("ecommContentId") long ecommContentId);

    List<EcommAttachmentEntity> findByEcommErrorsId(@Param("ecommErrorsId") long ecommErrorsId);

}
