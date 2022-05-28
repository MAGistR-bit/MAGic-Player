package com.example.projectMP3.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.projectMP3.Adapter.AlbumAdapter;
import com.example.projectMP3.R;

import static com.example.projectMP3.Activity.MainActivity.musicFile;

/** @author Mikhail */
public class AlbumFragment extends Fragment {
    /** Элемент, оптимизирующий работу со списком */
    RecyclerView recyclerView;
    /** Адаптер для отображения данных */
    AlbumAdapter albumAdapter ;

    /** Пустой конструктор */
    public AlbumFragment() {

    }

    /** Фрагмент загружает на экран свой интерфейс
     * @return Представление фрагмента */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // "Надуваем" разметку фрагмента
        View view =  inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.Album_recyclerView);
        // Размещаем элементы по двум столбцам
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        albumAdapter = new AlbumAdapter(musicFile, getContext());
        recyclerView.setAdapter(albumAdapter);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }
}