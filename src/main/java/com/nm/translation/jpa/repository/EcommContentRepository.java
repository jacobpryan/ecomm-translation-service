package com.nm.translation.jpa.repository;

import com.nm.translation.jpa.entity.EcommContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface EcommContentRepository extends JpaRepository<EcommContentEntity, Long> {
}
