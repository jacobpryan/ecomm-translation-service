package com.nm.translation.jpa.repository;

import com.nm.translation.jpa.entity.EcommErrorsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface EcommErrorsRepository extends JpaRepository<EcommErrorsEntity, Long> {

    //    Optional<EcommErrorsEntity> findByErrorEcommContentId(@Param("ecommContentId") long ecommContentId);

}
