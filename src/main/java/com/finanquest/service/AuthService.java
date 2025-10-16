package com.finanquest.service;

import com.finanquest.config.JwtService;
import com.finanquest.dto.AuthResponseDTO;
import com.finanquest.dto.LoginRequestDTO;
import com.finanquest.dto.UserProfileResponseDTO;
import com.finanquest.dto.UserRegistrationDTO;
import com.finanquest.entity.User;
import com.finanquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public User register(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByEmail(registrationDTO.email()).isPresent()) {
            throw new RuntimeException("O email informado já está em uso.");
        }

        User newUser = new User();
        newUser.setName(registrationDTO.name());
        newUser.setEmail(registrationDTO.email());

        // CORREÇÃO: Criptografar a palavra-passe antes de a salvar.
        newUser.setPassword(passwordEncoder.encode(registrationDTO.password()));

        newUser.setLevel(1);
        newUser.setExperiencePoints(0L);

        return userRepository.save(newUser);
    }

    public AuthResponseDTO login(LoginRequestDTO loginDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.email(),
                        loginDTO.password()
                )
        );

        var user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado após autenticação."));

        var jwtToken = jwtService.generateToken((UserDetails) user);

        var userProfile = new UserProfileResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLevel(),
                user.getExperiencePoints()
        );

        return new AuthResponseDTO(jwtToken, userProfile);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, UserRegistrationDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        existingUser.setName(dto.name());
        existingUser.setEmail(dto.email());
        existingUser.setPassword(dto.password()); // futuramente: aplicar BCrypt aqui

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        userRepository.deleteById(id);
    }

}
