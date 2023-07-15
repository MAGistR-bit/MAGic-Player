package com.example.projectMP3;


import android.content.Context;
import android.net.Uri;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SplashScreenActivityTest.class,
        MainActivityTest.class,
        AboutAppActivityTest.class,
        SongFragmentTest.class,
        AlbumFragmentTest.class
        ,AlbumDetailsActivityTest.class
        ,PlayerActivityTest.class
} )
public class SuitTests {


}
