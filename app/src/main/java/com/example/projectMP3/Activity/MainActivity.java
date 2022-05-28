package com.example.projectMP3.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import  androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.projectMP3.Fragment.AlbumFragment;
import com.example.projectMP3.Fragment.SongFragment;
import com.example.projectMP3.R;
import com.example.projectMP3.model.MusicFile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/** Главная активность приложения, реализующая интерфейс
 *  @see <a href=https://developer.android.com/reference/android/widget/SearchView.OnQueryTextListener>
 *      SearchView.OnQueryTextListener</a>
 *  @author Mikhail */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    /** Список для хранения объектов {@link MusicFile}*/
    public static ArrayList<MusicFile> musicFile;
    /** Код запроса приложения (должен быть больше нуля).*/
    int RequestCode = 1;


    /** Первоначальная настройка activity
     *  @param savedInstanceState Сохранение состояния активности */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Устанавливаем содержимое активности из макета
        setContentView(R.layout.activity_main);
        // Запрашиваем доступ
        permission();
        // Работа с ViewPager
        int_viewPager();
    }

    /** Получает доступ к данным (аудиозаписям)
     *  @param context Предоставляет доступ к базовым функциям приложения
     *  @return ArrayList, состоящий из музыкальных файлов */
    public static ArrayList<MusicFile> getAllAudio(Context context) {
        ArrayList<MusicFile> tempFile = new ArrayList<>();
        // Используем контент-провайдер MediaStore
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] Projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM
        };

        /* Получаем набор строк в табличном виде.
           Параметры метода query():
        *  @param uri Позволяет определить, какой контент-провайдер будет использован
        *  @param Projection Массив имен колонок таблицы, которые
        *         будут возвращены в результате запроса
        *  @param null Аналогичен WHERE в SQL
        *  @param null Массив аргументов
        *  @param null Порядок, в котором будут возвращены результаты запроса */
        Cursor cursor = context.getContentResolver().query(uri, Projection,
                null, null, null);


        if (cursor != null) {
            while (cursor.moveToNext()) {
                String ID = cursor.getString(0);
                String TITLE = cursor.getString(1);
                String DURATION = cursor.getString(2);
                String DATA = cursor.getString(3);
                String ARTIST = cursor.getString(4);
                String ALBUM = cursor.getString(5);
                MusicFile musicFile = new MusicFile(DATA, TITLE, ARTIST, ALBUM, DURATION, ID);
                // Добавляем аудиозапись в ArrayList
                tempFile.add(musicFile);
            }
            // Закрываем Cursor (освобождаем память).
            cursor.close();
        }
        return tempFile;
    }

    /** Разрешить приложению доступ */
    private void permission() {
        /* Проверяем, предоставил ли пользователь разрешение приложению.
        *  Метод checkSelfPermission возвращает либо PERMISSION_GRANTED,
        *  либо PERMISSION_DENIED. */
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            /* Запрашиваем разрешение WRITE_EXTERNAL_STORAGE напрямую.
            *  Код запроса (RequestCode) должен быть сопоставлен с
            *  результатом метода onRequestPermissionsResult. */
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode);
        } else {
            // Работаем с ViewPager
            int_viewPager();
            // Получаем аудиозаписи (ArrayList)
            musicFile = getAllAudio(this);
        }
    }

    /** Данный метод вызывается для каждого вызова requestPermissions.
     *  @param requestCode Код запроса, переданный в requestPermissions
     *  @param permissions Запрошенные разрешения. Никогда не нуль
     *  @param grantResults Соответствующие
     *                      разрешения (PERMISSION_GRANTED, PERMISSION_DENIED)*/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (RequestCode == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Работаем с ViewPager
                int_viewPager();
                // Получаем аудиозаписи (ArrayList)
                musicFile = getAllAudio(this);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode);
            }
        }
    }

    /** Осуществляет настройку пользовательского интерфейса
     *  (ViewPager, TabLayout, FloatingActionButton) */
    private void int_viewPager() {
        // Показ и прокрутка данных влево-вправо
        ViewPager viewPager;
        // Горизонтальная компоновка для отображения вкладок
        TabLayout tabLayout;
        // Плавающая кнопка действия (FAB)
        FloatingActionButton openMusicPlayer;

        androidx.appcompat.widget.Toolbar toolbar;
        // Получаем ViewPager и TabLayout
        viewPager = findViewById(R.id.view_Pager);
        tabLayout = findViewById(R.id.tab_layout);

        // Работа с элементом (toolbar)
        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("MAGic Player");
        toolbar.setTitleTextColor(Color.CYAN);

        /* Чтобы интерфейс (toolbar) успешно отображался на старых
        *  версиях Android, вызываем метод setSupportActionBar(). */
        setSupportActionBar(toolbar);

        // Создание PagerAdapter, добавление фрагментов
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new SongFragment(), "Песни");
        viewPagerAdapter.addFragment(new AlbumFragment(), "Альбомы");

        // Устанавливаем для ViewPager адаптер
        viewPager.setAdapter(viewPagerAdapter);
        // Передаем ViewPager в TabLayout
        tabLayout.setupWithViewPager(viewPager);

        openMusicPlayer = findViewById(R.id.open_Music);
        openMusicPlayer.setOnClickListener(view -> {
            // Переход на другую активность
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            startActivity(intent);
        });

    }

     /** Инициализирует содержимое меню */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Доступ к ранее созданному пункту меню
        MenuItem menuItem;

        /* Виджет, который предоставляет пользователю
        *  интерфейс для ввода запроса. */
        androidx.appcompat.widget.SearchView searchView;

        // Добавляем использование menu.xml
        getMenuInflater().inflate(R.menu.menu, menu);

        menuItem = menu.findItem(R.id.search_option);
        // Получаем текущее представление действия для этого пункта меню
        searchView = (SearchView) menuItem.getActionView();

        /* Устанавливает слушателя для SearchView
        *  @param listener */
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }


    /** Метод реализует выход из приложения */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    /** Данный метод вызывается при выборе команды меню */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.about_app){
            startActivity(new Intent(MainActivity.this, AboutAppActivity.class));
        } else if(item.getItemId() == R.id.setting_app){
            showDialogBox();
        } else if (item.getItemId() == R.id.exit){
            onBackPressed();
        }
        return true;
    }

    /** Данный метод отображает диалоговое окно,
     *  свидетельствующее о том, что страница требует обновления. */
    public void showDialogBox() {

        Dialog dialog = new Dialog(MainActivity.this);
        // Выводим окно без заголовка
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Размещаю пользовательский интерфейс, используя макет
        dialog.setContentView(R.layout.dialog_waiting_update);
        // Разрешаю закрытие окна
        dialog.setCancelable(true);

        WindowManager.LayoutParams wm = new WindowManager.LayoutParams();

        try{
            wm.copyFrom(dialog.getWindow().getAttributes());
            wm.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wm.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } catch (NullPointerException e) {
            System.out.println(e.toString());
            Toast.makeText(MainActivity.this, "Null Pointer Exception",
                    Toast.LENGTH_LONG).show();
        }

        dialog.show();
        dialog.getWindow().setAttributes(wm);
    }

    /** Вызывается, когда пользователь отправил запрос
     *  @param query Текст запроса, который должен быть отправлен
     *  @return false - Действия SearchView по умолчанию */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /** Вызывается при изменении текста запроса пользователем
     *  @param newText Новое содержимое текстового поля
     *  @return Выполнение действия по умолчанию (false),
     *  либо выполнение действия слушателем (true)*/
    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        // Создаю список
        ArrayList<MusicFile> mySong = new ArrayList<>();
        for(MusicFile song : musicFile)
        {
            if(song.getTitle().toLowerCase().contains(userInput))
            {
                // Добавляю композицию в список
                mySong.add(song);
            }
        }
        // Обновляем плейлист
        SongFragment.songAdapter.UpdateList(mySong);
        return true;
    }

    /** Данный класс использует стандартную реализацию PagerAdapter,
    *  работающую с фрагментами. */
    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;

        /** Создает новый объект.
         *  @param fm Класс, отвечающий за выполнение действий над фрагментами приложения*/
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.titles = new ArrayList<>();
            this.fragments = new ArrayList<>();
        }

        /** Добавляет фрагмент.
         *  @param fragment Интерфейс приложения, содержащий собственный layout-файл
         *  @param title Заголовок фрагмента*/
        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        /** @param position Позиция фрагмента
         *  @return Возвращает фрагмент, связанный с определенной
         *  позицией*/
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        /** @return Возвращает количество фрагментов */
        @Override
        public int getCount() {
            return fragments.size();
        }

        /** Получение строки заголовка для указанной страницы
         *  @param position Позиция запрашиваемого заголовка
         *  @return Заголовок для запрашиваемой страницы (CharSequence)*/
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}