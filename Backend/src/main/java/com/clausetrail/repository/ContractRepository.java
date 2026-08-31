package com.clausetrail.repository;

import com.clausetrail.model.Contract;
import com.clausetrail.model.ContractStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends MongoRepository<Contract, String> {
    List<Contract> findByStatus(ContractStatus status, Sort sort);
    List<Contract> findByStatusIn(List<ContractStatus> statuses, Sort sort);
    List<Contract> findByContractType(String contractType, Sort sort);
    
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'parties': { $regex: ?0, $options: 'i' } }, { 'contractType': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    List<Contract> searchContracts(String keyword, Sort sort);

    long countByStatus(ContractStatus status);
}
