package com.craft.externalmanagementsystemms.services.externalstrategy;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import com.craft.complaint.external.data.externaldata.PurchaseData;
import com.craft.complaint.external.data.externaldata.UserData;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import com.craft.externalmanagementsystemms.services.rests.UserManagementClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserDataCompliantStrategy extends ExternalDataComplaintStrategy{

    @Autowired
    private UserManagementClient userManagementClient;

    @Override
    public RegisterLoadingExternalData createRegisterLoadingExternalData(BaseComplaintSystemDto baseComplaintSystemDto) {
        return createRegisterLoadingExternalData(baseComplaintSystemDto.getId(), baseComplaintSystemDto.getUserId());
    }

    @Override
    public String getTypeJsonObject() {
        return "userData";
    }

    @Override
    protected ResponseEntity<ObjectNode> getExternalDataById(RegisterLoadingExternalData registerLoadingExternalData) {
        return userManagementClient.getUserData((UUID) registerLoadingExternalData.getSourceId());
    }

    @Override
    public DataType getDataType() {
        return DataType.USER;
    }
}
