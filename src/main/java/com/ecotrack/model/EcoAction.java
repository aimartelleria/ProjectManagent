package com.ecotrack.model;

import com.ecotrack.enums.ActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entity representing a single eco‑friendly action logged by a user.
 */
@Entity
@Table(name = "eco_actions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EcoAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many actions belong to one user. Loading lazy to avoid fetching user unless necessary.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Type of eco action.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    /**
     * Date when the action occurred.
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Optional description or notes about the action.
     */
    private String description;

    /**
     * Points awarded for this action.
     */
    private int points;
}
