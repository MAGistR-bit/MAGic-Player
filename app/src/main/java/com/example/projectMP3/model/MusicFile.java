package com.example.projectMP3.model;
/** Класс служит для хранения информации об музыкальном файле
 *  @author Mikhail */
public class MusicFile {
    /** Путь к аудиофайлу */
    private String path;
    /** Название аудиофайла */
    private String title;
    /** Исполнитель */
    private String artist;
    /** Музыкальный альбом */
    private String album;
    /** Длительность аудиозаписи */
    private String duration;
    /** Идентификатор аудиозаписи */
    private String Id;


    /** Создает новый объект
     *  @param path Путь к файлу
     *  @param title Название файла
     *  @param artist Исполнитель
     *  @param album Музыкальный альбом
     *  @param duration Продолжительность аудиозаписи
     *  @param id Идентификатор */
    public MusicFile(String path, String title, String artist,
                     String album, String duration, String id) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        Id = id;
    }

    public MusicFile() {
    }

    /** Получает значение path, которое можно задать
     * с помощью метода {@link #setPath(String)}
     * @return Значение свойства path */
    public String getPath() {
        return path;
    }

    /** Задает значение path, которое можно получить
     *  при помощи метода {@link #getPath()}
     *  @param path Новое значение свойства path */
    public void setPath(String path) {
        this.path = path;
    }

    /** Получает значение title
     * @return Значение свойства title */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /** Получает значение artist
     *  @return Значение свойства artist */
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /** Получает значение id, которое можно задать
     *  с помощью метода {@link #setId(String)}
     *  @return Значение свойства id*/
    public String getId() {
        return Id;
    }

    /** Задает значение id, которое можно получить при помощи
     * метода {@link #getPath()}
     * @param id Идентификатор аудиозаписи */
    public void setId(String id) {
        Id = id;
    }
}
