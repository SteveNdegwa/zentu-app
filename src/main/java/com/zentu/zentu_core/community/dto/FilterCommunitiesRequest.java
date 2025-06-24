package com.zentu.zentu_core.community.dto;

import lombok.Data;

@Data
public class FilterCommunitiesRequest {
    private String state;
    private String userId;
    private String creatorId;
    private String searchTerm;
}
