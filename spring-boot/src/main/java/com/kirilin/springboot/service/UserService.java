package com.kirilin.springboot.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirilin.springboot.config.MailConfig;
import com.kirilin.springboot.dto.CreateUserDto;
import com.kirilin.springboot.dto.UserDto;
import com.kirilin.springboot.entity.User;
import com.kirilin.springboot.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JavaMailSender mailSender;
    private final MailConfig mailConfig;

    private static final String DAILY_CONTENT = """
            Today ruble is: {ruble}
            """;

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

    @Scheduled(cron = "0 0 9 * * ?", zone = "Europe/Moscow")
    public void sendDailyNotification() {
        List<User> users = userRepository.findAllByEnabled(true);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        String content = dailyMessage();

        for (User user : users) {
            try {
                mimeMessageHelper.setFrom(mailConfig.getFrom(), mailConfig.getSender());
                mimeMessageHelper.setTo(user.getEmail());
                mimeMessageHelper.setSubject(mailConfig.getSubject());
                mimeMessageHelper.setText(content, true);

                mailSender.send(mimeMessage);
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

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

    public String dailyMessage() {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://openexchangerates.org/api/latest.json?app_id=%s&symbols=%s".formatted(
                            "e79b9f808bc749a8a504307f73a48dd9",
                            "RUB"
                    ))
                    .get()
                    .addHeader("accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body().string());
            double rate = root.path("rates").path("RUB").asDouble();

            String content = DAILY_CONTENT;
            content = content.replace("{ruble}", String.valueOf(rate));

            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

