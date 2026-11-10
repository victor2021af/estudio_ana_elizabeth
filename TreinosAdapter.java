package br.com.estudioanaelizabeth.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.estudioanaelizabeth.app.data.model.Treino;

public class TreinosAdapter extends RecyclerView.Adapter<TreinosAdapter.VH> {

    public interface OnTreinoClick {
        void onClick(Treino treino);
    }

    private final List<Treino> itens = new ArrayList<>();
    private final OnTreinoClick listener;

    public TreinosAdapter(OnTreinoClick listener) {
        this.listener = listener;
    }

    public void atualizar(List<Treino> novos) {
        itens.clear();
        itens.addAll(novos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treino, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Treino treino = itens.get(position);
        holder.titulo.setText(treino.titulo);
        holder.detalhes.setText(holder.itemView.getContext().getString(
                R.string.item_detalhes, treino.dataPrevista, treino.distanciaPrevistaKm));

        if (treino.isConcluido()) {
            holder.status.setText(holder.itemView.getContext().getString(
                    R.string.item_concluido, treino.distanciaRealizadaKm, treino.duracaoMinutos));
            holder.itemView.setOnClickListener(null);
        } else {
            holder.status.setText(R.string.item_pendente);
            holder.itemView.setOnClickListener(v -> listener.onClick(treino));
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView titulo;
        final TextView detalhes;
        final TextView status;

        VH(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.itemTitulo);
            detalhes = itemView.findViewById(R.id.itemDetalhes);
            status = itemView.findViewById(R.id.itemStatus);
        }
    }
}
