package br.com.compass.challenge3SpringBoot.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Redefinição de senha";
        String text = """
            Olá,

            Você solicitou a redefinição de senha.
            Use o seguinte código/token para cadastrar uma nova senha:

            %s

            Se você não solicitou, ignore este e-mail.
            """.formatted(token);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("no-reply@suaapp.com");

        mailSender.send(message);
    }
}
