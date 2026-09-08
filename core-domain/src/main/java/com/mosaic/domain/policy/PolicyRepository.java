package com.mosaic.domain.policy;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PolicyRepository extends MongoRepository<Policy, String> {

    List<Policy> findByClientId(String clientId);
}
