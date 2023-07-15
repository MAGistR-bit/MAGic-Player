package com.example.projectMP3;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.projectMP3.Activity.MainActivity.musicFile;
import static org.hamcrest.Matchers.allOf;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.projectMP3.Activity.MainActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //фиксируем порядок выполнения тестов по имени
@RunWith(AndroidJUnit4ClassRunner.class)
public class AlbumFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void  test1_ensureFragmentViewIsPresent() throws Exception {

        onView(allOf(withText("Альбомы"), isDescendantOfA(withId(R.id.tab_layout))))
                .check (matches(isDisplayed()))
                .perform(click());
        Thread.sleep(500);

        //проверяем видимость элементов
        onView(withId(R.id.Album_recyclerView)).check(matches(isDisplayed()));
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void test2_scrollToItemcheckItsText()   throws Exception {
        if (!musicFile.isEmpty()) {
            onView(allOf(withText("Альбомы"), isDescendantOfA(withId(R.id.tab_layout))))
                    .check (matches(isDisplayed()))
                    .perform(click());
            Thread.sleep(500);
            //скролируем до первой позиции
            onView(withId(R.id.Album_recyclerView))
                    .perform(RecyclerViewActions.scrollToPosition(0));

            Thread.sleep(1000); //пауза для анимации

            //проверяем что на экране есть строка с первой альбомом
            onView(EspressoTestsMatchers.first(withText(musicFile.get(0).getTitle())))
                    .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));


        }
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }


    @Test
    public void  test4_selectListItem()  throws Exception {
        if (!musicFile.isEmpty()) {
            onView(allOf(withText("Альбомы"), isDescendantOfA(withId(R.id.tab_layout))))
                    .check (matches(isDisplayed()))
                    .perform(click());
            Thread.sleep(500);
            // нажимаем на песню
            onView(withId(R.id.Album_recyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            Thread.sleep(2000); //пауза для анимации
            //проверяем что отобразилось окно с данным по альбому
            onView(withId(R.id.song_play_all))
                    .check(matches(isDisplayed()));


        }
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

}