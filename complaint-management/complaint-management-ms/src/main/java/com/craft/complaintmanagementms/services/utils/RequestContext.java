package com.craft.complaintmanagementms.services.utils;

import org.slf4j.MDC;

public class RequestContext {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    private String id;

    public static RequestContext getContext() {
        RequestContext result = CONTEXT.get();

        if (result == null) {
            result = new RequestContext();
            CONTEXT.set(result);
        }

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        setLogId();
    }

    /**
     * So we can also get the value in log via %X{flowId} or %X{reqId}
     */
    private void setLogId() {
        MDC.put("flowId", id);
        MDC.put("reqId", id);
    }
}
