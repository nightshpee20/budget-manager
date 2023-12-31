package uni.fmi.androidproject.model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    public static class TransactionLocalDateDeserializer implements JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            long epochDay = json.getAsLong();
            return LocalDate.ofEpochDay(epochDay);
        }
    }

    public static class TransactionBooleanDeserializer implements JsonDeserializer<Boolean> {

        @Override
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            int intValue = json.getAsInt();
            return intValue != 0;
        }
    }

    public static class ExcludeTransactionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getName().equals("formattedDate");
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    public static class TransactionSerializer implements JsonSerializer<Transaction> {
        @Override
        public JsonElement serialize(Transaction transaction, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", transaction.getId());
            jsonObject.addProperty("description", transaction.getDescription());
            jsonObject.addProperty("amount", transaction.getAmount());
            jsonObject.addProperty("date", transaction.getDate().toEpochDay());
            jsonObject.addProperty("isIncome", transaction.getIsIncome());
            jsonObject.addProperty("isRecurring", transaction.getIsRecurring());
            jsonObject.addProperty("interval", transaction.getInterval());
            jsonObject.addProperty("userId",transaction.getUserId());

            return jsonObject;
        }
    }
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
    private LocalDate date;
    private String formattedDate;
    private Boolean isIncome;
    private Boolean isRecurring;
    private Integer interval;
    private Integer userId;

    private final DateTimeFormatter dateTimeFormatter;

    public Transaction() {
        dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    }

    public Transaction(String description, Double amount, LocalDate date, Boolean isIncome, Boolean isRecurring, Integer interval) {
        this();
        this.description = description;
        this.amount = amount;
        this.date = date;
        formattedDate = dateTimeFormatter.format(date);
        this.isIncome = isIncome;
        this.isRecurring = isRecurring;
        this.interval = interval;
    }

    public Transaction(Integer id, String description, Double amount, LocalDate date, Boolean isIncome, Boolean isRecurring, Integer interval) {
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
                ", isIncome=" + isIncome +
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
        formattedDate = null == date ? null : dateTimeFormatter.format(date);
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
