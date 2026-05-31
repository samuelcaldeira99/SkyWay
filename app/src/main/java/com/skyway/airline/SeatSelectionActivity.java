package com.skyway.airline;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {

    // 0=disponível, 1=ocupado, 2=extra legroom
    private static final int[][] LAYOUT = {
        {1, 1, 0,  0, 1, 0},
        {0, 1, 1,  0, 0, 1},
        {2, 2, 0,  0, 2, 2},  // extra legroom
        {0, 1, 0,  0, 0, 1},
        {1, 0, 1,  0, 1, 0},
        {0, 0, 1,  0, 1, 0},
        {1, 0, 0,  0, 0, 1},
        {0, 1, 0,  0, 0, 0},
        {1, 0, 1,  1, 0, 1},
        {0, 0, 0,  0, 1, 0},
    };

    private static final String[] COL_LABELS = {"A", "B", "C", "", "D", "E", "F"};
    private static final int MAX_SEATS = 3;

    private final List<String> selectedSeats = new ArrayList<>();
    private final List<Button> seatButtons   = new ArrayList<>();

    private TextView tvAssentosSelecionados, tvContagem;
    private ProgressBar progressAssentos;
    private Button btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        tvAssentosSelecionados = findViewById(R.id.tvAssentosSelecionados);
        tvContagem             = findViewById(R.id.tvContagem);
        progressAssentos       = findViewById(R.id.progressAssentos);
        btnConfirmar           = findViewById(R.id.btnConfirmar);

        // Recebe info do voo
        String infoVoo = getIntent().getStringExtra("info_voo");
        if (infoVoo != null) {
            ((TextView) findViewById(R.id.tvInfoVoo)).setText(infoVoo);
        }

        // Botão voltar
        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        // Monta grade
        montarGrade();

        // Botão confirmar
        btnConfirmar.setOnClickListener(v -> {
            String preco = getIntent().getStringExtra("preco");
            Toast.makeText(this,
                "✅ Assentos confirmados: " + String.join(", ", selectedSeats) +
                "\n💰 Total: " + (preco != null ? preco : "R$ 289") +
                "\n\nReserva realizada com sucesso!",
                Toast.LENGTH_LONG).show();

            // Volta para a tela inicial após 2s
            new android.os.Handler().postDelayed(this::finish, 2500);
        });
    }

    private void montarGrade() {
        LinearLayout container = findViewById(R.id.layoutAssentos);
        int seatSize  = dpToPx(40);
        int seatMargin = dpToPx(4);
        int aisleWidth = dpToPx(20);

        // Header colunas
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER);
        headerRow.setPadding(0, 0, 0, dpToPx(8));

        // espaço para número da fileira
        View numSpace = new View(this);
        numSpace.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(28), seatSize));
        headerRow.addView(numSpace);

        for (String lbl : COL_LABELS) {
            if (lbl.isEmpty()) {
                View aisle = new View(this);
                aisle.setLayoutParams(new LinearLayout.LayoutParams(aisleWidth, seatSize));
                headerRow.addView(aisle);
            } else {
                TextView tv = new TextView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(seatSize, seatSize);
                lp.setMargins(seatMargin, 0, seatMargin, 0);
                tv.setLayoutParams(lp);
                tv.setText(lbl);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(12);
                tv.setTextColor(ContextCompat.getColor(this, R.color.text_mid));
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
                headerRow.addView(tv);
            }
        }
        container.addView(headerRow);

        // Fileiras
        for (int row = 0; row < LAYOUT.length; row++) {
            LinearLayout rowView = new LinearLayout(this);
            rowView.setOrientation(LinearLayout.HORIZONTAL);
            rowView.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowLp.setMargins(0, seatMargin, 0, seatMargin);
            rowView.setLayoutParams(rowLp);

            // Número da fileira
            TextView rowNum = new TextView(this);
            rowNum.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(28), seatSize));
            rowNum.setText(String.valueOf(row + 1));
            rowNum.setGravity(Gravity.CENTER);
            rowNum.setTextSize(11);
            rowNum.setTextColor(ContextCompat.getColor(this, R.color.text_light));
            rowView.addView(rowNum);

            int seatIdx = 0;
            for (int col = 0; col < COL_LABELS.length; col++) {
                if (COL_LABELS[col].isEmpty()) {
                    View aisle = new View(this);
                    aisle.setLayoutParams(new LinearLayout.LayoutParams(aisleWidth, seatSize));
                    rowView.addView(aisle);
                } else {
                    int status = LAYOUT[row][seatIdx];
                    String seatId = COL_LABELS[col] + (row + 1);
                    Button seat = createSeatButton(seatId, status, seatSize, seatMargin);
                    rowView.addView(seat);
                    seatIdx++;
                }
            }

            // Fileira de extra legroom — label
            if (LAYOUT[row][0] == 2) {
                TextView extraLabel = new TextView(this);
                LinearLayout.LayoutParams elp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, seatSize);
                elp.setMargins(dpToPx(8), 0, 0, 0);
                extraLabel.setLayoutParams(elp);
                extraLabel.setText("Extra leg");
                extraLabel.setGravity(Gravity.CENTER);
                extraLabel.setTextSize(10);
                extraLabel.setTextColor(ContextCompat.getColor(this, R.color.orange));
                rowView.addView(extraLabel);
            }

            container.addView(rowView);
        }
    }

    private Button createSeatButton(String seatId, int status, int size, int margin) {
        Button btn = new Button(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.setMargins(margin, 0, margin, 0);
        btn.setLayoutParams(lp);
        btn.setText(seatId.substring(0, 1)); // só a letra
        btn.setTextSize(10);
        btn.setPadding(0, 0, 0, 0);

        if (status == 1) {
            // Ocupado
            btn.setBackgroundResource(R.drawable.seat_occupied);
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_mid));
            btn.setEnabled(false);
            btn.setText("✕");
        } else if (status == 2) {
            // Extra legroom disponível
            btn.setBackgroundResource(R.drawable.seat_extra);
            btn.setTextColor(Color.WHITE);
            btn.setEnabled(true);
            seatButtons.add(btn);
            final String id = seatId;
            btn.setOnClickListener(v -> toggleSeat(btn, id, true));
        } else {
            // Disponível
            btn.setBackgroundResource(R.drawable.seat_available);
            btn.setTextColor(Color.WHITE);
            btn.setEnabled(true);
            seatButtons.add(btn);
            final String id = seatId;
            btn.setOnClickListener(v -> toggleSeat(btn, id, false));
        }
        return btn;
    }

    private void toggleSeat(Button btn, String seatId, boolean isExtra) {
        if (selectedSeats.contains(seatId)) {
            // Desselecionar
            selectedSeats.remove(seatId);
            btn.setBackgroundResource(isExtra ? R.drawable.seat_extra : R.drawable.seat_available);
        } else {
            if (selectedSeats.size() >= MAX_SEATS) {
                Toast.makeText(this, "Máximo de " + MAX_SEATS + " assentos atingido", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedSeats.add(seatId);
            btn.setBackgroundResource(R.drawable.seat_selected);
        }
        atualizarRodape();
    }

    private void atualizarRodape() {
        int count = selectedSeats.size();
        tvContagem.setText(count + " / " + MAX_SEATS);
        progressAssentos.setProgress(count);

        if (count == 0) {
            tvAssentosSelecionados.setText("Nenhum assento selecionado");
        } else {
            tvAssentosSelecionados.setText("Assentos: " + String.join(", ", selectedSeats));
        }

        boolean completo = count >= MAX_SEATS;
        btnConfirmar.setEnabled(completo);
        btnConfirmar.setAlpha(completo ? 1f : 0.5f);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
