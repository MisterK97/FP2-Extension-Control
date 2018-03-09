package fp2.extensioncontrol;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.christophesmet.android.views.colorpicker.ColorPickerView;
import com.felhr.usbserial.UsbSerialDevice;

public class SoundFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sound_fragment, container, false);

        ColorPickerView colorPickerView = view.findViewById(R.id.colorpicker1);
        colorPickerView.setColorListener(this::sendColor);

        Switch onOffSwitch = view.findViewById(R.id.switch9);
        onOffSwitch.setOnCheckedChangeListener((button, isChecked) -> sendColor(isChecked ? colorPickerView.getColor() : 0));
        return view;
    }

    /**
     * Send the RGB color trough the serial port.
     */
    private void sendColor(int argb) {
        UsbSerialDevice serialPort = ((MainActivity) getActivity()).getSerialPort();

        if (serialPort == null) {
            return;
        }
        byte red = (byte) (0xFF & (argb >> 16));
        byte green = (byte) (0xFF & (argb >> 8));
        byte blue = (byte) (0xFF & argb);
        serialPort.write(new byte[]{4, red, green, blue});
    }
}
