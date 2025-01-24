package de.unibayreuth.se.taskboard.data.impl;

import de.unibayreuth.se.taskboard.business.domain.User;
import de.unibayreuth.se.taskboard.business.exceptions.DuplicateNameException;
import de.unibayreuth.se.taskboard.business.exceptions.UserNotFoundException;
import de.unibayreuth.se.taskboard.business.ports.UserPersistenceService;
import de.unibayreuth.se.taskboard.data.mapper.UserEntityMapper;
import de.unibayreuth.se.taskboard.data.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@RequiredArgsConstructor
@Primary
public class UserPersistenceServiceEventSourcingImpl implements UserPersistenceService {
    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void clear() {
        userRepository.findAll()
                .forEach(userEntity -> eventRepository.saveAndFlush(
                        EventEntity.deleteEventOf(userEntityMapper.fromEntity(userEntity), null))
                );
        if (userRepository.count() != 0) {
            throw new IllegalStateException("Tasks not successfully deleted.");
        }
    }

    @NonNull
    @Override
    public List<User> getAll() {
        return userRepository.findAll().stream()
                .map(userEntityMapper::fromEntity)
                .toList();
    }

    @NonNull
    @Override
    public Optional<User> getById(UUID id) {
        return userRepository.findById(id)
                .map(userEntityMapper::fromEntity);
    }

    @NonNull
    @Override
    public User upsert(User user) throws UserNotFoundException, DuplicateNameException {
        // TODO: Implement upsert
        /*
        The upsert method in the UserPersistenceServiceEventSourcingImpl class handles both the creation and updating of users.
        If the user ID is null, it creates a new user by generating a new UUID, saving an insert event, and returning the newly created user.
        If the user ID is not null, it updates the existing user by finding it in the repository, updating its fields, saving an update event, and returning the updated user.
        In both cases, it uses the EventRepository to log the changes and the UserRepository to persist the user data.
        */
        UUID userId = user.getId() == null ? UUID.randomUUID() : user.getId();

        if (user.getId() == null && userRepository.existsByName(user.getName())) {
            throw new DuplicateNameException("User  with name " + user.getName() + " already exists.");
        }

        UserEntity userEntity = userEntityMapper.toEntity(user);
        userEntity.setId(userId);
        if (user.getId() == null) {
            userEntity.setCreatedAt(LocalDateTime.now());
        }
        EventEntity event;

        if (userRepository.existsById(userId)) {

            User existingUser  = userRepository.findById(userId)
                    .map(userEntityMapper::fromEntity)
                    .orElseThrow(() -> new UserNotFoundException("User  not found with ID: " + userId));

            existingUser.setName(user.getName());

            userEntity = userEntityMapper.toEntity(existingUser);
            event = EventEntity.updateEventOf(userEntity, userId, objectMapper);
        } else {
            event = EventEntity.insertEventOf(userEntity, userId, objectMapper);
        }

        eventRepository.saveAndFlush(event);
        userRepository.save(userEntity);

        return userEntityMapper.fromEntity(userEntity);
//        return new User("Firstname Lastname");
    }
}
