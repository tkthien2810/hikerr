package com.example.hiker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Observation {
    private String observationText;
    private String observationTime;
    private String additionalComment;

    public Observation(String text, String observationText, String additionalComment) {
        this.observationText = observationText;
        this.additionalComment = additionalComment;

        // Lấy thời gian hiện tại làm thời gian quan sát (có thể thay bằng giá trị thực tế từ hệ thống)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.observationTime = dateFormat.format(new Date());
    }

    public String getObservationText() {
        return observationText;
    }

    public String getObservationTime() {
        return observationTime;
    }

    public String getAdditionalComment() {
        return additionalComment;
    }
}