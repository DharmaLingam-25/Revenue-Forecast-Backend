package com.clt.ops.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public abstract class CustomFileReader<T> {

	protected String seperator;

	// private String file;
	protected MultipartFile multipartFile;

	protected Map<String, Field> privateFields = new LinkedHashMap<String, Field>();

	protected Class<T> genericType;

	protected List<T> data;

	protected List<String> order;

	protected List<String> headers;

	protected boolean initCompleted;

	protected boolean hasHeader;

	private void initialize() {
		if (!this.initCompleted) {
			Field[] allFields = genericType.getDeclaredFields();
			for (Field field : allFields) {
				if (Modifier.isPrivate(field.getModifiers())) {
					privateFields.put(field.getName(), field);
				}
			}

			try {
				readData();
			} catch (InstantiationException | IllegalAccessException e) {
				this.initCompleted = false;
			}
			this.initCompleted = true;
		}
	}

	public CustomFileReader(final Class<T> type, MultipartFile file, boolean hasHeader) {
		this.multipartFile = file;
		this.hasHeader = hasHeader;
		this.genericType = type;
		this.seperator = ",";
	}

	public CustomFileReader(final Class<T> type, MultipartFile file, boolean hasHeader, String separator) {
		this.multipartFile = file;
		this.hasHeader = hasHeader;
		this.genericType = type;
		this.seperator = separator;
	}

	/**
	 * @return the data
	 */
	public List<T> getData() {
		if (null == data) {
			data = new ArrayList<T>();
		}
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<T> data) {
		this.data = data;
	}

	/**
	 * @return the headers
	 */
	public List<String> getHeaders() {
		return headers;
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	/**
	 * @return the order
	 */
	public List<String> getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public CustomFileReader<T> setOrder(List<String> order) {
		this.order = order;
		return this;
	}

	public CustomFileReader<T> read(List<String> order) {
		this.setOrder(order);
		initialize();
		return this;
	}

	public CustomFileReader<T> read() {
		initialize();
		return this;
	}
	
	public abstract void readData() throws InstantiationException, IllegalAccessException;
	
	

	

	public CustomFileReader<T> process(GenericProcessor<T> processsor) {
		if (!this.initCompleted) {
			initialize();
		}

		if (null != processsor) {
			List<T> list = getData();
			for (T obj : list) {
				obj = processsor.process(obj);
			}
		}
		return this;
	}
}
