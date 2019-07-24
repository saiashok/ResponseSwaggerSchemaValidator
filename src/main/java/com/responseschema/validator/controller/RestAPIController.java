package com.responseschema.validator.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.responseschema.validator.model.ErrorObject;
import com.responseschema.validator.model.JsonRequestPayload;
import com.responseschema.validator.model.SwaggerDefinitionModel;
import com.responseschema.validator.model.SwaggerPopulateResponse;
import com.responseschema.validator.model.ToolRequest;
import com.responseschema.validator.service.InvokeValidations;
import com.responseschema.validator.service.ServiceRequestTransformation;
import com.responseschema.validator.service.SwaggerSpecificationParser;

/**
 * @author af98826
 *
 */
@RestController
@RequestMapping(value="/schemavalidator")
public class RestAPIController {

	
	private ServiceRequestTransformation jSV = new ServiceRequestTransformation();

	@ModelAttribute("jsonReq")
	public JsonRequestPayload construct() {

		return new JsonRequestPayload();
	}

	@Autowired
	private InvokeValidations invokeValidations;
	private SwaggerPopulateResponse sPR;
	private List<SwaggerDefinitionModel> lSD;
	private String devUrl;
	private SwaggerDefinitionModel sObj;
	private List<String> operationNames = new ArrayList<>();
	
	
	@RequestMapping(value="/test", method= RequestMethod.GET)
	public String test(){
		return "success";
	}

	@RequestMapping(value = "/swagger", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String populateUIForSwagger(@RequestBody JsonRequestPayload jsonReq, Model m, BindingResult result)
			throws IOException, JSONException {
		ObjectMapper mapper = new ObjectMapper();
		jSV = new ServiceRequestTransformation(jsonReq);

		List<ToolRequest> lVR = jSV.convertJsonToObject();
		SwaggerSpecificationParser sPU = new SwaggerSpecificationParser();

		ToolRequest v = lVR.get(0);
		this.devUrl = jsonReq.getSwaggerFileLocation();

		JSONObject json = invokeValidations.getSwaggerJSON(v);
		
		if(!json.has("error")){
		this.sPR = new SwaggerPopulateResponse();

		lSD = new ArrayList<SwaggerDefinitionModel>(sPU.getSwaggerDetails(json));

		this.sPR.setResponse(lSD);
		System.out.println(this.sPR);
	
		// Object to JSON in String
		String jsonInString = mapper.writeValueAsString(sPR);

		for (SwaggerDefinitionModel s : lSD) {
			System.out.println("Operation Name added.." + s.getOperationName());
			operationNames.add(s.getOperationName());
		}

		if (jsonReq.getOperationName() != null || jsonReq.getSwaggerFileLocation() != null) {
			for (SwaggerDefinitionModel s : lSD) {
				if (s.getOperationName().equals(jsonReq.getOperationName())) {
					this.sObj = s;
				}
			}
		} else {
			this.sObj = lSD.get(0);
		}

		return jsonInString;
		}else{
			
			ErrorObject err = new ErrorObject();
			err.setErrorCode("400");
			err.setErrorMessage("Bad Request");
			err.setErrorDescription(json.getString("error"));
			String jsonInString = mapper.writeValueAsString(err);
			return jsonInString;
		}
		

	}
	
	@RequestMapping(value = "/validationreport", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String getValidationReportJson(@RequestBody JsonRequestPayload jsonReq, Model m, BindingResult result)
			throws IOException, NumberFormatException, JSONException {
		jSV = new ServiceRequestTransformation(jsonReq);
		// Object to JSON in String	
		List<ToolRequest> lVR = jSV.convertJsonToObject();
			return  invokeValidations.generateValidationReportJSON(lVR.get(0));

	}

}
