package com.example.projectMP3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.TypeSafeMatcher;

//свой метод сравнения для первого элмента и для рисунков
public class EspressoTestsMatchers {
    private static final String TAG = EspressoTestsMatchers.class.getCanonicalName();

    //получаем первый элемент сравнения из списка совпадений
    public static <T> Matcher<T>  first(final Matcher<T>  matcher) {
        return new BaseMatcher<T> () {
            boolean isFirst = true;

            @Override
            public boolean matches(final Object item) {
                if (isFirst && matcher.matches(item)) {
                    isFirst = false;
                    return true;
                }

                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("should return first matching item");
            }
        };
    }

    //сравниваем цвет фона
    public static Matcher<View> withBackgroundColor(final int resourceColorId, final Activity activity) {
        return new TypeSafeDiagnosingMatcher<View>() {

            String error;

            @Override
            public void describeTo(Description description) {
                description.appendText(error);
            }

            @Override
            protected boolean matchesSafely(View view, Description mismatchDescription) {
                Drawable drawable = view.getBackground();
                Drawable otherDrawable = ContextCompat.getDrawable(view.getContext(), resourceColorId);

                if (drawable instanceof ColorDrawable) {
                    int colorId = ((ColorDrawable) drawable).getColor();

                    if(colorId == resourceColorId) {
                        return true;
                    } else {
                        error = "FAILED Got: " + colorId;
                    }
                } else {
                    Log.e(TAG, drawable.toString());
                    Log.e(TAG, otherDrawable.toString());
                    error = "Not ColorDrawable's!!";
                }

                return false;
            }
        };
    }

    private static int getColor(Context context, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        } else {
            return ContextCompat.getColor(context, color);
        }
    }

    public static int getBackgroundColor(Context context, View v, int defaultColor) {
        Drawable drawable = v.getBackground();
        if (drawable instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            return colorDrawable.getColor();
        } else {
            return getColor(context, defaultColor);
        }
    }
    //сравниваем фон
    public static Matcher<View> withBackground(final int resourceId) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                return sameBitmap(view.getContext(), view.getBackground(), resourceId);
            }

            @Override
            protected void describeMismatchSafely(View item, Description mismatchDescription) {
                mismatchDescription.appendText("view.getBackground() returned: " + item.getBackground());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("" + resourceId);
            }
        };
    }

    //сравниваем рисунок (не работает если есть наложение фона)
    public static Matcher<View> withCompoundDrawable(final int resourceId) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has compound drawable resource " + resourceId);
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                for (Drawable drawable : textView.getCompoundDrawables()) {
                    if (sameBitmap(textView.getContext(), drawable, resourceId)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
    //сравниваем рисунок (не работает если есть наложение фона)
    public static Matcher<View> withImageDrawable(final int resourceId) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has image drawable resource " + resourceId);
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                return sameBitmap(imageView.getContext(), imageView.getDrawable(), resourceId);
            }
        };
    }

    private static boolean sameBitmap(Context context, Drawable drawable, int resourceId) {
        Drawable otherDrawable = context.getResources().getDrawable(resourceId);
        if (drawable == null || otherDrawable == null) {
            Log.e(TAG, "drawable null!!");
            return false;
        }
        if (drawable instanceof StateListDrawable && otherDrawable instanceof StateListDrawable) {
            Log.e(TAG, "other drawable!!");
            return drawable.getCurrent().equals(otherDrawable.getCurrent());
        }
        if (drawable instanceof BitmapDrawable) {
            Log.e(TAG, "bitmap drawable!!");
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap otherBitmap = ((BitmapDrawable) otherDrawable).getBitmap();
            return bitmap.sameAs(otherBitmap);
        }
        return false;
    }
}
