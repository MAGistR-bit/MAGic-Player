package com.example.projectMP3;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.projectMP3.Activity.AboutAppActivity;
import com.example.projectMP3.Activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class AboutAppActivityTest {

    @Rule
    public ActivityScenarioRule<AboutAppActivity> scenarioRule = new ActivityScenarioRule<>(AboutAppActivity.class);

    @Test
    public void test1_ensureActivityViewIsPresent() throws Exception {

        //проверяем видимость элементов
        onView(withId(R.id.id_version))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.version_app)));

        onView(withText(R.string.name_player))
                .check(matches(isDisplayed()));

        onView(withId(R.id.card_developer))
                .check(matches(isDisplayed()));
        onView(withContentDescription(R.string.icon_app))
                .check(matches(isDisplayed()));

        onView(withId(R.id.general_back))
                .check(matches(isDisplayed()));
        onView(withId(R.id.id_developer))
                .check(matches(isDisplayed()));
        onView(withContentDescription(R.string.edit_icon))
                .check(matches(isDisplayed()));
        onView(withText(R.string.developed_by))
                .check(matches(isDisplayed()));


        scenarioRule.getScenario().moveToState(Lifecycle.State.DESTROYED);
        scenarioRule.getScenario().close();
    }

}