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
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Management all the process of external system
 * <note>
 *     its generic class for pulling data for Complaint
 * </note>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalManagementSystemServiceImpl implements ExternalManagementSystemService {

    /////////////////////////////////////// services ////////////////////////////////////////

    private final ComplaintSystemAdditionalDataRepository complaintSystemAdditionalDataRepository;
    private final RegisterLoadingExternalDataRepository registerLoadingExternalDataRepository;
    private final List<ExternalDataComplaintStrategy> externalDataComplaintStrategies;

    ////////////////////// getters methods /////////////////////////////////

    /**
     *  get Complaint System Additional Data for all the ids in the list
     */
    @Override
    public Map<String, List<AdditionalData>> getComplaintSystemAdditionalData(List<String> ids) {
        return complaintSystemAdditionalDataRepository.findAllByIdIn(ids).stream()
                .collect(Collectors.toMap(ComplaintSystemAdditionalData::getId,ComplaintSystemAdditionalData::getAdditionalDatas));
    }

    /**
     * get Complaint System Additional Data for the id
     */
    @Override
    public List<AdditionalData> getComplaintSystemAdditionalData(String id) {
        return complaintSystemAdditionalDataRepository.findById(id)
                .map(ComplaintSystemAdditionalData::getAdditionalDatas)
                .orElse(new LinkedList<>());
    }

    ////////////////////// add new ComplaintSystem /////////////////////////////////

    /**
     * register New Complaint System
     */
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

    ////////////////////// apply loading /////////////////////////////////

    /**
     * Loading data for all the RegisterLoadingExternalData that exists in DB.
     * <note>
     *     When its will be done, the RegisterLoadingExternalData will remove from DB.
     *     The relevant ComplaintSystemAdditionalData will set with the new values
     * </note>
     * <trigger>
     *     By Scheduled or external api
     * </trigger>
     */
    @DurationLog
    @Scheduled(fixedDelayString="${loading.external.data.schedule}")
    public void applyLoadingData(){
        externalDataComplaintStrategies.stream()
                .filter(externalDataComplaintStrategy -> !externalDataComplaintStrategy.isInProcess())
                .forEach(ExternalDataComplaintStrategy::applyLoadingData);
    }

    /**
     * apply loading data for specific items
     * <note>
     *     All the rest calls that failed will be save in DB as RegisterLoadingExternalData and will be try again in the next Scheduled.
     *     The relevant ComplaintSystemAdditionalData will set with the new values
     * </note>
     * <trigger>
     *     When get new notification of complaint
     * </trigger>
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

    /////////////////////////////// delete data ///////////////////////////////

    /**
     * delete all the reference to BaseComplaintSystemDto
     */
    @Override
    public void deleteComplaintSystemData(BaseComplaintSystemDto baseComplaintSystemDto) {
        if(baseComplaintSystemDto == null || baseComplaintSystemDto.getId() == null){
            return;
        }

        String id = baseComplaintSystemDto.getId();

        complaintSystemAdditionalDataRepository.deleteById(id);
        registerLoadingExternalDataRepository.deleteAllByComplaintId(id);
    }

    //////////////////////////////////// inner use //////////////////////////

    /**
     * filter RegisterLoadingExternalData by type
     */
    private List<RegisterLoadingExternalData> filterByType(DataType dataType, List<RegisterLoadingExternalData> registerLoadingExternalDatas) {
        if(CollectionUtils.isEmpty(registerLoadingExternalDatas) || dataType == null){
            return registerLoadingExternalDatas;
        }

        return registerLoadingExternalDatas.stream()
                .filter(registerLoadingExternalData -> registerLoadingExternalData.getDataType() == dataType)
                .collect(Collectors.toList());
    }


}
