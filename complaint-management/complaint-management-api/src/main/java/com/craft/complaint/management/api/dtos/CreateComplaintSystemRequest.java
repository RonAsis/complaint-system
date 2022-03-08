package com.craft.complaint.management.api.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateComplaintSystemRequest {

    @NotNull
    private UUID userId;
    @NotBlank
    private String subject;
    @NotBlank
    private String complaint;
    @NotNull
    private UUID purchaseId;

}
