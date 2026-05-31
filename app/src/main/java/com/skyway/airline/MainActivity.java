package com.skyway.airline;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etDestino, etOrigem, etIda, etVolta;
    private TextView tvErroDestino, tvResultadoHeader;
    private LinearLayout layoutResultados;
    private Button btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etOrigem          = findViewById(R.id.etOrigem);
        etDestino         = findViewById(R.id.etDestino);
        etIda             = findViewById(R.id.etIda);
        etVolta           = findViewById(R.id.etVolta);
        tvErroDestino     = findViewById(R.id.tvErroDestino);
        tvResultadoHeader = findViewById(R.id.tvResultadoHeader);
        layoutResultados  = findViewById(R.id.layoutResultados);
        btnBuscar         = findViewById(R.id.btnBuscar);

        // Spinners de passageiros
        String[] passageiros = {"1 adulto", "2 adultos", "2 adultos, 1 criança", "3 adultos", "4 adultos"};
        ArrayAdapter<String> adapterPax = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, passageiros);
        adapterPax.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((android.widget.Spinner) findViewById(R.id.spinnerPassageiros)).setAdapter(adapterPax);
        ((android.widget.Spinner) findViewById(R.id.spinnerPassageiros)).setSelection(2);

        // Spinner de classe
        String[] classes = {"Econômica", "Econômica Premium", "Executiva", "Primeira Classe"};
        ArrayAdapter<String> adapterClasse = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, classes);
        adapterClasse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((android.widget.Spinner) findViewById(R.id.spinnerClasse)).setAdapter(adapterClasse);

        // Botão buscar
        btnBuscar.setOnClickListener(v -> buscarVoos());

        // Botões selecionar voo
        String infoVoo = buildInfoVoo();
        findViewById(R.id.btnSelecionarVoo1).setOnClickListener(v -> abrirAssentos(infoVoo, "R$ 289"));
        findViewById(R.id.btnSelecionarVoo2).setOnClickListener(v -> abrirAssentos(infoVoo, "R$ 312"));
        findViewById(R.id.btnSelecionarVoo3).setOnClickListener(v -> abrirAssentos(infoVoo, "R$ 198"));

        // Tabs
        configurarTabs();
    }

    private void configurarTabs() {
        TextView tabIda    = findViewById(R.id.tabIdaVolta);
        TextView tabSoIda  = findViewById(R.id.tabSoIda);
        TextView tabMulti  = findViewById(R.id.tabMultiplos);

        View.OnClickListener listener = v -> {
            tabIda.setBackgroundResource(R.drawable.tab_unselected);
            tabSoIda.setBackgroundResource(R.drawable.tab_unselected);
            tabMulti.setBackgroundResource(R.drawable.tab_unselected);
            tabIda.setTextColor(getColor(R.color.text_mid));
            tabSoIda.setTextColor(getColor(R.color.text_mid));
            tabMulti.setTextColor(getColor(R.color.text_mid));

            ((TextView) v).setBackgroundResource(R.drawable.tab_selected);
            ((TextView) v).setTextColor(getColor(R.color.white));

            if (v.getId() == R.id.tabSoIda) {
                etVolta.setEnabled(false);
                etVolta.setAlpha(0.4f);
                etVolta.setText("");
            } else {
                etVolta.setEnabled(true);
                etVolta.setAlpha(1f);
                if (etVolta.getText().toString().isEmpty()) {
                    etVolta.setText("22/06/2025");
                }
            }
        };

        tabIda.setOnClickListener(listener);
        tabSoIda.setOnClickListener(listener);
        tabMulti.setOnClickListener(listener);
    }

    private void buscarVoos() {
        String destino = etDestino.getText().toString().trim();

        if (destino.isEmpty()) {
            tvErroDestino.setVisibility(View.VISIBLE);
            etDestino.setBackgroundResource(R.drawable.field_bg_error);
            layoutResultados.setVisibility(View.GONE);
            return;
        }

        tvErroDestino.setVisibility(View.GONE);
        etDestino.setBackgroundResource(R.drawable.field_bg);

        String origem = etOrigem.getText().toString().trim();
        String data   = etIda.getText().toString().trim();
        tvResultadoHeader.setText("32 voos encontrados  |  " + origem + " → " + destino + "  |  " + data);
        layoutResultados.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Buscando voos...", Toast.LENGTH_SHORT).show();
    }

    private String buildInfoVoo() {
        String origem  = etOrigem.getText().toString().trim();
        String destino = etDestino.getText().toString().trim();
        String data    = etIda.getText().toString().trim();
        if (origem.isEmpty())  origem  = "GRU";
        if (destino.isEmpty()) destino = "GIG";
        if (data.isEmpty())    data    = "15/06/2025";
        return origem + " → " + destino + "  |  15 Jun";
    }

    private void abrirAssentos(String infoVoo, String preco) {
        Intent intent = new Intent(this, SeatSelectionActivity.class);
        intent.putExtra("info_voo", infoVoo);
        intent.putExtra("preco",    preco);
        startActivity(intent);
    }
}
