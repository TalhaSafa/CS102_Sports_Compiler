package com.example.sportscompiler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;



public class FragmentLoad
{

    public static void loadFragment(AppCompatActivity activity, int containerID, Fragment fragment) {

        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(containerID, fragment);
        transaction.commit();
    }

    //call with (this, className.class)
    //context must be activity
    public static void changeActivity(Context current, Class<?> target)
    {
        Intent intent = new Intent(current, target);
        if(!(current instanceof Activity))
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        current.startActivity(intent);
        if(current instanceof Activity)
        {
            ((Activity) current).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
