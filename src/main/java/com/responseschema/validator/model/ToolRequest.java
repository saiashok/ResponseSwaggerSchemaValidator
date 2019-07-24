package com.responseschema.validator.model;

public class ToolRequest {
	
	private String operationName;
	private String swagger;
	private String operationType;
	private String statusCode;
	private String apiResponse;
	
	
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public String getSwagger() {
		return swagger;
	}
	public void setSwagger(String swagger) {
		this.swagger = swagger;
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
	public String getApiResponse() {
		return apiResponse;
	}
	public void setApiResponse(String apiResponse) {
		this.apiResponse = apiResponse;
	}
	
	

}
