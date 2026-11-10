package br.com.estudioanaelizabeth.app;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.estudioanaelizabeth.app.data.ApiClient;
import br.com.estudioanaelizabeth.app.data.ApiService;
import br.com.estudioanaelizabeth.app.data.model.ConcluirRequest;
import br.com.estudioanaelizabeth.app.data.model.Evolucao;
import br.com.estudioanaelizabeth.app.data.model.Treino;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Tela inicial: treinos da semana + resumo da evolução da aluna. */
public class MainActivity extends AppCompatActivity {

    // TODO: substituir por login/identificação real da aluna (ex.: telefone + código por WhatsApp)
    private static final long ALUNA_ID_PROVISORIO = 1L;

    private ApiService api;
    private TreinosAdapter adapter;
    private TextView resumoEvolucao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = ApiClient.getService();
        resumoEvolucao = findViewById(R.id.resumoEvolucao);
        adapter = new TreinosAdapter(this::abrirDialogoConclusao);

        RecyclerView lista = findViewById(R.id.listaTreinos);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar();
    }

    private void carregar() {
        api.listarTreinos(ALUNA_ID_PROVISORIO).enqueue(new ApiCallback<List<Treino>>() {
            @Override
            void sucesso(List<Treino> corpo) {
                adapter.atualizar(corpo);
            }
        });

        api.evolucao(ALUNA_ID_PROVISORIO).enqueue(new ApiCallback<Evolucao>() {
            @Override
            void sucesso(Evolucao e) {
                String pace = e.paceMedioMinPorKm == null
                        ? getString(R.string.sem_pace)
                        : getString(R.string.pace_formatado, e.paceMedioMinPorKm);
                resumoEvolucao.setText(getString(
                        R.string.resumo_evolucao, e.totalTreinosConcluidos, e.totalKm, pace));
            }
        });
    }

    private void abrirDialogoConclusao(Treino treino) {
        int margem = (int) (16 * getResources().getDisplayMetrics().density);

        LinearLayout caixa = new LinearLayout(this);
        caixa.setOrientation(LinearLayout.VERTICAL);
        caixa.setPadding(margem, margem, margem, 0);

        EditText campoKm = new EditText(this);
        campoKm.setHint(R.string.hint_km);
        campoKm.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        caixa.addView(campoKm);

        EditText campoMinutos = new EditText(this);
        campoMinutos.setHint(R.string.hint_minutos);
        campoMinutos.setInputType(InputType.TYPE_CLASS_NUMBER);
        caixa.addView(campoMinutos);

        new AlertDialog.Builder(this)
                .setTitle(R.string.concluir_titulo)
                .setView(caixa)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.salvar, (dialogo, botao) -> {
                    try {
                        double km = Double.parseDouble(campoKm.getText().toString().replace(',', '.'));
                        int minutos = Integer.parseInt(campoMinutos.getText().toString().trim());
                        concluir(treino, km, minutos);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, R.string.erro_valores, Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    private void concluir(Treino treino, double km, int minutos) {
        api.concluir(treino.id, new ConcluirRequest(km, minutos)).enqueue(new ApiCallback<Treino>() {
            @Override
            void sucesso(Treino corpo) {
                Toast.makeText(MainActivity.this, R.string.treino_registrado, Toast.LENGTH_SHORT).show();
                carregar();
            }
        });
    }

    /** Callback padrão: trata erro de rede e erro HTTP com uma mensagem na tela. */
    private abstract class ApiCallback<T> implements Callback<T> {

        abstract void sucesso(T corpo);

        @Override
        public void onResponse(Call<T> call, Response<T> resposta) {
            if (resposta.isSuccessful() && resposta.body() != null) {
                sucesso(resposta.body());
            } else {
                erro(getString(R.string.erro_servidor, resposta.code()));
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            erro(getString(R.string.erro_rede));
        }

        private void erro(String mensagem) {
            Toast.makeText(MainActivity.this, mensagem, Toast.LENGTH_LONG).show();
        }
    }
}
