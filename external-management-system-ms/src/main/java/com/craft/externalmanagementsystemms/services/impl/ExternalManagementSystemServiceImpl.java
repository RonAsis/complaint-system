package com.craft.externalmanagementsystemms.services.impl;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.ComplaintSystemAdditionalData;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import com.craft.externalmanagementsystemms.domain.repositores.ComplaintSystemAdditionalDataRepository;
import com.craft.externalmanagementsystemms.domain.repositores.RegisterLoadingExternalDataRepository;
import com.craft.externalmanagementsystemms.services.ExternalManagementSystemService;
import com.craft.externalmanagementsystemms.services.externalstrategy.ExternalDataComplaintStrategy;
import com.craft.externalmanagementsystemms.web.annontation.DurationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    public void registerNewComplaintSystem(BaseComplaintSystemDto baseComplaintSystemDto) {
        if(baseComplaintSystemDto == null || baseComplaintSystemDto.getId() == null ||
                complaintSystemAdditionalDataRepository.existsById(baseComplaintSystemDto.getId())){
            return;
        }

        List<RegisterLoadingExternalData> registerLoadingExternalData = externalDataComplaintStrategies.stream().map(
                externalDataComplaintStrategy -> externalDataComplaintStrategy.createRegisterLoadingExternalData(baseComplaintSystemDto))
                .collect(Collectors.toList());

        complaintSystemAdditionalDataRepository.save(new ComplaintSystemAdditionalData(baseComplaintSystemDto.getId()));

        applyLoadingData(registerLoadingExternalData);
    }

    @DurationLog
    @Scheduled(fixedDelayString="${loading.external.data.schedule}")
    public void applyLoadingData(){
        externalDataComplaintStrategies.stream()
                .filter(externalDataComplaintStrategy -> !externalDataComplaintStrategy.isInProcess())
                .forEach(ExternalDataComplaintStrategy::applyLoadingData);
    }

    /**
     * apply loading data for specific items
     */
    private void applyLoadingData(List<RegisterLoadingExternalData> registerLoadingExternalData){
        List<RegisterLoadingExternalData> allFailedLoadingData = new LinkedList<>();

        externalDataComplaintStrategies
                .forEach(externalDataComplaintStrategy -> {
                    List<RegisterLoadingExternalData> failedLoadingData = externalDataComplaintStrategy.applyLoadingData(filterByType(externalDataComplaintStrategy.getDataType(), registerLoadingExternalData));

                    if(!CollectionUtils.isEmpty(failedLoadingData)){
                        allFailedLoadingData.addAll(failedLoadingData);
                    }
                });

       if(CollectionUtils.isEmpty(allFailedLoadingData)){
           return;
       }

        registerLoadingExternalDataRepository.saveAll(allFailedLoadingData);
    }

    private List<RegisterLoadingExternalData> filterByType(DataType dataType, List<RegisterLoadingExternalData> registerLoadingExternalDatas) {
        if(CollectionUtils.isEmpty(registerLoadingExternalDatas) || dataType == null){
            return registerLoadingExternalDatas;
        }

        return registerLoadingExternalDatas.stream()
                .filter(registerLoadingExternalData -> registerLoadingExternalData.getDataType() == dataType)
                .collect(Collectors.toList());
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
