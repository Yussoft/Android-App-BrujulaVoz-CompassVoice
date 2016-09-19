package com.npi.yus.brujulavoz;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

/*

    *Copyright (C) 2016  Jesús Sánchez de Castro
    *This program is free software: you can redistribute it and/or modify
    *it under the terms of the GNU General Public License as published by
    *the Free Software Foundation, either version 3 of the License, or
    *(at your option) any later version.
    *This program is distributed in the hope that it will be useful,
    *but WITHOUT ANY WARRANTY; without even the implied warranty of
    *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    *GNU General Public License for more details.
    *You should have received a copy of the GNU General Public License
    *along with this program.  If not, see <http://www.gnu.org/licenses/>
    *
    * @author Jesús Sánchez de Castro
    * @version 19.09.2016
    *
    Last Modification: 19/09/2016
    https://justyusblog.wordpress.com/
    https://github.com/Yussoft


    App idea: This app will ask the used to use the speech recognition and say a cardinal point
    followed by a margin error. For example Norte 10, which means the application will be search
    that cardinal point with a margin of x. North 5 means [-5,5].

    When the speech recognition is done, the user can press the next activity button to open the
    compass and track the objetive. If the user is in the right direction the app will make a bell
    sound.
 */


/*
    MainActivity class:

    Implements all the methods needed to used Speech Recognition. Launches the compass
    Activity.

 */
public class MainActivity extends Activity {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    public final static String EXTRA_MESSAGE = "com.yus.BrujulaVoz.MESSAGE";

    //String sent in the intent (Norte 10/Sur 5)/(North 10/South 5)
    private String speechG;

    //Button used to launch an Activity
    protected Button startCompassA;

    /**********************************************************************************************/
    /*
        Method called when the Activity is created, initializes the ImageButton and sets a listener.
        Also portrait only mode is set. The ImageButton that launches the next activity is invisible
        until the user has used speech recognition.

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        ImageButton micButton = (ImageButton)findViewById(R.id.MicButton);
        startCompassA = (Button)findViewById(R.id.NextActivityButton);
        startCompassA.setVisibility(View.INVISIBLE);
        micButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                // Speech Input
                promptSpeechInput();
            }
        });
    }

    /**********************************************************************************************/
    /*
        Method called when the ImageButton is pressed. Launches the google speech recognition
        activity.

     */
    private void promptSpeechInput() {
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //EXTRA_LANGUAGE - Spanish
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**********************************************************************************************/
    /*
        Method called when the google speech recognition activity has finished.

        Parameters:
        - requestCode: reports from what Activity the app is coming from.
        - resultCode: reports if there was any error.
        - data: intent with the speech.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TextView txtSpeechInput = (TextView)findViewById(R.id.SpeechText);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    //Next Activity button is set to visible
                    startCompassA.setVisibility(View.VISIBLE);

                    //Getting the speech from the intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String speech = result.get(0);

                    if(speech == null){
                        speech = "Ha habido algún error.";
                        txtSpeechInput.setText(speech);
                        speechG = speech;
                    }
                    else {
                        //Set text and save speech
                        txtSpeechInput.setText(speech);
                        txtSpeechInput.setTextSize(60);
                        speechG = speech;
                    }
                }
                break;
            }
        }
    }


    /**********************************************************************************************/
    /*
        Method called when the ButtonImage is clicked. Launches CompassActivity. Intent contains
        the speech from the speech recognition activity.

     */
    protected void compassActivity (View view){

        Intent intent = new Intent(this, CompassActivity.class);
        intent.putExtra(EXTRA_MESSAGE, speechG);
        startActivity(intent);
    }
}
