package uk.co.scottlogic.gradProject.server.repos.documents;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(indexes = {@Index(name = "idx_transaction_customer", columnList = "customer"),
        @Index(name = "idx_transaction_category", columnList = "category"),
        @Index(name = "idx_transaction_payee", columnList = "payee"),
        @Index(name = "idx_transaction_date", columnList = "date")})
public class Transaction {

    @Id
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer")
    private ApplicationUser customer;

    private long amount;

    // The note
    private String description;

    private Date date;

    // True = Cleared, false = pending
    private boolean state;

    public Transaction(ApplicationUser customer, long amount, String description) {
        this.customer = customer;
        this.amount = amount;
        this.description = description;
        this.date = new Date();
        id = UUID.randomUUID();
    }

    public Transaction() {
    }

    public int getMonth() {
        return new DateTime(this.date).getMonthOfYear();
    }

    public int getYear() {
        return new DateTime(this.date).getYear();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ApplicationUser getCustomer() {
        return customer;
    }

    public void setCustomer(ApplicationUser customer) {
        this.customer = customer;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }



}