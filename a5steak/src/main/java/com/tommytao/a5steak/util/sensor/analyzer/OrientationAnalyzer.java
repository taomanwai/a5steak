package com.tommytao.a5steak.util.sensor.analyzer;

import android.content.Context;
import android.hardware.SensorManager;
import com.tommytao.a5steak.util.Foundation;

/**
 * Responsible for getting gravity field reading
 *
 */
public class OrientationAnalyzer extends Foundation {

    private static OrientationAnalyzer instance;

    public static OrientationAnalyzer getInstance() {

        if (instance == null)
            instance = new OrientationAnalyzer();

        return instance;
    }

    private OrientationAnalyzer() {

    }

    // --

    public static class OrientationAnalyzed {

        private double yaw;
        private double pitch;
        private double roll;

        public OrientationAnalyzed(double yaw, double pitch, double roll) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public double getYaw() {
            return yaw;
        }

        public double getPitch() {
            return pitch;
        }

        public double getRoll() {
            return roll;
        }
    }

    @Deprecated
    public boolean init(Context context) {
        return super.init(context);
    }

    public OrientationAnalyzed calculateYawPitchRoll(float gravityX, float gravityY, float gravityZ, float geoMagneticX, float geoMagneticY, float geoMagneticZ ){

        float R[] = new float[9];
        float I[] = new float[9];

        float[] gravityValues = {gravityX, gravityY, gravityZ};
        float[] geoMagneticValues = {geoMagneticX, geoMagneticY, geoMagneticZ};

        boolean succeed = SensorManager.getRotationMatrix(R, I, gravityValues, geoMagneticValues);

        if (!succeed)
            return null;

        float[] fineOrientationValues = new float[3];
        getSensorManager().getOrientation(R, fineOrientationValues);


        double yaw = Math.toDegrees(fineOrientationValues[0]);
        double pitch = Math.toDegrees(fineOrientationValues[1]);
        double row = Math.toDegrees(fineOrientationValues[2]);



        return new OrientationAnalyzed(yaw, pitch, row);







    }



}
