/**
 * 
 */
package io.spire.api;

import io.spire.api.Api.APIDescriptionModel.APISchemaModel;
import io.spire.request.Request.RequestType;
import io.spire.request.Request;
import io.spire.request.RequestData;
import io.spire.request.RequestFactory;
import io.spire.request.Response;
import io.spire.request.ResponseException;

import java.io.IOException;
import java.util.Map;


/**
 * @author jorge
 *
 */
public class Resource {
	
	protected ResourceModel model;
	protected APISchemaModel schema;
	
	
	/**
	 * 
	 */
	public Resource() {
	}
	
	public Resource(APISchemaModel schema) {
		this.schema = schema;
	}
	
	public Resource(ResourceModel model, APISchemaModel schema) {
		this(schema);
		this.model = model;
	}
	
	public static class ResourceModel implements ResourceModelInterface {
		private Map<String, Object> rawModel;
		
		public ResourceModel(Map<String, Object> data){
			this.rawModel = data;
		}

		@Override
		public <T>T getProperty(String propertyName, Class<T> type){
			return (T)rawModel.get(propertyName);
		}
		
		@Override
		public void setProperty(String propertyName, Object data){
			rawModel.put(propertyName, data);
		}
	}

	public String getUrl(){
		return model.getProperty("url", String.class);
	}
	
	public String getResourceName(){
		return this.getClass().getSimpleName().toLowerCase();
	}
	
	public String getMediaType(){
		String resourceName = this.getResourceName();
		return schema.getMediaType(resourceName);
	}
	
	public String getCapability(){
		return model.getProperty("capability", String.class);
	}
	
	public void get() throws ResponseException, IOException{
		RequestData data = RequestFactory.createRequestData();
		data.method = RequestType.HTTP_GET;
		data.url = model.getProperty("url", String.class);
		data.headers.put("Authorization", "Capability " + model.getProperty("capability", String.class));
		data.headers.put("Accept", this.getMediaType());
		
		Request request = RequestFactory.createRequest(data);
		Response response = request.send();
		if(!response.isSuccessStatusCode())
			throw new ResponseException(response, "Error getting " + getResourceName());
		
		System.out.println("got resource " + getResourceName());
	}

	public void update() throws ResponseException, IOException{
		RequestData data = RequestFactory.createRequestData();
		data.method = RequestType.HTTP_PUT;
		data.url = model.getProperty("url", String.class);
		
		data.headers.put("Authorization", "Capability " + model.getProperty("capability", String.class));
		data.headers.put("Accept", this.getMediaType());
		data.headers.put("Content-Type", this.getMediaType());
		
		Request request = RequestFactory.createRequest(data);
		Response response = request.send();
		if(!response.isSuccessStatusCode())
			throw new ResponseException(response, "Error updating " + getResourceName());
		
		System.out.println("updated resource " + getResourceName());
	}
	
	public void delete() throws ResponseException, IOException{
		RequestData data = RequestFactory.createRequestData();
		data.method = RequestType.HTTP_DELETE;
		data.url = model.getProperty("url", String.class);
		
		data.headers.put("Authorization", "Capability " + model.getProperty("capability", String.class));
		data.headers.put("Accept", this.getMediaType());
		data.headers.put("Content-Type", this.getMediaType());
		
		Request request = RequestFactory.createRequest(data);
		Response response = request.send();
		if(!response.isSuccessStatusCode())
			throw new ResponseException(response, "Error deleting " + getResourceName());
		
		System.out.println("deleted resource " + getResourceName());
	}
}