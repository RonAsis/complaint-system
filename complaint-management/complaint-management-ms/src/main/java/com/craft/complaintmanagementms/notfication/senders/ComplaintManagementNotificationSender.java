package com.craft.complaintmanagementms.notfication.senders;

import com.craft.complaint.management.api.notfication.ComplaintManagementNotificationMessageQIF;
import com.craft.complaint.management.api.notfication.ComplaintSystemNotification;
import com.craft.complaint.common.utils.AsyncRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Send complaint management notification
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintManagementNotificationSender {

    private final RabbitTemplate rabbitTemplate;

    /**
     * send tosca template notification
     */
    public void sendNotification(ComplaintSystemNotification complaintSystemNotification) {
        log.info("Sent complaint management notification: {}", complaintSystemNotification);

        rabbitTemplate.convertAndSend(ComplaintManagementNotificationMessageQIF.EXCHANGE_NAME,
                ComplaintManagementNotificationMessageQIF.ROUTING_KEY_COMPLAINTS,
                complaintSystemNotification);
    }
}
