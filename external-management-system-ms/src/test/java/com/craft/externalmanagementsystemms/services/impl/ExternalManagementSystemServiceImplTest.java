package com.craft.externalmanagementsystemms.services.impl;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import com.craft.complaint.external.data.externaldata.PurchaseData;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.ComplaintSystemAdditionalData;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import com.craft.externalmanagementsystemms.domain.repositores.ComplaintSystemAdditionalDataRepository;
import com.craft.externalmanagementsystemms.domain.repositores.RegisterLoadingExternalDataRepository;
import com.craft.externalmanagementsystemms.services.externalstrategy.PurchaseDataCompliantStrategy;
import com.craft.externalmanagementsystemms.services.externalstrategy.UserDataCompliantStrategy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ExternalManagementSystemServiceImpl.class,
})
@DataMongoTest
@EnableMongoRepositories("com.craft.externalmanagementsystemms.domain.repositores")
class ExternalManagementSystemServiceImplTest {

    @Autowired
    private ExternalManagementSystemServiceImpl externalManagementSystemService;

    @Autowired
    private ComplaintSystemAdditionalDataRepository complaintSystemAdditionalDataRepository;

    @Autowired
    private RegisterLoadingExternalDataRepository registerLoadingExternalDataRepository;

    @MockBean
    private PurchaseDataCompliantStrategy purchaseDataCompliantStrategy;

    @MockBean
    private UserDataCompliantStrategy userDataCompliantStrategy;

    @AfterEach
    public void clean(){
        complaintSystemAdditionalDataRepository.deleteAll();
        registerLoadingExternalDataRepository.deleteAll();
    }

    @Test
    void getComplaintSystemAdditionalData() {
        //init
        ComplaintSystemAdditionalData complaintSystemAdditionalData = new ComplaintSystemAdditionalData("test");
        complaintSystemAdditionalData.addAdditionalData(new PurchaseData());
        ComplaintSystemAdditionalData complaintSystemAdditionalDataSaved = complaintSystemAdditionalDataRepository.save(complaintSystemAdditionalData);

        //test
        List<String> ids = Collections.singletonList(complaintSystemAdditionalData.getId());
        Map<String, List<AdditionalData>> idToAdditionalData = externalManagementSystemService.getComplaintSystemAdditionalData(ids);

        //assertions
        Assertions.assertEquals(idToAdditionalData.size(), 1);
        Assertions.assertEquals(idToAdditionalData.get(complaintSystemAdditionalData.getId()), complaintSystemAdditionalDataSaved.getAdditionalDatas());
    }

    @Test
    void testGetComplaintSystemAdditionalData() {
        //init
        ComplaintSystemAdditionalData complaintSystemAdditionalData = new ComplaintSystemAdditionalData("test1");
        complaintSystemAdditionalData.addAdditionalData(new PurchaseData());
        ComplaintSystemAdditionalData complaintSystemAdditionalDataSaved = complaintSystemAdditionalDataRepository.save(complaintSystemAdditionalData);

        //test
        List<AdditionalData> additionalData = externalManagementSystemService.getComplaintSystemAdditionalData(complaintSystemAdditionalData.getId());

        //assertions
        Assertions.assertEquals(additionalData.size(), 1);
        Assertions.assertEquals(additionalData, complaintSystemAdditionalDataSaved.getAdditionalDatas());
    }

