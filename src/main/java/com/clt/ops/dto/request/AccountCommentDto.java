package com.clt.ops.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCommentDto {
    private String accId;
    private String comment;
    private int month;
    private int year;
}

