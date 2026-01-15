package pl.agh.edu.library.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // Usuwamy JavaMailSender, żeby nie wymagał konfiguracji SMTP
    // private final JavaMailSender emailSender;

    public EmailService() {
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        // Zamiast wysyłać, wypisujemy na konsolę (SYMULACJA)
        System.out.println("\n================ EMAIL SIMULATION ================");
        System.out.println("TO: " + to);
        System.out.println("SUBJECT: " + subject);
        System.out.println("TEXT:\n" + text);
        System.out.println("==================================================\n");
    }
}
