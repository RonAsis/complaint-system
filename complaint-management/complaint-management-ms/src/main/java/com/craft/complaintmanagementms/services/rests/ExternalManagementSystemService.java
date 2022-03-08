package com.craft.complaintmanagementms.services.rests;

import com.craft.complaint.external.data.AdditionalData;
import com.craft.complaint.common.utils.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExternalManagementSystemService extends RestService {

    @Value("${external.management.url}")
    private String externalManagementUrl;

    public List<AdditionalData> getAdditionalData(String id) {
        Map<String, List<AdditionalData>> additionalData = getAdditionalData(Collections.singletonList(id));

        if(additionalData == null){
            return new LinkedList<>();
        }

        return additionalData.get(id);
    }

    public Map<String, List<AdditionalData>> getAdditionalData(List<String> ids) {
        log.info("Start getAdditionalData, ids: {} ",ids);
        String url = externalManagementUrl + "/complaint-additional-data";

        return runRest(url, () ->{
            ResponseEntity<Map<String, List<AdditionalData>>> response = restTemplate.exchange(
                    createUri(url, getParam("ids"), ids),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, List<AdditionalData>>>() {
                    });
            log.info("OUT: Rest call to {}", url);
           return response.getBody();
        });
    }
}
