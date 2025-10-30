package com.ecotrack.repository;

import com.ecotrack.model.EcoAction;
import com.ecotrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for CRUD operations on eco actions.
 */
@Repository
public interface EcoActionRepository extends JpaRepository<EcoAction, Long> {

    /**
     * Return all eco actions for a given user ordered by date descending.
     *
     * @param user the owner of the actions
     * @return list of actions
     */
    List<EcoAction> findByUserOrderByDateDesc(User user);
}
