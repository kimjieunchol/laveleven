package com.labelai.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoryResponse {
    private String id;
    private String type;
    private String fileName;
    private String date;
    private String time;
    private String status;
    private String userId;
}