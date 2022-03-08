package com.craft.complaintmanagementms.services.impl;

import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.complaint.management.api.dtos.ComplaintSystemDto;
import com.craft.complaintmanagementms.domain.model.entites.ComplaintSystem;
import com.craft.complaintmanagementms.domain.repositores.ComplaintSystemRepository;
import com.craft.complaintmanagementms.notfication.senders.ComplaintManagementNotificationSender;
import com.craft.complaintmanagementms.services.conveters.ComplaintManagementConverter;
import com.craft.complaintmanagementms.services.rests.ExternalManagementSystemService;
import com.craft.complaintmanagementms.services.utils.AsyncRunner;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ComplaintManagementServiceImpl.class,
        ComplaintManagementConverter.class
})
@WithModelMapper
@DataMongoTest
@EnableMongoRepositories("com.craft.complaintmanagementms.domain.repositores")
public class ComplaintManagementServiceImplTest {

    @Autowired
    private ComplaintManagementServiceImpl complaintManagementService;

    @MockBean
    private ExternalManagementSystemService externalManagementSystemService;

    @MockBean
    private ComplaintManagementNotificationSender complaintManagementNotificationSender;

    @MockBean
    private AsyncRunner asyncRunner;

    @Autowired
    private ComplaintSystemRepository complaintSystemRepository;

    @Test
    void createCompliantSystem() {
        // init
        UUID userId = UUID.randomUUID();
        String subject = "subject test";
        String complaint = "complaint test";
        UUID purchaseId = UUID.randomUUID();

        //test
        BaseComplaintSystemDto compliantSystem = complaintManagementService.createCompliantSystem(userId, subject, complaint, purchaseId);

        //assertion
        Mockito.verify(asyncRunner, Mockito.times(1)).asynTaskNoPool(any());

        Assertions.assertEquals(userId, compliantSystem.getUserId());
        Assertions.assertEquals(subject, compliantSystem.getSubject());
        Assertions.assertEquals(complaint, compliantSystem.getComplaint());
        Assertions.assertEquals(purchaseId, compliantSystem.getPurchaseId());
        Assertions.assertNotNull(compliantSystem.getId());
        Assertions.assertNotNull(compliantSystem.getCreatedTime());
    }

    @Test
    void getCompliantSystemIds() {
        // init
        UUID userId = UUID.randomUUID();
        String subject = "subject test";
        String complaint = "complaint test";
        UUID purchaseId = UUID.randomUUID();

        ComplaintSystem complaintSystem = new ComplaintSystem(userId, subject, complaint, purchaseId, new Date());
        ComplaintSystem complaintSystemSaved = complaintSystemRepository.save(complaintSystem);

        //test
        ComplaintSystemDto compliantSystem = complaintManagementService.getCompliantSystemIds(complaintSystemSaved.getId());

        //assertion
        Mockito.verify(externalManagementSystemService, Mockito.times(1)).getAdditionalData(any(String.class));

        Assertions.assertEquals(userId, compliantSystem.getUserId());
        Assertions.assertEquals(subject, compliantSystem.getSubject());
        Assertions.assertEquals(complaint, compliantSystem.getComplaint());
        Assertions.assertEquals(purchaseId, compliantSystem.getPurchaseId());
        Assertions.assertEquals(complaintSystemSaved.getId(), compliantSystem.getId());
        Assertions.assertEquals(complaintSystemSaved.getCreatedTime(), compliantSystem.getCreatedTime());
        Assertions.assertTrue(CollectionUtils.isEmpty(compliantSystem.getAdditionalData()));
    }
}
