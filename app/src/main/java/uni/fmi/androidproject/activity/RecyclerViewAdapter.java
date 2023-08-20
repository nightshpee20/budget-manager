package uni.fmi.androidproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import uni.fmi.androidproject.R;
import uni.fmi.androidproject.dao.TransactionDao;
import uni.fmi.androidproject.model.Transaction;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout recyclerViewRow;
        ImageView isExpenseImageView;
        TextView descriptionTextView;
        TextView amountTextView;
        ImageView recurringImageView;
        TextView recurringTextView;

        TextView transactionIdTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerViewRow = itemView.findViewById(R.id.recyclerViewRow);
            isExpenseImageView = itemView.findViewById(R.id.isExpenseImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            recurringImageView = itemView.findViewById(R.id.recurringImageView);
            recurringTextView = itemView.findViewById(R.id.recurringTextView);
            transactionIdTextView = itemView.findViewById(R.id.transactionIdTextView);
        }
    }

    private Context context;
    private List<Transaction> transactionList;
    private Transaction currentTransaction;
    private TransactionDao transactionDao;
    private TextView balanceTextView;

    public RecyclerViewAdapter(Context context, List<Transaction> transactionList, TransactionDao transactionDao) {
        this.context = context;
        this.transactionList = transactionList;
        this.transactionDao = transactionDao;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_row, parent, false);
        return new RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.isExpenseImageView.setImageResource(transaction.getIsIncome() ? R.drawable.baseline_add_circle_24 : R.drawable.baseline_remove_circle_24);
        holder.descriptionTextView.setText(transaction.getDescription());
        holder.amountTextView.setText(transaction.getAmount().toString());
        holder.recurringImageView.setVisibility(transaction.getIsRecurring() ? View.VISIBLE : View.INVISIBLE);
        holder.transactionIdTextView.setText("" + transaction.getId());
        holder.transactionIdTextView.setVisibility(View.INVISIBLE);
        String text = null;
        if (transaction.getIsRecurring()) {
            text = switch (transaction.getInterval()) {
                case 0 -> "Every Day";
                case 1 -> "Every Week";
                case 2 -> "Every 2 Weeks";
                case 3 -> "Every 3 Weeks";
                case 4 -> "Every 4 Week";
                case 5 -> "Every Month";
                case 6 -> "Every 2 Months";
                case 7 -> "Every 3 Months";
                case 8 -> "Every 6 Months";
                case 9 -> "Every Year";
                default -> null;
            };
        }
        if (text != null)
            holder.recurringTextView.setText(text);
        holder.recurringTextView.setVisibility(transaction.getIsRecurring() ? View.VISIBLE : View.INVISIBLE);
        holder.recyclerViewRow.setOnClickListener(this::recyclerViewRowOnClick);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public void recyclerViewRowOnClick(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Choose an Option");
        alertDialogBuilder.setMessage("Please select an option:");

        alertDialogBuilder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConstraintLayout constraintLayout = (ConstraintLayout) view;
                int id = -1;
                for (int i = 0; i < constraintLayout.getChildCount(); i++)
                    if (constraintLayout.getChildAt(i).getId() == R.id.transactionIdTextView)
                        id = Integer.parseInt(((TextView)constraintLayout.getChildAt(i)).getText().toString());
                Transaction filter = new Transaction();
                filter.setId(id);
                Transaction transaction = transactionDao.getTransactionByFilter(filter).get(0);
                Intent intent = new Intent(context, ExpenseIncomeActivity.class);
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                String selectedDate = transaction.getDate().format(dateTimeFormatter);
                intent.putExtra("ID", transaction.getId());
                intent.putExtra("DATE", selectedDate);
                intent.putExtra("DESCRIPTION", transaction.getDescription());
                intent.putExtra("AMOUNT", transaction.getAmount());
                intent.putExtra("IS_INCOME", transaction.getIsIncome());
                intent.putExtra("IS_RECURRING", transaction.getIsRecurring());
                if (transaction.getIsRecurring())
                    intent.putExtra("INTERVAL", transaction.getInterval());
                ((Activity) context).startActivityForResult(intent, Activity.RESULT_OK);
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ConstraintLayout constraintLayout = (ConstraintLayout) view;
                int id = -1;
                for (int i = 0; i < constraintLayout.getChildCount(); i++)
                    if (constraintLayout.getChildAt(i).getId() == R.id.transactionIdTextView)
                        id = Integer.parseInt(((TextView)constraintLayout.getChildAt(i)).getText().toString());
                Transaction filter = new Transaction();
                filter.setId(id);
                Transaction transaction = transactionDao.getTransactionByFilter(filter).get(0);
                transactionDao.deleteTransaction(transaction);
                int index = -1;
                for (int i = 0; i < transactionList.size(); i++)
                    if (Objects.equals(transactionList.get(i).getId(), transaction.getId())) {
                        index = i;
                        break;
                    }
                transactionList.remove(index);
                Double balance = Double.parseDouble(balanceTextView.getText().toString().substring(9));
                balance += transaction.getIsIncome() ? transaction.getAmount() * -1 : transaction.getAmount();
                balanceTextView.setText("Balance: " + balance);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void setBalanceTextView(TextView balanceTextView) {
        this.balanceTextView = balanceTextView;
    }
}
