package com.responseschema.validator.service;

import java.util.ArrayList;
import java.util.List;

import com.responseschema.validator.model.JsonRequestPayload;
import com.responseschema.validator.model.ToolRequest;

public class ServiceRequestTransformation {

	JsonRequestPayload jsonRequestPL;
	
	public ServiceRequestTransformation() {
		
	}
	
	public ServiceRequestTransformation(JsonRequestPayload jsonRequest) {
		this.jsonRequestPL = jsonRequest;
	}
	
       public List<ToolRequest> convertJsonToObject(){
		
		JsonRequestPayload jsonRequestPL= this.jsonRequestPL;
		
		List<ToolRequest> lis = new ArrayList<ToolRequest>();
		
		ToolRequest v = new ToolRequest();

		v.setOperationName(jsonRequestPL.getOperationName());
		v.setOperationType(jsonRequestPL.getOperationType());
		v.setApiResponse(jsonRequestPL.getResponse());
		v.setSwagger(jsonRequestPL.getSwaggerFileLocation());
		v.setStatusCode(jsonRequestPL.getStatusCode());
		
		lis.add(v);
		
		return lis;
		
	}
}
