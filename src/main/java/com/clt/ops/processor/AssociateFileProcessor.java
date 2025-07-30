package com.clt.ops.processor;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.clt.ops.entity.AssociateEntity;
import com.clt.ops.model.AssociateData;
import com.clt.ops.repository.AssociateRepository;
import com.clt.ops.util.GenericProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class AssociateFileProcessor implements GenericProcessor<AssociateData> {
	
	private String sanitizeField(String value) {
	    if (value == null) return null;

	    String trimmed = value.trim();
	    if (trimmed.equalsIgnoreCase("null") || 
	        trimmed.equalsIgnoreCase("-") || 
	        trimmed.equalsIgnoreCase("#N/A") ||
	        trimmed.isEmpty()) {
	        return null;
	    }
	    return trimmed;
	}
	private String toWholeNumberString(String value) {
	    String cleaned = sanitizeField(value);
	    return (cleaned == null) ? null : String.valueOf(new BigDecimal(cleaned).intValue());
	}


    private final AssociateRepository associateRepository;

    @Override
    public AssociateData process(AssociateData associateData) {
    	String ctsId = associateData.getCTSID();
        if (ctsId == null || ctsId.isBlank()) {
            log.error("CTS ID is missing in the input data: {}", associateData);
            throw new IllegalArgumentException("CTS ID must not be null or empty.");
        }
        Optional<AssociateEntity> existingEntityOpt = associateRepository.findByCtsId(ctsId);
        AssociateEntity associateEntity = existingEntityOpt.orElse(new AssociateEntity());
        associateEntity.setExternalId(associateData.getExternalID().toUpperCase());
        associateEntity.setCtsId(String.valueOf(new BigDecimal(associateData.getCTSID()).intValue()));
        associateEntity.setContractorName(associateData.getContractorName());
        associateEntity.setContractorEmail(associateData.getContractorEmail());   
      

        associateEntity.setEsaProjectId(toWholeNumberString(associateData.getESAProjectID()));
        associateEntity.setProjectDescription(sanitizeField(associateData.getProjectDescription()));
        associateEntity.setProjectType(sanitizeField(associateData.getProjectType()));
        associateEntity.setEsaPm(sanitizeField(associateData.getESAPM()));
        associateEntity.setEsaId(toWholeNumberString(associateData.getESAID()));
        associateEntity.setSl(sanitizeField(associateData.getSL()));
        
        associateEntity.setAccId(toWholeNumberString(associateData.getACCID()));

        associateEntity.setAccName(associateData.getACCNAME());
        associateEntity.setSbu(associateData.getSBU());

       
        log.info("Successfully persisted associate with CTS ID: {}", associateData.toString());
        associateRepository.save(associateEntity);

        
        return associateData;
    }
}
