package com.craft.complaintmanagementms.web.controllers;


import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import com.craft.complaint.management.api.dtos.ComplaintSystemDto;
import com.craft.complaint.management.api.dtos.CreateComplaintSystemRequest;
import com.craft.complaint.common.ErrorDto;
import com.craft.complaintmanagementms.services.ComplaintManagementService;
import com.craft.complaintmanagementms.web.annontation.DurationLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "Complaints manager apis")
@RestController
@RequiredArgsConstructor
@RequestMapping("/complaints")
@Slf4j
public class ComplaintManagementController {

  private final ComplaintManagementService complaintManagementService;

  /**
   * Assignment requirement 1
   */
  @DurationLog
  @ApiOperation(value = "Create new Compliant System")
  @ApiResponses({
          @ApiResponse(code = 200, message = "Create new compliant succeed"),
          @ApiResponse(code = 400, message = "Create new compliant failed", response = ErrorDto.class),
  })
  @PostMapping
  public BaseComplaintSystemDto createCompliantSystem(@Validated @NotNull @RequestBody CreateComplaintSystemRequest createComplaintSystemRequest){
    return complaintManagementService.createCompliantSystem(createComplaintSystemRequest.getUserId(), createComplaintSystemRequest.getSubject(),
            createComplaintSystemRequest.getComplaint(), createComplaintSystemRequest.getPurchaseId());
  }

  /**
   * Assignment requirement 2
   */
  @DurationLog
  @ApiOperation(value = "Get Compliant System")
  @ApiResponses({
          @ApiResponse(code = 200, message = "Get compliant system succeed"),
          @ApiResponse(code = 400, message = "Get compliant system failed", response = ErrorDto.class),
  })
  @GetMapping("/{id}")
  public ComplaintSystemDto getCompliantSystem(@Validated @NotBlank @PathVariable(name = "id") String id){
      return complaintManagementService.getCompliantSystem(id);
  }

  //////////////////////////////////// for debugger //////////////////////////////////////////////

  /**
   * @return All Compliant System
   */
  @DurationLog
  @ApiOperation(value = "Get all Compliant System")
  @ApiResponses({
          @ApiResponse(code = 200, message = "Get all compliant system succeed"),
          @ApiResponse(code = 400, message = "Get all compliant system failed", response = ErrorDto.class),
  })
  @GetMapping()
  public List<ComplaintSystemDto> getAllCompliantSystem(){
    return complaintManagementService.getAllCompliantSystem();
  }

  /**
   * @return All Compliant System by ids
   */
  @DurationLog
  @ApiOperation(value = "Get all Compliant System in ids list")
  @ApiResponses({
          @ApiResponse(code = 200, message = "Get all compliant system in ids list succeed"),
          @ApiResponse(code = 400, message = "Get all compliant system in ids list failed", response = ErrorDto.class),
  })
  @GetMapping("/ids")
  public List<ComplaintSystemDto> getCompliantSystemByIds(@Validated @NotEmpty @RequestParam List<String> ids) {
    return complaintManagementService.getCompliantSystemByIds(ids);
  }

}
