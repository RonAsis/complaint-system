package com.craft.externalmanagementsystemms.services.externalstrategy;

import com.craft.complaint.common.utils.AsyncRunner;
import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.external.data.DataType;
import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.externalmanagementsystemms.domain.model.entites.RegisterLoadingExternalData;
import com.craft.externalmanagementsystemms.services.helpers.HelperPage;
import com.craft.externalmanagementsystemms.services.rests.PurchaseManagementClient;
import com.craft.externalmanagementsystemms.web.annontation.DurationLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class PurchaseDataCompliantStrategy extends ExternalDataComplaintStrategy{

    private PurchaseManagementClient purchaseManagementClient;

    @Override
    protected ResponseEntity<Object> getExternalDataById(RegisterLoadingExternalData registerLoadingExternalData) {
        return purchaseManagementClient.getPurchaseData((UUID) registerLoadingExternalData.getSourceId());
    }

    @Override
    public RegisterLoadingExternalData createRegisterLoadingExternalData(BaseComplaintSystemDto baseComplaintSystemDto) {
        return createRegisterLoadingExternalData(baseComplaintSystemDto.getId(), baseComplaintSystemDto.getPurchaseId());
    }

    @Override
    public AdditionalData convert(Object body) {
        return null;
    }


    @Override
    public DataType getDataType() {
        return DataType.PURCHASE;
    }
}
