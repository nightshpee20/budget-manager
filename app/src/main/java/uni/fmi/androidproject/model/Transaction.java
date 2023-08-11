package uni.fmi.androidproject.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    public static final String T_TRANSACTION = "T_TRANSACTION",
                               ID = "ID",
                               DESCRIPTION = "DESCRIPTION",
                               AMOUNT = "AMOUNT",
                               DATE = "DATE",
                               IS_INCOME = "IS_INCOME",
                               IS_RECURRING = "IS_RECURRING",
                               INTERVAL = "INTERVAL";
    private Integer id;
    private String description;
    private Double amount;
    private Date date;
    private String formattedDate;
    private Boolean isIncome;
    private Boolean isRecurring;
    private Integer interval;

    private final SimpleDateFormat simpleDateFormat;

    public Transaction() {
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    public Transaction(String description, Double amount, Date date, Boolean isIncome, Boolean isRecurring, Integer interval) {
        this();
        this.description = description;
        this.amount = amount;
        this.date = date;
        formattedDate = simpleDateFormat.format(date);
        this.isIncome = isIncome;
        this.isRecurring = isRecurring;
        this.interval = interval;
    }

    public Transaction(Integer id, String description, Double amount, Date date, Boolean isIncome, Boolean isRecurring, Integer interval) {
        this(description, amount, date, isIncome, isRecurring, interval);
        this.id = id;
    }

    @Override
    public String toString() {
        return "Transaction{ " +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date='" + formattedDate + '\'' +
                ", isExpense=" + isIncome +
                ", isRecurring=" + isRecurring +
                ", interval=" + interval +
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

    public Boolean getIsIncome() {
        return isIncome;
    }

    public void setIsIncome(Boolean isIncome) {
        this.isIncome = isIncome;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
