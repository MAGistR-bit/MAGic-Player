package com.example.projectMP3;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.projectMP3.Activity.MainActivity.musicFile;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.KeyEvent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.viewpager.widget.ViewPager;

import com.example.projectMP3.Activity.MainActivity;
import com.example.projectMP3.Fragment.AlbumFragment;
import com.example.projectMP3.Fragment.SongFragment;
import com.example.projectMP3.model.MusicFile;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.ArrayList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) //фиксируем порядок выполнения тестов по имени
@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest  {




    @Rule
    public  ActivityScenarioRule<MainActivity> scenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void test0_ensureActivityViewIsPresent() throws Exception {

        //проверяем видимость элементов
        onView(withId(R.id.toolBar)).check(matches(isDisplayed()));
        onView(withId(R.id.tab_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.view_Pager)).check(matches(isDisplayed()));
        onView(withId(R.id.open_Music)).check(matches(isDisplayed()));

        //по окончании теста активность уничтожается - для корректности сделаем это принудительно, иначе последующие тесты могут не сработать
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
      }

    @Test
    public void test1_getAllAudio() throws Exception {
        ;
        scenarioRule.getScenario().onActivity(
                activity -> {
                    ArrayList<MusicFile> l = activity.getAllAudio(activity);
                    assertTrue(l.size() > 0);
                }
        );
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void test2_checkViewPage() throws Exception {
        scenarioRule.getScenario().onActivity(
                activity -> {
                    ViewPager viewPager = activity.findViewById(R.id.view_Pager);
                    androidx.appcompat.widget.Toolbar toolBar = activity.findViewById(R.id.toolBar);

                    assertNotNull(toolBar); //проверяем что Toolbar существует
                    assertEquals(toolBar.getTitle(), "MAGic Player"); //проверяем текст на панели совпадает

                    assertNotNull(viewPager); //проверяем что viewPager существует
                    assertNotNull(viewPager.getAdapter()); //проверяем что адаптер существует
                    assertTrue(viewPager.getAdapter().getCount() == 2); //проверяем количество элементов в адаптере

                }
        );
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();

    }

    @Test
    public void test3_checkTabSwitch()throws Exception {
        // переключаем вкладки
        onView(allOf(withText("Альбомы"), isDescendantOfA(withId(R.id.tab_layout))))
                .check (matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);
        // проверяем что перключенная вкладка стала Альбомы
        onView(withId(R.id.view_Pager)).check((viewPager, noViewFoundException) ->
        {
            assertEquals(((ViewPager) viewPager).getAdapter().getPageTitle(((ViewPager) viewPager).getCurrentItem()), "Альбомы");
        });
        // проверяем что перключенная вкладка стала Альбомы (варинат 2)
        /*scenarioRule.getScenario().onActivity(
                activity -> {
                    ViewPager viewPager = activity.findViewById(R.id.view_Pager);
                    assertEquals(viewPager.getAdapter().getPageTitle(viewPager.getCurrentItem()), "Альбомы");
                }
        );*/
        Thread.sleep(1000);
        // переключаем вкладки
        onView(allOf(withText("Песни"), isDescendantOfA(withId(R.id.tab_layout))))
                .check (matches(isDisplayed()))
                .perform(click());
        Thread.sleep(1000);
        // проверяем что перключенная вкладка стала Песни
        onView(withId(R.id.view_Pager)).check((viewPager, noViewFoundException) ->
        {
            assertEquals(((ViewPager) viewPager).getAdapter().getPageTitle(((ViewPager) viewPager).getCurrentItem()), "Песни");
        });
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void test4_clickMenuSettings()throws Exception {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
        onView(withText(R.string.menu_settings))
                .check (matches(isDisplayed()))
                .perform(click());
        Thread.sleep(3000);
        //проверяем что открылось новое окно где есть рисунок
        onView(withText(R.string.update_page_app))
                .check(matches(isDisplayed()));
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }
    @Test
    public void test5_clickMenuAbout()throws Exception {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getContext());
        onView(withText(R.string.menu_about_application))
                .check (matches(isDisplayed()))
                .perform(click());
        Thread.sleep(3000);
        //проверяем что открылось с текстом
        onView(withText(R.string.version_app))
                .check(matches(isDisplayed()));
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void test6_clickFilterMusic()throws Exception{
        if (!musicFile.isEmpty())
        {
            //нажимаем на кнопку поиска
            onView(withId(R.id.search_option))
                    .check (matches(isDisplayed()))
                    .perform(click());
            // вводим в поле текст названия первой песни
            onView(withId(androidx.appcompat.R.id.search_src_text))
                    .check (matches(isDisplayed()))
                    .perform(replaceText(musicFile.get(0).getTitle())) //вводим текст
                    .perform(pressKey(KeyEvent.KEYCODE_ENTER)); // нажмаем кнопку
            Thread.sleep(3000);//пауза для анимации
            //проверяем что количество песен при фильтрации совпадает с колчиеством песен в адаптере фрагмента
            onView(withId(R.id.view_Pager)).check((viewPager, noViewFoundException) ->
            {
                assertEquals(((SongFragment)((MainActivity.ViewPagerAdapter)((ViewPager) viewPager).getAdapter()).getItem(0)).songAdapter.getItemCount(),
                        musicFile.stream().filter( item -> item.getTitle().toLowerCase().contains(musicFile.get(0).getTitle().toLowerCase())).count());

            });
            Thread.sleep(3000);//пауза для анимации
            onView(withId(R.id.search_close_btn))
                    .check (matches(isDisplayed()))
                    .perform(click()); // очищаем поле
            Thread.sleep(3000);//пауза для анимации
            //проверяем что кол-во мелодий стало начальным
            onView(withId(R.id.view_Pager)).check((viewPager, noViewFoundException) ->
            {
                assertEquals(((SongFragment)((MainActivity.ViewPagerAdapter)((ViewPager) viewPager).getAdapter()).getItem(0)).songAdapter.getItemCount(),
                        musicFile.size());

            });
            Thread.sleep(3000);//пауза для анимации
            onView(withId(R.id.search_close_btn))
                    .check (matches(isDisplayed()))
                    .perform(click()); // закрываем окно поиска
        }
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

    @Test
    public void test7_clickOpenMusic()throws Exception{
        if (!musicFile.isEmpty())
        {
            //нажимаем на кнопку
            onView(withId(R.id.open_Music))
                    .check (matches(isDisplayed()))
                    .perform(click());
            Thread.sleep(3000); //пауза для анимации
            //проверяем что отобразилось окно с плеером
            onView(withId(R.id.seek_bar_music))
                    .check(matches(isDisplayed()));

            Thread.sleep(1000); //пауза для анимации
            onView(withId(R.id.play_pause))
                    .check(matches(isDisplayed()))
                    .perform(click());
        }
        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }


}