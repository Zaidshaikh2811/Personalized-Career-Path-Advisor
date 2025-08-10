package com.child1.ai_service.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;
import java.util.List;


@Document(collection = "recommendations")
@Data
public class Recommendation {

    @NotBlank
    @Id
    private String id;

    @NotBlank
    private String activityId;

    @NotBlank
    private String userId;

    @NotBlank
    private String activityType;

    @NotBlank
    @Size(max = 1000)
    private String recommendationText;

    @NotNull
    @Size(max = 10)
    private List<@NotBlank String> improvements;

    @NotNull
    @Size(max = 10)
    private List<@NotBlank String> suggestions;

    @NotNull
    @Size(max = 10)
    private List<@NotBlank String> safety;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
