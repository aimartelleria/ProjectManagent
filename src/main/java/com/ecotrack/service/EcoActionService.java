package com.ecotrack.service;

import com.ecotrack.model.EcoAction;
import com.ecotrack.model.User;
import com.ecotrack.repository.EcoActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for CRUD operations and aggregate calculations around eco actions.
 */
@Service
public class EcoActionService {

    private final EcoActionRepository ecoActionRepository;

    @Autowired
    public EcoActionService(EcoActionRepository ecoActionRepository) {
        this.ecoActionRepository = ecoActionRepository;
    }

    /**
     * Persist a new eco action.
     *
     * @param action the action to save
     * @return saved entity
     */
    public EcoAction save(EcoAction action) {
        return ecoActionRepository.save(action);
    }

    /**
     * Retrieve all actions for a user ordered by date descending.
     *
     * @param user the owner
     * @return list of actions
     */
    public List<EcoAction> findByUser(User user) {
        return ecoActionRepository.findByUserOrderByDateDesc(user);
    }

    /**
     * Delete an action by id if it belongs to the user.
     *
     * @param id action id
     */
    public void delete(long id) {
        ecoActionRepository.deleteById(id);
    }

    /**
     * Compute total points for a user.
     *
     * @param user the owner
     * @return sum of points
     */
    public int computeTotalPoints(User user) {
        return findByUser(user).stream()
                .mapToInt(EcoAction::getPoints)
                .sum();
    }

    /**
     * Aggregate points per week (ISO week-of-year) for the given user. Useful for building a progress chart.
     * The map key is a formatted string "YYYY-WW" representing the ISO week.
     *
     * @param user the owner
     * @return map of week string to cumulative points in that week
     */
    public Map<String, Integer> computePointsByWeek(User user) {
        WeekFields weekFields = WeekFields.ISO;
        return findByUser(user).stream()
                .collect(Collectors.groupingBy(
                        action -> {
                            LocalDate date = action.getDate();
                            int week = date.get(weekFields.weekOfWeekBasedYear());
                            int year = date.get(weekFields.weekBasedYear());
                            return String.format("%d-%02d", year, week);
                        },
                        TreeMap::new,
                        Collectors.summingInt(EcoAction::getPoints)
                ));
    }
}
