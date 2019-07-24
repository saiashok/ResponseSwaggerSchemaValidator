package com.responseschema.validator.model;

import java.util.List;

public class SwaggerDefinitionModel {

	private String operationName;
	private String operationType;
	private List<String> statusCodes;
	
	
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
	public List<String> getStatusCodes() {
		return statusCodes;
	}
	public void setStatusCodes(List<String> statusCodes) {
		this.statusCodes = statusCodes;
	}
	
	
}
