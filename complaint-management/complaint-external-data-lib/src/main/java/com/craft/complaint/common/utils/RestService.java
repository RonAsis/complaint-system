package com.craft.complaint.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class RestService {

	private static final Logger logger = LoggerFactory.getLogger(RestService.class);

    @Autowired
    protected RestTemplate restTemplate;

    public Map<String, Object> getParamsMap(String[] names, Object... objs) throws IndexOutOfBoundsException{
    	if(names.length != objs.length){
    		throw new IndexOutOfBoundsException("Names and object amount doesn't match");
    	}
    	HashMap<String, Object> params = new HashMap<>();
    	int i = 0;
    	for(String name : names){
    		params.put(name, objs[i++]);
    	}
    	return params;
    }

    public String[] getParam(String... params) {
		return params;
	}

	public URI createUri(String url) {
    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    	return builder.build().encode().toUri();
	}

    public URI createUri(String url, String[] names, Object... objs) throws IndexOutOfBoundsException{
    	if(names.length != objs.length){
    		throw new IndexOutOfBoundsException("Names and object amount doesn't match");
    	}

    	UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

    	int i = 0;
    	for(String name : names){
    		if(objs[i] instanceof List){
    			for(Object obj : (List<?>)(objs[i])){
    				builder.queryParam(name, obj);
    			}
    			i++;
    		}else{
    			builder.queryParam(name, objs[i++]);
    		}
    	}
    	return builder.build().encode().toUri();
    }

    public <T>HttpEntity<T> createHttpEntity(T body) {
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		final HttpEntity<T> entity = new HttpEntity<T>(body, headers);
		return entity;
	}

    public <T>HttpEntity<T> createHttpFileEntity(T body) {
 		HttpHeaders headers = new HttpHeaders();
 		final HttpEntity<T> entity = new HttpEntity<T>(body, headers);
 		return entity;
 	}

	public <T>T runRest(String url, Callable<T> runnable){
		try{
			logger.debug("IN : Rest call to {}", url);
			return runnable.call();
		} catch (Exception e) {
			logger.info("OUT ERROR: Rest call to {} , {}", url, e.getMessage());
			logger.debug("Error: " ,e);
			return null;
		}
	}

	public <T>T runRestGet(String url, ParameterizedTypeReference<T> parameterizedTypeReference, String[] names, Object... objs) {
		return runRest(url, () ->{
			ResponseEntity<T> response = restTemplate.exchange(
					createUri(url, names, objs),
					HttpMethod.GET,
					null,
					parameterizedTypeReference);
			logger.debug("OUT: Rest call to {}", url);
			return response.getBody();
		});
	}

	public <T>T runRestGet(String url, ParameterizedTypeReference<T> parameterizedTypeReference) {
		return runRest(url, () ->{
			ResponseEntity<T> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					null,
					parameterizedTypeReference);
			logger.debug("OUT: Rest call to {}", url);
			return response.getBody();
		});
	}

	public <T>T runRestGet(URI uri, ParameterizedTypeReference<T> parameterizedTypeReference) {
		return runRest(uri.toString(), () ->{
			ResponseEntity<T> response = restTemplate.exchange(
					uri,
					HttpMethod.GET,
					null,
					parameterizedTypeReference);
			logger.debug("OUT: Rest call to {}", uri);
			return response.getBody();
		});
	}


	public <T> T runRestDelete(String url) {
		return runRest(url, () ->{
			ResponseEntity<T> response = restTemplate.exchange(
					url,
					HttpMethod.DELETE,
					null,
					new ParameterizedTypeReference<T>(){});
			logger.debug("OUT: Rest call to {}", url);
			return response.getBody();
		});
	}

	public <T> T runRestPut(String url, ParameterizedTypeReference<T> parameterizedTypeReference, T body) {
		RequestEntity<T> requestEntity =
				RequestEntity.put(URI.create(url)).header("cookie", "Authorization=" + "" /*token*/).body(body);
		return runRest(url, () -> {
			ResponseEntity<T> response = restTemplate.exchange(
					url,
					HttpMethod.PUT,
					requestEntity,
					parameterizedTypeReference);
			logger.debug("OUT: Rest call to {}", url);
			return response.getBody();
		});
	}

}//end of class
