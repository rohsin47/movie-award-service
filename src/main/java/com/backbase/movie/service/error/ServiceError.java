package com.backbase.movie.service.error;

import javax.ws.rs.core.Response;

public final class ServiceError extends RuntimeException {
    private final int statusCode;
    private final String addOnmessage;

    ServiceError(Response.StatusType statusType, String addOnmessage) {
        super(addOnmessage);
        this.statusCode = statusType != null ? statusType.getStatusCode() : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        this.addOnmessage = addOnmessage;
    }

    public static ServiceError getInstance(Response.StatusType statusType, String addOnmessage) {
        return new ServiceError(statusType, addOnmessage);
    }

    public static ServiceError getInstance(Response.StatusType statusType, String msgFormat, Object... objects) {
        return new ServiceError(statusType, String.format(msgFormat, objects));
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getAddOnmessage() {
        return this.addOnmessage;
    }
}
