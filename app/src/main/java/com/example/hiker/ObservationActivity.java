package com.example.hiker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObservationActivity extends AppCompatActivity {

    private EditText editTextObservation;
    private EditText editTextAdditionalComment;
    private ListView observationListView;
    private List<Observation> observationList;
    private ObservationAdapter observationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);

        editTextObservation = findViewById(R.id.editTextObservation);
        editTextAdditionalComment = findViewById(R.id.editTextAdditionalComment);
        observationListView = findViewById(R.id.observationListView);

        observationList = new ArrayList<>();
        observationAdapter = new ObservationAdapter(this, R.layout.observation_item, observationList);
        observationListView.setAdapter(observationAdapter);

        Button buttonAddObservation = findViewById(R.id.buttonAddObservation);
        buttonAddObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addObservation();
            }
        });
    }

    private void addObservation() {
        String observationText = editTextObservation.getText().toString().trim();
        String additionalComment = editTextAdditionalComment.getText().toString().trim();
        String observationTime = getCurrentTime();

        Observation observation = new Observation(observationText, additionalComment, observationTime);
        observationList.add(observation);

        observationAdapter.notifyDataSetChanged();

        // Clear input fields
        editTextObservation.setText("");
        editTextAdditionalComment.setText("");
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public void onStopButtonClick(View view) {
        Intent intent = new Intent(this, DetailsActivity.class);
        // ... thêm các dữ liệu khác vào intent nếu cần thiết
        startActivity(intent);
    }

}