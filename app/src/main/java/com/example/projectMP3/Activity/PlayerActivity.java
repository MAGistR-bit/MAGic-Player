package com.example.projectMP3.Activity;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectMP3.R;
import com.example.projectMP3.model.MusicFile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.projectMP3.Adapter.AlbumDetailsAdapter.album_Music;
import static com.example.projectMP3.Adapter.SongAdapter.musicFilesSongAdapter;

/** Активность, проигрывающая аудиозаписи
 *  @author Mikhail */
public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    /** Позволяет проигрывать аудио/видео файлы
     * с возможностью сделать паузу,
     * перемотать в нужную позицию */
    static MediaPlayer mediaPlayer;
    /** Логические переменные (случайное воспроизведение, повтор аудиозаписи) */
    static boolean shuffleBoolean = false, repeatBoolean;
    /** Список музыкальных композиций */
    ArrayList<MusicFile> listSong = new ArrayList<>();
    /** Специальный идентификатор */
    Uri uri;
    /** Обработчик потока - обновляет сведения о времени */
    Handler handler = new Handler();
    /** Потоки воспроизведения */
    Thread playThread, prevThread, nextThread;

    /** Начальная инициализация */
    private int position = -1;

    private TextView songName, durationPlayed, totalDuration, artistName;
    private ImageView shuffleOnOff, playPrev, playNext, repeatOnOff, menuBtn, backBtn, songImg;
    private FloatingActionButton playPause;

    /** Ползунок */
    private SeekBar songSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Размещает пользовательский интерфейс на экране активности
        setContentView(R.layout.activity_player);
        // Инициализирует элементы управления
        initView();
        // Получает данные из Intent
        getIntentMethod();

        /* Добавляем listener, который получит
        *  уведомление, когда проигрывание закончится. */
        mediaPlayer.setOnCompletionListener(this);

        /* Открываем главную активность при нажатии
           на кнопку (chevron_left) */
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
            startActivity(intent);
        });
        // Отслеживаем перемещение ползунка
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // Уведомляет об изменении положения ползунка
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    // Переход к определенной позиции трека в миллисекундах
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            // Уведомляет о том, что пользователь начал перемещать ползунок
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            // Уведомляет о том, что пользователь закончил перемещать ползунок
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Перемещение функциональности в фоновый режим
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    // Текущая позиция воспроизведения
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    // Обновляем визуальное положение SeekBar
                    songSeekBar.setProgress(mCurrentPosition);
                    // Устанавливаем текущее время проигрывания трека
                    durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                // Запускаем тот же Runnable через 1 секунду
                handler.postDelayed(this, 1000);
            }
        });

        /* Устанавливаем иконку произвольного воспроизведения (shuffle)
        *  при нажатии на соответствующий ImageView */
        shuffleOnOff.setOnClickListener(v -> {
            if (shuffleBoolean) {
                shuffleBoolean = false;
                shuffleOnOff.setImageResource(R.drawable.ic_shuffle_off);
            } else {
                shuffleBoolean = true;
                shuffleOnOff.setImageResource(R.drawable.ic_shuffle_on);
            }
        });

        /* Устанавливаем иконку повторного воспроизведения (repeat)
        *  при нажатии на соответствующий ImageView */
        repeatOnOff.setOnClickListener(v -> {
            if (repeatBoolean) {
                repeatBoolean = false;
                repeatOnOff.setImageResource(R.drawable.ic_repeat_off);
            } else {
                repeatBoolean = true;
                repeatOnOff.setImageResource(R.drawable.ic_repeat_on);
            }
        });
    }

    /** Устанавливает формат времени
     *  @param mCurrentPosition Время в секундах
     *  @return Строковое представление времени */
    private String formattedTime(int mCurrentPosition) {
        String totalOut;
        String totalNew;
        String second = String.valueOf(mCurrentPosition % 60);
        String minute = String.valueOf(mCurrentPosition / 60);
        totalOut = minute + ":" + second;
        totalNew = minute + ":" + "0" + second;
        if (second.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }

    /** Получает данные из намерения (Intent) */
    private void getIntentMethod() {
        /* Получаем данные, добавленные ранее
        *  с помощью putExtra() */
        position = getIntent().getIntExtra("position", 0);
        String sender =  getIntent().getStringExtra("Sender");

        if(sender!= null && sender.equals("albumDetails")) {
            listSong = album_Music;
        } else {
            listSong = musicFilesSongAdapter;
        }

        if (listSong != null) {
            playPause.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSong.get(position).getPath());
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            // Освобождаем приобретенные ресурсы (память, кодеки)
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);
    }

    /** Устанавливает метаданные (обложка альбома и др.)
     * @param uri Объект, к которому мы хотим получить доступ
     * @see <a href="https://github.com/bumptech/glide">Glide</a>*/
    private void metaData(Uri uri) {
        // Общая продолжительность песни в секундах
        int TotalDuration = Integer.parseInt(listSong.get(position).getDuration()) / 1000;
        /* Устанавливаем соответствующие значения в TextView */
        totalDuration.setText(formattedTime(TotalDuration));
        songName.setText(listSong.get(position).getTitle());
        artistName.setText(listSong.get(position).getArtist());

        byte[] image = getAlbumArt(uri.getPath());
        if (image != null) {
            Glide.with(this).asBitmap().load(image).into(songImg);
        } else {
            Glide.with(this)
                    .load(R.drawable.listimage)
                    .into(songImg);
        }
    }

    /** Получает обложку альбома
     *  @param uri Строка, идентифицирующая ресурс (изображение, документ и др.)
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


    /** Вызывается после возобновления активности
     *  (after onResume() has been called) */
    @Override
    protected void onPostResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onPostResume();
    }

    /** Создает поток для воспроизведения (play) аудиозаписи */
    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                /* Добавляем слушателя при нажатии
                  на FloatingActionButton */
                playPause.setOnClickListener(v -> playPauseBtnClicked());
            }
        };
        playThread.start();
    }

    /** Обрабатывает нажатие кнопки play/pause */
    private void playPauseBtnClicked() {
        // Проверяем, играет ли медиаплеер
        if (mediaPlayer.isPlaying()) {
            playPause.setImageResource(R.drawable.ic_play);
            // Приостанавливаем воспроизведение аудиозаписи
            mediaPlayer.pause();
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
            /* Регистрируем listener, который будет вызван,
            *  когда будет достигнут конец воспроизведения */
            mediaPlayer.setOnCompletionListener(this);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        songSeekBar.setProgress(mCurrentPosition);
                        durationPlayed.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            playPause.setImageResource(R.drawable.ic_pause);
            // Возобновляем воспроизведение
            mediaPlayer.start();
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
            /* Регистрируем listener, который будет вызван,
             *  когда будет достигнут конец воспроизведения */
            mediaPlayer.setOnCompletionListener(this);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        songSeekBar.setProgress(mCurrentPosition);
                        durationPlayed.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    /** Создает поток для воспроизведения предыдущей аудиозаписи */
    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                /* Добавляю слушателя при нажатии
                * на кнопку (Previous Audio)*/
                playPrev.setOnClickListener(v -> prevBtnClicked());
            }
        };
        prevThread.start();
    }

    /** Обрабатывает нажатие кнопки (Play previous)*/
    private void prevBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            /* Останавливаем воспроизведение,
            * освобождаем ресурсы, связанные с этим объектом */
            mediaPlayer.stop();
            mediaPlayer.release();

            /* Проверяем, включены ли режимы Shuffle, Repeat */
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSong.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSong.size() - 1) : (position - 1));
            }

            uri = Uri.parse(listSong.get(position).getPath());
            // Создаем MediaPlayer для заданного URI
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            // Устанавливаем метаданные
            metaData(uri);
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        songSeekBar.setProgress(mCurrentPosition);
                        durationPlayed.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });

            playPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            /* Проверяем, включены ли режимы Shuffle, Repeat */
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSong.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSong.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSong.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        songSeekBar.setProgress(mCurrentPosition);
                        durationPlayed.setText(formattedTime(mCurrentPosition));

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPause.setImageResource(R.drawable.ic_play);
        }
        /* Регистрируем вызов, который будет вызван,
        *  когда музыкальный фрагмент достигнет конца */
        mediaPlayer.setOnCompletionListener(this);
    }

    /** Создает поток для воспроизведения следующей аудиозаписи */
    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                  /* Добавляем слушателя при нажатии
                  на ImageView (Play next) */
                playNext.setOnClickListener(v -> nextBtnClicked());
            }
        };
        nextThread.start();
    }

    /** Обрабатывает нажатие кнопки (Play next)*/
    private void nextBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            /* Останавливаем воспроизведение,
             * освобождаем ресурсы, связанные с этим объектом */
            mediaPlayer.stop();
            mediaPlayer.release();

            /* Проверяем, включены ли режимы Shuffle, Repeat */
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSong.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSong.size());
            }

            uri = Uri.parse(listSong.get(position).getPath());
            // Создаем MediaPlayer для заданного URI
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            // Устанавливаем метаданные
            metaData(uri);
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
            mediaPlayer.setOnCompletionListener(this);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        songSeekBar.setProgress(mCurrentPosition);
                        durationPlayed.setText(formattedTime(mCurrentPosition));

                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            /* Проверяем, включены ли режимы Shuffle, Repeat */
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSong.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSong.size());
            }

            uri = Uri.parse(listSong.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
            mediaPlayer.setOnCompletionListener(this);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        songSeekBar.setProgress(mCurrentPosition);
                        durationPlayed.setText(formattedTime(mCurrentPosition));

                    }
                    handler.postDelayed(this, 1000);

                }
            });
            playPause.setImageResource(R.drawable.ic_play);
        }
    }

    /** Получает случайное число
     * @param i Индекс последней аудиозаписи
     * @return Случайное int значение в диапазоне от 0 до i+1 */
    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    /** Получает ссылки элементов управления */
    private void initView() {
        songName = findViewById(R.id.song_name);
        durationPlayed = findViewById(R.id.duration_played);
        totalDuration = findViewById(R.id.duration_total);
        shuffleOnOff = findViewById(R.id.shuffle_on);
        playPrev = findViewById(R.id.play_prev);
        playNext = findViewById(R.id.play_next);
        repeatOnOff = findViewById(R.id.repeat);
        menuBtn = findViewById(R.id.menu_btn);
        backBtn = findViewById(R.id.back_btn);
        playPause = findViewById(R.id.play_pause);
        songSeekBar = findViewById(R.id.seek_bar_music);
        songImg = findViewById(R.id.music_art);
        artistName = findViewById(R.id.song_artist);
    }

    /** Метод слушателя OnCompletionListener.
     * Вызывается, когда достигнут конец проигрываемого содержимого.*/
    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if (mediaPlayer != null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
        // Устанавливаем изображение FAB
        playPause.setImageResource(R.drawable.ic_pause);
    }
}