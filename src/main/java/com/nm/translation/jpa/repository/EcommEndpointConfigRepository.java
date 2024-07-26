package com.nm.translation.jpa.repository;

import com.nm.translation.jpa.entity.EcommEndpointConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface EcommEndpointConfigRepository extends JpaRepository<EcommEndpointConfigEntity, Long> {
    @Query(value = "select c FROM EcommEndpointConfigEntity c where LOWER(c.ecommSourceMatch) like LOWER(:source)")
    EcommEndpointConfigEntity getEndpointConfigBySource(@Param("source") String source);

    @Query(value = "select c FROM EcommEndpointConfigEntity c where LOWER(c.ecommSourceMatch) like LOWER(:source) and c.ecommContentTypeMatch like LOWER(:subtype)")
    String getClientIdBySourceAndSubtype(@Param("source") String source, @Param("subtype") String subtype);
}
