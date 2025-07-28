package com.clt.ops.controller;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clt.ops.dto.AccountComparisonDto;
import com.clt.ops.dto.AssociateComparisonDTO;
import com.clt.ops.dto.DateLevelComparisonDto;

import com.clt.ops.dto.ProjectTypeLevelSummaryDto;
import com.clt.ops.dto.request.AccountCommentDto;
import com.clt.ops.dto.request.RetriveDto;
import com.clt.ops.entity.AccountCommentEntity;
import com.clt.ops.repository.comparison.AccountCommentRepository;
import com.clt.ops.repository.comparison.AccountComparisonRepository;
import com.clt.ops.repository.comparison.AssociateComparisonRepository;
import com.clt.ops.repository.comparison.DateLevelComparisonRepository;
import com.clt.ops.repository.comparison.ProjectComparisonRepository;
import com.clt.ops.repository.comparison.ProjectTypeLevelSummaryRepository;

import lombok.RequiredArgsConstructor;
@CrossOrigin(origins = "http://localhost:3000") 
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RetrivalController {

    private final AccountComparisonRepository retrivalRepository;
    private final ProjectComparisonRepository projectComparisonRepository;
    private final AssociateComparisonRepository associateComparisonRepository;
    private final DateLevelComparisonRepository dateLevelComparisonRepository;
    private final AccountCommentRepository accountCommentRepository;
    private final ProjectTypeLevelSummaryRepository projectTypeLevelSummaryRepository;
    @PostMapping("/account")
    public ResponseEntity<?> getAccountComparison(@RequestBody RetriveDto dto) {
        try {
            LocalDate startDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
            LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
            List<AccountComparisonDto> result = retrivalRepository.getAccountComparison(
                    startDate,
                    endDate,
                    dto.getMonth(),
                    dto.getYear(),
                    dto.getMonthNameParam() 
                );            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching account comparison: " + e.getMessage());
        }
    }

    

    @PostMapping(value = "/associate", consumes = "application/json")
    public ResponseEntity<?> getAssociateSummary(@RequestBody RetriveDto dto) {
        try {
            LocalDate startDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
            LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
            List<AssociateComparisonDTO> list = associateComparisonRepository.getAssociateSummary(
                startDate, endDate, dto.getProjectId()
            );
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching associate summary: " + e.getMessage());
        }
    }

    @PostMapping(value = "/date-level", consumes = "application/json")
    public ResponseEntity<?> getDateLevelComparison(@RequestBody RetriveDto dto) {
        try {
            LocalDate startDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
            LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
            List<DateLevelComparisonDto> result = dateLevelComparisonRepository.getDateLevelComparison(
                dto.getProjectId(), dto.getAssociateId(), startDate, endDate
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching date-level comparison: " + e.getMessage());
        }
    }
    


    @PostMapping("/account/comment")
    public ResponseEntity<?> saveOrUpdateAccountComment(@RequestBody AccountCommentDto dto) {
        try {
        	Optional<AccountCommentEntity> existingComment = accountCommentRepository
                    .findByAccIdAndMonthAndYear(dto.getAccId(), dto.getMonth(), dto.getYear());
            if (existingComment.isPresent()) {
                AccountCommentEntity entity = existingComment.get();
                entity.setComment(dto.getComment());
                entity.setMonth(dto.getMonth());
                entity.setYear(dto.getYear());
                accountCommentRepository.save(entity);
            } else {
                AccountCommentEntity newEntity = new AccountCommentEntity();
                newEntity.setAccId(dto.getAccId());
                newEntity.setComment(dto.getComment());
                newEntity.setMonth(dto.getMonth());
                newEntity.setYear(dto.getYear());
                
                accountCommentRepository.save(newEntity);
            }

            return ResponseEntity.ok("Comment saved/updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving comment: " + e.getMessage());
        }
        
    }
    
    @PostMapping(value = "/project-type-level", consumes = "application/json")
    public ResponseEntity<?> getProjectTypeLevelSummary(@RequestBody RetriveDto dto) {
        try {
            LocalDate startDate = LocalDate.of(dto.getYear(), dto.getMonth(), 1);
            LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

            List<ProjectTypeLevelSummaryDto> result = projectTypeLevelSummaryRepository.getSummaryByType(
                startDate.toString(), endDate.toString(), dto.getAccId()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching project type level summary: " + e.getMessage());
        }
    }
  
}