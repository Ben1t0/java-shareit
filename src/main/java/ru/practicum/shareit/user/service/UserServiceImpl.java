package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        ensureUserExists(userDto.getId());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto patchUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException(userDto.getId()));

        User userToUpdate = User.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();

        if (userDto.getEmail() != null) {
            userToUpdate.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userRepository.save(userToUpdate));
    }

    @Override
    public void deleteUser(Long id) {
        ensureUserExists(id);
        userRepository.deleteById(id);
    }

    private void ensureUserExists(Long id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
