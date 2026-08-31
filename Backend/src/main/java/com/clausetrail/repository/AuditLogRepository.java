package com.clausetrail.repository;

import com.clausetrail.model.AuditAction;
import com.clausetrail.model.AuditLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByContractId(String contractId, Sort sort);
    List<AuditLog> findByUserId(String userId, Sort sort);
    List<AuditLog> findByAction(AuditAction action, Sort sort);
    List<AuditLog> findByTimestampBetween(Instant start, Instant end, Sort sort);
}
