package pl.agh.edu.library.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.agh.edu.library.model.Loan;
import pl.agh.edu.library.repository.LoanRepository;
import pl.agh.edu.library.service.EmailService;

import java.time.LocalDate;
import java.util.List;

@Component
public class LoanScheduler {

    private final LoanRepository loanRepository;
    private final EmailService emailService;

    @Autowired
    public LoanScheduler(LoanRepository loanRepository, EmailService emailService) {
        this.loanRepository = loanRepository;
        this.emailService = emailService;
    }

    // Uruchamia się co 30 sekund (30000 ms) dla celów testowych
    @Scheduled(fixedRate = 30000)
    public void checkOverdueLoans() {
        System.out.println("[Scheduler] Checking for loans due tomorrow...");
        List<Loan> loans = loanRepository.findAll();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        for (Loan loan : loans) {
            if ("LOANED".equals(loan.getState()) && loan.getDueDate() != null) {
                LocalDate dueDate = loan.getDueDate().toLocalDate();
                
                // Jeśli termin mija jutro
                if (dueDate.isEqual(tomorrow)) {
                    String email = loan.getUser().getEmail();
                    String subject = "Przypomnienie o zwrocie książki";
                    String text = "Witaj " + loan.getUser().getFirstName() + ",\n\n" +
                            "Przypominamy, że termin zwrotu książki '" + loan.getBook().getName() + "' mija jutro (" + dueDate + ").\n" +
                            "Prosimy o terminowy zwrot.\n\n" +
                            "Pozdrawiamy,\nBiblioteka";
                    
                    emailService.sendSimpleMessage(email, subject, text);
                }
            }
        }
    }
}
