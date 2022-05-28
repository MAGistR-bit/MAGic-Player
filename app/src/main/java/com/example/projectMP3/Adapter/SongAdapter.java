package com.example.projectMP3.Adapter;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectMP3.Activity.PlayerActivity;
import com.example.projectMP3.R;
import com.example.projectMP3.model.MusicFile;

import java.io.File;
import java.util.ArrayList;

/** Данный класс создает адаптер
 * (созданный класс наследует RecyclerView.Adapter)
 * @author Mikhail */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    /** Список */
    public static ArrayList<MusicFile> musicFilesSongAdapter;
    /** Доступ к базовым функциям приложения (context) */
    public Context context;

    /** Создает новый объект
     *  @param musicFilesSongAdapter Список музыкальных файлов
     *  @param context Контекст */
    public SongAdapter(ArrayList<MusicFile> musicFilesSongAdapter, Context context) {
        SongAdapter.musicFilesSongAdapter = musicFilesSongAdapter;
        this.context = context;
    }

    /**
     * @param parent ViewGroup, в которую будет добавлено новое представление
     * @param viewType Тип представления
     * @return Объект MyViewHolder
     * @see <a href="https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup,%20int)">
     *     onCreateViewHolder</a>*/
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* Необходимо указать идентификатор макета для отдельного элемента списка  */
        View view = LayoutInflater.from(context).inflate(R.layout.music_list_item,
                parent, false);
        return new MyViewHolder(view);

    }

    /** Отображает элементы адаптера в указанной позиции
     * @param holder ViewHolder, который должен быть обновлен
     * @param position Положение элемента в наборе данных
     * @see <a href="https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#onBindViewHolder(VH,%20int)">
     * onBindViewHolder</a>*/
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MusicFile musicFileTemp = musicFilesSongAdapter.get(position);
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

        /* Запускаем PlayerActivity при нажатии
        на определенную позицию */
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });


        holder.itemView.setOnLongClickListener(v -> {
            // Создаем объект класса AlertDialog.Builder
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setMessage("Вы действительно хотите удалить?")
                    .setPositiveButton("Да", (dialogInterface, i) -> {
                        /* Изменяем URI файла, добавив идентификатор
                        * в конец пути */
                        Uri uri = ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(musicFilesSongAdapter.get(position).getId())
                        );

                        /* Удаляем файл */
                        File file = new File(musicFilesSongAdapter.get(position).getPath());
                        boolean deleted = file.delete();

                        if (deleted) {
                            // Удаляет строку (согласно URI)
                            context.getContentResolver().delete(uri, null, null);
                            musicFilesSongAdapter.remove(position);
                            /* Сообщаем о том, что элемент был удален из набора.
                            *  Отображение элементов должно быть изменено. */
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, musicFilesSongAdapter.size());

                            Toast.makeText(context, "Файл удален", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Нет", null);

            final AlertDialog alertDialog = builder.create();

            alertDialog.setOnShowListener(dialogInterface -> {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            });

            alertDialog.setTitle("Удаление песни");
            alertDialog.setIcon(R.drawable.delete_trash);
            alertDialog.show();
            return false;
        });
    }

    /** Адаптеру нужно знать, сколько элементов
     * предоставить компоненту (для отображения на экране)
     * @return Количество элементов списка */
    @Override
    public int getItemCount() {
        return musicFilesSongAdapter.size();
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

    /** Служит для оптимизации ресурсов (своеобразный контейнер)
     * @author Mikhail */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Используемые компоненты
        ImageView imageView;
        TextView textView;
        RelativeLayout relativeLayout;

        /** Создает новый объект
         * @param itemView Элемент списка на основе View*/
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.music_img);
            textView = itemView.findViewById(R.id.music_filename);
            relativeLayout = itemView.findViewById(R.id.music_items);
        }
    }

    /** Обновляет музыкальный плейлист
     *  @param musicFilesArrayList Список музыкальных композиций */
    public void UpdateList(ArrayList<MusicFile> musicFilesArrayList) {
        musicFilesSongAdapter = new ArrayList<>();
        musicFilesSongAdapter.addAll(musicFilesArrayList);
        // Сообщаем об изменении набора данных
        notifyDataSetChanged();
    }

}
