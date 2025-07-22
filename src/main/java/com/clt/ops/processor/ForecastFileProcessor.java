package com.clt.ops.processor;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.clt.ops.entity.ForecastEntity;
import com.clt.ops.model.ForecastData;
import com.clt.ops.repository.ForecastRepository;
import com.clt.ops.util.GenericProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ForecastFileProcessor implements GenericProcessor<ForecastData> {

    private final ForecastRepository forecastRepository;
    
    private String toWholeNumberString(String value) {
	   
	    return (value == null) ? null : String.valueOf(new BigDecimal(value).intValue());
	}

    
    @Override
    public ForecastData process(ForecastData inData) {
        String accountId = toWholeNumberString(inData.getAccountID());
        String plCategory = inData.getPLCategory();
        String plHeader=inData.getPLHeader();

        ForecastEntity entity = forecastRepository
            .findByAccountIdAndPlCategoryAndPlHeader(accountId, plCategory,plHeader)
            .orElse(new ForecastEntity()); 
                	
        
        entity.setAccountId(accountId);
        entity.setPlCategory(plCategory);

        
        entity.setAccountName(inData.getAccountName());
        entity.setPlHeader(inData.getPLHeader());

        entity.setYtd(parseDecimal(inData.getYTD()));
        entity.setFy(parseDecimal(inData.getFY()));
        entity.setQ1(parseDecimal(inData.getQ1()));
        entity.setQ2(parseDecimal(inData.getQ2()));
        entity.setQ3(parseDecimal(inData.getQ3()));
        entity.setQ4(parseDecimal(inData.getQ4()));
        entity.setYear(LocalDate.now().getYear());

        entity.setJan(parseDecimal(inData.getJan()));
        entity.setFeb(parseDecimal(inData.getFeb()));
        entity.setMar(parseDecimal(inData.getMar()));
        entity.setApr(parseDecimal(inData.getApr()));
        entity.setMay(parseDecimal(inData.getMay()));
        entity.setJun(parseDecimal(inData.getJun()));
        entity.setJul(parseDecimal(inData.getJul()));
        entity.setAug(parseDecimal(inData.getAug()));
        entity.setSep(parseDecimal(inData.getSep()));
        entity.setOct(parseDecimal(inData.getOct()));
        entity.setNov(parseDecimal(inData.getNov()));
        entity.setDece(parseDecimal(inData.getDec()));

        forecastRepository.save(entity);

        log.info("Upserted RevenueForecast for AccountID '{}' and PL Category '{}'", accountId, plCategory);
        return inData;
    }

    private BigDecimal parseDecimal(String value) {
        try {
            return value != null && !value.isBlank() ? new BigDecimal(value.trim()) : BigDecimal.ZERO;
        } catch (NumberFormatException e) {
            log.warn("Invalid number format: '{}'", value);
            return BigDecimal.ZERO;
        }
    }

    
}
