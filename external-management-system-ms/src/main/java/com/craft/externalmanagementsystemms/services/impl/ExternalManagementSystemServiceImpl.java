package com.craft.externalmanagementsystemms.services.impl;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.ComplaintSystemAdditionalData;
import com.craft.externalmanagementsystemms.domain.repositores.ComplaintSystemRepository;
import com.craft.externalmanagementsystemms.services.ExternalManagementSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalManagementSystemServiceImpl implements ExternalManagementSystemService {

    private final ComplaintSystemRepository complaintSystemRepository;

    @Override
    public Map<String, List<AdditionalData>> getComplaintSystemAdditionalData(List<String> ids) {
        return complaintSystemRepository.findAllByIdIn(ids).stream()
                .collect(Collectors.toMap(ComplaintSystemAdditionalData::getId,ComplaintSystemAdditionalData::getAdditionalData));
    }

    @Override
    public List<AdditionalData> getComplaintSystemAdditionalData(String id) {
        return complaintSystemRepository.findById(id)
                .map(ComplaintSystemAdditionalData::getAdditionalData)
                .orElse(new LinkedList<>());
    }

    @Override
    public void registerNewComplaintSystem(BaseComplaintSystemDto baseComplaintSystemDto) {

    }

    @Override
    public void unRegisterComplaintSystem(BaseComplaintSystemDto baseComplaintSystemDto) {

    }

}
