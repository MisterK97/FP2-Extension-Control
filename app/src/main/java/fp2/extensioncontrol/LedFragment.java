package fp2.extensioncontrol;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.felhr.usbserial.UsbSerialDevice;

public class LedFragment extends Fragment {
    private Switch[] switches;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.led_fragment, container, false);
        switches = new Switch[]{
                view.findViewById(R.id.switch1),
                view.findViewById(R.id.switch2),
                view.findViewById(R.id.switch3),
                view.findViewById(R.id.switch4),
                view.findViewById(R.id.switch5),
                view.findViewById(R.id.switch6),
                view.findViewById(R.id.switch7),
                view.findViewById(R.id.switch8)
        };

        for (Switch s: switches){
            s.setOnCheckedChangeListener((button, isChecked) -> sendLedValues());
        }
        return view;
    }

    /**
     * Send the values off all LEDs trough the serial port.
     */
    private void sendLedValues() {
        UsbSerialDevice serialPort = ((MainActivity) getActivity()).getSerialPort();

        if (serialPort == null) {
            return;
        }
        byte activatedLeds = 0;

        for (int i = 0; i < switches.length; i++) {
            activatedLeds |= (switches[i].isChecked() ? 1 : 0) << i;
        }
        serialPort.write(new byte[] {2, activatedLeds, 0, 0});
    }
}
