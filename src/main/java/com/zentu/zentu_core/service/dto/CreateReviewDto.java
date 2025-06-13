package com.zentu.zentu_core.service.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewDto {
    @Min(1)
    @Max(5)
    private int rating;

    private String comment;

    @NotNull
    private String reviewer;

}
