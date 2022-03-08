package com.craft.externalmanagementsystemms.notfication.recivers;

import com.craft.complaint.management.api.notfication.ComplaintManagementNotificationMessageQIF;
import com.craft.complaint.management.api.notfication.ComplaintSystemNotification;
import com.craft.externalmanagementsystemms.services.impl.ExternalManagementSystemServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RabbitListener(queues = {"#{complaintManagementQueue.name}"})
@Slf4j
public class ComplaintManagementNotificationReceiver implements ComplaintManagementNotificationMessageQIF {

    /////////////////////////////////// services /////////////////////////////////////

    @Autowired
    private ExternalManagementSystemServiceImpl externalManagementSystemService;

    /////////////////////////////////// receive /////////////////////////////////////

    @Override
    public void receive(ComplaintSystemNotification complaintSystemNotification) {
        switch (complaintSystemNotification.getNotificationType()){
            case CREATED:
                externalManagementSystemService.registerNewComplaintSystem(complaintSystemNotification.getBaseComplaintSystemDto());
                break;
            case DELETED:
                externalManagementSystemService.unRegisterComplaintSystem(complaintSystemNotification.getBaseComplaintSystemDto());
                break;
            default:
                log.info("Not relevant ComplaintSystemNotification: {}", complaintSystemNotification);
                break;
        }
    }
}
