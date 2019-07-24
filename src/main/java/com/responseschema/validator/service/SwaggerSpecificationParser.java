package com.responseschema.validator.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.responseschema.validator.model.SwaggerDefinitionModel;

import io.swagger.models.ArrayModel;
import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.refs.RefFormat;
import io.swagger.parser.SwaggerParser;

public class SwaggerSpecificationParser {

	private Map<String, Object> listOfElementsRequired = new HashMap<String, Object>();
	private Map<String, Object> listOfElementsOptional = new HashMap<String, Object>();
	private Swagger swag = null;
	private Map<String, Object> all = new HashMap<String, Object>();
	private String responseStatusCode;
	private String statusCode;
	private String statusDesc;

	public void runNewSwagger(JSONObject swaggerJson, String operationType, String operationName) throws IOException {

		System.out.println("Swagger JSON -"+ swaggerJson);
		System.out.println("Operation Type- "+ operationType);
		System.out.println("Operation Name- "+ operationName);
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(swaggerJson.toString());

		Swagger swag = new SwaggerParser().read(jsonNode);

		for (Map.Entry<String, Path> entry : swag.getPaths().entrySet()) {
			printOperations(swag, entry.getValue().getOperationMap(), operationType, operationName);
		}

	}

	private void printOperations(Swagger swagger, Map<HttpMethod, Operation> operationMap, String operationType,
			String operationName) {

		this.swag = swagger;

		for (Map.Entry<HttpMethod, Operation> op : operationMap.entrySet()) {

			if (op.getKey().toString().equals(operationType)) {
				System.out.println(op.getKey() + "-" + op.getValue().getOperationId());
				if (op.getValue().getOperationId().equals(operationName)) {
					System.out.println("parameters");
					for (Parameter p : op.getValue().getParameters()) {
						System.out.println(p.getName());
					}

					printResponses(swagger, op.getValue().getResponses());
				} else {
//					System.out.println(op.getKey().toString());
//					System.out.println("Check your operation name...");
				}

			}
		}
	}

	@SuppressWarnings("deprecation")
	private void printResponses(Swagger swagger, Map<String, Response> responseMap) {

		System.out.println("In print responses");
		for (Map.Entry<String, Response> response : responseMap.entrySet()) {
			
			
			System.out.println("Response status code- "+ response.getKey().toString().equals(this.responseStatusCode+""));
			this.statusCode = response.getKey();

			if (response.getKey().toString().equals(this.responseStatusCode)) {

				System.out.println("Response status code- "+ response.getKey() +" "+this.responseStatusCode);
				
				if (response.getValue().getSchema() instanceof RefProperty) {
					
					String path = "$";
					System.out.println("RefProp-"+path);
					RefProperty rp = (RefProperty) response.getValue().getSchema();
					printReferences(swagger, rp, path, "");
				}

				if (response.getValue().getSchema() instanceof ArrayProperty) {

					ArrayProperty ap = (ArrayProperty) response.getValue().getSchema();

					if (ap.getItems() instanceof RefProperty) {
						String path = "$";
						RefProperty rp = (RefProperty) ap.getItems();
						printReferences(swagger, rp, path, "");

					} else if (ap.getItems() instanceof ObjectProperty) {
						String path = "$";
						ObjectProperty rp = (ObjectProperty) ap.getItems();
						path = path + "(O)";
						parserOfObjectProperty(rp, path);
					}
				}

				if (response.getValue().getSchema() instanceof ObjectProperty) {
					String path = "$";
					ObjectProperty rp = (ObjectProperty) response.getValue().getSchema();
					parserOfObjectProperty(rp, path);
				}

			}
		}

	}

	private String parserOfObjectProperty(ObjectProperty obj, String path) {

		Map<String, Property> innerProperties = ((ObjectProperty) obj).getProperties();
		String path2 = "";

		for (Map.Entry<String, Property> entry : innerProperties.entrySet()) {
			path2 = path + "." + entry.getKey();

			if (entry.getValue() instanceof ArrayProperty) {
				path2 = path2 + "[A]";
				ArrayProperty ar = (ArrayProperty) entry.getValue();
			} else if (entry.getValue() instanceof ObjectProperty) {
				path2 = path2 + "(O)";
				ObjectProperty innerObj = (ObjectProperty) entry.getValue();
			} else if (entry.getValue() instanceof RefProperty) {
				path2 = path2 + "(O)";
				RefProperty rp = (RefProperty) entry.getValue();
				printReferences(swag, rp, path2, "");
			} else {

				if (entry.getValue().getRequired()) {
					if (entry.getValue().getType().equals("string")) {

						if (entry.getValue().getFormat() != null && !entry.getValue().getFormat().equals("string")) {
							all.put(path2, entry.getValue().getFormat());
							listOfElementsRequired.put(path2, entry.getValue().getFormat());
						} else {
							all.put(path2, entry.getValue().getType());
							listOfElementsRequired.put(path2, entry.getValue().getType());
						}
					} else {
						all.put(path2, entry.getValue().getType());
						listOfElementsRequired.put(path2, entry.getValue().getType());
					}
				} else {
					if (entry.getValue().getType().equals("string")) {

						if (entry.getValue().getFormat() != null && !entry.getValue().getFormat().equals("string")) {
							all.put(path2, entry.getValue().getFormat());
							listOfElementsOptional.put(path2, entry.getValue().getFormat());
						} else {
							all.put(path2, entry.getValue().getType());
							listOfElementsOptional.put(path2, entry.getValue().getType());
						}
					} else {
						all.put(path2, entry.getValue().getType());
						listOfElementsOptional.put(path2, entry.getValue().getType());
					}
				}

			}
		}
		return path2;
	}

