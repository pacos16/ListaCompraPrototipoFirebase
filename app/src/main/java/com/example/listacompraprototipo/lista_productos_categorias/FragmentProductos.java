package com.example.listacompraprototipo.lista_productos_categorias;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listacompraprototipo.R;
import com.example.listacompraprototipo.model.ListaCompra;
import com.example.listacompraprototipo.model.Producto;

import java.util.ArrayList;

public class FragmentProductos extends Fragment implements IProductoListener{

    private ArrayList<Producto> productos;
    private ListaCompra listaCompra;
    private RecyclerView rvProductos;

    public FragmentProductos(ArrayList<Producto> productos, ListaCompra listaCompra) {
        this.productos = productos;
        this.listaCompra = listaCompra;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rv_productos,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rvProductos=getActivity().findViewById(R.id.rvProductos);
        AdapterProductos adapterProductos=new AdapterProductos(productos,this);
        LinearLayoutManager layoutManager=new LinearLayoutManager
                (this.getContext(),LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(rvProductos.getContext(),
                layoutManager.getOrientation() );

        rvProductos.setAdapter(adapterProductos);
        rvProductos.addItemDecoration(dividerItemDecoration);
        rvProductos.setLayoutManager(layoutManager);

    }

    @Override
    public void onProductoSelecionado(int posicion) {

    }
}

