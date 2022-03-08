package com.craft.complaintmanagementms.domain.model.entites;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintSystem {

    @Id
    private String id;
    private UUID userId;
    private String subject;
    private String complaint;
    private UUID purchaseId;
    private Date createdTime;

    public ComplaintSystem(UUID userId, String subject, String complaint, UUID purchaseId, Date createdTime) {
        this.userId = userId;
        this.subject = subject;
        this.complaint = complaint;
        this.purchaseId = purchaseId;
        this.createdTime = createdTime;
    }
}
