package cl.casero;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.BuildConfig;

import java.util.Calendar;

import cl.casero.listener.DebtForgivenessDateListener;
import cl.casero.listener.search.customer.OnCustomerClickListener;
import cl.casero.listener.search.customer.OnCustomerLongClickListener;
import cl.casero.listener.PaymentDateListener;
import cl.casero.listener.ReturnProductDateListener;
import cl.casero.listener.search.customer.SearchTextChangedListener;
import cl.casero.model.util.K;
import cl.casero.model.util.Util;
import cl.casero.model.Resource;
import cl.casero.service.StatisticsService;
import cl.casero.service.impl.StatisticsServiceImpl;

public class MainActivity extends AppCompatActivity {

    private EditText searchNameEditText;
    private ListView customersListView;
    private TextView versionTextView;

    private static MainActivity instance;

    private StatisticsService statisticsService;

    private OnDateSetListener paymentDateListener;
    private OnDateSetListener returnProductDateListener;
    private OnDateSetListener debtForgivenessDateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        statisticsService = new StatisticsServiceImpl();

        loadComponents();
        loadListeners();

        if (K.searchName != null) {
            searchNameEditText.setText(K.searchName);
            searchNameEditText.setSelection(K.searchName.length(), K.searchName.length());
        }

        versionTextView.setText("v" + BuildConfig.VERSION_NAME);
    }

    private void loadListeners() {
        searchNameEditText.addTextChangedListener(new SearchTextChangedListener());
        customersListView.setOnItemClickListener(new OnCustomerClickListener());
        customersListView.setOnItemLongClickListener(new OnCustomerLongClickListener());

        paymentDateListener = new PaymentDateListener();
        returnProductDateListener = new ReturnProductDateListener();
        debtForgivenessDateListener = new DebtForgivenessDateListener();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // el mes comienza de 0
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (id == 999) {
            // formulario de date de payment
            return new DatePickerDialog(this, paymentDateListener, year, month, day);
        } else if (id == 1) {
            // formulario de date de devolución
            return new DatePickerDialog(this, returnProductDateListener, year, month, day);
        } else if (id == 2) {
            // formulario de date de condonacion de debt
            return new DatePickerDialog(this, debtForgivenessDateListener, year, month, day);
        }
        return null;
    }

    private void loadComponents() {
        searchNameEditText = (EditText) findViewById(R.id.searchNameEditText);
        searchNameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        customersListView = (ListView) findViewById(R.id.customersListView);
        versionTextView = (TextView) findViewById(R.id.versionTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.create_customer_action) {
            Intent intent = new Intent(MainActivity.this, CreateCustomerActivity.class);
            MainActivity.this.startActivity(intent);
        } else if (id == R.id.view_statistics_action) {
            //MonthlyStatistic em = dao.getMonthlyStatistic("2016-01-01","2016-02-01");
            //Toast.makeText(MainActivity.this.getApplicationContext(), em.toString(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            MainActivity.this.startActivity(intent);
        } else if (id == R.id.see_total_debt_action) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(Resource.getString(R.string.total_debt));

            int totalDebt = statisticsService.getTotalDebt();

            builder.setMessage(Util.formatPrice(totalDebt));
            builder.setPositiveButton(Resource.getString(R.string.ok), null);

            builder.create().show();
        } else if (id == R.id.see_chart_action) {
            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
            MainActivity.this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Editable text = searchNameEditText.getText();
        String string = text.toString();

        searchNameEditText.setText("");
        searchNameEditText.setText(string);
        searchNameEditText.setSelection(string.length());
    }
}
