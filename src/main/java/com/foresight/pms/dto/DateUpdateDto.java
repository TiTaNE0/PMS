package com.foresight.pms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class DateUpdateDto {
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate;
}
