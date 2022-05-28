package com.example.projectMP3.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/** @author Mikhail */
public class MusicFileTest {

    MusicFile musicFile;

    @Before
    public void beforeMethod(){
        musicFile = new MusicFile("Внутренняя память/bluetooth/kino-kukushka.mp3",
                "Кукушка","Кино", "bluetooth", "06:40","1");
    }


    @Test
    public void getPathToMusicFile() {
        String pathToMusicFile = "Внутренняя память/bluetooth/kino-kukushka.mp3";
        assertEquals(pathToMusicFile, musicFile.getPath());
    }

    @Test
    public void setPath() {
        musicFile.setPath("Внутренняя память/Music/Favorite/");
        String filePath = "Внутренняя память/Music/Favorite/";
        assertEquals(filePath, musicFile.getPath());
    }

    @Test
    public void getSongTitle() {
        String songName = musicFile.getTitle();
        assertEquals("Кукушка", songName);
        assertNotEquals("Мама, не плачь", songName);
    }

    @Test
    public void setSongTitle() {
        musicFile.setTitle("Кино - Виктор Цой - Кукушка");
        String expectedSongTitle = "Кино - Виктор Цой - Кукушка";
        assertEquals("Название песни: ", expectedSongTitle, musicFile.getTitle());
    }

    @Test
    public void getMusicArtist() {
        Assert.assertNotNull("Исполнитель: ", musicFile.getArtist());
        assertEquals("Кино", musicFile.getArtist());
    }

    @Test
    public void setMusicArtist() {
        musicFile.setArtist("Nautilus Pompilius");
        String expectedMusicArtist = "Nautilus Pompilius";
        boolean result = expectedMusicArtist.equals(musicFile.getArtist());
        assertTrue(result);
    }

    @Test
    public void getMusicAlbum() {
        assertEquals("bluetooth", musicFile.getAlbum());
    }

    @Test
    public void setMusicAlbum() {
        musicFile.setAlbum("Это не любовь");
        assertSame("Это не любовь", musicFile.getAlbum());
    }

    @Test
    public void getDurationMusicFile() {
        assertEquals("06:40", musicFile.getDuration());
    }

    @Test
    public void setDurationMusicFile() {
        musicFile.setDuration("04:20");
        assertNotNull(musicFile.getDuration());
        assertNotEquals("06:40", musicFile.getDuration());
    }

    @Test
    public void getIdMusicFile() {
        assertNotEquals("5", musicFile.getId());
        assertEquals("1", musicFile.getId());
    }

    @Test
    public void setIdMusicFile() {
        musicFile.setId("5");
        assertNotEquals("2", musicFile.getId());
    }
}