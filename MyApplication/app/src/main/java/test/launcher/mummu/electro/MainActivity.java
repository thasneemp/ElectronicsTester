package test.launcher.mummu.electro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import test.launcher.mummu.electro.adapters.DeviceAdapter;
import test.launcher.mummu.electro.models.DeviceModel;

public class MainActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener,
        DeviceAdapter.SetOnButtonListener {
    private EditText mUrlEditText;
    private EditText mMessageEditText;
    private EditText mRepeatEditText;
    private EditText mDelayEditText;
    private AppCompatCheckBox mRelaySwitchCompat;
    private AppCompatButton mSendAppCompatButton;
    private ProgressBar mProgressBar;
    private SwitchCompat mSecondSwitch;
    private AppCompatTextView mText;
    private ListView mListView;
    private ArrayList<DeviceModel> deviceModels;
    private int j = 0;
    private int i = 0;
    private Timer timer;
    private TimerTask timerTask;
    private DeviceAdapter deviceAdapter;
    private boolean pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUI();
    }

    private void setUI() {
        mUrlEditText = (EditText) findViewById(R.id.urlEditText);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mRepeatEditText = (EditText) findViewById(R.id.repeatCountEditText);
        mDelayEditText = (EditText) findViewById(R.id.delayEditText);

        mRelaySwitchCompat = (AppCompatCheckBox) findViewById(R.id.relaySwitch);
        mSendAppCompatButton = (AppCompatButton) findViewById(R.id.sendButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSecondSwitch = (SwitchCompat) findViewById(R.id.secondSwitch);
        mText = (AppCompatTextView) findViewById(R.id.secondSwitchText);
        mListView = (ListView) findViewById(R.id.listview);


        deviceModels = new ArrayList<>();
        deviceModels.add(new DeviceModel("50001"));

        deviceAdapter = new DeviceAdapter(this, deviceModels);

        mListView.setAdapter(deviceAdapter);

        deviceAdapter.setOnButtonListener(this);

        mSendAppCompatButton.setOnClickListener(this);

        mSecondSwitch.setOnCheckedChangeListener(this);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mText.setText("S");
        } else {
            mText.setText("Ms");
        }
    }

    @Override
    public void onClick(View v) {

        if (mSendAppCompatButton.getText().toString().equalsIgnoreCase("START")) {
            pause = false;
            mSendAppCompatButton.setText("PAUSE");
            i = Integer.parseInt(mRepeatEditText.getText().toString());
            mProgressBar.setMax(i);

            if (timer == null && timerTask == null) {
                timer = new Timer();

                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!pause) {
                                    if (j >= i) {
                                        if (timerTask != null) {
                                            mSendAppCompatButton.setText("START");
                                            Toast.makeText(MainActivity.this, "Test run completed", Toast.LENGTH_SHORT).show();
                                            timer.cancel();
                                            timer.purge();
                                            timerTask.cancel();
                                            timerTask = null;
                                            timer = null;
                                            j = 0;
                                            i = 0;
                                        }
                                    } else {
                                        mRelaySwitchCompat.setChecked(!mRelaySwitchCompat.isChecked());
                                        doSend();
                                        j++;
                                        mProgressBar.setProgress(j);
                                    }
                                } else {
                                    Log.d("TAG", "run: " + "paused");
                                }

                            }
                        });
                    }
                };
                if (mSecondSwitch.isChecked()) {
                    timer.scheduleAtFixedRate(timerTask, 0l, (Long.parseLong(mDelayEditText.getText().toString()) * 1000));
                } else {
                    timer.scheduleAtFixedRate(timerTask, 0l, (Long.parseLong(mDelayEditText.getText().toString()) * 1000) / 1000);
                }
            }

        } else {
            mSendAppCompatButton.setText("START");
            if (timerTask != null) {
                pause = true;
            }

        }

    }

    private void doSend() {


        for (int j = 0; j < deviceModels.size(); j++) {
            DeviceModel model = deviceModels.get(j);
            String s = model.getEditText().getText().toString();
            JsonObject json = new JsonObject();
            json.addProperty("RelayID", s);
            json.addProperty("DeviceState", mRelaySwitchCompat.isChecked());
            json.addProperty("MsgStatus", mMessageEditText.getText().toString());

            Log.d("TAG", "doSend: " + json.toString());


            Ion.with(this)
                    .load(mUrlEditText.getText().toString())
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {

                        }
                    });
        }
    }

    @Override
    public void onItemAdded(int pos, DeviceModel model) {
        deviceModels.add(new DeviceModel());
        model.setDeviceId(model.getEditText().getText().toString());
        deviceAdapter.notifyDataSetChanged();

        mListView.setSelection(pos);


    }

    @Override
    public void onItemRemoved(int pos, DeviceModel model) {
        deviceModels.remove(model);
        deviceAdapter.notifyDataSetChanged();
        mListView.setSelection(pos);
    }
}
