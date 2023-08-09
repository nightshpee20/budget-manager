package uni.fmi.androidproject.model;

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
    private String date;
    private Boolean isExpense;
    private Boolean isRecurring;

    public Transaction() {}

    public Transaction(Integer id, String description, Double amount, String date, Boolean isExpense, Boolean isRecurring) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.isExpense = isExpense;
        this.isRecurring = isRecurring;
    }

    @Override
    public String toString() {
        return "Transaction{ " +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
