package com.craft.complaintmanagementms.services.impl;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.complaint.management.api.dtos.ComplaintSystemDto;
import com.craft.complaint.management.api.notfication.ComplaintSystemNotification;
import com.craft.complaint.management.api.notfication.ComplaintSystemNotificationType;
import com.craft.complaintmanagementms.domain.model.entites.ComplaintSystem;
import com.craft.complaintmanagementms.domain.repositores.ComplaintSystemRepository;
import com.craft.complaintmanagementms.notfication.senders.ComplaintManagementNotificationSender;
import com.craft.complaintmanagementms.services.ComplaintManagementService;
import com.craft.complaintmanagementms.services.conveters.ComplaintManagementConverter;
import com.craft.complaintmanagementms.services.rests.ExternalManagementSystemService;
import com.craft.complaint.common.utils.AsyncRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Responsible for manage all the life cycle of Complaint System
 */
@Service
@RequiredArgsConstructor
public class ComplaintManagementServiceImpl implements ComplaintManagementService {

    //////////////////////////////// services //////////////////////////////////

    /**
     * complaint System Repository
     */
    private final ComplaintSystemRepository complaintSystemRepository;
    /**
     * external Management System Service
     */
    private final ExternalManagementSystemService externalManagementSystemService;
    /**
     * complaint Management Notification Sender
     */
    private final ComplaintManagementNotificationSender complaintManagementNotificationSender;
    /**
     * async Runner for running in threads
     */
    private final AsyncRunner asyncRunner;

    //////////////////////////////// creation methods ////////////////////////////////

    /**
     * create Compliant System
     */
    @Override
    public BaseComplaintSystemDto createCompliantSystem(UUID userId, String subject, String complaint, UUID purchaseId) {
        ComplaintSystem complaintSystem = new ComplaintSystem(userId, subject, complaint, purchaseId, new Date());
        ComplaintSystem complaintSystemSaved = complaintSystemRepository.save(complaintSystem);

        BaseComplaintSystemDto baseComplaintSystemDto = ComplaintManagementConverter.convert(complaintSystemSaved);

        sendNotification(baseComplaintSystemDto, ComplaintSystemNotificationType.CREATED);

        return baseComplaintSystemDto;
    }

    //////////////////////////////// getters methods ////////////////////////////////

    /**
     * @return CompliantSystem by id
     */
    @Override
    public ComplaintSystemDto getCompliantSystem(String id) {
        return complaintSystemRepository.findById(id)
                .map(complaintSystem -> {
                    List<AdditionalData> additionalData = externalManagementSystemService.getAdditionalData(id);
                    return ComplaintManagementConverter.convert(complaintSystem, additionalData);
                })
                .orElseGet(ComplaintSystemDto::new);
    }

    /**
     * @return All CompliantSystem
     */
    @Override
    public List<ComplaintSystemDto> getAllCompliantSystem() {
        List<ComplaintSystem> complaintSystems = complaintSystemRepository.findAll();

        List<String> ids = extractIds(complaintSystems);
        Map<String, List<AdditionalData>> idToAdditionalData = externalManagementSystemService.getAdditionalData(ids);

        return ComplaintManagementConverter.convert(complaintSystems, idToAdditionalData);
    }

    /**
     * @return All CompliantSystem by ids
     */
    @Override
    public List<ComplaintSystemDto> getCompliantSystemByIds(List<String> ids) {
        List<ComplaintSystem> complaintSystems = complaintSystemRepository.findAllByIdIn(ids);
        Map<String, List<AdditionalData>> idToAdditionalData = externalManagementSystemService.getAdditionalData(ids);

        return ComplaintManagementConverter.convert(complaintSystems, idToAdditionalData);
    }


    /////////////////////// private use ///////////////////////////////

    /**
     * extract ids from list of ComplaintSystem
     */
    private List<String> extractIds(List<ComplaintSystem> complaintSystems) {
        if(CollectionUtils.isEmpty(complaintSystems)){
            return new LinkedList<>();
        }

        return complaintSystems.stream()
                .map(ComplaintSystem::getId)
                .collect(Collectors.toList());
    }

    /**
     * send notification
     * <note>
     *     Its happened in different thread
     * </note>
     */
    private void sendNotification(BaseComplaintSystemDto baseComplaintSystemDto, ComplaintSystemNotificationType complaintSystemNotificationType) {
        asyncRunner.asynTaskNoPool(() ->
                complaintManagementNotificationSender.sendNotification(ComplaintSystemNotification.builder()
                        .notificationType(complaintSystemNotificationType)
                        .baseComplaintSystemDto(baseComplaintSystemDto)
                        .build()));
    }

}
