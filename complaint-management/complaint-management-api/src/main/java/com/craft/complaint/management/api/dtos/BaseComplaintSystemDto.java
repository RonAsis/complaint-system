package com.craft.complaint.management.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseComplaintSystemDto {

    private String id;
    private UUID userId;
    private String subject;
    private String complaint;
    private UUID associatedPurchase;
    private Date createdTime;

}
