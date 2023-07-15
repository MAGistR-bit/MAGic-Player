package com.example.projectMP3;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.projectMP3.Activity.MainActivity.musicFile;
import static com.example.projectMP3.EspressoTestsMatchers.withImageDrawable;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.projectMP3.Fragment.AlbumFragment;
import com.example.projectMP3.Fragment.SongFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SplashScreenActivityTest {

    //запуск Activity и подписка на его жизненный цикл
    @Rule
    public ActivityScenarioRule<SplashScreenActivity> scenarioRule = new ActivityScenarioRule(SplashScreenActivity.class);


    @Test
    public void ensureSplashScreenActivityViewIsPresent() throws Exception {

        onView(withId(R.id.Splash_Activity)).check(matches(isDisplayed())); //проверяем что view отображается

        onView(withId(R.id.imageView2)).check(matches(isDisplayed()));//проверяем что view отображается
        onView(withId(R.id.imageView2)).check(matches(withContentDescription(R.string.startSplashScreen))); //проверяем строку описания
        onView(withId(R.id.imageView2)).check(matches(withImageDrawable(R.drawable.splash_screen_logo))); // проверяем отбражаемое изображение

        onView(withId(R.id.textView3)).check(matches(isDisplayed()));//проверяем что view отображается
        onView(withId(R.id.textView3)).check(matches(withText(R.string.appName)));// проверяем отбражаемый текст

        onView(withId(R.id.textView)).check(matches(isDisplayed()));//проверяем что view отображается
        onView(withId(R.id.textView)).check(matches(withText(R.string.all_rights_are_reserved)));// проверяем отбражаемый текст

        onView(withId(R.id.toolBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE))); // проверяем view скрыт


        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }


}