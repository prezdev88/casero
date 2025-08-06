package cl.casero;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.*;

import cl.casero.listener.create.customer.CreateCustomerBackButtonOnClickListener;
import cl.casero.listener.create.customer.CreateCustomerOnClickListener;
import cl.casero.model.Resource;
import cl.casero.service.StatisticsService;
import cl.casero.service.impl.StatisticsServiceImpl;

public class CreateCustomerActivity extends AppCompatActivity {

    private Button backButton;
    private Button createButton;
    private TextView countTextView;

    private final StatisticsService statisticsService;

    public CreateCustomerActivity(){
        this.statisticsService = new StatisticsServiceImpl();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);

        loadComponents();
        loadListeners();

        String customers = Resource.getString(R.string.customers);

        customers = customers.replace("{0}", String.valueOf(statisticsService.getCustomersCount()));

        countTextView.setText(customers);
    }

    private void loadListeners() {
        backButton.setOnClickListener(new CreateCustomerBackButtonOnClickListener(this));
        createButton.setOnClickListener(new CreateCustomerOnClickListener(this));
    }

    private void loadComponents() {
        backButton = (Button) findViewById(R.id.backButton);
        createButton = (Button) findViewById(R.id.createButton);
        countTextView = (TextView) findViewById(R.id.countTextView);
    }
}
