package com.responseschema.validator.model;

import com.google.gson.annotations.SerializedName;

public class TypeMismatchFields {

	@SerializedName("swaggerPath")
	private String swaggerPath;
	@SerializedName("expectedVariableType")
	private String expectedVariableType;
	public String getExpectedVariableType() {
		return expectedVariableType;
	}
	public void setExpectedVariableType(String expectedVariableType) {
		this.expectedVariableType = expectedVariableType;
	}
	@SerializedName("responsePath")
	private String responsePath;
	@SerializedName("actualVariableType")
	private String actualVariableType;
	
	public String getSwaggerPath() {
		return swaggerPath;
	}
	public void setSwaggerPath(String swaggerPath) {
		this.swaggerPath = swaggerPath;
	}
	
	public String getResponsePath() {
		return responsePath;
	}
	public void setResponsePath(String responsePath) {
		this.responsePath = responsePath;
	}
	public String getActualVariableType() {
		return actualVariableType;
	}
	public void setActualVariableType(String actualVariableType) {
		this.actualVariableType = actualVariableType;
	}
	
	
	
}
