package com.example.projectMP3;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.projectMP3.Activity.MainActivity.musicFile;
import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.projectMP3.Activity.AlbumDetailsActivity;
import com.example.projectMP3.Activity.MainActivity;


import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //фиксируем порядок выполнения тестов по имени
@RunWith(AndroidJUnit4ClassRunner.class)
public class AlbumDetailsActivityTest  {

    @Rule
    public ActivityScenarioRule<MainActivity> scenarioRuleMain = new ActivityScenarioRule<>(MainActivity.class);
    //запускам данные об альбоме с данные 1 альбома в списке
    @Rule
    public ActivityScenarioRule<AlbumDetailsActivity> scenarioRule = new ActivityScenarioRule<>(getAlbom());

    Intent getAlbom()
    {
        try {
            Context instrumentationContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent intent = new Intent(instrumentationContext, AlbumDetailsActivity.class);
            intent.putExtra("AlbumPosition", musicFile.get(0).getAlbum());
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
        onView(withId(R.id.image)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_album_all_play)).check(matches(isDisplayed()));
        onView(withId(R.id.btn)).check(matches(isDisplayed()));
        onView(withId(R.id.song_play_all)).check(matches(isDisplayed()));
        onView(withId(R.id.album_activity_songs_count)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerView_details)).check(matches(isDisplayed()));

        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void  test2_selectListItem_LongClick()  throws Exception {
        if (!musicFile.isEmpty()) {
            // нажимаем на песню
            onView(withId(R.id.recyclerView_details))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
            Thread.sleep(3000); //пауза для анимации
            //проверяем что отобразилось окно с подтверждением удаления
            onView(withText("Удаление песни"))
                    .check(matches(isDisplayed()));
        }

        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void  test3_selectListItem()  throws Exception {
        if (!musicFile.isEmpty()) {
            // нажимаем на песню
            onView(withId(R.id.recyclerView_details))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            onView(withId(R.id.play_pause))
                    .check(matches(isDisplayed()))
                    .perform(click());
            Thread.sleep(2000); //пауза для анимации
            //проверяем что отобразилось окно с плеером наименование песни совпадает
            onView(EspressoTestsMatchers.first(withText(musicFile.get(0).getTitle())))
                    .check(matches(isDisplayed()));
            //Thread.sleep(3000); //пауза для анимации

        }

        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }
}