package sn.afribnpl.clientservice.config;


import org.springframework.http.HttpStatus;

import java.util.Date;


public class ApiResponseException {

    private HttpStatus status;
    private String message;
    private Date timestamp;

    public ApiResponseException() {
    }

    public ApiResponseException(HttpStatus status, String message, Date timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
