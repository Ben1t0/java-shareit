package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public UserDto getUserDtoByOrThrow(Long id) {
        return UserMapper.toUserDto(getUserByIdOrThrow(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        getUserByIdOrThrow(userDto.getId());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto patchUser(UserDto userDto) {
        User user = getUserByIdOrThrow(userDto.getId());

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
        getUserByIdOrThrow(id);
        userRepository.deleteById(id);
    }
}
