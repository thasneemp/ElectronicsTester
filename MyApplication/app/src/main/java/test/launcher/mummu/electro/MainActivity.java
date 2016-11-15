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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private EditText mUrlEditText;
    private EditText mMessageEditText;
    private EditText mRepeatEditText;
    private EditText mDelayEditText;
    private EditText mRelayEditText;
    private EditText mRelayOneEditText;
    private EditText mRelayTwoEditText;
    private EditText mRelayThreeEditText;
    private AppCompatCheckBox mRelaySwitchCompat;
    private AppCompatButton mSendAppCompatButton;
    private ProgressBar mProgressBar;
    private SwitchCompat mSecondSwitch;
    private AppCompatTextView mText;


    private int j = 0;
    private int i = 0;
    private Timer timer;
    private TimerTask timerTask;

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
        mRelayEditText = (EditText) findViewById(R.id.relayIdEditText);

        mRelayOneEditText = (EditText) findViewById(R.id.relayIdOneEditText);
        mRelayTwoEditText = (EditText) findViewById(R.id.relayIdTwoEditText);
        mRelayThreeEditText = (EditText) findViewById(R.id.relayIdThreeEditText);

        mRelaySwitchCompat = (AppCompatCheckBox) findViewById(R.id.relaySwitch);
        mSendAppCompatButton = (AppCompatButton) findViewById(R.id.sendButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSecondSwitch = (SwitchCompat) findViewById(R.id.secondSwitch);
        mText = (AppCompatTextView) findViewById(R.id.secondSwitchText);

        mSendAppCompatButton.setOnClickListener(this);

        mSecondSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mText.setText("S");
        } else {
            mText.setText("M");
        }
    }

    @Override
    public void onClick(View v) {

        i = 0;
        j = 0;
        i = Integer.parseInt(mRepeatEditText.getText().toString());
        mProgressBar.setMax(i);
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (j >= i) {
                            if (timerTask != null) {
                                Toast.makeText(MainActivity.this, "Test run completed", Toast.LENGTH_SHORT).show();
                                timer.cancel();
                                timer.purge();
                                timerTask.cancel();
                                timerTask = null;
                                timer = null;
                            }

                        } else {
                            mRelaySwitchCompat.setChecked(!mRelaySwitchCompat.isChecked());
                            doSend();
                            j++;
                            mProgressBar.setProgress(j);
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

    private void doSend() {


        JsonObject json = new JsonObject();
        json.addProperty("RelayID", mRelayEditText.getText().toString());
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
