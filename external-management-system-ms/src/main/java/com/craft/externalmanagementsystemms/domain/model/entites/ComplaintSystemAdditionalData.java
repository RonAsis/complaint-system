package com.craft.externalmanagementsystemms.domain.model.entites;

import com.craft.complaint.external.data.AdditionalData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintSystemAdditionalData {

    @Id
    private String id;
    private List<AdditionalData> additionalDatas;
    @Version
    private String version;

    public ComplaintSystemAdditionalData(String id) {
        this(id, null);
    }

    public ComplaintSystemAdditionalData(String id, AdditionalData additionalData) {
        this.id = id;

        this.additionalDatas = new LinkedList<>();

        if(additionalData != null){
            this.additionalDatas.add(additionalData);;
        }

    }

    public void addAdditionalData(AdditionalData additionalData) {
        additionalDatas.add(additionalData);
    }
}
