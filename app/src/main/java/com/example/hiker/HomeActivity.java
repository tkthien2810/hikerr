package com.example.hiker;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import static com.example.hiker.repository.UserRepository.databaseHelper;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private EditText editTextHikeName, editTextLocation, editTextDate, editTextLength, editTextDifficulty, editTextDescription,
            editTextCustom1, editTextCustom2;
    private RadioGroup radioGroupParking;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private int hikeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        editTextHikeName = findViewById(R.id.editTextHikeName);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLength = findViewById(R.id.editTextLength);
        editTextDifficulty = findViewById(R.id.editTextDifficulty);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextCustom1 = findViewById(R.id.editTextCustom1);
        editTextCustom2 = findViewById(R.id.editTextCustom2);
        radioGroupParking = findViewById(R.id.radioGroupParking);

        Button buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        Button buttonViewDetails = findViewById(R.id.buttonViewDetails);
        Button buttonDeleteAll = findViewById(R.id.buttonDeleteAll);
        Button buttonGetLocation = findViewById(R.id.buttonGetLocation);
        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện xử lý khi người dùng nhấn nút Su
                submitForm();
            }
        });
        buttonViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi nhấn nút "View Details"
                Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
                // Truyền dữ liệu nếu cần thiết
                startActivity(intent);
            }
        });

        buttonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi nhấn nút "Delete All"
                // Gọi hàm xóa tất cả từ cơ sở dữ liệu
                deleteAllHikeDetails();
            }
        });
    }

    private void submitForm() {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        // Lấy giá trị từ các EditText
        String hikeName = editTextHikeName.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String length = editTextLength.getText().toString().trim();
        String difficulty = editTextDifficulty.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String custom1 = editTextCustom1.getText().toString().trim();
        String custom2 = editTextCustom2.getText().toString().trim();

        RadioGroup radioGroupParking = findViewById(R.id.radioGroupParking);
        int selectedId = radioGroupParking.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String parking = radioButton.getText().toString();


        long hiker_id = db.insertDetails(hikeName,location,date,parking,length,difficulty,description,custom1,custom1);
        Toast.makeText(this, "done" + hiker_id, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ObservationActivity.class);
                // ... thêm các dữ liệu khác vào intent
                startActivity(intent);

        if (TextUtils.isEmpty(hikeName) || TextUtils.isEmpty(location) || TextUtils.isEmpty(date) /* || ... */) {
            // Hiển thị thông báo lỗi nếu một trong các trường bắt buộc không được nhập
            saveObservationsForHike(hikeId);
            Intent detailsIntent = new Intent(this, DetailsActivity.class);
            intent.putExtra("Hike_Id", hikeId);
            startActivity(intent);
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();

            return;  // Không tiếp tục lưu vào cơ sở dữ liệu nếu có lỗi
        }

        // Nếu mọi thứ hợp lệ, tiếp tục lưu vào cơ sở dữ liệu
        saveToDatabase(hikeName, location, date, parking, length, difficulty, description,custom1,custom2);

    }
    private void saveObservationsForHike(long hikeId) {
        // Lấy danh sách quan sát từ adapter hoặc nơi khác
        List<Observation> observations = databaseHelper.getAllObservationsForHike(hikeId);


        // Lưu mỗi quan sát cùng với hikeId vào cơ sở dữ liệu
        for (Observation observation : observations) {
            databaseHelper.insertObservation(hikeId, observation.getObservationText(), observation.getObservationTime(), observation.getAdditionalComment());;
        }
    }
    private long saveToDatabase(String hikeName, String location, String date, String parking,
                               String length, String difficulty, String description,
                               String custom1, String custom2) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Thay thế các hằng số dưới đây bằng tên cột và bảng thực tế của bạn
        values.put("column_hike_name", hikeName);
        values.put("column_location", location);
        values.put("column_date", date);
        values.put("column_parking", parking);
        values.put("column_length", length);
        values.put("column_difficulty", difficulty);
        values.put("column_description", description);
        values.put("column_custom1", custom1);
        values.put("column_custom2", custom2);

        return db.insert(DatabaseHelper.TABLE_HIKERS, null, values);
    }
    private void deleteAllHikeDetails() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        boolean isDeleted = databaseHelper.deleteAllHikeDetails();

        if (isDeleted) {
            Toast.makeText(this, "Xóa tất cả không thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đã xóa tất cả thành công", Toast.LENGTH_SHORT).show();
        }
    }
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Xử lý thông tin vị trí thời gian thực ở đây
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Hiển thị thông báo (hoặc làm gì đó khác với thông tin vị trí)
                Toast.makeText(HomeActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();

                // Ngừng lắng nghe vị trí sau khi có thông tin
                locationManager.removeUpdates(this);

                // Lấy địa chỉ từ vị trí thời gian thực
                String address = getAddressFromLocation(latitude, longitude);

                // Hiển thị địa chỉ trong editTextLocation
                editTextLocation.setText(address);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        // Kiểm tra quyền và yêu cầu nếu cần
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Cài đặt lắng nghe vị trí thời gian thực
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // Yêu cầu quyền nếu chưa có
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Lấy địa chỉ từ đối tượng Address và định dạng thành một chuỗi
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
