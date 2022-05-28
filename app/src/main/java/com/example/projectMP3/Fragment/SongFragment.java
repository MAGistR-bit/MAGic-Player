package com.example.projectMP3.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectMP3.Adapter.SongAdapter;
import com.example.projectMP3.R;

import static com.example.projectMP3.Activity.MainActivity.musicFile;

/** Данный класс формирует
 *  музыкальный фрагмент (SongFragment)
 *  @see <a href="https://developer.android.com/reference/android/app/Fragment">Fragment</a>
 *  @author Mikhail */
public class SongFragment extends Fragment {

    /** Отображение прокручиваемого списка */
    RecyclerView recyclerView;

    /** Адаптер */
    @SuppressLint("StaticFieldLeak")
    public static SongAdapter songAdapter;

    public SongFragment() {
    }

    /** Создает разметку для фрагмента
     * @return Представление фрагмента
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* "Надуть" (inflate) разметку фрагмента
        *  Параметры метода inflate:
        *  1) Идентификатор ресурса разметки
        *  2) Корневой компонент, к которому нужно присоединить
        *  надутые объекты
        *  3) Нужно ли присоединять надутые объекты к корневому элементу */
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        /* Для размещения элементов используется специальный менеджер макетов */
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        songAdapter = new SongAdapter(musicFile, getContext());
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }
}