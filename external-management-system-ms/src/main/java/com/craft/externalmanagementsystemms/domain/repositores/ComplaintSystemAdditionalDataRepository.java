package com.craft.externalmanagementsystemms.domain.repositores;

import com.craft.externalmanagementsystemms.domain.model.entites.ComplaintSystemAdditionalData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintSystemAdditionalDataRepository extends MongoRepository<ComplaintSystemAdditionalData, String> {

    List<ComplaintSystemAdditionalData> findAllByIdIn(List<String> ids);

}
