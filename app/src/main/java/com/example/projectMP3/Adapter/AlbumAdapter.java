package com.example.projectMP3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectMP3.Activity.AlbumDetailsActivity;
import com.example.projectMP3.R;
import com.example.projectMP3.model.MusicFile;

import java.util.ArrayList;

/** Создает адаптер для отображения данных
 * @author Mikhail */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    /** Список музыкальных альбомов */
    public ArrayList<MusicFile> AlbumFilesAdapter;
    /** Объект, предоставляющий доступ к базовым функциям приложения */
    private final Context context;

    /** Создает новый объект
     * @param albumFilesSongAdapter Список музыкальных альбомов
     * @param context Контекст*/
    public AlbumAdapter(ArrayList<MusicFile> albumFilesSongAdapter, Context context) {
        AlbumFilesAdapter = albumFilesSongAdapter;
        this.context = context;
    }

    /** Создает компоненты внутри фрагмента
     * @param parent ViewGroup, в которую будет добавлено новое представление
     * @param viewType Тип представления
     * @return Объект MyViewHolder
     * @see <a href="https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup,%20int)">
     *     onCreateViewHolder</a>*/
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Указываем идентификатор макета для отдельного элемента списка
        View view = LayoutInflater.from(context).inflate(R.layout.album_list_item,
                parent, false);
        return new MyViewHolder(view);
    }

    /** Отображает элементы адаптера в указанной позиции
     * @param holder ViewHolder, который должен быть обновлен
     * @param position Положение элемента в наборе данных
     * @see <a href="https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#onBindViewHolder(VH,%20int)">
     * onBindViewHolder</a>
     * @see <a href="https://github.com/bumptech/glide">Glide</a>*/
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MusicFile musicFileTemp = AlbumFilesAdapter.get(position);
        // Устанавливаем заголовок для textView
        holder.textView.setText(musicFileTemp.getTitle());
        // Получаем музыкальную обложку
        byte[] image = getAlbumArt(musicFileTemp.getPath());

        /* Загружаем картинку в ImageView, если она была найдена.
         *  Иначе в ImageView устанавливаем картинку из ресурсов. */
        if (image != null) {
            Glide.with(context).asBitmap().load(image).into(holder.imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.listimage)
                    .into(holder.imageView);
        }

        /* Формируем намерение на запуск
           активности Album_details */
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AlbumDetailsActivity.class);
            intent.putExtra("AlbumPosition", AlbumFilesAdapter.get(position).getAlbum());
            context.startActivity(intent);
        });

    }

    /** Получает обложку альбома
     *  @param uri Строка, идентифицирующая какой-либо ресурс
     *  @return Возвращает массив байтов (обложка, связанная с источником данных)
     *  @see <a href="https://developer.android.com/reference/android/media/MediaMetadataRetriever">
     *  MediaMetadataRetriever </a> - Класс, позволяющий извлекать метаданные
     *  из входного файла */
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

    /** Предоставляет количество элементов (для отображения на экране)
     * @return Количество элементов списка */
    @Override
    public int getItemCount() {
        return AlbumFilesAdapter.size();
    }

    /** Служит для оптимизации ресурсов (своеобразный контейнер)
     * @author Mikhail */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        // Карточка с закруглёнными углами
        CardView cardView;

        /** Создает новый объект
         *  @param itemView Элемент списка на основе View */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Получаем ссылки на объекты
            imageView = itemView.findViewById(R.id.album_image);
            textView = itemView.findViewById(R.id.album_text);
            cardView = itemView.findViewById(R.id.album_items);
        }
    }

}
