package com.deliverit.absgp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class JiraData {

    private int timeSpentSeconds;
    private String issueKey;
    private LocalDateTime started;

    public String getDate() {
        return started.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getStarTime() {
        return started.format(DateTimeFormatter.ofPattern("HHmm"));
    }

    public String getFinalTime() {
        LocalDateTime finalTime = started.plusSeconds(timeSpentSeconds);
        return finalTime.format(DateTimeFormatter.ofPattern("HHmm"));
    }

}
