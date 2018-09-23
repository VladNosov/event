package com.vn.es;

/**
 * @author Vladislav Nosov
 */
public interface IEventStatistic {

    /**
     * Регистрация события
     * @param timestamp время генерации события
     */
    void registerEvent(final long timestamp);

    /**
     * Получить количество зарегестрированных событий за текущий календарный день
     * @return кол-во зарегестрированных событий
     */
    int getCountByLastDay();

    /**
     * Получить количество зарегестрированных событий за текущий час
     * @return кол-во зарегестрированных событий
     */
    int getCountByLastHour();

    /**
     * Получить количество зарегестрированных событий за текущую минуту
     * @return кол-во зарегестрированных событий
     */
    int getCountByLastMinute();
}