package edith.example.capturardatos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener {
    Button btnVerSensores, btnCapturarDatos;
    private Intent inCapturarDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapturarDatos = findViewById(R.id.btnCapturarDatos);
        btnVerSensores = findViewById(R.id.btnVerSensores);

        inCapturarDatos = new Intent(this, SensoresActivity.class);

        btnCapturarDatos.setOnClickListener(this);
        btnVerSensores.setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCapturarDatos:
                startActivity(inCapturarDatos);
                break;
            case R.id.btnVerSensores:
                cargarFragment(new VerSensoresFragment());
                break;
        }
    }

    private void cargarFragment(Fragment fragment) {
        //Muestra el fragmento
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flVerSensores, fragment).commit();
        }
    }
}
