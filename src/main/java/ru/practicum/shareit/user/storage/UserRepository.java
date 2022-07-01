package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);

    Collection<User> getAll();
}