    @Test
    void registerNewComplaintSystem() {
        //init
        BaseComplaintSystemDto baseComplaintSystemDto = new BaseComplaintSystemDto();
        baseComplaintSystemDto.setId("1");
        baseComplaintSystemDto.setComplaint("complaint");
        baseComplaintSystemDto.setCreatedTime(new Date());
        baseComplaintSystemDto.setSubject("subject");
        baseComplaintSystemDto.setPurchaseId(UUID.randomUUID());
        baseComplaintSystemDto.setUserId(UUID.randomUUID());

        RegisterLoadingExternalData registerLoadingExternalDataPurchase = new RegisterLoadingExternalData();
        registerLoadingExternalDataPurchase.setDataType(DataType.PURCHASE);
        registerLoadingExternalDataPurchase.setSourceId(baseComplaintSystemDto.getPurchaseId());
        registerLoadingExternalDataPurchase.setComplaintId(baseComplaintSystemDto.getId());

        RegisterLoadingExternalData registerLoadingExternalDataUser = new RegisterLoadingExternalData();
        registerLoadingExternalDataUser.setDataType(DataType.USER);
        registerLoadingExternalDataUser.setSourceId(baseComplaintSystemDto.getUserId());
        registerLoadingExternalDataUser.setComplaintId(baseComplaintSystemDto.getId());

        //mock
        Mockito.when(purchaseDataCompliantStrategy.createRegisterLoadingExternalData(any())).thenReturn(registerLoadingExternalDataPurchase);
        Mockito.when(userDataCompliantStrategy.createRegisterLoadingExternalData(any())).thenReturn(registerLoadingExternalDataUser);

        Mockito.when(purchaseDataCompliantStrategy.applyLoadingData(any())).thenReturn(Collections.singletonList(registerLoadingExternalDataPurchase));
        Mockito.when(userDataCompliantStrategy.applyLoadingData(any())).thenReturn(Collections.singletonList(registerLoadingExternalDataUser));

        //test
        externalManagementSystemService.registerNewComplaintSystem(baseComplaintSystemDto);

        ComplaintSystemAdditionalData complaintSystemAdditionalData = complaintSystemAdditionalDataRepository.findById(baseComplaintSystemDto.getId()).get();
        List<RegisterLoadingExternalData> allRegisterLoadingExternalDatas = registerLoadingExternalDataRepository.findAll();

        //assertions
        Assertions.assertEquals(complaintSystemAdditionalData.getId(), baseComplaintSystemDto.getId());
        Assertions.assertEquals(complaintSystemAdditionalData.getAdditionalDatas().size(), 0);
        Assertions.assertEquals(Arrays.asList(registerLoadingExternalDataPurchase, registerLoadingExternalDataUser)
                , allRegisterLoadingExternalDatas);
    }

    @Test
    void applyLoadingData() {
        //test
        externalManagementSystemService.applyLoadingData();

        //assertions
        Mockito.verify(userDataCompliantStrategy, Mockito.times(1)).applyLoadingData();
        Mockito.verify(purchaseDataCompliantStrategy, Mockito.times(1)).applyLoadingData();
    }

    @Test
    void deleteComplaintSystemData() {
        //init
        ComplaintSystemAdditionalData complaintSystemAdditionalData = new ComplaintSystemAdditionalData("test2");

        BaseComplaintSystemDto baseComplaintSystemDto = new BaseComplaintSystemDto();
        baseComplaintSystemDto.setId(complaintSystemAdditionalData.getId());
        baseComplaintSystemDto.setComplaint("complaint");
        baseComplaintSystemDto.setCreatedTime(new Date());
        baseComplaintSystemDto.setSubject("subject");
        baseComplaintSystemDto.setPurchaseId(UUID.randomUUID());
        baseComplaintSystemDto.setUserId(UUID.randomUUID());

        RegisterLoadingExternalData registerLoadingExternalDataPurchase = new RegisterLoadingExternalData();
        registerLoadingExternalDataPurchase.setDataType(DataType.PURCHASE);
        registerLoadingExternalDataPurchase.setSourceId(baseComplaintSystemDto.getPurchaseId());
        registerLoadingExternalDataPurchase.setComplaintId(baseComplaintSystemDto.getId());

        RegisterLoadingExternalData registerLoadingExternalDataUser = new RegisterLoadingExternalData();
        registerLoadingExternalDataUser.setDataType(DataType.USER);
        registerLoadingExternalDataUser.setSourceId(baseComplaintSystemDto.getUserId());
        registerLoadingExternalDataUser.setComplaintId(baseComplaintSystemDto.getId());

        complaintSystemAdditionalDataRepository.save(complaintSystemAdditionalData);
        registerLoadingExternalDataRepository.saveAll(Arrays.asList(registerLoadingExternalDataUser, registerLoadingExternalDataPurchase));

        //test
        externalManagementSystemService.deleteComplaintSystemData(baseComplaintSystemDto);
    }
}