	public String parserofArrayProperty(ArrayProperty ap, String path) {

		if (ap.getItems() instanceof ObjectProperty) {
			path = path + "(O)";
			ObjectProperty obj = (ObjectProperty) ap.getItems();

			parserOfObjectProperty(obj, path);
		}
		if (ap.getItems() instanceof ArrayProperty) {
			path = path + "[A]";
			ArrayProperty arrayIn = (ArrayProperty) ap.getItems();
		}

		if (ap.getItems() instanceof RefProperty) {
			path = path + "(O)";
			RefProperty rp = (RefProperty) ap.getItems();
			printReferences(swag, rp, path, "");
		}

		return path;
	}

	private String printReferences(Swagger swagger, RefProperty rp, String path, String required) {

		if (rp.getRefFormat().equals(RefFormat.INTERNAL) && swagger.getDefinitions().containsKey(rp.getSimpleRef())) {

			Model m = swagger.getDefinitions().get(rp.getSimpleRef());

			if (m.getProperties() != null) {

				for (Map.Entry<String, Property> propertyEntry : m.getProperties().entrySet()) {
					String path2 = path + "." + propertyEntry.getKey();

					if (propertyEntry.getValue() instanceof RefProperty) {
						path2 = path2 + "(O)";

						if (propertyEntry.getValue().getRequired()) {
							printReferences(swagger, (RefProperty) propertyEntry.getValue(), path2, "yes");
						} else {
							propertyEntry.getValue().getRequired();
							printReferences(swagger, (RefProperty) propertyEntry.getValue(), path2, "no");
						}

					} else if (propertyEntry.getValue() instanceof ObjectProperty) {

						path2 = path2 + "(O)";
						ObjectProperty objZ = (ObjectProperty) propertyEntry.getValue();
						parserOfObjectProperty(objZ, path2);
					} else if (propertyEntry.getValue() instanceof ArrayProperty) {
						path2 = path2 + "[A]";
						ArrayProperty arrModelPrp = (ArrayProperty) propertyEntry.getValue();

						parserofArrayProperty(arrModelPrp, path2);
					} else {

						if (required.equals("yes")) {
							all.put(path2, propertyEntry.getValue().getType());
							listOfElementsRequired.put(path2, true);
						} else {
							if (propertyEntry.getValue().getRequired()) {
								
								if (propertyEntry.getValue().getType().equals("string")) {

									if (propertyEntry.getValue().getFormat() != null
											&& !propertyEntry.getValue().getFormat().equals("string")) {
										all.put(path2, propertyEntry.getValue().getFormat());
										listOfElementsRequired.put(path2, propertyEntry.getValue().getFormat());
									} else {
										all.put(path2, propertyEntry.getValue().getType());
										listOfElementsRequired.put(path2, propertyEntry.getValue().getType());
									}
								} else {
									all.put(path2, propertyEntry.getValue().getType());
									listOfElementsRequired.put(path2, propertyEntry.getValue().getType());
								}
							} else {
								
									if (propertyEntry.getValue().getType().equals("string")) {

										if (propertyEntry.getValue().getFormat() != null
												&& !propertyEntry.getValue().getFormat().equals("string")) {
											all.put(path2, propertyEntry.getValue().getFormat());
											listOfElementsOptional.put(path2, propertyEntry.getValue().getFormat());
										} else {
											all.put(path2, propertyEntry.getValue().getType());
											listOfElementsOptional.put(path2, propertyEntry.getValue().getType());
										}
									} else {
										all.put(path2, propertyEntry.getValue().getType());
										listOfElementsOptional.put(path2, propertyEntry.getValue().getType());
									}
							}

						}
						
						if(propertyEntry.getValue() instanceof ArrayProperty) {
							path2 = path2+"[A]";
							
							ArrayProperty arP = (ArrayProperty) propertyEntry.getValue();
							parserofArrayProperty(arP, path2);
							
							ArrayProperty array = (ArrayProperty)propertyEntry.getValue();
							
							if(array.getItems() instanceof RefProperty) {
								RefProperty arrayModelRefProp = (RefProperty)array.getItems();
								printReferences(swagger, arrayModelRefProp, path2, "");
							}else if(array.getItems() instanceof ArrayProperty) {
								
								ArrayProperty arrModelPrp = (ArrayProperty)array.getItems();
								parserofArrayProperty(arrModelPrp, path2);
								
							}else {
								
								if (propertyEntry.getValue().getRequired()) {
									if (propertyEntry.getValue().getType().equals("string")) {

										if (propertyEntry.getValue().getFormat() != null
												&& !propertyEntry.getValue().getFormat().equals("string")) {
											all.put(path2, propertyEntry.getValue().getFormat());
											listOfElementsRequired.put(path2, propertyEntry.getValue().getFormat());
										} else {
											all.put(path2, propertyEntry.getValue().getType());
											listOfElementsRequired.put(path2, propertyEntry.getValue().getType());
										}
									} else {
										all.put(path2, propertyEntry.getValue().getType());
										listOfElementsRequired.put(path2, propertyEntry.getValue().getType());
									}
								} else {
									
										if (propertyEntry.getValue().getType().equals("string")) {

											if (propertyEntry.getValue().getFormat() != null
													&& !propertyEntry.getValue().getFormat().equals("string")) {
												all.put(path2, propertyEntry.getValue().getFormat());
												listOfElementsOptional.put(path2, propertyEntry.getValue().getFormat());
											} else {
												all.put(path2, propertyEntry.getValue().getType());
												listOfElementsOptional.put(path2, propertyEntry.getValue().getType());
											}
										} else {
											all.put(path2, propertyEntry.getValue().getType());
											listOfElementsOptional.put(path2, propertyEntry.getValue().getType());
										}
								}
							}
						}
					}
				}
			}else {
				
				if(m instanceof ArrayModel) {
					ArrayModel arryM = (ArrayModel)m;
					
					String path2 = path+"[A]";
					
					if(arryM.getItems() instanceof RefProperty) {
						Model m1 = swagger.getDefinitions().get((RefProperty)arryM.getItems().getExample());
						
						printReferences(swagger, (RefProperty)arryM.getItems(), path2, "");
					}else if (arryM.getItems() instanceof ObjectProperty) {
						path2 = path2+"(O)";
						ObjectProperty objZ = (ObjectProperty)arryM.getItems();
						
						parserOfObjectProperty(objZ, path2);
						
					}
				}
			}
			
		}
		return path;

	}

