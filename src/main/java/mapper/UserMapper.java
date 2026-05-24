package mapper;

import dto.UserRegistrationDTO;
import dto.UserResponseDTO;
import entity.Role;
import entity.User;
import entity.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRegistrationDTO dto, PasswordEncoder encoder) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(UserStatus.PENDING);

        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {

        UserResponseDTO dto = new UserResponseDTO();
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(dto.getLastName());
        dto.setEmail(dto.getEmail());

        return dto;

    }

}
