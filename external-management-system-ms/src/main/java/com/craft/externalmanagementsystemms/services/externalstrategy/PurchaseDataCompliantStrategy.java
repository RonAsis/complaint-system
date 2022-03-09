package com.craft.externalmanagementsystemms.services.externalstrategy;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import com.craft.complaint.external.data.externaldata.PurchaseData;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import com.craft.externalmanagementsystemms.services.rests.PurchaseManagementClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class PurchaseDataCompliantStrategy extends ExternalDataComplaintStrategy {

    @Autowired
    private PurchaseManagementClient purchaseManagementClient;

    @Override
    protected ResponseEntity<ObjectNode> getExternalDataById(RegisterLoadingExternalData registerLoadingExternalData) {
        return purchaseManagementClient.getPurchaseData((UUID) registerLoadingExternalData.getSourceId());
    }

    @Override
    public RegisterLoadingExternalData createRegisterLoadingExternalData(BaseComplaintSystemDto baseComplaintSystemDto) {
        return createRegisterLoadingExternalData(baseComplaintSystemDto.getId(), baseComplaintSystemDto.getPurchaseId());
    }

    @Override
    public String getTypeJsonObject() {
        return "purchaseData";
    }

    @Override
    public DataType getDataType() {
        return DataType.PURCHASE;
    }
}
