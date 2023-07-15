package com.example.projectMP3;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.projectMP3.Activity.MainActivity.musicFile;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.projectMP3.Activity.MainActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //фиксируем порядок выполнения тестов по имени
@RunWith(AndroidJUnit4ClassRunner.class)
public class SongFragmentTest  {

    @Rule
    public ActivityScenarioRule<MainActivity> scenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void  test1_ensureFragmentViewIsPresent() throws Exception {

        //проверяем видимость элементов
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void test2_scrollToItemcheckItsText()   throws Exception {
        if (!musicFile.isEmpty()) {

            //скролируем до первой позиции
            onView(withId(R.id.recyclerView))
                    .perform(RecyclerViewActions.scrollToPosition(0));

            Thread.sleep(1000); //пауза для анимации

            //проверяем что на экране есть строка с первой песней
            onView(EspressoTestsMatchers.first(withText(musicFile.get(0).getTitle())))
                    .check(matches(isDisplayed()));


        }
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void  test4_selectListItem_LongClick()  throws Exception {
        if (!musicFile.isEmpty()) {
            // нажимаем на песню
            onView(withId(R.id.recyclerView))
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
    public void  test5_selectListItem()  throws Exception {
        if (!musicFile.isEmpty()) {
            // нажимаем на песню
            onView(withId(R.id.recyclerView))
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