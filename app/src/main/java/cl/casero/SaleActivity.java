package cl.casero;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cl.casero.model.Customer;
import cl.casero.model.Resource;
import cl.casero.model.enums.SaleType;
import cl.casero.model.Transaction;
import cl.casero.model.enums.TransactionType;
import cl.casero.model.util.K;
import cl.casero.model.util.Util;
import cl.casero.service.CustomerService;
import cl.casero.service.TransactionService;
import cl.casero.service.impl.CustomerServiceImpl;
import cl.casero.service.impl.TransactionServiceImpl;

public class SaleActivity extends AppCompatActivity {

    private TextView saleCustomerNameTextView;
    private TextView saleDateTextView;
    private EditText saleDetailEditText;
    private EditText saleItemsCountEditText;
    private EditText saleAmountEditText;
    private Button saleDateButton;
    private Button saleCreateButton;

    private Customer customer;

    private CustomerService customerService;

    // TODO: Separar si o si esto
    /*FECHA!*/
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int anio, int mes, int dia) {
            // el mes comienza de 0

            SimpleDateFormat f = new SimpleDateFormat(Resource.getString(R.string.database_date_pattern));
            SimpleDateFormat f2 = new SimpleDateFormat(Resource.getString(R.string.date_pattern));
            try {
                String selectedDate = anio + "-" + (mes + 1) + "-" + dia;
                K.date = f.parse(selectedDate);
                saleDateTextView.setText(f2.format(K.date));
            } catch (ParseException ex) {
            }
        }
    };
    /*FECHA!*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        customerService = new CustomerServiceImpl();

        loadComponents();
        loadCustomerName();
        loadListeners();
    }

    private void loadCustomerName() {
        customer = customerService.readById(K.customerId);
        saleCustomerNameTextView.setText(customer.getName());
    }

    private void loadListeners() {
        saleDateButton.setOnClickListener(v -> showDialog(999));

        saleCreateButton.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        CustomerService customerService = new CustomerServiceImpl();
                        TransactionService transactionService = new TransactionServiceImpl();

                        int subtotal = -1;
                        int itemsCount = -1;

                        try {
                            itemsCount = Integer.parseInt(saleItemsCountEditText.getText().toString());
                        } catch (NumberFormatException ex) {
                            Toast.makeText(
                                    SaleActivity.this,
                                    Resource.getString(R.string.only_numbers_in_items),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        if (itemsCount != -1) {
                            try {
                                subtotal = Integer.parseInt(saleAmountEditText.getText().toString());
                            } catch (NumberFormatException ex) {
                                Toast.makeText(
                                        SaleActivity.this,
                                        Resource.getString(R.string.only_numbers_in_total_price),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            if (subtotal != -1) {
                                Transaction transaction = new Transaction();

                                transaction.setCustomerId((int) K.customerId);

                                String sailDetail = saleDetailEditText.getText().toString();
                                String transactionDetail = Resource.getString(R.string.transaction_detail);

                                transactionDetail = transactionDetail.replace("{0}", sailDetail);
                                transactionDetail = transactionDetail.replace("{1}", String.valueOf(itemsCount));
                                transactionDetail = transactionDetail.replace("{2}", Util.formatPrice(subtotal));

                                transaction.setDetail(transactionDetail);
                                transaction.setDate(K.date);

                                int currentBalance = customerService.getDebt((int) K.customerId);

                                SaleType saleType = (currentBalance == 0 ? SaleType.NEW_SALE : SaleType.MAINTENANCE);

                                currentBalance = currentBalance + subtotal;

                                transaction.setBalance(currentBalance);
                                transaction.setAmount(subtotal);
                                transaction.setType(TransactionType.SALE.getId());

                                transactionService.createSale(transaction, itemsCount, saleType);

                                String maintenanceCreated = Resource.getString(R.string.maintenance_created);

                                maintenanceCreated = maintenanceCreated.replace("{0}", Util.formatPrice(currentBalance));

                                Toast.makeText(
                                        SaleActivity.this,
                                        maintenanceCreated,
                                        Toast.LENGTH_LONG
                                ).show();

                                Intent intent = new Intent(SaleActivity.this, MainActivity.class);
                                SaleActivity.this.startActivity(intent);
                            }
                        }

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            if (saleDetailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(
                        SaleActivity.this,
                        Resource.getString(R.string.enter_maintenance_detail),
                        Toast.LENGTH_SHORT
                ).show();
            } else if (saleItemsCountEditText.getText().toString().trim().equals("")) {
                Toast.makeText(
                        SaleActivity.this,
                        Resource.getString(R.string.enter_items_count),
                        Toast.LENGTH_SHORT
                ).show();
            } else if (saleAmountEditText.getText().toString().trim().equals("")) {
                Toast.makeText(
                        SaleActivity.this,
                        Resource.getString(R.string.enter_total_price),
                        Toast.LENGTH_SHORT
                ).show();
            } else if (saleDateTextView.getText().toString().equals(Resource.getString(R.string.saleDate))) {
                Toast.makeText(
                        SaleActivity.this,
                        Resource.getString(R.string.enter_sale_date),
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                try {
                    int itemsCount = Integer.parseInt(saleItemsCountEditText.getText().toString());

                    if (itemsCount > 0) {
                        try {
                            int subtotal = Integer.parseInt(saleAmountEditText.getText().toString());

                            if (subtotal > 0) {
                                String saleSummary = Resource.getString(R.string.sale_summary);

                                String saleAmountString = saleAmountEditText.getText().toString();
                                Integer saleAmount = Integer.parseInt(saleAmountString);

                                saleSummary = saleSummary.replace("{0}", customer.getName());
                                saleSummary = saleSummary.replace("{1}", saleDetailEditText.getText().toString());
                                saleSummary = saleSummary.replace("{2}", saleItemsCountEditText.getText().toString());
                                saleSummary = saleSummary.replace("{3}", Util.formatPrice(saleAmount));
                                saleSummary = saleSummary.replace("{4}", saleDateTextView.getText().toString());

                                String saleConfirm = Resource.getString(R.string.sale_confirm);

                                saleConfirm = saleConfirm.replace("{0}", saleSummary);

                                new AlertDialog
                                        .Builder(SaleActivity.this)
                                        .setMessage(saleConfirm)
                                        .setPositiveButton(Resource.getString(R.string.yes), dialogClickListener)
                                        .setNegativeButton(Resource.getString(R.string.no), dialogClickListener)
                                        .show();
                            } else {
                                Toast.makeText(
                                        SaleActivity.this,
                                        Resource.getString(R.string.negative_sale_price),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } catch (NumberFormatException ex) {
                            Toast.makeText(
                                    SaleActivity.this,
                                    Resource.getString(R.string.only_numbers_in_total_price),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    } else {
                        Toast.makeText(
                                SaleActivity.this,
                                Resource.getString(R.string.negative_items_count),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                } catch (NumberFormatException ex) {
                    Toast.makeText(
                            SaleActivity.this,
                            Resource.getString(R.string.only_numbers_in_items),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            // el mes comienza de 0
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(this, myDateListener, year, month, day);
        }

        return null;
    }

    private void loadComponents() {
        saleCustomerNameTextView = (TextView) findViewById(R.id.saleCustomerNameTextView);
        saleDateTextView = (TextView) findViewById(R.id.saleDateTextView);
        saleDetailEditText = (EditText) findViewById(R.id.saleDetailEditText);
        saleDetailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        saleItemsCountEditText = (EditText) findViewById(R.id.saleItemsCountEditText);
        saleAmountEditText = (EditText) findViewById(R.id.saleAmountEditText);
        saleDateButton = (Button) findViewById(R.id.saleDateButton);
        saleCreateButton = (Button) findViewById(R.id.saleCreateButton);
    }
}
