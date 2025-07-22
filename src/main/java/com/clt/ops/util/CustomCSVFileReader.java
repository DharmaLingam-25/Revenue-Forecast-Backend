package com.clt.ops.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class CustomCSVFileReader<T> extends CustomFileReader<T>{

	public CustomCSVFileReader(Class<T> type, MultipartFile file, boolean hasHeader) {
		super(type, file, hasHeader);
	}
	public CustomCSVFileReader(Class<T> type, MultipartFile file, boolean hasHeader,String separator) {
		super(type, file, hasHeader,separator);
	}
	
	public void readData() throws InstantiationException, IllegalAccessException {

		BufferedReader  reader = null;
		
		String line =null;
		try{  
			InputStreamReader isr = new InputStreamReader(multipartFile.getInputStream(),StandardCharsets.UTF_8);
			reader = new BufferedReader(isr);
			while ((line = reader.readLine()) != null) {
				
				List<String> row = Arrays.asList(line.split(seperator));
				
				if (this.hasHeader){
					setHeaders(row);;
					this.hasHeader = false;
					continue;
				}
				
				T refObject = genericType.newInstance();
				int index = 0;				
				
				List<String> listOfFieldNames = (null != getOrder()) ? getOrder() : new ArrayList<String>(privateFields.keySet());
				
				for(String fieldName : listOfFieldNames) {
					if( index >= row.size()) {
						break;
					}
					assign(refObject,privateFields.get(toCamelCaseWithStringBuilder(fieldName)),row.get(index++));
				}
				getData().add(refObject);
			}
			
			reader.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch ( IOException e ) { 	
			e.printStackTrace();
		}
		finally{  
			
			try { 
				reader.close(); 
				} 
			catch ( Exception e ) { 
				
			}
		}
	}
	
	private String toCamelCaseWithStringBuilder(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		char firstChar = sb.charAt(0);
		if (Character.isUpperCase(firstChar)) { // Only change if it's currently uppercase
			sb.setCharAt(0, Character.toLowerCase(firstChar));
		}
		return sb.toString();
	}
		
	

	private Field assign(T refObject, Field field, String value)
			throws IllegalArgumentException, IllegalAccessException {
		field.setAccessible(true);
		field.set(refObject, !value.isBlank()?value.replace("\"", "").trim():value);
		field.setAccessible(false);
		return field;
	}

}
