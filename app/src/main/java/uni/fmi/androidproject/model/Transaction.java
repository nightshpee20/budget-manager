package uni.fmi.androidproject.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    public static final String T_TRANSACTION = "T_TRANSACTION",
                               ID = "ID",
                               DESCRIPTION = "DESCRIPTION",
                               AMOUNT = "AMOUNT",
                               DATE = "DATE",
                               IS_EXPENSE = "IS_EXPENSE",
                               IS_RECURRING = "IS_RECURRING";
    private Integer id;
    private String description;
    private Double amount;
    private Date date;
    private String formattedDate;
    private Boolean isExpense;
    private Boolean isRecurring;

    private final SimpleDateFormat simpleDateFormat;

    public Transaction() {
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    public Transaction(String description, Double amount, Date date, Boolean isExpense, Boolean isRecurring) {
        this();
        this.description = description;
        this.amount = amount;
        this.date = date;
        formattedDate = simpleDateFormat.format(date);
        this.isExpense = isExpense;
        this.isRecurring = isRecurring;
    }

    public Transaction(Integer id, String description, Double amount, Date date, Boolean isExpense, Boolean isRecurring) {
        this(description, amount, date, isExpense, isRecurring);
        this.id = id;
    }

    @Override
    public String toString() {
        return "Transaction{ " +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date='" + formattedDate + '\'' +
                ", isExpense=" + isExpense +
                ", isRecurring=" + isRecurring +
                " }";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        formattedDate = null == date ? null : simpleDateFormat.format(date);
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public Boolean getIsExpense() {
        return isExpense;
    }

    public void setIsExpense(Boolean isExpense) {
        this.isExpense = isExpense;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }
}
