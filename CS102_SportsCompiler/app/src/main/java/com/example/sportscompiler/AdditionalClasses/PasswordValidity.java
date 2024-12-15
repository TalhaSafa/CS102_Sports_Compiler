package com.example.sportscompiler.AdditionalClasses;

import android.content.Context;
import android.widget.Toast;

import com.example.sportscompiler.RegisterPageAc;

public class PasswordValidity {

    public static boolean isPasswordValid(String password, Context context)
    {
        boolean isValid = true;
        if(password.length() < 7)
        {
            isValid = false;
            Toast.makeText(context, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
        }
        else if (!doesIncludeUppercase(password))
        {
            isValid = false;
            Toast.makeText(context, "Password must include at least 1 uppercase", Toast.LENGTH_SHORT).show();
        }
        else if(!doesIncludeLowercase(password))
        {
            isValid = false;
            Toast.makeText(context, "Password must include at least 1 lowercase", Toast.LENGTH_SHORT).show();
        }
        else if(!doesIncludeNumeric(password))
        {
            isValid = true;
            Toast.makeText(context, "Password must include at least 1 digit", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private static boolean doesIncludeUppercase(String pass)
    {
        for(int i = 0 ; i < pass.length() ; i++)
        {
            if(Character.isUpperCase(pass.charAt(i)))
            {
                return  true;
            }
        }
        return false;
    }

    private static boolean doesIncludeLowercase(String pass)
    {
        for(int i = 0 ; i < pass.length() ; i++)
        {
            if(Character.isLowerCase(pass.charAt(i)))
            {
                return  true;
            }
        }
        return false;
    }

    private static boolean doesIncludeNumeric(String pass)
    {
        for(int i = 0 ; i < pass.length() ; i++)
        {
            if(Character.isDigit(pass.charAt(i)))
            {
                return  true;
            }
        }
        return false;
    }
}
