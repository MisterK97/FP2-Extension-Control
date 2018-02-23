package fp2.extensioncontrol;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.felhr.usbserial.UsbSerialDevice;

public class IRRemoteFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ir_remote_fragment, container, false);

        Button[] buttons = new Button[] {
                view.findViewById(R.id.button1),
                view.findViewById(R.id.button2),
                view.findViewById(R.id.button3),
                view.findViewById(R.id.button4),
                view.findViewById(R.id.button5)
        };

        for (int i = 0; i < buttons.length; i++) {
            int command = i + 1;
            buttons[i].setOnClickListener(v -> sendCommandByte((byte) command));
        }
        return view;
    }

    /**
     * Send the command byte trough the serial port.
     */
    public void sendCommandByte(byte command) {
        UsbSerialDevice serialPort = ((MainActivity) getActivity()).getSerialPort();

        if (serialPort == null) {
            return;
        }
        serialPort.write(new byte[]{1, command, 0, 0});
    }
}
