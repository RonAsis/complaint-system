package com.craft.complaintmanagementms.services;

import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.complaint.management.api.dtos.ComplaintSystemDto;

import java.util.List;
import java.util.UUID;

public interface ComplaintManagementService {

    BaseComplaintSystemDto createCompliantSystem(UUID userId, String subject, String complaint, UUID purchaseId);

    ComplaintSystemDto getCompliantSystem(String id);

    List<ComplaintSystemDto> getAllCompliantSystem();

    List<ComplaintSystemDto> getCompliantSystemByIds(List<String> ids);
}
