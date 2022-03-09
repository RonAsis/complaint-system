package com.craft.externalmanagementsystemms.web.controller;

import com.craft.complaint.common.ErrorDto;
import com.craft.complaint.external.data.AdditionalData;
import com.craft.externalmanagementsystemms.services.ExternalManagementSystemService;
import com.craft.externalmanagementsystemms.web.annontation.DurationLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Api(tags = "External data apis")
@RestController
@RequiredArgsConstructor
@RequestMapping("/external-data")
@Slf4j
public class ExternalManagementSystemController {

    private final ExternalManagementSystemService externalManagementSystemService;

    /**
     * give all the external data of specific complaint ids
     */
    @DurationLog
    @ApiOperation(value = "Get Compliant System external data for ids")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get compliant system external data for ids succeed"),
            @ApiResponse(code = 400, message = "Get compliant system external data for ids failed", response = ErrorDto.class),
    })
    @GetMapping("/complaints")
    public Map<String, List<AdditionalData>> getComplaintSystemAdditionalData(@Validated @NotEmpty @RequestParam List<String> ids){
        return externalManagementSystemService.getComplaintSystemAdditionalData(ids);
    }

    /**
     * give all the external data of specific complaint id
     */
    @DurationLog
    @ApiOperation(value = "Get Compliant System external data")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get compliant system external data succeed"),
            @ApiResponse(code = 400, message = "Get compliant system external data failed", response = ErrorDto.class),
    })
    @GetMapping("/complaints/{id}")
    public List<AdditionalData> getComplaintSystemAdditionalData(@Validated @NotBlank @PathVariable(name = "id") String id){
        return externalManagementSystemService.getComplaintSystemAdditionalData(id);
    }


    /**
     * Trigger the loading data from external systems
     */
    @DurationLog
    @ApiOperation(value = "Apply loading data")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Apply loading data succeed"),
            @ApiResponse(code = 400, message = "Apply loading data failed", response = ErrorDto.class),
    })
    @PutMapping("/loading-data")
    public void applyLoadingData(){
        externalManagementSystemService.applyLoadingData();
    }
}
