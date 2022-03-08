package com.craft.complaint.management.api.dtos;

import com.craft.complaint.external.data.AdditionalData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintSystemDto extends BaseComplaintSystemDto{

    private List<AdditionalData> additionalData;
}
