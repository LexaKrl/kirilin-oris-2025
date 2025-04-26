package com.kirilin.springboot.service;


import com.kirilin.springboot.config.MailConfig;
import com.kirilin.springboot.dto.CreateUserDto;
import com.kirilin.springboot.dto.UserDto;
import com.kirilin.springboot.entity.User;
import com.kirilin.springboot.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JavaMailSender mailSender;
    private final MailConfig mailConfig;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder encoder,
                       JavaMailSender mailSender,
                       MailConfig mailConfig) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.mailSender = mailSender;
        this.mailConfig = mailConfig;
    }

    public UserDto create(CreateUserDto dto, String baseUrl) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        String verification = UUID.randomUUID().toString();
        user.setVerificationCode(verification);

        // send verification code
        sendVerificationEmail(dto, baseUrl, verification);

        return UserDto.fromUser(userRepository.save(user));
    }

    @Transactional
    public boolean verify(String username, String code) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else if (user.get().isEnabled()) {
            return true;
        } else if (user.get().getVerificationCode().equals(code)){
            userRepository.updateEnabledStatus(username, true);
            return true;
        }
        return false;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserDto::fromUser).collect(Collectors.toList());
    }

    private void sendVerificationEmail(CreateUserDto dto, String baseUrl, String verificationCode) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        String content = mailConfig.getContent();

        try {
            mimeMessageHelper.setFrom(mailConfig.getFrom(), mailConfig.getSender());
            mimeMessageHelper.setTo(dto.getEmail());
            mimeMessageHelper.setSubject(mailConfig.getSubject());

            content = content.replace("{name}", dto.getUsername());
            content = content.replace("{url}", baseUrl + "/{username}/verification?code=" + verificationCode);
            content = content.replace("{username}", dto.getUsername());

            mimeMessageHelper.setText(content, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

