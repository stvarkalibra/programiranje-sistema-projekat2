package org.raflab.avanturaservice.entityLayer;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdventureReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating; // 1-5

    private String comment;

    private LocalDateTime createdAt;

    @ManyToOne
    private Adventure adventure;
}