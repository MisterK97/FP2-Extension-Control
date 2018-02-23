package fp2.extensioncontrol;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.felhr.usbserial.UsbSerialInterface;

import java.util.Arrays;

public class TempSensorFragment extends Fragment{
    private EditText textBox;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.temp_sensor_fragment, container, false);

        textBox = view.findViewById(R.id.editText);

        return view;
    }

    /**
     * Callback which triggers whenever data is read.
     */
    public final UsbSerialInterface.UsbReadCallback receiveCallback = bytes -> {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (textBox != null) {
                    String string = Arrays.toString(bytes);
                    textBox.append(string + '\n');
                }
            });
        }
    };
}
