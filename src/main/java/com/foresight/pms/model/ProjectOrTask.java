package com.foresight.pms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@EqualsAndHashCode(of = {"uid"})
@Entity
public class ProjectOrTask implements Serializable {

    static final long serialVersionUID = 42L;

    @Id
    String uid;
    String name;
    @Enumerated(EnumType.STRING)
    ProjectOrTaskEnum type;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endDate;
    String parentUid;


    public double getCompletionStatusOnDate(LocalDate onDate) {
        if (this.startDate.isAfter(onDate))
            return 0.0;
        if (this.endDate.isBefore(onDate))
            return 1.0;
        long totalDays = ChronoUnit.DAYS.between(this.startDate, this.endDate);
        long daysSpent = ChronoUnit.DAYS.between(this.startDate, onDate);
        return (double) daysSpent / (double )totalDays;
    }

}
