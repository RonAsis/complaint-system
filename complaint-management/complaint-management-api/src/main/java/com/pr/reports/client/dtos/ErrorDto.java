package com.pr.reports.client.dtos;

import lombok.Data;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.List;

@Data
public class ErrorDto<T> {

    private String errorCode;
    private List<T> errors;
    private long timestamp;
    private String path;

    public static class ErrorDTOBuilder<T> {

        private ErrorDto<T> errorDTO;

        public ErrorDTOBuilder() {
            errorDTO = new ErrorDto<>();
        }

        public ErrorDTOBuilder<T> errorCode(String errorCode) {
            errorDTO.setErrorCode(errorCode);
            return this;
        }

        public ErrorDTOBuilder<T> timestamp(long timestamp) {
            errorDTO.setTimestamp(timestamp);
            return this;
        }

        public ErrorDTOBuilder<T> defTimestamp() {
            errorDTO.setTimestamp(Calendar.getInstance().getTimeInMillis());
            return this;
        }

        public ErrorDTOBuilder<T> errors(List<T> errors) {
            errorDTO.setErrors(errors);
            return this;
        }

        public ErrorDTOBuilder<T> path(String path) {
            errorDTO.setPath(path);
            return this;
        }

        public ErrorDTOBuilder<T> path(WebRequest webRequest) {
            errorDTO.setPath(webRequest.getDescription(true));
            return this;
        }

        public ErrorDto<T> build() {
            return errorDTO;
        }
    }
}
