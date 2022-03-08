package com.craft.complaint.management.api.notfication;

public interface ComplaintManagementNotificationMessageQIF {

    public static final String CHANGES_PREFIX = "changes.";
    public static final String COMPLAINTS_POSTFIX = "complaints";


    //////////////////// Exchange name to use on client side ///////////////////////////////

    public final String EXCHANGE_NAME = "complaint-management";

    //////////// Routing keys combination to use on clients side ///////////////////////////

    public static final String ROUTING_KEY_COMPLAINTS = CHANGES_PREFIX + COMPLAINTS_POSTFIX;

    //////////////////////////////////////receives/////////////////////////////////////////

    void receive(ComplaintSystemNotification complaintSystemNotification);

}
