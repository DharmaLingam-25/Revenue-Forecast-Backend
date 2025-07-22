package com.clt.ops.service;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.clt.ops.model.AssociateData;
import com.clt.ops.model.ClientData;
import com.clt.ops.model.CompanyData;
import com.clt.ops.model.ForecastData;
import com.clt.ops.processor.AssociateFileProcessor;
import com.clt.ops.processor.ClientFileProcessor;
import com.clt.ops.processor.CompanyFileProcessor;
import com.clt.ops.processor.ForecastFileProcessor;
import com.clt.ops.util.CustomCSVFileReader;
import com.clt.ops.util.CustomExcelFileReader;
import com.clt.ops.util.CustomFileReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueService {

	@Value("${revenue.client.header}")
	private String clientHeaders;

	@Value("${revenue.company.header}")
	private String companyHeaders;

	@Value("${revenue.associate.header}")
	private String associateHeaders;
	
	@Value("${revenue.forecast.header}")
	private String forecastHeaders;

	private final ForecastFileProcessor forecastFileProcessor;

	private final CompanyFileProcessor companyFileProcessor;
	private final ClientFileProcessor clientFileProcessor;
	private final AssociateFileProcessor associateFileProcessor;

	public void uploadFile(String type, MultipartFile file) {

		String fileName = file.getOriginalFilename();
		boolean isCSV = fileName != null && fileName.toLowerCase().endsWith(".csv");

		if ("COMPANY".equalsIgnoreCase(type)) {
			CustomFileReader<CompanyData> companyFile;

			if (isCSV) {
				companyFile = new CustomCSVFileReader<>(CompanyData.class, file, true)
						.setOrder(Arrays.asList(companyHeaders.split(",")));
			} else {
				companyFile = new CustomExcelFileReader<>(CompanyData.class, file, true)
						.setOrder(Arrays.asList(companyHeaders.split(",")));
			}

			companyFile.read().process(companyFileProcessor);
		} else if ("CLIENT".equalsIgnoreCase(type)) {
			CustomFileReader<ClientData> clientFile;

			if (isCSV) {
				clientFile = new CustomCSVFileReader<>(ClientData.class, file, true)
						.setOrder(Arrays.asList(clientHeaders.split(",")));
			} else {
				clientFile = new CustomExcelFileReader<>(ClientData.class, file, true)
						.setOrder(Arrays.asList(clientHeaders.split(",")));
			}

			clientFile.read().process(clientFileProcessor);
		}

				
		else if ("FORECAST".equalsIgnoreCase(type)) {
			CustomFileReader<ForecastData> forecastFile;
		
		
		List<String> baseHeaders = Arrays.asList(forecastHeaders.split(","));
		
					
			if (isCSV) {
				forecastFile = new CustomCSVFileReader<>(ForecastData.class, file, true)
						.setOrder(baseHeaders);
			} else {
				forecastFile = new CustomExcelFileReader<>(ForecastData.class, file, true)
						.setOrder(baseHeaders);
			}

			forecastFile.read().process(forecastFileProcessor);		    
		}




		else {
			CustomFileReader<AssociateData> associateFile;

			if (isCSV) {
				associateFile = new CustomCSVFileReader<>(AssociateData.class, file, true)
						.setOrder(Arrays.asList(associateHeaders.split(",")));
			} else {
				associateFile = new CustomExcelFileReader<>(AssociateData.class, file, true)
						.setOrder(Arrays.asList(associateHeaders.split(",")));
			}

			associateFile.read().process(associateFileProcessor);
		}

	}
}
