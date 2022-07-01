package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryInMemoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long nextID = 1;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User createUser(User user) {
        if (users.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", user.getEmail()));
        }
        user.setId(nextID++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User userFromStorage = users.get(user.getId());
        if (user.getEmail() != null) {
            if (users.values().stream()
                    .filter(u -> u.getId() != user.getId())
                    .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
                throw new UserAlreadyExistsException(String.format("User with email %s already exists", user.getEmail()));
            } else {
                userFromStorage.setEmail(user.getEmail());
            }
        }
        if (user.getName() != null) {
            userFromStorage.setName(user.getName());
        }
        return users.get(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }
}
