package com.responseschema.validator.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets.SetView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.responseschema.validator.model.TypeMismatchFields;

@Component
public class WorkingJSONResponseAndSwagger {

	private Map<String, Object> listOfElements = new HashMap<String, Object>();
	List<String> newVirtualList = null;
	List<String> newSwaggerList = null;
	List<String> responseDifferences = new ArrayList<String>();
	List<String> responseDifferencesnew = new ArrayList<String>();
	private Map<String, Object> listOfElementsRequired = new HashMap<String, Object>();
	Set<String> missingMandatory= null;
	List<TypeMismatchFields> listOfTypeMis = new ArrayList<TypeMismatchFields>();
	


	private String swaggerStatusCode; 
	

	public void getJsonParser(JsonElement jelement) {
		StringBuilder sb = new StringBuilder();
		if (jelement.isJsonArray()) {

			JsonArray jA = jelement.getAsJsonArray();

			for (int i = 0; i < jA.size(); i++) {
				JsonElement element = jA.get(i);
				getJsonParser(element);
			}

		} else if (jelement.isJsonPrimitive()) {

			/* System.out.println(jelement.getAsString()); */
		}

		JsonObject jobject = jelement.getAsJsonObject();

		Set<Entry<String, JsonElement>> set = jobject.entrySet();

		for (Entry<String, JsonElement> s : set) {
			/* System.out.println(s); */
			sb.append(s);

			sb.append('\n');
			getJsonParser(s.getValue());

		}

	}

	public JSONObject readJSONFile(String path) throws IOException, JSONException {

		BufferedReader reader = null;
		StringBuilder content = null;
		String result = null;

		reader = new BufferedReader(new FileReader(path));

		String line = null;
		content = new StringBuilder();

		while ((line = reader.readLine()) != null) {
			content.append(line);
		}
		reader.close();
		result = content.toString();

		Gson g = new Gson();
		try {
			g.toJson(result, Object.class);
			// System.out.println("valid json");

		} catch (Exception e) {
			// System.out.println("Not a valid json");
		}

		new JsonParser().parse(result);

		JSONObject jsonObj = new JSONObject(result);

		return jsonObj;
	}

