package com.craft.complaintmanagementms.services.conveters;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.complaint.management.api.dtos.ComplaintSystemDto;
import com.craft.complaintmanagementms.domain.model.entites.ComplaintSystem;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ComplaintManagementConverter {

    private static ModelMapper modelMapper;

    @Autowired
    public ComplaintManagementConverter(ModelMapper modelMapper){
        ComplaintManagementConverter.modelMapper = modelMapper;
    }

    public static BaseComplaintSystemDto convert(ComplaintSystem complaintSystem){
        if(complaintSystem == null){
            return new BaseComplaintSystemDto();
        }

        return modelMapper.map(complaintSystem, BaseComplaintSystemDto.class);
    }

    public static ComplaintSystemDto convert(ComplaintSystem complaintSystem, List<AdditionalData> additionalData){
        if(complaintSystem == null){
            return new ComplaintSystemDto();
        }

        ComplaintSystemDto complaintSystemDto = modelMapper.map(complaintSystem, ComplaintSystemDto.class);
        complaintSystemDto.setAdditionalData(additionalData);

        return complaintSystemDto;
    }

    public static List<ComplaintSystemDto> convert(List<ComplaintSystem> complaintSystems, Map<String, List<AdditionalData>> idToAdditionalData) {
        if(CollectionUtils.isEmpty(complaintSystems)){
            return new LinkedList<>();
        }

        if(idToAdditionalData == null){
            idToAdditionalData = new HashMap<>();
        }

        Map<String, List<AdditionalData>> finalIdToAdditionalData = idToAdditionalData;

        return complaintSystems.stream()
                .map(complaintSystem -> convert(complaintSystem, finalIdToAdditionalData.get(complaintSystem.getId())))
                .collect(Collectors.toList());
    }
}
