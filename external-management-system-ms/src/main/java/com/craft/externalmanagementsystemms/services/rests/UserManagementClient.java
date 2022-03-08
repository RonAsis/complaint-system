package com.craft.externalmanagementsystemms.services.rests;

import com.craft.complaint.common.utils.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UserManagementClient extends RestService {

    @Value("${user.management.url}")
    private String userManagementUrl;

    public ResponseEntity<Object> getUserData(UUID userId) {
        log.info("Start getUserData, userId: {} ", userId);
        String url = userManagementUrl + "/users/" + userId;

        return runRest(url, () -> {
            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Object>() {
                    });
            log.info("OUT: Rest call to {}", url);
            return response;
        });
    }
}
