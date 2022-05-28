package com.example.projectMP3.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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

/** Данный класс работает с музыкальными композициями,
 *  который вложены в определенный альбом.
 *  @author Mikhail */
public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyViewHolder> {
    /** Контекст */
    @SuppressLint("StaticFieldLeak")
    static Context context;
    /** Список композиций, входящих в альбом */
    public static ArrayList<MusicFile> album_Music;

    /** Создает новый объект */
    public AlbumDetailsAdapter(Context context, ArrayList<MusicFile> album_Music) {
        AlbumDetailsAdapter.context = context;
        AlbumDetailsAdapter.album_Music = album_Music;
    }

    /** Создает компоненты внутри фрагмента
     * @param parent ViewGroup, в которую будет добавлено новое представление
     * @param viewType Тип представления
     * @return Объект MyViewHolder
     * @see <a href="https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter#onCreateViewHolder(android.view.ViewGroup,%20int)">
     *     onCreateViewHolder</a>*/
    @NonNull
    @Override
    public AlbumDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                               int viewType) {
        // Указываем идентификатор макета для отдельного элемента списка
        View view = LayoutInflater.from(context).inflate(R.layout.music_list_item,
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
    public void onBindViewHolder(@NonNull AlbumDetailsAdapter.MyViewHolder holder, int position) {
        MusicFile musicFileTemp = album_Music.get(position);
        // Устанавливаем заголовок для textView
        holder.textView.setText(musicFileTemp.getTitle());
        // Получаем музыкальную обложку
        byte[] image = getAlbumArt(musicFileTemp.getPath());

        if (image != null) {
            Glide.with(context).asBitmap().load(image).into(holder.imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.listimage)
                    .into(holder.imageView);
        }

        /* Запускаем PlayerActivity,
        *  отправляем данные (putExtra) */
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("Sender", "albumDetails");
            intent.putExtra("position", position);
            context.startActivity(intent);
        });

        /* Слушатель, реагирующий на
           долгое нажатие на музыкальную композицию*/
        holder.itemView.setOnLongClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setMessage("Вы действительно хотите удалить?")
                    .setPositiveButton("Да", (dialogInterface, i) -> {
                        /* Изменяем URI файла, добавив идентификатор
                         * в конец пути */
                        Uri uri = ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(album_Music.get(position).getId())
                        );
                        /* Удаляем файл */
                        File file = new File(album_Music.get(position).getPath());
                        boolean deleted = file.delete();

                        if (deleted) {
                            // Удаляет строку (согласно URI)
                            context.getContentResolver().delete(uri, null, null);
                            album_Music.remove(position);
                            /* Сообщаем о том, что элемент был удален из набора.
                             * Отображение элементов должно быть изменено. */
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, album_Music.size());

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
        return album_Music.size();
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

    /** Служит для оптимизации ресурсов (своеобразный контейнер)
     * @author Mikhail */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        /** Создает новый объект
         *  @param itemView Элемент списка на основе View */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Получаем ссылки на объекты
            imageView = itemView.findViewById(R.id.music_img);
            textView = itemView.findViewById(R.id.music_filename);
        }
    }
}
