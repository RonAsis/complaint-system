package com.craft.complaint.management.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseComplaintSystemDto extends CreateComplaintSystemRequest{

    private String id;
    private Date createdTime;

}
