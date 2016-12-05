package test.launcher.mummu.electro.models;

import android.widget.EditText;

/**
 * Created by muhammed on 11/16/2016.
 */

public class DeviceModel {

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    private EditText editText;

    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceModel(String deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceModel() {
    }
}
