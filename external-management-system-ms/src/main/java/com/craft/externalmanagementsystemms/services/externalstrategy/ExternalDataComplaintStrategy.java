package com.craft.externalmanagementsystemms.services.externalstrategy;

import com.craft.complaint.common.utils.AsyncRunner;
import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import com.craft.externalmanagementsystemms.domain.repositores.ComplaintSystemAdditionalDataRepository;
import com.craft.externalmanagementsystemms.domain.repositores.RegisterLoadingExternalDataRepository;
import com.craft.externalmanagementsystemms.web.annontation.DurationLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract class for loading data from external resources.
 * Using the Strategy pattern
 */
@Slf4j
public abstract class ExternalDataComplaintStrategy {

    ////////////////////////// data //////////////////////////////

    /**
     * if its found in process for know not schedule the process again(relevant only when schedule from external api)
     */
    protected AtomicBoolean isInProcess = new AtomicBoolean(false);

    ////////////////////////// converters //////////////////////////////

    /**
     * object mapper for convert data from api to structure data
     */
    protected ObjectMapper objectMapper = new ObjectMapper();

    ////////////////////////// services //////////////////////////////

    /**
     * service for run with thread
     */
    @Autowired
    protected AsyncRunner asyncRunner;

    /**
     * register Loading External Data Repository
     */
    @Autowired
    protected RegisterLoadingExternalDataRepository registerLoadingExternalDataRepository;

    /**
     * complaint System Additional DataR epository
     */
    @Autowired
    protected ComplaintSystemAdditionalDataRepository complaintSystemAdditionalDataRepository;

    ///////////////////////////// factories //////////////////////////////////////

    /**
     * create Register Loading External Data
     */
    protected RegisterLoadingExternalData createRegisterLoadingExternalData(String complaintId, Object sourceId){
        RegisterLoadingExternalData registerLoadingExternalData = new RegisterLoadingExternalData();

        registerLoadingExternalData.setComplaintId(complaintId);
        registerLoadingExternalData.setDataType(getDataType());
        registerLoadingExternalData.setSourceId(sourceId);

        return registerLoadingExternalData;
    }

    ///////////////////////////// apply loading data //////////////////////////////////////

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
    public List<RegisterLoadingExternalData> applyLoadingData(List<RegisterLoadingExternalData> filterByType) {
        if(CollectionUtils.isEmpty(filterByType)){
            return filterByType;
        }

        List<RegisterLoadingExternalData> failedToLoading = new LinkedList<>(filterByType);

        for(RegisterLoadingExternalData registerLoadingExternalData: filterByType){
            ResponseEntity<ObjectNode> data = getExternalDataById(registerLoadingExternalData);

            if(data == null){
                log.error("There is no connection with service that supply data for {}", getDataType());
                break;
            }

            if(data.getBody() != null){
                saveExternalDataIntoCompliant(registerLoadingExternalData, data);
            }

            failedToLoading.remove(registerLoadingExternalData);
        }

        return failedToLoading;
    }

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
    public void applyLoadingData() {
        isInProcess.set(true);

        AsyncRunner.JobCounter externalDataJob = new AsyncRunner.JobCounter(getDataType() + " page");

        Pageable pageable = PageRequest.of(0, 100);

        int size = 0;

        AtomicBoolean isLast = new AtomicBoolean(false);
        AtomicBoolean thereIsConnectionWithServer = new AtomicBoolean(true);
        List<RegisterLoadingExternalData> toRemove = new LinkedList<>();

        while (!isLast.get() && thereIsConnectionWithServer.get()){
            PageImpl<RegisterLoadingExternalData> allByDataType = registerLoadingExternalDataRepository.findAllByDataType(getDataType(), pageable);

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

                    if(!thereIsConnectionWithServer.get()){
                        return;
                    }

                    ResponseEntity<ObjectNode> data = getExternalDataById(registerLoadingExternalData);
                    if(data == null){
                        log.error("There is no connection with service that supply data for {}", getDataType());
                        thereIsConnectionWithServer.set(false);
                        externalDataJob.jobFinished();
                        return;
                    }
                    if(data.getBody() != null){
                        saveExternalDataIntoCompliant(registerLoadingExternalData, data);
                    }
                    toRemove.add(registerLoadingExternalData);
                }
            }, externalDataJob);

        }

        externalDataJob.waitForAllJobsDone(size);

        registerLoadingExternalDataRepository.deleteAll(toRemove);

        isInProcess.set(false);
    }

    ///////////////////////////////// save in DB method /////////////////////////////

    /**
     * set the external data into the complaint object
     */
    @Transactional
    protected void saveExternalDataIntoCompliant(RegisterLoadingExternalData registerLoadingExternalData, ResponseEntity<ObjectNode> data) {
        complaintSystemAdditionalDataRepository.findById(registerLoadingExternalData.getComplaintId())
                .ifPresent(complaintSystemAdditionalData -> {
                    try {
                        complaintSystemAdditionalData.addAdditionalData(convert(data.getBody()));
                        complaintSystemAdditionalDataRepository.save(complaintSystemAdditionalData);
                    } catch (JsonProcessingException e) {
                       log.error("Failed to convert {} to {} type", data.getBody(), getDataType());
                    }
                });
    }

    ///////////////////////////////// is-methods /////////////////////////////

    /**
     * is in process
     */
    public boolean isInProcess(){
        return isInProcess.get();
    }

    ///////////////////////////////// converters /////////////////////////////

    public AdditionalData convert(ObjectNode body) throws JsonProcessingException {
        ObjectNode objectNodeData = objectMapper.createObjectNode();
        objectNodeData.set(getTypeJsonObject(), body);
        return objectMapper.treeToValue(objectNodeData, AdditionalData.class);
    }

    /////////////////// abstracts /////////////////////////

    protected abstract ResponseEntity<ObjectNode> getExternalDataById(RegisterLoadingExternalData registerLoadingExternalData);

    public abstract RegisterLoadingExternalData createRegisterLoadingExternalData(BaseComplaintSystemDto baseComplaintSystemDto);

    public abstract String getTypeJsonObject();

    public abstract DataType getDataType();

}
