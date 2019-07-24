package com.responseschema.validator.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.responseschema.validator.model.ToolRequest;
import com.responseschema.validator.model.TypeMismatchFields;
import com.responseschema.validator.model.ValidationReportJson;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Component
public class InvokeValidations {
	
	
	private WorkingJSONResponseAndSwagger jsonResponseAndSwagger;
	
	@Autowired
	public InvokeValidations(WorkingJSONResponseAndSwagger jsonResponse_SwaggerValidator) {
		this.jsonResponseAndSwagger= jsonResponse_SwaggerValidator;
	}
	
	@Autowired
	ValidationReportJson vR;
	
	
	private JSONObject swaggerJson;
	
	public JSONObject getSwaggerJSON(ToolRequest v) {
		
		Header h1 = new Header("Content-Type", "application/json");
		
		List<Header> list = new ArrayList<Header>();
		list.add(h1);
		
		Headers headers = new Headers(list);
		
		RequestSpecification httpRequest = RestAssured.given().relaxedHTTPSValidation().headers(headers);
		
		
		if (isValid(v.getSwagger())) {

			try{
			Response response = httpRequest.get(v.getSwagger());
			String responseBody = response.getBody().asString();
			this.swaggerJson = new JSONObject(responseBody);
			}catch(Exception e){
				this.swaggerJson = new JSONObject();
				try {
					this.swaggerJson.put("error", "Cannot connect or parse the JSON from the URL...");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		} else if (isJsonValid(v.getSwagger())){
			try {
				this.swaggerJson = new JSONObject(v.getSwagger());
			} catch (JSONException e) {

				this.swaggerJson = new JSONObject();
				try {
						this.swaggerJson.put("error", "Swagger JSON is incorrect");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
			}

		}else{
			this.swaggerJson = new JSONObject();
			try {
				this.swaggerJson.put("error", "The given URL or JSON is incorrect");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}

		return this.swaggerJson;
		
	}
	
	public String generateValidationReportJSON(ToolRequest v) throws NumberFormatException, IOException, JSONException {

		String responseBody = v.getApiResponse().toString();
		this.getSwaggerJSON(v);
		
		if (!responseBody.isEmpty()) {

			
			jsonResponseAndSwagger.getListOfDifferences(responseBody, swaggerJson, v.getOperationType(), v.getOperationName(),
					v.getStatusCode());
			
			Set<String> emptySet = new HashSet<String>();
			if(jsonResponseAndSwagger.missingMandatoryFields().size()!=0) {
			vR.setMissingMandatoryFields(jsonResponseAndSwagger.missingMandatoryFields());// Fields// missing
			}else {
				vR.setMissingMandatoryFields(emptySet);
			}
			
			if(jsonResponseAndSwagger.virtualListNotMatchedNew().size()!=0) {
				vR.setResponseListNotMatched(jsonResponseAndSwagger.virtualListNotMatchedNew());
				}else {
					vR.setResponseListNotMatched(new ArrayList(emptySet));
				}
			
			
			if(jsonResponseAndSwagger.swaggerListNotMatchedNew().size()!=0) {
				vR.setSwaggerListNotMatched(jsonResponseAndSwagger.swaggerListNotMatchedNew());
				}else {
					vR.setSwaggerListNotMatched(new ArrayList(emptySet));
				}
			
			if(jsonResponseAndSwagger.swaggerMatchedList().size()!=0) {
				vR.setMatchedFields(jsonResponseAndSwagger.swaggerMatchedList());
				}else {
					vR.setMatchedFields(new ArrayList(emptySet));
				}
			
			List<TypeMismatchFields> emptyTypeList = new ArrayList<>();
			if(jsonResponseAndSwagger.getListOfTypeMis().size()!=0) {
				vR.setTypeMismatches(jsonResponseAndSwagger.getListOfTypeMis());
				}else {
					vR.setTypeMismatches(emptyTypeList);
				}
																							
			
			ObjectMapper mapper = new ObjectMapper();

			// Object to JSON in String
			String jsonInString = mapper.writeValueAsString(vR);
			
			System.out.println(jsonInString);
			return jsonInString;
		} else {
			return null;
		}

	}
	public static boolean isValid(String url) {
		try {
			
			new URL(url).toURI();
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isJsonValid(String jsonString) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonString);
			return true;
		}catch(Exception e) {
			return false;
		}
	}

}
