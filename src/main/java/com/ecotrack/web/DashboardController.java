package com.ecotrack.web;

import com.ecotrack.model.User;
import com.ecotrack.service.EcoActionService;
import com.ecotrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for the user dashboard that summarizes eco points and progress.
 */
@Controller
public class DashboardController {

    private final EcoActionService ecoActionService;
    private final UserService userService;

    @Autowired
    public DashboardController(EcoActionService ecoActionService, UserService userService) {
        this.ecoActionService = ecoActionService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        User user = getCurrentUser();
        int totalPoints = ecoActionService.computeTotalPoints(user);
        String level = determineLevel(totalPoints);
        Map<String, Integer> pointsByWeek = ecoActionService.computePointsByWeek(user);

        // prepare chart labels and data lists for the frontend
        List<String> labels = new ArrayList<>(pointsByWeek.keySet());
        List<Integer> data = new ArrayList<>(pointsByWeek.values());

        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("level", level);
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartData", data);
        return "dashboard";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email).orElseThrow();
    }

    /**
     * Simple level determination based on total points. Adjust thresholds as needed.
     */
    private String determineLevel(int totalPoints) {
        if (totalPoints >= 200) {
            return "Platinum";
        } else if (totalPoints >= 100) {
            return "Gold";
        } else if (totalPoints >= 50) {
            return "Silver";
        } else {
            return "Bronze";
        }
    }
}
