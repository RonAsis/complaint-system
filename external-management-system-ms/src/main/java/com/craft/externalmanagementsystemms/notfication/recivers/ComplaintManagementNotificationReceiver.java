package com.craft.externalmanagementsystemms.notfication.recivers;

import com.craft.complaint.management.api.notfication.ComplaintManagementNotificationMessageQIF;
import com.craft.complaint.management.api.notfication.ComplaintSystemNotification;
import com.craft.externalmanagementsystemms.services.impl.ExternalManagementSystemServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RabbitListener(queues = {"#{complaintManagementQueue.name}"})
@Slf4j
public class ComplaintManagementNotificationReceiver implements ComplaintManagementNotificationMessageQIF {

    /////////////////////////////////// services /////////////////////////////////////

    @Autowired
    private ExternalManagementSystemServiceImpl externalManagementSystemService;

    /////////////////////////////////// receive /////////////////////////////////////

    @RabbitHandler
    @Override
    public void receive(ComplaintSystemNotification complaintSystemNotification) {
        switch (complaintSystemNotification.getNotificationType()){
            case CREATED:
                externalManagementSystemService.registerNewComplaintSystem(complaintSystemNotification.getBaseComplaintSystemDto());
                break;
            case DELETED:
                externalManagementSystemService.deleteComplaintSystemData(complaintSystemNotification.getBaseComplaintSystemDto());
                break;
            default:
                log.info("Not relevant ComplaintSystemNotification: {}", complaintSystemNotification);
                break;
        }
    }
}
