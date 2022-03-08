package com.craft.externalmanagementsystemms.domain.model.entites;

import com.craft.complaint.external.data.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterLoadingExternalData{

    @Id
    private String id;

    private DataType dataType;

    private String complaintId;

    private Object sourceId;

}
