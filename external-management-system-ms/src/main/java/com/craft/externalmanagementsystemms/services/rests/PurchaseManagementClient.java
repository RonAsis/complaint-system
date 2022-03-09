package com.craft.externalmanagementsystemms.services.rests;

import com.craft.complaint.common.utils.RestService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class PurchaseManagementClient extends RestService {

    @Value("${purchase.management.url}")
    private String purchaseManagementUrl;

    public ResponseEntity<ObjectNode> getPurchaseData(UUID purchaseId) {
        log.info("Start getPurchaseData, userId: {} ", purchaseId);
        String url = purchaseManagementUrl + "/purchases/" + purchaseId;

        return runRest(url, () -> {
            ResponseEntity<ObjectNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ObjectNode>() {
                    });
            log.info("OUT: Rest call to {}", url);
            return response;
        });
    }
}
