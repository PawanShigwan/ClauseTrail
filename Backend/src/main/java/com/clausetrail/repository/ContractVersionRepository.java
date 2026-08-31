package com.clausetrail.repository;

import com.clausetrail.model.ContractVersion;
import com.clausetrail.model.VersionStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractVersionRepository extends MongoRepository<ContractVersion, String> {
    List<ContractVersion> findByContractId(String contractId, Sort sort);
    Optional<ContractVersion> findByContractIdAndVersionNumber(String contractId, int versionNumber);
    List<ContractVersion> findByStatus(VersionStatus status, Sort sort);
    Optional<ContractVersion> findFirstByContractIdOrderByVersionNumberDesc(String contractId);
    void deleteByContractId(String contractId);
}
