package com.craft.complaintmanagementms.domain.repositores;

import com.craft.complaintmanagementms.domain.model.entites.ComplaintSystem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintSystemRepository extends MongoRepository<ComplaintSystem, String> {

    List<ComplaintSystem> findAllByIdIn(List<String> ids);


    ////////////////////// inner interfaces  ///////////////////////

    interface IdInjection {

        String getId();
    }

}
