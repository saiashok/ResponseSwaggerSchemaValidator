
package com.responseschema.validator.model;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.gson.annotations.SerializedName;

@Component
public class ValidationReportJson {

	@SerializedName("swaggerListNotMatched")
	List<String> swaggerListNotMatched;
	
	@SerializedName("responseListNotMatched")
	List<String> responseListNotMatched;
	
	@SerializedName("missingMandatoryFields")
	Set<String> missingMandatoryFields;
	
	@SerializedName("matchedFields")
	List<String> matchedFields;
	
	@SerializedName("typeMismatches")
	List<TypeMismatchFields> typeMismatches;

	public List<String> getSwaggerListNotMatched() {
		return swaggerListNotMatched;
	}

	public void setSwaggerListNotMatched(List<String> swaggerListNotMatched) {
		this.swaggerListNotMatched = swaggerListNotMatched;
	}

	public List<String> getResponseListNotMatched() {
		return responseListNotMatched;
	}

	public void setResponseListNotMatched(List<String> responseListNotMatched) {
		this.responseListNotMatched = responseListNotMatched;
	}

	public Set<String> getMissingMandatoryFields() {
		return missingMandatoryFields;
	}

	public void setMissingMandatoryFields(Set<String> missingMandatoryFields) {
		this.missingMandatoryFields = missingMandatoryFields;
	}

	public List<String> getMatchedFields() {
		return matchedFields;
	}

	public void setMatchedFields(List<String> matchedFields) {
		this.matchedFields = matchedFields;
	}

	public List<TypeMismatchFields> getTypeMismatches() {
		return typeMismatches;
	}

	public void setTypeMismatches(List<TypeMismatchFields> typeMismatches) {
		this.typeMismatches = typeMismatches;
	}

	
	
	
}
