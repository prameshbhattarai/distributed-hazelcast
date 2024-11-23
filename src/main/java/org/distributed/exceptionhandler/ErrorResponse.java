package org.distributed.exceptionhandler;

public record ErrorResponse(int status, String reason, String description) {
}
