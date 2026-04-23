package com.padelplay.server.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoRecordatorioService {

    private final ObjectProvider<JavaMailSender> javaMailSenderProvider;
    private final String remitente;

    public CorreoRecordatorioService(ObjectProvider<JavaMailSender> javaMailSenderProvider,
                                     @Value("${padelplay.reminders.from:noreply@padelplay.com}") String remitente) {
        this.javaMailSenderProvider = javaMailSenderProvider;
        this.remitente = remitente;
    }

    public void enviarRecordatorio(String destinatario, String asunto, String cuerpo) {
        JavaMailSender javaMailSender = javaMailSenderProvider.getIfAvailable();
        if (javaMailSender == null) {
            throw new IllegalStateException("No hay un servidor de correo configurado.");
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);

        javaMailSender.send(mensaje);
    }
}