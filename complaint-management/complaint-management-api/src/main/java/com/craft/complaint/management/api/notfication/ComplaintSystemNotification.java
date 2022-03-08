package com.craft.complaint.management.api.notfication;

import com.craft.complaint.management.api.dtos.BaseComplaintSystemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintSystemNotification {

    private ComplaintSystemNotificationType notificationType;

    private BaseComplaintSystemDto baseComplaintSystemDto;
}
