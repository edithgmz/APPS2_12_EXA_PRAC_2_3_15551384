package edith.example.capturardatos;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerSensoresFragment extends Fragment {
    private Context context;
    private List<Sensor> lstSensores;

    public VerSensoresFragment() {
        // Required empty public constructor
    }

    @Override public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_ver_sensores, container, false);

        ListView lvSensores = linearLayout.findViewById(R.id.lvSensores);

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lstSensores = sensorManager.getSensorList(Sensor.TYPE_ALL);
        }
        String[] asSensores = new String[lstSensores.size()];
        int i = 0;
        for (Sensor sensor : lstSensores) {
            asSensores[i] = sensor.getName();
            i++;
        }
        lvSensores.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, asSensores));

        return linearLayout;
    }

}
