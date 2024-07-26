package com.nm.translation.jpa.repository;

import com.nm.translation.jpa.entity.EcommReconciliationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface EcommReconciliationRepository extends JpaRepository<EcommReconciliationEntity, Long> {

}
