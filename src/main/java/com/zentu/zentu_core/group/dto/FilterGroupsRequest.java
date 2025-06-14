package com.zentu.zentu_core.group.dto;

import lombok.Data;

@Data
public class FilterGroupsRequest {
    private String state;
    private String userId;
    private String creatorId;
    private String searchTerm;
}
