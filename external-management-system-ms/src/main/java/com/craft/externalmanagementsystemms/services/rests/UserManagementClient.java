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

/**
 * responsible for all the rest api to the user management system
 */
@Slf4j
@Service
public class UserManagementClient extends RestService {

    @Value("${user.management.url}")
    private String userManagementUrl;

    /**
     * rest api
     */
    public ResponseEntity<ObjectNode> getUserData(UUID userId) {
        log.info("Start getUserData, userId: {} ", userId);
        String url = userManagementUrl + "/users/" + userId;

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
