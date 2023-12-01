package com.example.hiker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private TextView textViewHikeName;
    private TextView textViewObservations;
    private EditText editTextSearch;
    private Button buttonSearch;
    private Button buttonBackToHome;
    private Button buttonDelete;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        databaseHelper = new DatabaseHelper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Ánh xạ các thành phần giao diện
        textViewHikeName = findViewById(R.id.textViewHikeName);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);
        buttonDelete = findViewById(R.id.buttonDelete);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            long hikeId = intent.getLongExtra("Hike_Id", -1);
            if (hikeId != -1) {
                // Lấy thông tin chuyến đi và hiển thị lên TextView
                String hikeDetails = databaseHelper.getHikeDetailsByName(String.valueOf(hikeId));
                textViewHikeName.setText("Hike Details:\n" + hikeDetails);

                // Lấy thông tin quan sát và hiển thị lên TextView
                String observationDetails = getObservationDetailsForHike(hikeId);
                textViewObservations.setText(observationDetails);
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin chuyến đi", Toast.LENGTH_SHORT).show();
            }
        }

        // Xử lý sự kiện khi nhấn nút "Back to Home"
        buttonBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển về trang chính
                startActivity(new Intent(DetailsActivity.this, HomeActivity.class));
                finish();
            }
        });

        // Xử lý sự kiện khi nhấn nút "Search"
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện tìm kiếm theo nội dung trong EditText
                String searchTerm = editTextSearch.getText().toString().trim();
                // Thực hiện tìm kiếm và hiển thị kết quả từ cơ sở dữ liệu
                searchAndDisplayResults(searchTerm);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHikeDetails();
            }
        });

    }

    private void searchAndDisplayResults(String searchTerm) {
        if (databaseHelper == null) {
            // Không thể thực hiện tìm kiếm nếu databaseHelper không được khởi tạo
            return;
        }
        String hikeDetails = databaseHelper.getHikeDetailsByName(searchTerm);

        // Hiển thị kết quả lên TextView
        textViewHikeName.setText(hikeDetails);
    }
    private void deleteHikeDetails() {
        String searchTerm = editTextSearch.getText().toString().trim();

        // Gọi hàm xóa từ DatabaseHelper
        boolean isDeleted = databaseHelper.deleteHikeDetailsByName(searchTerm);

        if (isDeleted) {
            Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Xóa không thành công", Toast.LENGTH_SHORT).show();
        }
    }
    private String getObservationDetailsForHike(long hikeId) {
        // Lấy danh sách quan sát từ database
        List<Observation> observations = databaseHelper.getAllObservationsForHike(hikeId);

        // Chuyển danh sách quan sát thành chuỗi
        StringBuilder observationDetails = new StringBuilder();
        for (Observation observation : observations) {
            observationDetails.append("Observation Text: ").append(observation.getObservationText()).append("\n");
            observationDetails.append("Time: ").append(observation.getObservationTime()).append("\n");
            observationDetails.append("Additional Comment: ").append(observation.getAdditionalComment()).append("\n");
            observationDetails.append("\n");
        }

        return observationDetails.toString();
    }
}