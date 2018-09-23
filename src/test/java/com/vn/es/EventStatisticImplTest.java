package com.vn.es;

import static org.testng.Assert.*;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit-тесты для {@link EventStatisticImpl}
 *
 * @author Vladislav Nosov
 */
public class EventStatisticImplTest {

    private EventStatisticImpl eventStatistic;

    private static final long now = System.currentTimeMillis();

    @BeforeMethod
    public void setUp() {
        eventStatistic = new EventStatisticImpl();
    }

    @Test
    public void testRegisterEventPositive() {
        eventStatistic.registerEvent(System.currentTimeMillis());

        assertEquals(eventStatistic.getCountByLastMinute(), 1);
        assertEquals(eventStatistic.getCountByLastHour(), 1);
        assertEquals(eventStatistic.getCountByLastDay(), 1);
    }

    @Test
    public void testRegisterEventWithOldDate() {
        eventStatistic.registerEvent(System.currentTimeMillis() - Duration.ofDays(1L).toMillis());

        assertEquals(eventStatistic.getCountByLastMinute(), 0);
        assertEquals(eventStatistic.getCountByLastHour(), 0);
        assertEquals(eventStatistic.getCountByLastDay(), 0);
    }

    @Test
    public void testRegisterEventNegative() {
        IllegalArgumentException exception = Assert
                .expectThrows(IllegalArgumentException.class, () -> eventStatistic.registerEvent(0));

        assertEquals(eventStatistic.getCountByLastMinute(), 0);
        assertEquals(eventStatistic.getCountByLastHour(), 0);
        assertEquals(eventStatistic.getCountByLastDay(), 0);
        assertEquals(exception.getMessage(), "Timestamp cannot be 0");
    }

    @Test
    public void testRegisterEventsInDifferentMinutesPositive() {
        eventStatistic.registerEvent(now);
        eventStatistic.registerEvent(now + Duration.ofMinutes(1L).toMillis());

        assertEquals(eventStatistic.getCountByLastMinute(), 1);
        assertEquals(eventStatistic.getCountByLastHour(), 2);
        assertEquals(eventStatistic.getCountByLastDay(), 2);
    }

    @Test
    public void testRegisterEventsInDifferentHoursPositive() {
        eventStatistic.registerEvent(now);
        eventStatistic.registerEvent(now + Duration.ofHours(1L).toMillis());

        assertEquals(eventStatistic.getCountByLastMinute(), 1);
        assertEquals(eventStatistic.getCountByLastHour(), 1);
        assertEquals(eventStatistic.getCountByLastDay(), 2);
    }

    @Test
    public void testRegisterEventsInDifferentDaysPositive() {
        eventStatistic.registerEvent(now);
        eventStatistic.registerEvent(now + Duration.ofDays(1L).toMillis());

        assertEquals(eventStatistic.getCountByLastMinute(), 1);
        assertEquals(eventStatistic.getCountByLastDay(), 1);
        assertEquals(eventStatistic.getCountByLastHour(), 1);
    }

    @Test
    public void testGetCountByLastDay() {
        eventStatistic.registerEvent(now);
        assertEquals(eventStatistic.getCountByLastDay(), 1);
    }

    @Test
    public void testRegisterNextDayEventAndGetCountByNextDay() {
        eventStatistic.registerEvent(now + Duration.ofDays(1L).toMillis());
        assertEquals(eventStatistic.getCountByLastDay(), 0);
    }

    @Test
    public void testGetCountByLastHour() {
        eventStatistic.registerEvent(now);
        assertEquals(eventStatistic.getCountByLastHour(), 1);
    }

    @Test
    public void testRegisterLastHourEventAndGetCountByLastHour() {
        eventStatistic.registerEvent(now - Duration.ofHours(1L).toMillis());
        assertEquals(eventStatistic.getCountByLastHour(), 0);
    }

    @Test
    public void testRegisterNextHourEventAndGetCountByNextHour() {
        eventStatistic.registerEvent(now + Duration.ofHours(1L).toMillis());
        assertEquals(eventStatistic.getCountByLastHour(), 0);
    }

    @Test
    public void testGetCountByLastMinute() {
        eventStatistic.registerEvent(now);
        assertEquals(eventStatistic.getCountByLastMinute(), 1);
    }

    @Test
    public void testRegisterLastMinuteEventAndGetCountByLastMinute() {
        eventStatistic.registerEvent(now - Duration.ofMinutes(1L).toMillis());
        assertEquals(eventStatistic.getCountByLastMinute(), 0);
    }

    @Test
    public void testRegisterNextMinuteEventAndGetCountByNextMinute() {
        eventStatistic.registerEvent(now + Duration.ofMinutes(1L).toMillis());
        assertEquals(eventStatistic.getCountByLastMinute(), 0);
    }

    @Test
    public void testLoadEventStatistic() throws InterruptedException {
        final int expectedLoad = 10_000;
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executorService.invokeAll(Collections.nCopies(expectedLoad, () -> {
            eventStatistic.registerEvent(now);
            return now;
        }));

        assertEquals(eventStatistic.getCountByLastMinute(), expectedLoad);
        assertEquals(eventStatistic.getCountByLastDay(), expectedLoad);
        assertEquals(eventStatistic.getCountByLastHour(), expectedLoad);
    }
}