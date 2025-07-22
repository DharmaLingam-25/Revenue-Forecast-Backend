//package com.clt.ops.processor;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Optional;
//import org.springframework.stereotype.Component;
//import com.clt.ops.entity.CompanyEntity;
//import com.clt.ops.model.CompanyData;
//import com.clt.ops.repository.CompanyRepository;
//import com.clt.ops.util.GenericProcessor;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class CompanyFileProcessor implements GenericProcessor<CompanyData> {
//	private final CompanyRepository companyRepository;
//    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
//    
//    private static LocalDate convertToLocalDate(String inputDateString) {
//        LocalDateTime tempDateTime = LocalDateTime.parse(inputDateString, INPUT_DATE_TIME_FORMATTER);
//        return tempDateTime.toLocalDate();
//    }
//
//    @Override
//    public CompanyData process(CompanyData companyData) {
//
//        log.info("Processing CompanyData: {}", companyData.toString());
//
//        LocalDate reportingDate = convertToLocalDate(companyData.getReportingDate());
//        LocalDate submissionDate = convertToLocalDate(companyData.getSubmissionDate());
//        String str=companyData.getAssociateId();
//
//        Optional<CompanyEntity> existingCompanyEntity = companyRepository.findByAssociateIdAndReportingDate(str,reportingDate);
//
//        CompanyEntity companyEntity= existingCompanyEntity.orElse(new CompanyEntity());
//            
//
//            companyEntity.setAssociateName(companyData.getAssociateName());
//            companyEntity.setProjectId(String.valueOf(new BigDecimal(companyData.getProjectId()).intValue()));
//            companyEntity.setAssociateId(String.valueOf(new BigDecimal(companyData.getAssociateId()).intValue())); 
//            
//            companyEntity.setProjectName(companyData.getProjectName());
//            companyEntity.setProjectPolicyHours(companyData.getProjectPolicyHours());
//            companyEntity.setTimesheetId(companyData.getTimesheetId()); 
//            companyEntity.setOnsiteOffshore(companyData.getOnsiteOffshore());
//            companyEntity.setTimeQuantity(new BigDecimal(companyData.getTimeQuantity()));
//            companyEntity.setSubmissionDate(submissionDate);
//            companyEntity.setClientBillable(companyData.getClientBillable());
//            companyEntity.setReportingDate(reportingDate);
////             
//
//        CompanyEntity savedEntity = companyRepository.save(companyEntity);
//        log.info("CompanyFileProcessor: Company data saved/updated successfully with ID: {}", savedEntity.getId());
//        log.info("Saved/Updated CompanyEntity details: {}", savedEntity.toString());
//
//        return companyData;
//    }
//
//}



package com.clt.ops.processor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.clt.ops.entity.CompanyEntity;
import com.clt.ops.model.CompanyData;
import com.clt.ops.repository.CompanyRepository;
import com.clt.ops.util.GenericProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyFileProcessor implements GenericProcessor<CompanyData> {
	private final CompanyRepository companyRepository;
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy"); // Added yyyy
    
    private static LocalDate convertToLocalDate(String inputDateString) {
        
        try {
            LocalDateTime tempDateTime = LocalDateTime.parse(inputDateString, INPUT_DATE_TIME_FORMATTER);
            return tempDateTime.toLocalDate();
        } catch (java.time.format.DateTimeParseException e) {
            log.error("Error parsing date string: {}. Attempting with different format patterns if needed.", inputDateString, e);
            throw e; 
        }
    }

    @Override
    public CompanyData process(CompanyData companyData) {

        log.info("Processing CompanyData: {}", companyData.toString());

        LocalDate reportingDate = convertToLocalDate(companyData.getReportingDate());
        LocalDate submissionDate = convertToLocalDate(companyData.getSubmissionDate());
        String associateId = String.valueOf(new BigDecimal(companyData.getAssociateId()).intValue());

        Optional<CompanyEntity> existingCompanyEntityOptional = companyRepository.findByAssociateIdAndReportingDate(associateId, reportingDate);

        CompanyEntity companyEntity;

        if (existingCompanyEntityOptional.isPresent()) {
            companyEntity = existingCompanyEntityOptional.get();
            log.info("Found existing CompanyEntity with Associate ID: {} and Reporting Date: {}. Updating record with ID: {}", 
                     associateId, reportingDate, companyEntity.getId());
        } else {
            
            companyEntity = new CompanyEntity();
            log.info("No existing CompanyEntity found for Associate ID: {} and Reporting Date: {}. Creating a new record.",
                     associateId, reportingDate);
        }
        
        
        companyEntity.setAssociateName(companyData.getAssociateName());
        companyEntity.setProjectId(String.valueOf(new BigDecimal(companyData.getProjectId()).intValue()));
        companyEntity.setAssociateId(associateId); 

        companyEntity.setProjectName(companyData.getProjectName());
        companyEntity.setProjectPolicyHours(companyData.getProjectPolicyHours());
        companyEntity.setTimesheetId(companyData.getTimesheetId());
        companyEntity.setOnsiteOffshore(companyData.getOnsiteOffshore());
        companyEntity.setTimeQuantity(new BigDecimal(companyData.getTimeQuantity()));
        companyEntity.setSubmissionDate(submissionDate);
        companyEntity.setClientBillable(companyData.getClientBillable());
        companyEntity.setReportingDate(reportingDate); 

        CompanyEntity savedEntity = companyRepository.save(companyEntity);
        log.info("CompanyFileProcessor: Company data saved/updated successfully with ID: {}", savedEntity.getId());
        log.info("Saved/Updated CompanyEntity details: {}", savedEntity.toString());

        return companyData;
    }
}
