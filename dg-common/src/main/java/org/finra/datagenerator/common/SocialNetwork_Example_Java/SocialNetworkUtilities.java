/*
 * Copyright 2014 DataGenerator Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finra.datagenerator.common.SocialNetwork_Example_Java;

import org.finra.datagenerator.common.Helpers.RandomHelper;
import scala.NotImplementedError;
import scala.Tuple2;

import java.sql.Date;

/**
 * Utility methods relating to data that are part of social event users.
 * Not fully implemented, and not used, because this is for data generation and not stub generation, and
 * the current implementation only supports stub generation and doesn't yet have any implementation of
 * stub-to-data translation.
 */
public final class SocialNetworkUtilities {
    private SocialNetworkUtilities() {
        // Not called -- utility class
    }

    /**
     * Randomly get whether it is a secret account
     * @return Boolean
     */
    public static Boolean getRandomIsSecret() {
        return RandomHelper.evaluateProbability(0.1); // 10% of accounts are secret accounts.
    }

    /**
     * Get random geographical location
     * @return 2-Tuple of ints (latitude, longitude)
     */
    public static Tuple2<Double, Double> getRandomGeographicalLocation() {
        return new Tuple2<>(
            (double) (RandomHelper.randWithConfiguredSeed().nextInt(999) + 1) / 100,
            (double) (RandomHelper.randWithConfiguredSeed().nextInt(999) + 1) / 100);
    }

    /**
     * Get distance between geographical coordinates
     * @param point1 Point1
     * @param point2 Point2
     * @return Distance (double)
     */
    public static Double getDistanceBetweenCoordinates(Tuple2<Double, Double> point1, Tuple2<Double, Double> point2) {
        // sqrt( (x2-x1)^2 + (y2-y2)^2 )
        Double xDiff = point1._1() - point2._1();
        Double yDiff = point1._2() - point2._2();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    /**
     * Not implemented.
     * @param point1 Point1
     * @param point2 Point2
     * @return Throws an exception.
     */
    public static Double getDistanceWithinThresholdOfCoordinates(
            Tuple2<Double, Double> point1, Tuple2<Double, Double> point2) {
        throw new NotImplementedError();
    }

    /**
     * Not implemented.
     * @return Throws exception.
     */
    public static Date getRandomBirthDate() {
        throw new NotImplementedError();
    }

    private static final Double COORDINATE_THRESHOLD = 2.0;

    /**
     * Whether or not points are within some threshold.
     * @param point1 Point 1
     * @param point2 Point 2
     * @return True or false
     */
    public static Boolean areCoordinatesWithinThreshold(Tuple2<Double, Double> point1, Tuple2<Double, Double> point2) {
        return getDistanceBetweenCoordinates(point1, point2) < COORDINATE_THRESHOLD;
    }
}
