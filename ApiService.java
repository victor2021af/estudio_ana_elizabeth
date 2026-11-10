package br.com.estudioanaelizabeth.app.data;

import java.util.List;

import br.com.estudioanaelizabeth.app.data.model.ConcluirRequest;
import br.com.estudioanaelizabeth.app.data.model.Evolucao;
import br.com.estudioanaelizabeth.app.data.model.Treino;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/alunas/{alunaId}/treinos")
    Call<List<Treino>> listarTreinos(@Path("alunaId") long alunaId);

    @GET("api/alunas/{alunaId}/evolucao")
    Call<Evolucao> evolucao(@Path("alunaId") long alunaId);

    @POST("api/treinos/{treinoId}/concluir")
    Call<Treino> concluir(@Path("treinoId") long treinoId, @Body ConcluirRequest corpo);
}
