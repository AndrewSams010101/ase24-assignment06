package de.unibayreuth.se.taskboard.data.persistence;

import de.unibayreuth.se.taskboard.business.domain.TaskStatus;
import de.unibayreuth.se.taskboard.business.domain.Identifiable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Database entity for a task.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class TaskEntity implements Identifiable{
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "assignee_id")
    private UUID assigneeId; // deliberately not using a @ManyToOne relationship here to decouple the entities

    @Override
    public long getSerialVersionUID() {
        return 1L;
    }
}
