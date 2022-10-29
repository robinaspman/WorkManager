package com.example.myworkmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button runWork = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        runWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                Data data = new Data.Builder()
                        .putString(MyWorker.DATA_KEY, " Data from Activity")
                        .build();

                PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                        .Builder(MyWorker.class, 10, TimeUnit.HOURS)
                        .build();

                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setConstraints(constraints)
                        .addTag("MyWork")
                        .setInputData(data)
                        .build();

                WorkManager.getInstance(getApplicationContext())
                        .enqueueUniquePeriodicWork("Unique", ExistingPeriodicWorkPolicy.KEEP,
                                periodicWorkRequest);
//                        .beginWith(workRequest)
//                        .then(workRequest)
//                        .then(workRequest)
//                        .enqueue();
                WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
                WorkManager.getInstance(getApplicationContext())
                        .getWorkInfoByIdLiveData(workRequest.getId())
                        .observe(MainActivity.this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                                    String result = workInfo
                                            .getOutputData().getString(MyWorker.DATA_KEY);
                                    textView.setText(result);

                                }

                            }
                        });
            }
        });
    }

    private void cancelWork(WorkRequest workRequest) {
        WorkManager.getInstance(getApplicationContext())
                .cancelWorkById(workRequest.getId());
    }
}