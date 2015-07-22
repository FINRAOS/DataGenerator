package SocialNetwork_Example.java;

import Helpers.RandomHelper;
import scala.NotImplementedError;
import scala.Tuple2;

import java.sql.Date;

/**
 * Utility methods relating to data that are part of social event users.
 * Not fully implemented, and not used, because this is for data generation and not stub generation, and
 * the current implementation only supports stub generation and doesn't yet have any implementation of
 * stub-to-data translation.
 */
public class SocialNetworkUtilities {
    public static Boolean getRandomIsSecret() {
        return RandomHelper.evaluateProbability(0.1); // 10% of accounts are secret accounts.
    }

    public static Tuple2<Double,Double> getRandomGeographicalLocation() {
        return new Tuple2<>(
            (double)(RandomHelper.randWithConfiguredSeed().nextInt(999) + 1) / 100,
            (double)(RandomHelper.randWithConfiguredSeed().nextInt(999) + 1) / 100);
    }

    public static Double getDistanceBetweenCoordinates(Tuple2<Double,Double> point1, Tuple2<Double,Double> point2) {
        // sqrt( (x2-x1)^2 + (y2-y2)^2 )
        Double xDiff = point1._1() - point2._1();
        Double yDiff = point1._2() - point2._2();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public static Double getDistanceWithinThresholdOfCoordinates(
            Tuple2<Double,Double> point1, Tuple2<Double,Double> point2) {
        throw new NotImplementedError();
    }

    public static Date getRandomBirthDate() {
        throw new NotImplementedError();
    }

    private static final Double COORDINATE_THRESHOLD = 2.0;
    public static Boolean areCoordinatesWithinThreshold(Tuple2<Double,Double> point1, Tuple2<Double,Double> point2) {
        return getDistanceBetweenCoordinates(point1, point2) < COORDINATE_THRESHOLD;
    }
}
