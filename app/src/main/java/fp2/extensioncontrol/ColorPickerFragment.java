package fp2.extensioncontrol;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.christophesmet.android.views.colorpicker.ColorPickerView;
import com.felhr.usbserial.UsbSerialDevice;

public class ColorPickerFragment extends Fragment{
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.color_picker_fragment, container, false);

        ColorPickerView mColorPickerView = view.findViewById(R.id.colorpicker);
        mColorPickerView.setColorListener(this::sendColor);

        Button offButton = view.findViewById(R.id.off_button);
        offButton.setOnClickListener(v -> sendColor(0));
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

        int red = (argb >> 16) & 0xff;
        int green = (argb >>  8) & 0xff;
        int blue = (argb      ) & 0xff;

        serialPort.write(new byte[]{3, (byte) red, (byte) green,(byte) blue});
    }
}
