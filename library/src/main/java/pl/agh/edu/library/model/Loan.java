package pl.agh.edu.library.model;

import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String state;
    private Date reservationDate;
    private Date loanDate;
    private Date returnDate;
    private Date dueDate;
    
    private boolean extended = false; // Czy przedłużono?

    @Transient // Pole nie zapisywane w bazie, obliczane w locie
    private double penalty = 0.0;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    public Loan() {}


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public double getPenalty() {
        // Obliczanie kary w locie
        if ("LOANED".equals(state) && dueDate != null) {
            LocalDate due = dueDate.toLocalDate();
            LocalDate now = LocalDate.now();
            
            if (now.isAfter(due)) {
                long daysOverdue = ChronoUnit.DAYS.between(due, now);
                double calculatedPenalty = daysOverdue * 0.50;
                return Math.min(calculatedPenalty, 70.0); // Max 70 PLN
            }
        }
        return 0.0;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
