package com.craft.externalmanagementsystemms.domain.repositores;

import com.craft.complaint.external.data.DataType;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RegisterLoadingExternalDataRepository extends MongoRepository<RegisterLoadingExternalData, String> {

    void deleteAllByComplaintId(String complaintId);

    PageImpl<RegisterLoadingExternalData> findAllByDataType(DataType dataType, Pageable pageable);
}
