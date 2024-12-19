# SportsCompiler
The SportsCompiler is an application designed for Bilkent University students to simplify the organization and participation in football matches. The app provides a mobile-friendly interface for creating, joining, and managing matches, along with additional features such as weather forecasts, and communication tools. 

Application Video: [SportsCompiler Working Demo Video](https://drive.google.com/file/d/1m5daqhhGMY3UIxR19fJYQUPDtS4Yr-kH/view?usp=sharing)
To Download the App as APK: [SportsCompiler APK for Android Devices](https://drive.google.com/file/d/1_FuJreQ7UaV3NPU8bbMI84_b1nDoUzey/view?usp=sharing)

## **How to run the program:**

When the application is first installed, users can log in using their Bilkent email if they already have an account. If they forget their password, they can use the "Forgot Password" button to verify their identity via email authentication. For new users without an account, clicking the "Register" button allows them to create one easily with the requireed information.  After creating an account, users are required to verify their email address to complete the process. An email is sent to the user, containing a link that must be clicked to proceed. 

After you enter your account, users will come to the home page where they can see the next three days weather forecast and their upcoming matches.  They can see the details about this match and by clicking on them, they can see other players who registered for that match, and leave the match if they wish. With the bottom navigation view at the bottom users can navigate through matches page and profile page. And if user is the admin of one of the upcoming matches, they are able to click on that match and accept or decline the other users applications.

On profile page users can view the past matches and required information about their profile. By clicking settings button, users can access the setitngs page and in the settings page users can change passwords and send öessages to applicaton admins. And in profile page users can change their profile image. When users click to a past match, they will be able to rate other players and see the information about the match. If there is anything wrong with the information users are able to report the admins by clikcking the report admin button and sending messages to application admin button. If the user is the admin, they are able to enter the match score and rate other players.

On the matches page users are able to filter the existing matches according to the specified date, number of players and place. Users are also sort the all existing matches according to the date, number of players and place. Users are able to click on any existing match and if there is a suitable position according to the users will, they are able to apply to that position. On this page users are also able to create new matches by clicking the plus button. After clicking the plus button, users can enter the required informations and create a new match. After the match is created, it will be added as one of the matches in the matches page. 

## **Creators:** 
CodingBat Masters:
- Talha Safa Küçük
- Eren Sucuoğlu
- Burak Yılmaz
- Çağan Aksoy

## **Dependencies:** 

com.google.firebase:firebase-bom:33.5.1

com.google.firebase:firebase-analytics

com.google.firebase:firebase-auth:23.1.0

com.google.android.gms:play-services-auth:21.2.0

com.google.firebase:firebase-firestore:24.9.0

androidx.fragment:fragment:1.8.5

androidx.recyclerview:recyclerview:1.3.2

androidx.appcompat:appcompat:1.7.0

com.google.android.material:material:1.12.0

androidx.activity:activity:1.9.2

androidx.constraintlayout:constraintlayout:2.1.4

androidx.leanback:leanback:1.0.0

com.github.bumptech.glide:glide:4.11.0

com.squareup.retrofit2:retrofit:2.9.0

com.squareup.retrofit2:converter-gson:2.9.0

com.squareup.okhttp3:logging-interceptor:4.9.3

com.github.bumptech.glide:glide:4.15.1

com.github.bumptech.glide:compiler:4.15.1

