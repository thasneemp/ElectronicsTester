package test.launcher.mummu.electro.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import test.launcher.mummu.electro.R;
import test.launcher.mummu.electro.models.DeviceModel;

/**
 * Created by muhammed on 11/16/2016.
 */
public class DeviceAdapter extends ArrayAdapter<DeviceModel> {
    private ArrayList<DeviceModel> list;
    private SetOnButtonListener listener;
    private ViewHolder viewHolder;

    public DeviceAdapter(Context context, ArrayList<DeviceModel> resource) {
        super(context, R.layout.items, resource);
        this.list = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.items, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mAddFrameLayout = (FrameLayout) convertView.findViewById(R.id.addButton);
            viewHolder.mRelayEditText = (EditText) convertView.findViewById(R.id.editText);
            viewHolder.mIconsImageView = (ImageView) convertView.findViewById(R.id.icons);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mAddFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (position == list.size() - 1) {
                        listener.onItemAdded(position, list.get(position));

                    } else {
                        listener.onItemRemoved(position, list.get(position));

                    }

                }
            }
        });

        viewHolder.mRelayEditText.setText(list.get(position).getDeviceId());

        if (position == list.size() - 1) {
            viewHolder.mIconsImageView.setImageResource(R.drawable.add);
            viewHolder.mIconsImageView.setTag("add");
        } else {
            viewHolder.mIconsImageView.setImageResource(R.drawable.remove);
            viewHolder.mIconsImageView.setTag("remove");
        }
        list.get(position).setEditText(viewHolder.mRelayEditText);

        return convertView;
    }

    private static class ViewHolder {
        EditText mRelayEditText;
        FrameLayout mAddFrameLayout;
        ImageView mIconsImageView;
    }

    public void setOnButtonListener(SetOnButtonListener listener) {
        this.listener = listener;
    }

    public interface SetOnButtonListener {

        void onItemAdded(int pos, DeviceModel model);

        void onItemRemoved(int pos, DeviceModel model);
    }
}
