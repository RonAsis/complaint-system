package com.craft.externalmanagementsystemms.services;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;

import java.util.List;
import java.util.Map;

public interface ExternalManagementSystemService {

    List<AdditionalData> getComplaintSystemAdditionalData(String id);

    Map<String, List<AdditionalData>> getComplaintSystemAdditionalData(List<String> ids);

    void registerNewComplaintSystem(BaseComplaintSystemDto baseComplaintSystemDto);

    void deleteComplaintSystemData(BaseComplaintSystemDto baseComplaintSystemDto);

    void applyLoadingData();
}