	public SetView<String> getListOfDifferences(String apiResponse, JSONObject devPortalSwaggerJSON,	String operationType, String operationName, String apistatusCode) throws IOException, JSONException {
		
		responseDifferencesnew.clear();
		Set<String> newvirtual = new HashSet<String>();
		Set<String> newSwagger = new HashSet<String>();
		ParseSampleResponse j1 = new ParseSampleResponse(apiResponse);

		SwaggerSpecificationParser newSwag = new SwaggerSpecificationParser();
		newSwag.setResponseStatusCode(apistatusCode);
		
		
		this.swaggerStatusCode = newSwag.getStatusCode();
		
		newSwag.runNewSwagger(devPortalSwaggerJSON, operationType, operationName);

		newSwag.getListOfAll();
	
		
		listOfElementsRequired = newSwag.getListOfElementsRequired();

		listOfElements = j1.getKeyValue();
		newSwag.getListOfAll();

	

		for (Entry<?, ?> s : newSwag.getListOfAll().entrySet()) {
			// System.out.println(s.getKey()+":"+s.getValue());
			newSwagger.add(s.getKey().toString());

		}

		for (Entry<?, ?> s : j1.getKeyValue().entrySet()) {
			/* System.out.println(s.getKey()+":"+s.getValue()); */

			newvirtual.add(s.getKey().toString());
		}

		for (Entry<?, ?> s1 : newSwag.getListOfAll().entrySet()) {

			for (Entry<?, ?> s : j1.getKeyValue().entrySet()) {
				TypeMismatchFields typeMism = new TypeMismatchFields();
				if (s1.getKey().toString().equals(s.getKey().toString())) {

					if (s1.getValue().equals("string") && (s.getValue() instanceof String || s.getValue().toString().equals("")) ) {
			} else if ((s1.getValue().equals("number") || s1.getValue().equals("integer")) && s.getValue() instanceof Number) {
					} else if (s1.getValue().equals("boolean") && s.getValue() instanceof Boolean) {
					} else if (s1.getValue().equals("date") && !s.getValue().toString().equals("")){
						
						if(!isThisDateValid(s.getValue().toString(), "yyyy-MM-dd")){
							
							typeMism.setSwaggerPath(s1.getKey().toString() + " - "+ s1.getValue());
							typeMism.setExpectedVariableType("yyyy-MM-dd");
							typeMism.setResponsePath(s.getKey()+ " - "+ s.getValue());
							typeMism.setActualVariableType(s.getValue()+"");
							listOfTypeMis.add(typeMism);
						}
						
						
						
					}else if( s1.getValue().equals("date-time") &&  !s.getValue().toString().equals("")){
						
						if(!isThisDateValid(s.getValue().toString(), "yyyy-MM-dd'T'HH:mm:ss")){					
							typeMism.setSwaggerPath(s1.getKey().toString() + " - "+ s1.getValue());
							typeMism.setExpectedVariableType("yyyy-MM-dd'T'HH:mm:ss");
							typeMism.setResponsePath(s.getKey()+ " - "+ s.getValue());
							typeMism.setActualVariableType(s.getValue()+"");
							listOfTypeMis.add(typeMism);
						}
					}else if((s1.getValue().equals("date-time") || (s1.getValue().equals("date"))) && s.getValue().toString().equals("") ){
						
					}
										
					else {
						typeMism.setSwaggerPath("path: "+s1.getKey().toString() + "<"+ s1.getValue()+ ">");
						typeMism.setExpectedVariableType(""+s1.getValue());
						typeMism.setResponsePath("path: "+s.getKey()+ "<"+ s.getValue()+ ">");
						typeMism.setActualVariableType(""+s.getValue().getClass().getSimpleName());
						
						listOfTypeMis.add(typeMism);
				}
				}
				
				

			}

		}

		
		List<String> virtualForNew = new ArrayList<String>(newvirtual);

		setListOfTypeMis(listOfTypeMis);

		newVirtualList = virtualForNew;
		newSwaggerList = new ArrayList<String>(newSwagger);

		/*
		 * responseDifferences = new ArrayList<String>(); responseDifferencesnew
		 * = new ArrayList<String>();
		 */

		for (int i = 0; i < virtualForNew.size(); i++) {

			for (int k = 0; k < newSwagger.size(); k++) {

				if ((newVirtualList.get(i).toString().equals(newSwaggerList.get(k).toString()))) {

					responseDifferencesnew.add(newVirtualList.get(i).toString());
				}
			}
		}
		
		System.out.println("Swagger list "+newSwaggerList);

		

		@SuppressWarnings("unchecked")
		SetView<String> difference = com.google.common.collect.Sets.symmetricDifference(new HashSet<String>(newVirtualList),new HashSet<String>(newSwaggerList));

		newVirtualList.removeAll(responseDifferencesnew);
		newSwaggerList.removeAll(responseDifferencesnew);


		System.out.println("Virtual New List not matched -" + newVirtualList);
		System.out.println("Swagger New List not matched -" + newSwaggerList);

		List<String> mandFileds = allMandatoryFields();
		 missingMandatory= new HashSet<String>();
		 
		for(int i=0; i<newSwaggerList.size(); i++){
			
			for(int k=0; k<mandFileds.size(); k++){
				
				if( newSwaggerList.get(i).equals(mandFileds.get(k))){
					System.out.println("missing mandatory fields"+newSwaggerList.get(i));
					
					missingMandatory.add(newSwaggerList.get(i));
					
				}
			}
		}
		return difference;
	}

	public Map<String, Object> getAPIResponse() {
		return listOfElements;

	}



	public List<String> virtualListNotMatchedNew() {

		return this.newVirtualList;
	}

	public List<String> swaggerListNotMatchedNew() {

		return this.newSwaggerList;
	}

	public List<String> swaggerMatchedList() {
		return this.responseDifferencesnew;
	}
	
	
	public Set<String> missingMandatoryFields(){
		
		return this.missingMandatory;
		
	}
	
	public List<String> allMandatoryFields(){
		List<String> mand = new ArrayList<String>();
		
		for (Entry<?, ?> s : listOfElementsRequired.entrySet()) {
			// System.out.println(s.getKey()+":"+s.getValue());
			mand.add(s.getKey().toString());

		}
		
		return mand;
	}

	public String getSwaggerStatusCode() {
		return swaggerStatusCode;
	}

	
	public boolean isThisDateValid(String dateToValidate, String dateFromat){
		
		if(dateToValidate == null){
			return false;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);		
		try {
			
			//if not valid, it will throw ParseException
			Date date = sdf.parse(dateToValidate);
			System.out.println(date);
		
		} catch (ParseException e) {
			
			System.out.println("Invalid dateFormat"+e.getMessage());
			return false;
		}
		
		return true;
	}
		
	
	public List<TypeMismatchFields> getListOfTypeMis() {
		return listOfTypeMis;
	}

	private void setListOfTypeMis(List<TypeMismatchFields> listOfTypeMis) {
		this.listOfTypeMis = listOfTypeMis;
	}

}
