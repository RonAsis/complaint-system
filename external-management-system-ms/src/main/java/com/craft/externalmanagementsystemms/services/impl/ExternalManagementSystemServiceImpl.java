package com.craft.externalmanagementsystemms.services.impl;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.ComplaintSystemAdditionalData;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import com.craft.externalmanagementsystemms.domain.repositores.ComplaintSystemAdditionalDataRepository;
import com.craft.externalmanagementsystemms.domain.repositores.RegisterLoadingExternalDataRepository;
import com.craft.externalmanagementsystemms.services.ExternalManagementSystemService;
import com.craft.externalmanagementsystemms.services.externalstrategy.ExternalDataComplaintStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalManagementSystemServiceImpl implements ExternalManagementSystemService {

    private final ComplaintSystemAdditionalDataRepository complaintSystemAdditionalDataRepository;
    private final RegisterLoadingExternalDataRepository registerLoadingExternalDataRepository;
    private final List<ExternalDataComplaintStrategy> externalDataComplaintStrategies;

    @Override
    public Map<String, List<AdditionalData>> getComplaintSystemAdditionalData(List<String> ids) {
        return complaintSystemAdditionalDataRepository.findAllByIdIn(ids).stream()
                .collect(Collectors.toMap(ComplaintSystemAdditionalData::getId,ComplaintSystemAdditionalData::getAdditionalDatas));
    }

    @Override
    public List<AdditionalData> getComplaintSystemAdditionalData(String id) {
        return complaintSystemAdditionalDataRepository.findById(id)
                .map(ComplaintSystemAdditionalData::getAdditionalDatas)
                .orElse(new LinkedList<>());
    }

    @Override
    @Transactional
    public void registerNewComplaintSystem(BaseComplaintSystemDto baseComplaintSystemDto) {
        if(baseComplaintSystemDto == null || baseComplaintSystemDto.getId() == null){
            return;
        }

        List<RegisterLoadingExternalData> registerLoadingExternalData = externalDataComplaintStrategies.stream().map(
                externalDataComplaintStrategy -> externalDataComplaintStrategy.createRegisterLoadingExternalData(baseComplaintSystemDto))
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(registerLoadingExternalData)){
            return;
        }

        registerLoadingExternalDataRepository.saveAll(registerLoadingExternalData);
        complaintSystemAdditionalDataRepository.save(new ComplaintSystemAdditionalData(baseComplaintSystemDto.getId()));
    }

    @Scheduled(fixedDelayString="${loading.external.data.schedule}")
    public void applyLoadingData(){
        externalDataComplaintStrategies.stream()
                .filter(externalDataComplaintStrategy -> !externalDataComplaintStrategy.isInProcess())
                .forEach(ExternalDataComplaintStrategy::applyLoadingData);
    }

    @Transactional
    @Override
    public void deleteComplaintSystemData(BaseComplaintSystemDto baseComplaintSystemDto) {
        if(baseComplaintSystemDto == null || baseComplaintSystemDto.getId() == null){
            return;
        }

        String id = baseComplaintSystemDto.getId();

        complaintSystemAdditionalDataRepository.deleteById(id);
        registerLoadingExternalDataRepository.deleteAllByComplaintId(id);
    }


}
