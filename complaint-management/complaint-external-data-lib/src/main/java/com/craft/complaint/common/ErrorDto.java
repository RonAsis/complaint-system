package com.craft.complaint.common;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.List;

@Data
@Builder
public class ErrorDto {

    private HttpStatus errorCode;
    private String message;
    private long timestamp;
    private String path;

}
