package com.responseschema.validator.model;

public class JsonRequestPayload {

	private String swaggerFileLocation;
	private String operationName;
	private String operationType;
	private String statusCode;
	private String response;
	
	public String getSwaggerFileLocation() {
		return swaggerFileLocation;
	}
	public void setSwaggerFileLocation(String swaggerFileLocation) {
		this.swaggerFileLocation = swaggerFileLocation;
	}
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public String getOperationType() {
		return operationType;
	}
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	
}
