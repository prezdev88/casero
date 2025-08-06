package cl.casero;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cl.casero.model.MonthlyStatistic;
import cl.casero.model.util.K;
import cl.casero.model.util.Util;
import cl.casero.model.Resource;
import cl.casero.service.StatisticsService;
import cl.casero.service.impl.StatisticsServiceImpl;

public class StatisticsActivity extends AppCompatActivity {

    private Spinner yearSpinner;
    private Spinner monthSpinner;
    private Button startDateButton;
    private Button statisticsRangeButton;
    private Button endDateButton;
    private Button monthlyStatisticsButton;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private TextView finishedCardsTextView;
    private TextView newCardsTextView;
    private TextView maintenanceTextView;
    private TextView itemCountsTextView;
    private TextView paymentsTextView;
    private TextView salesTextView;
    private TextView titleTextView;

    private StatisticsService statisticsService;

    // TODO: Separar si o si esto
    // TODO: Arreglar algoritmo
    /*FECHA!*/
    private DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            // el mes comienza de 0

            SimpleDateFormat f = new SimpleDateFormat(Resource.getString(R.string.database_date_pattern));
            SimpleDateFormat f2 = new SimpleDateFormat(Resource.getString(R.string.statistics_date_pattern));
            try {
                Date fIni = f.parse(year + "-" + (month + 1) + "-" + day);
                K.startDate = year + "-" + ((month + 1) < 10 ? "0" + (month + 1) : (month + 1)) + "-" + (day < 10 ? "0" + day : day);
                startDateTextView.setText(f2.format(fIni));
            } catch (ParseException ex) {
            }
        }
    };

    // TODO: Arreglar algoritmo
    private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            // el mes comienza de 0

            SimpleDateFormat f = new SimpleDateFormat(Resource.getString(R.string.database_date_pattern));
            SimpleDateFormat f2 = new SimpleDateFormat(Resource.getString(R.string.statistics_date_pattern));
            try {
                Date fFin = f.parse(year + "-" + (month + 1) + "-" + day);
                K.endDate = year + "-" + ((month + 1) < 10 ? "0" + (month + 1) : (month + 1)) + "-" + (day < 10 ? "0" + day : day);
                endDateTextView.setText(f2.format(fFin));
            } catch (ParseException ex) {
            }
        }
    };
    /*FECHA!*/

    /*FECHA!*/
    // TODO: Investigar el deprecated
    @Override
    protected Dialog onCreateDialog(int id) {
        // el mes comienza de 0
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (id == 1) {
            return new DatePickerDialog(this, startDateListener, year, month, day);
        } else if (id == 2) {
            return new DatePickerDialog(this, endDateListener, year, month, day);
        }

        return null;
    }
    /*FECHA!*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statisticsService = new StatisticsServiceImpl();

        loadComponents();
        loadListeners();

        K.startDate = null;
        K.endDate = null;
    }

    private void loadListeners() {
        Util.loadYears(StatisticsActivity.this, yearSpinner);

        monthlyStatisticsButton.setOnClickListener(
                view -> {
                    String monthString = monthSpinner.getSelectedItem().toString();

                    int month = monthSpinner.getSelectedItemPosition();
                    int year = Integer.parseInt(yearSpinner.getSelectedItem().toString());

                    month++;

                    String startDate, endDate;

                    startDate = year + "-" + (month < 10 ? "0" + month : month) + "-01";

                    if (month == 12) {
                        endDate = (year + 1) + "-01-01";
                    } else {
                        endDate = year + "-" + ((month + 1) < 10 ? "0" + (month + 1) : (month + 1)) + "-01";
                    }

                    String statisticsTitle = Resource.getString(R.string.statistics_by_year_month);

                    statisticsTitle = statisticsTitle.replace("{0}", monthString);
                    statisticsTitle = statisticsTitle.replace("{1}", String.valueOf(year));

                    loadStatistics(startDate, endDate, false, statisticsTitle);
                }
        );

        startDateButton.setOnClickListener(view -> showDialog(1));
        endDateButton.setOnClickListener(v -> showDialog(2));

        statisticsRangeButton.setOnClickListener(
                view -> {
                    if (K.startDate != null && K.endDate != null) {
                        loadStatistics(K.startDate, K.endDate, true, Resource.getString(R.string.statistics_between_dates));
                    } else {
                        Toast.makeText(
                                StatisticsActivity.this,
                                Resource.getString(R.string.enter_both_dates),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void loadStatistics(String fecIni, String fecFin, boolean isRango, String titulo) {
        MonthlyStatistic monthlyStatistic = statisticsService.getMonthlyStatistic(fecIni, fecFin, isRango);

        titleTextView.setText(titulo);
        finishedCardsTextView.setText(String.valueOf(monthlyStatistic.getFinishedCardsCount()));
        newCardsTextView.setText(String.valueOf(monthlyStatistic.getNewCardsCount()));
        maintenanceTextView.setText(String.valueOf(monthlyStatistic.getMaintenanceCount()));
        itemCountsTextView.setText(String.valueOf(monthlyStatistic.getTotalItemsCount()));
        paymentsTextView.setText(Util.formatPrice(monthlyStatistic.getPaymentsCount()));
        salesTextView.setText(Util.formatPrice(monthlyStatistic.getSalesCount()));
    }

    private void loadComponents() {
        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        startDateButton = (Button) findViewById(R.id.startDateButton);
        statisticsRangeButton = (Button) findViewById(R.id.statisticsRangeButton);
        endDateButton = (Button) findViewById(R.id.endDateButton);
        monthlyStatisticsButton = (Button) findViewById(R.id.monthlyStatisticsButton);
        startDateTextView = (TextView) findViewById(R.id.startDateTextView);
        endDateTextView = (TextView) findViewById(R.id.endDateTextView);
        finishedCardsTextView = (TextView) findViewById(R.id.finishedCardsTextView);
        newCardsTextView = (TextView) findViewById(R.id.newCardsTextView);
        maintenanceTextView = (TextView) findViewById(R.id.maintenanceTextView);
        itemCountsTextView = (TextView) findViewById(R.id.itemCountsTextView);
        paymentsTextView = (TextView) findViewById(R.id.paymentsTextView);
        salesTextView = (TextView) findViewById(R.id.salesTextView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
    }
}
