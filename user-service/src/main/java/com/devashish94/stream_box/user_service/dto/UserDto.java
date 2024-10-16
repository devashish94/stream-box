package com.devashish94.stream_box.user_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @JsonProperty("sub")
    private String userId;

    @JsonProperty("given_name")
    private String giveNName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("picture")
    private String profilePicture;

    @JsonProperty("email")
    private String email;

    private List<Integer> subscribers = new ArrayList<>();

}
