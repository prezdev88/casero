package cl.casero;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import java.util.List;

import cl.casero.adapter.TransactionAdapter;
import cl.casero.model.Customer;
import cl.casero.model.util.K;
import cl.casero.model.Transaction;
import cl.casero.model.Resource;
import cl.casero.model.util.Util;
import cl.casero.service.CustomerService;
import cl.casero.service.TransactionService;
import cl.casero.service.impl.CustomerServiceImpl;
import cl.casero.service.impl.TransactionServiceImpl;

public class CustomerViewActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView debtTextView;
    private ListView detailListView;
    private Switch orderSwitch;

    private CustomerService customerService;
    private TransactionService transactionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_view);

        customerService = new CustomerServiceImpl();
        transactionService = new TransactionServiceImpl();

        loadComponents();
        loadListeners();
        loadTransactions();
        loadDetailListViewOnLongClick();
    }

    private void loadDetailListViewOnLongClick() {
        detailListView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            CharSequence[] options = Resource.getStringArray(R.array.transaction_options);

            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerViewActivity.this);
            builder.setTitle(Resource.getString(R.string.choose_an_option));

            builder.setItems(options, (dialogInterface, optionIndex) -> {
                // TODO: Cambiar por enum
                if (optionIndex == 0) { // eliminar transacción
                    Transaction transaction = transactionService.readById(id);
                    String deleteTransactionConfirm = Resource.getString(R.string.delete_transaction_confirm);
                    String deleteTransactionConfirmDetailMessage = getConfirmDetailMessage(transaction);
                    deleteTransactionConfirm = deleteTransactionConfirm.replace("{0}", deleteTransactionConfirmDetailMessage);

                    new AlertDialog
                            .Builder(CustomerViewActivity.this)
                            .setMessage(deleteTransactionConfirm)
                            .setNegativeButton(Resource.getString(R.string.no), null)
                            .setPositiveButton(Resource.getString(R.string.yes), (dialog, which) -> {
                                transactionService.delete(id);
                                loadTransactions();
                                Util.message(CustomerViewActivity.this.getApplicationContext(), "Transacción eliminada");
                            })
                            .show();
                }
            });

            builder.show();

            return false;
        });
    }

    private String getConfirmDetailMessage(Transaction transaction) {
        return transaction.getDetail();
    }

    private void loadTransactions() {
        // TODO: Investigar como pasar el id del cliente de otra forma, no como atributo estático
        Customer customer = customerService.readById(K.customerId);

        nameTextView.setText(customer.getName());
        debtTextView.setText(customer.getFormattedDebt());

        List<Transaction> transactions = transactionService.readByCustomer(customer.getId(), false);

        detailListView.setAdapter(new TransactionAdapter(CustomerViewActivity.this, transactions));
    }

    private void loadListeners() {
        // TODO: Separar listeners
        orderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Customer customer = customerService.readById(K.customerId);

            nameTextView.setText(customer.getName());

            List<Transaction> transactions = transactionService.readByCustomer(customer.getId(), isChecked);

            detailListView.setAdapter(new TransactionAdapter(CustomerViewActivity.this, transactions));
        });

        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer customer = customerService.readById(K.customerId);

                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerViewActivity.this);

                String addressOf = Resource.getString(R.string.address_of);
                addressOf = addressOf.replace("{0}", customer.getName());
                builder.setTitle(addressOf);

                builder.setMessage(customer.getAddress() + ", " + customer.getSector());
                builder.setPositiveButton(Resource.getString(R.string.ok), null);
                builder.create().show();
            }
        });
    }

    private void loadComponents () {
        nameTextView = (TextView) findViewById(R.id.customerNameTextView);
        debtTextView = (TextView) findViewById(R.id.customerDebtTextView);
        detailListView = (ListView) findViewById(R.id.detailListView);
        orderSwitch = (Switch) findViewById(R.id.orderSwitch);
    }
}