	public Map<String, Object> getListOfElementsRequired() {
		return listOfElementsRequired;
	}

	public void setListOfElementsRequired(Map<String, Object> listOfElementsRequired) {
		this.listOfElementsRequired = listOfElementsRequired;
	}

	public Map<String, Object> getListOfElementsOptional() {
		return listOfElementsOptional;
	}

	public void setListOfElementsOptional(Map<String, Object> listOfElementsOptional) {
		this.listOfElementsOptional = listOfElementsOptional;
	}

	public Map<String, Object> getListOfAll() {
		return all;
	}

	public void setAll(Map<String, Object> all) {
		this.all = all;
	}

	public String getResponseStatusCode() {
		return responseStatusCode;
	}

	public void setResponseStatusCode(String responseStatusCode) {
		this.responseStatusCode = responseStatusCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	
	public Set<SwaggerDefinitionModel> getSwaggerDetails(JSONObject swaggerJson) throws IOException{
		
		String apiResponse = "";
		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println("Devportal" + swaggerJson.toString());
		JsonNode jsonNode = objectMapper.readTree(swaggerJson.toString());
		Swagger swag = new SwaggerParser().read(jsonNode);
		
		System.out.println(swag.getBasePath());
		
		Set<SwaggerDefinitionModel> lis = new HashSet<SwaggerDefinitionModel>();
		

		for (Map.Entry<String, Path> entry : swag.getPaths().entrySet()) {

			
		/*	System.out.println(entry.getKey());*/

			/*
			 * if(entry.getKey().equals("/{hcid}/claims")){
			 * printOperations(swag, entry.getValue().getOperationMap()); }
			 */
			
			
			for (Map.Entry<HttpMethod, Operation> op : entry.getValue().getOperationMap().entrySet()) {
				SwaggerDefinitionModel s = new SwaggerDefinitionModel();
				
			System.out.println("OperationType :"+ op.getKey().toString());
				apiResponse = apiResponse+ ""+ op.getKey().toString();
				
				s.setOperationType(op.getKey().toString());
				System.out.println("; OperationName :"+ op.getValue().getOperationId());
				apiResponse = apiResponse+ "-"+ op.getValue().getOperationId();
				s.setOperationName(op.getValue().getOperationId());
			//	System.out.println("; StatusCode : "+  op.getValue().getResponses());
				
				List<String> statusC = new ArrayList<String>();
				
				apiResponse = apiResponse+ "-"+  op.getValue().getResponses().keySet()+"\n";
				
				
				for (Map.Entry<String, Response> response : op.getValue().getResponses().entrySet()) {
			//		System.out.println(response.getKey() + ": " + response.getValue().getDescription());
					
					statusC.add(response.getKey());
				
				}
				
			//	System.out.println("statusC"+statusC);
				
				s.setStatusCodes(statusC);
			
				lis.add(s);
				
				
			}
		}
		
		
		
		
		return lis;
		
	}
	
}
