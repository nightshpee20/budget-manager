package uni.fmi.androidproject.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uni.fmi.androidproject.R;
import uni.fmi.androidproject.model.Transaction;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView isExpenseImageView;
        TextView descriptionTextView;
        TextView amountTextView;
        ImageView recurringImageView;
        TextView recurringTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            isExpenseImageView = itemView.findViewById(R.id.isExpenseImageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            recurringImageView = itemView.findViewById(R.id.recurringImageView);
            recurringTextView = itemView.findViewById(R.id.recurringTextView);
        }
    }

    private Context context;
    private List<Transaction> transactionList;

    public RecyclerViewAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
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
        holder.recurringTextView.setVisibility(transaction.getIsRecurring() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }
}
