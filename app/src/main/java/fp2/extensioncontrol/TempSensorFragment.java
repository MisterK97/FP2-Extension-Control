package fp2.extensioncontrol;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.felhr.usbserial.UsbSerialInterface;

import java.util.LinkedList;
import java.util.Queue;

public class TempSensorFragment extends Fragment {
    private Queue<Byte> bufferedBytes = new LinkedList<>();
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
                    for (byte aByte : bytes) {
                        if (aByte == '\n') {
                            while (bufferedBytes.size() > 0) {
                                char c = (char) bufferedBytes.poll().byteValue();
                                textBox.append("" + c);
                            }
                            textBox.append("\u00b0C\n");
                        } else {
                            bufferedBytes.add(aByte);
                        }
                    }
                }
            });
        }
    };
}
