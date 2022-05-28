package com.example.projectMP3.Activity;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectMP3.Adapter.AlbumDetailsAdapter;
import com.example.projectMP3.R;
import com.example.projectMP3.model.MusicFile;

import java.util.ArrayList;

import static com.example.projectMP3.Activity.MainActivity.musicFile;

/** Данный класс формирует общую обложку,
 *  список музыкальных композиций.
 *  Создается подробная информация об альбоме.
 *  @author Mikhail */
public class AlbumDetailsActivity extends AppCompatActivity {
    /** Оптимизация работы со списком */
    RecyclerView recyclerView;
    /** Изображение */
    ImageView imageView;

    ArrayList<MusicFile> albumSong = new ArrayList<>();
    String AlbumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Устанавливаем макет отображения
        setContentView(R.layout.activity_album_details);

        recyclerView = findViewById(R.id.recyclerView_details);
        imageView = findViewById(R.id.image);
        // Получаем данные из намерения
        AlbumName = getIntent().getStringExtra("AlbumPosition");
        int j = 0;
        for (int i = 0; i < musicFile.size(); i++) {
            if (AlbumName.equals(musicFile.get(i).getAlbum())) {
                // Добавляем элемент в ArrayList
                albumSong.add(j, musicFile.get(i));
                j++;
            }
        }


        TextView songCount = findViewById(R.id.album_activity_songs_count);
        // Используем заполнители (placeholders), чтобы добавить текст
        String fillCountAudio = getString(R.string.count_audio, albumSong.size());
        songCount.setText(fillCountAudio);

        // Получаем изображение (обложку альбома)
        byte[] image = getAlbumArt(albumSong.get(0).getPath());
        // Проверяем, найдено ли изображение
        if (image == null) {
            Glide.with(this)
                    .load(R.drawable.heart)
                    .into(imageView);
        } else {
            Glide.with(this)
                    .load(image)
                    .into(imageView);
        }
    }

    /** Состояние, в котором приложение взаимодействует с пользователем.
     *  @see <a href="https://developer.android.com/guide/components/activities/activity-lifecycle#onresume">
     *      onResume</a>*/
    @Override
    protected void onResume() {
        super.onResume();
        if (!(albumSong.size() < 1)) {
            AlbumDetailsAdapter album = new AlbumDetailsAdapter(this, albumSong);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            // Устанавливаем адаптер
            recyclerView.setAdapter(album);
            recyclerView.setLayoutManager(layoutManager);
        }
    }


    /** Получает обложку альбома
     *  @param uri Строка, идентифицирующая какой-либо ресурс (изображение, документ и др.)
     *  @return Возвращает массив байтов (обложка, связанная
     *  с источником данных)
     *  @see <a href="https://developer.android.com/reference/android/media/MediaMetadataRetriever">
     *      MediaMetadataRetriever </a>*/
    public byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        // Устанавливаем источник данных для использования
        retriever.setDataSource(uri);
        // Находим обложку, связанную с источником данных
        byte[] art = retriever.getEmbeddedPicture();
        // Освобождаем ресурсы
        retriever.release();
        return art;
    }

}