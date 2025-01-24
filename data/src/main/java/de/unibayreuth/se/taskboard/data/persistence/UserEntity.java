package de.unibayreuth.se.taskboard.data.persistence;


import de.unibayreuth.se.taskboard.business.domain.Identifiable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Database entity for a user.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity implements Identifiable {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(unique=true)
    private String name;

    @Override
    public long getSerialVersionUID() {
        return 1L;
    }
}
