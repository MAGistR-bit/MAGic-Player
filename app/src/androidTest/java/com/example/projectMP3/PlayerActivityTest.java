package com.example.projectMP3;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.projectMP3.Activity.MainActivity.musicFile;
import static com.example.projectMP3.Activity.PlayerActivity.mediaPlayer;
import static com.example.projectMP3.Activity.PlayerActivity.repeatBoolean;
import static com.example.projectMP3.Activity.PlayerActivity.shuffleBoolean;
import static org.junit.Assert.*;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projectMP3.Activity.AlbumDetailsActivity;
import com.example.projectMP3.Activity.MainActivity;
import com.example.projectMP3.Activity.PlayerActivity;
import com.example.projectMP3.model.MusicFile;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //фиксируем порядок выполнения тестов по имени
@RunWith(AndroidJUnit4ClassRunner.class)
public class PlayerActivityTest  {

    int position = 0;

    @Rule(order = 0)
    public ActivityScenarioRule<MainActivity> scenarioRuleMain = new ActivityScenarioRule<>(MainActivity.class);
    //запускам данные об альбоме с данные 1 альбома в списке
    @Rule(order = 1)
    public ActivityScenarioRule<PlayerActivity> scenarioRule = new ActivityScenarioRule<>(getMelody());

    static Context instrumentationContext;
    Intent getMelody()
    {
        try {
            instrumentationContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent intent = new Intent(instrumentationContext, PlayerActivity.class);
            intent.putExtra("position", position);
            return intent;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Test
    public void test1_ensureActivityViewIsPresent() throws Exception {

        //проверяем видимость элементов
        onView(withId(R.id.layout_top_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.back_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.music_art)).check(matches(isDisplayed()));
        onView(withId(R.id.song_artist)).check(matches(isDisplayed()));
        onView(withId(R.id.song_name)).check(matches(isDisplayed()));
        onView(withId(R.id.shuffle_on)).check(matches(isDisplayed()));
        onView(withId(R.id.play_prev)).check(matches(isDisplayed()));
        onView(withId(R.id.play_pause)).check(matches(isDisplayed()));
        onView(withId(R.id.play_next)).check(matches(isDisplayed()));
        onView(withId(R.id.repeat)).check(matches(isDisplayed()));
        onView(withId(R.id.duration_played)).check(matches(isDisplayed()));
        onView(withId(R.id.duration_total)).check(matches(isDisplayed()));
        onView(withId(R.id.seek_bar_music)).check(matches(isDisplayed()));

        //проверяем что песня и артист тот же что и в списке
        onView(withId(R.id.song_name)).check(matches(withText(musicFile.get(position).getTitle())));
        onView(withId(R.id.song_artist)).check(matches(withText(musicFile.get(position).getArtist())));
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }
    @Test
    public void test6_clickBack()throws Exception {
        //остановим проигрыватель
        onView(withId(R.id.play_pause))
                .check(matches(isDisplayed()))
                .perform(click());
        //нажимаем на кнопку
        onView(withId(R.id.back_btn))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000); //пауза для анимации
        //проверяем что отобразилось окно с списком песен
        onView(withId(R.id.open_Music))
                .check(matches(isDisplayed()));
        Thread.sleep(3000); //пауза для анимации
        scenarioRuleMain.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRuleMain.getScenario().close();
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();

    }

    @Test
    public void test3_clickshuffleOnOff()throws Exception{
        //остановим проигрыватель
        onView(withId(R.id.play_pause))
                .check(matches(isDisplayed()))
                .perform(click());
        //проверяем что рисунок соответсвует shuffle выключен
        assertFalse(shuffleBoolean);
            //нажимаем на кнопку
            onView(withId(R.id.shuffle_on))
                    .perform(click());
            Thread.sleep(2000); //пауза для анимации
        //меняется изображение кнопки
        assertTrue(shuffleBoolean);
        Thread.sleep(2000); //пауза для анимации
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void test4_clickRepeatOnOff()throws Exception{
        //остановим проигрыватель
        onView(withId(R.id.play_pause))
                .check(matches(isDisplayed()))
                .perform(click());
        //проверяем что рисунок соответсвует выключен
        assertFalse(repeatBoolean);
        //нажимаем на кнопку
        onView(withId(R.id.repeat))
                .perform(click());
        Thread.sleep(2000); //пауза для анимации
        //меняется изображение кнопки
        assertTrue(repeatBoolean);
        Thread.sleep(2000); //пауза для анимации

        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void test5_clickplayPause() throws Exception{

        //проверяем что рисунок соответсвует выключен
        assertTrue(mediaPlayer.isPlaying());
        //нажимаем на кнопку
        onView(withId(R.id.play_pause))
                .perform(click());
        Thread.sleep(1000); //пауза для анимации
        //меняется изображение кнопки
        assertFalse(mediaPlayer.isPlaying());
        Thread.sleep(2000); //пауза для анимации
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();

    }




}