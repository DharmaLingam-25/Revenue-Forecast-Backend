package com.clt.ops.processor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Component;
import com.clt.ops.entity.ClientEntity;
import com.clt.ops.model.ClientData;
import com.clt.ops.repository.ClientRepository;
import com.clt.ops.util.GenericProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientFileProcessor implements GenericProcessor<ClientData> {

	private final ClientRepository clientRepository;
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final DateTimeFormatter SIMPLE_INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");

	private static String convertToFormattedDate(String inputDateString) {
		LocalDateTime tempDateTime = LocalDateTime.parse(inputDateString, INPUT_DATE_TIME_FORMATTER);
		LocalDate dateOnly = tempDateTime.toLocalDate();
		return dateOnly.format(DATE_FORMATTER);
	}

	private static String convertSubmitionDate(String inputDateString) {
		LocalDate date = LocalDate.parse(inputDateString, SIMPLE_INPUT_DATE_FORMATTER);
		return date.format(DATE_FORMATTER);
	}

	public BigDecimal parseAmount(String amountStr) {
		if (amountStr != null && !amountStr.isEmpty()) {
			String cleaned = amountStr.replaceAll("[^\\d.\\-]", "");
			return new BigDecimal(cleaned);
		} else {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public ClientData process(ClientData clientData) {
		try {
			LocalDate endDate = LocalDate.parse(convertSubmitionDate(clientData.getEndDate()), DATE_FORMATTER);
			LocalDate submitionDate = LocalDate.parse(convertSubmitionDate(clientData.getSubmitionDate()),
					DATE_FORMATTER);
			LocalDate date = LocalDate.parse(convertToFormattedDate(clientData.getDate()), DATE_FORMATTER);

			Optional<ClientEntity> optionalEntity = clientRepository.findByExternalIdAndDate(clientData.getExternalID(),
					date);

			ClientEntity clientEntity = optionalEntity.orElse(new ClientEntity());

			clientEntity.setClientId(String.valueOf(new BigDecimal(clientData.getID()).intValue()));
			clientEntity.setTimesheetId(String.valueOf(new BigDecimal(clientData.getHeaderID()).intValue()));
			clientEntity.setExternalId(clientData.getExternalID().toUpperCase());
			clientEntity.setAssosiateName(clientData.getAssosiateName());

			clientEntity.setEmail(clientData.getEmail());
			clientEntity.setName(clientData.getName());
			clientEntity.setUnits(new BigDecimal(clientData.getUnits()));
			log.info("Raw RT Rate: '{}'", clientData.getRTRate());

			clientEntity.setRtRate(parseAmount(clientData.getRTRate()));
			clientEntity.setDate(date);
			clientEntity.setSubmitionDate(submitionDate);
			clientEntity.setEndDate(endDate);
			log.info("Preparing to process record # {}", clientData);

			clientRepository.save(clientEntity);
			log.info("Successfully persisted associate with CTS ID: {}", clientData.getExternalID());
		} catch (Exception e) {
			log.error("Failed to save ClientEntity for CTS ID: {}. Error: {}", clientData.getExternalID(),
					e.getMessage(), e);
			log.error("this record has issue :{}", clientData.toString());
		}

		log.info("Successfully persisted associate with CTS ID: {}", clientData.getExternalID());

		return clientData;
	}

}
