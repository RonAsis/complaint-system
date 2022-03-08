package com.craft.externalmanagementsystemms.services.externalstrategy;

import com.craft.complaint.common.utils.AsyncRunner;
import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import com.craft.externalmanagementsystemms.domain.repositores.ComplaintSystemAdditionalDataRepository;
import com.craft.externalmanagementsystemms.domain.repositores.RegisterLoadingExternalDataRepository;
import com.craft.externalmanagementsystemms.services.helpers.HelperPage;
import com.craft.externalmanagementsystemms.web.annontation.DurationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class ExternalDataComplaintStrategy {

    protected AtomicBoolean isInProcess;

    @Autowired
    protected AsyncRunner asyncRunner;

    @Autowired
    protected RegisterLoadingExternalDataRepository externalDataRepository;

    @Autowired
    protected ComplaintSystemAdditionalDataRepository complaintSystemAdditionalDataRepository;

    protected RegisterLoadingExternalData createRegisterLoadingExternalData(String complaintId, Object sourceId){
        RegisterLoadingExternalData registerLoadingExternalData = new RegisterLoadingExternalData();

        registerLoadingExternalData.setComplaintId(complaintId);
        registerLoadingExternalData.setDataType(getDataType());
        registerLoadingExternalData.setSourceId(sourceId);

        return registerLoadingExternalData;
    }

    @DurationLog
    public void applyLoadingData() {
        isInProcess.set(true);

        AsyncRunner.JobCounter externalDataJob = new AsyncRunner.JobCounter(getDataType() + " page");

        Pageable pageable = PageRequest.of(0, 100);

        int size = 0;

        AtomicBoolean isLast = new AtomicBoolean(false);
        List<RegisterLoadingExternalData> toRemove = new LinkedList<>();

        while (!isLast.get()){
            HelperPage<RegisterLoadingExternalData> allByDataType = externalDataRepository.findAllByDataType(getDataType(), pageable);

            if(allByDataType.isEmpty()){
                isLast.set(true);
                break;
            }

            List<RegisterLoadingExternalData> registerLoadingExternalDatas = allByDataType.getContent();

            log.info("Total RegisterLoadingExternalData: {} for page {} of {} for type {}", registerLoadingExternalDatas.size(),
                    allByDataType.getNumber()+ 1, allByDataType.getTotalPages(), getDataType());

            isLast.set(allByDataType.isLast());

            pageable= pageable.next();

            size++;

            asyncRunner.asynTaskNoPool(() ->{
                for(RegisterLoadingExternalData registerLoadingExternalData: registerLoadingExternalDatas){
                    ResponseEntity<Object> data = getExternalDataById(registerLoadingExternalData);
                    if(data.getBody() != null){
                        saveExternalDataIntoCompliant(registerLoadingExternalData, data);
                        toRemove.add(registerLoadingExternalData);

                    }
                }
            }, externalDataJob);

        }

        externalDataJob.waitForAllJobsDone(size);

        externalDataRepository.deleteAll(toRemove);

        isInProcess.set(false);
    }

    protected abstract ResponseEntity<Object> getExternalDataById(RegisterLoadingExternalData registerLoadingExternalData);


    @Transactional
    protected void saveExternalDataIntoCompliant(RegisterLoadingExternalData registerLoadingExternalData, ResponseEntity<Object> data) {
        complaintSystemAdditionalDataRepository.findById(registerLoadingExternalData.getComplaintId())
                .ifPresent(complaintSystemAdditionalData -> {
                    complaintSystemAdditionalData.addAdditionalData(convert(data.getBody()));
                    complaintSystemAdditionalDataRepository.save(complaintSystemAdditionalData);
                });
    }

    public boolean isInProcess(){
        return isInProcess.get();
    }

    /////////////////// abstracts /////////////////////////

    public abstract RegisterLoadingExternalData createRegisterLoadingExternalData(BaseComplaintSystemDto baseComplaintSystemDto);

    public abstract AdditionalData convert(Object body);

    public abstract DataType getDataType();
}
