package com.devashish94.stream_box.user_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Entity
@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String userId;

    @Column
    private String givenName;

    @Column
    private String familyName;

    @Column
    private String profilePicture;

    @Column
    private String email;

    @Column
    private String channelName;

    @Column
    private Long subscriberCount;

}
