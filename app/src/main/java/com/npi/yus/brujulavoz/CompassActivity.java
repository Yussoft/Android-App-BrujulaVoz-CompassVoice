package com.npi.yus.brujulavoz;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
    Author: Jesús Sánchez de Castro
    Last Modification: 25/08/2016
    https://github.com/Yussoft
    https://justyusblog.wordpress.com/

    Bibliography:
    - http://www.codingforandroid.com/2011/01/using-orientation-sensors-simple.html
    - https://www.codeofaninja.com/2013/08/android-compass-code-example.html
    - https://developer.android.com/training/basics/firstapp/starting-activity.html
    - https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    - https://developer.android.com/training/displaying-bitmaps/index.html
 */


/*
    CompassActivity class:

    Implements all the methods needed to create a functional compass.

 */
public class CompassActivity extends Activity implements SensorEventListener{

    //Mediaplayer for sounds
    MediaPlayer myMediaPlayer = null;

    //User Interface elements
    protected ImageView compass;
    protected TextView objective;
    protected TextView currentDTV;

    //Sensors
    private SensorManager mSensorManager;

    private String NSEO;

    //Variables used for orientation and rotation animation.
    private float currentDegree = 0f;//Current degree in the compass
    private float direction;    //Direction set (north,south,east,west)
    private float error;        //Error introduced by the user (north 5) [north-5, north+5]

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Layout elements
        objective = (TextView) findViewById(R.id.objectiveDegreeText);
        currentDTV = (TextView)findViewById(R.id.degreeText);
        compass = (ImageView) findViewById(R.id.compass);

        //Sound
        myMediaPlayer = MediaPlayer.create(this, R.raw.bell);

        //Sensor initialization
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Load bitmap
        compass.setImageBitmap(decodeSampledBitmapFromResource(getResources(),R.drawable.compass,500,500));

        //Get content from Intent
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        System.out.println(message);

        /*
            We get a pair of words from the Speech recognition Activiy (North 10).
            They are split in a array and used individually.

            The first word (Norte,Sur,Este,Oeste in Spanish)/(North,South,East,West)
            is used to set the orientation variable direction. The second word is a number
            which is used to set a error margin.

         */
        if (message != null) {
            String[] words = message.split(" ");
            if (words.length == 2 && correctFormat(words[0])) {
                String sError = words[1];
                error = Integer.parseInt(sError);
                objective.setText(NSEO+" "+error);

                switch (NSEO) {

                    case "Norte":
                        direction = 0;
                        break;
                    case "Sur":
                        direction = 180;
                        break;
                    case "Este":
                        direction = 90;
                        break;
                    case "Oeste":
                        direction = 270;
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), getString(R.string.error_dir), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_dir), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**********************************************************************************************/
    /*
        Method provided by google, used to change size of a bitmap in order to avoid
        java.lang.OutofMemoryError: bitmap size exceeds VM budget error. In this case
        the compass image is resized and loaded in memory with this method.

        source: https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**********************************************************************************************/
    /*
        Method provided by google that calculates the size of a bitmap given.

        source: https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**********************************************************************************************/
    /*
        Method used to make sure that the cardinal directions are the ones expected. IN SPANISH!
    */
    private boolean correctFormat(String word){
        switch(word){
            case "Norte":
                NSEO = "Norte";
                return true;
            case "norte":
                NSEO = "Norte";
                return true;
            case "Sur":
                NSEO = "Sur";
                return true;
            case "sur":
                NSEO = "Sur";
                return true;
            case "Este":
                NSEO = "Este";
                return true;
            case "este":
                NSEO = "Este";
                return true;
            case "Oeste":
                NSEO = "Oeste";
                return true;
            case "oeste":
                NSEO = "Oeste";
                return true;
            default: return false;
        }

    }

    /**********************************************************************************************/
    /*
        Method used to register the sensor listener when the app is resumend.
     */
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
    }

    /**********************************************************************************************/
    /*
        Method used to unregister the sensor listener when the app is paused.
     */
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**********************************************************************************************/
    /*
        Most important method of this activity, when the sensor values change, this method is
        called.

     */
    public void onSensorChanged(SensorEvent event) {
        //event is a variable that provides the values from the sensor. (ORIENTATION)
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            //First of all, get the orientation degree from the sensor.
            float objective_degree = Math.round(event.values[0]);

            currentDTV.setText(String.valueOf(objective_degree));

            /*
                Now we are going to create an animation to rotate the compass.
                This animation will change the orientation from current_degree
                to the new one obtained from the event (objective_degree).
             */

            RotateAnimation rotate = new RotateAnimation(currentDegree, -objective_degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(220);
            rotate.setInterpolator(new LinearInterpolator());
            compass.startAnimation(rotate);
            currentDegree = -objective_degree; //Update the current orientation

            //System.out.println(currentDegree + ", " + (direction - error) + ", "+(direction + error)) ;

            //Condition set to detect when the user is facing the right direction (Given in the first
            // activity).
            if ((-currentDegree) <= direction + (360 * error / 2) / 100 && (-currentDegree) >= direction - (360 * error / 2) / 100){
            //if(currentDegree <= (direction+error) && currentDegree >= (direction-error)){
                if(!myMediaPlayer.isPlaying())
                    myMediaPlayer.start();
            }
        }

    }

    /**********************************************************************************************/
    //Not used in this app.
    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

}
