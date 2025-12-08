package pl.agh.edu.library.model;

import jakarta.persistence.*;

import java.sql.Date;

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
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;


}
