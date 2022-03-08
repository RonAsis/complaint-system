package com.craft.externalmanagementsystemms.services.externalstrategy;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import com.craft.externalmanagementsystemms.services.rests.UserManagementClient;
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
    public AdditionalData convert(Object body) {
        return null;
    }

    @Override
    protected ResponseEntity<Object> getExternalDataById(RegisterLoadingExternalData registerLoadingExternalData) {
        return userManagementClient.getUserData((UUID) registerLoadingExternalData.getSourceId());
    }

    @Override
    public DataType getDataType() {
        return DataType.USER;
    }
}
