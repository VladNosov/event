package com.vn.es;

import static com.vn.es.util.TimeUtil.MILLI_PER_DAY;
import static com.vn.es.util.TimeUtil.getCurrentTimeInDays;
import static com.vn.es.util.TimeUtil.getCurrentTimeInHours;
import static com.vn.es.util.TimeUtil.getCurrentTimeInMinutes;
import static com.vn.es.util.TimeUtil.getCurrentTimestamp;
import static com.vn.es.util.TimeUtil.getTimeInDays;
import static com.vn.es.util.TimeUtil.getTimeInHours;
import static com.vn.es.util.TimeUtil.getTimeInMinutes;
import static com.vn.es.util.TimeUtil.minusDay;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Vladislav Nosov
 */
public class EventStatisticImpl implements IEventStatistic {

    private final Map<Long, Integer> data = new ConcurrentHashMap<>();

    private DataCleaner cleaner;

    public EventStatisticImpl() {
        cleaner = new DataCleaner();
        cleaner.setDaemon(true);
        cleaner.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void registerEvent(final long timestamp) {
        if (timestamp == 0) {
            throw new IllegalArgumentException("Timestamp cannot be 0");
        }
        //если событие устарело (уже прошло 24 или более часов), то нет необходимости в учете
        if (timestamp < minusDay(getCurrentTimestamp())) {
            return;
        }
        /*
        Подсчет статистики сделал в методе регистрации, исходя из следующего:
        - сами события хранить не требовалось
        - лучше один раз обновить значения, разбив их на день/месяц/минуту т.к. в итоге храним меньше данных и
        не нужно будет каждый раз фильтровать результаты при запросе статистики за выбранный промежуток
         */
        increment(getTimeInMinutes(timestamp));
        increment(getTimeInHours(timestamp));
        increment(getTimeInDays(timestamp));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountByLastDay() {
        return data.getOrDefault(getCurrentTimeInDays(), 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountByLastHour() {
        return data.getOrDefault(getCurrentTimeInHours(), 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountByLastMinute() {
        return data.getOrDefault(getCurrentTimeInMinutes(), 0);
    }

    private void increment(final long key) {
        data.put(key, data.getOrDefault(key, 0) + 1);
    }

    /**
     * Служебный класс, выполняющий очистку устаревших данных (все, что не относится к текущему дню)
     */
    private class DataCleaner extends Thread {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                if (isNotEmpty(data)) {
                    final long yesterday = minusDay(getCurrentTimestamp());

                    data.keySet()
                            .stream()
                            .filter(ts -> yesterday >= ts)
                            .forEach(data::remove);
                }
                try {
                    Thread.sleep(MILLI_PER_DAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}