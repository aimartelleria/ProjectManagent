package com.ecotrack.web;

import com.ecotrack.enums.ActionType;
import com.ecotrack.model.EcoAction;
import com.ecotrack.model.User;
import com.ecotrack.service.EcoActionService;
import com.ecotrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Controller managing eco action CRUD operations.
 */
@Controller
@RequestMapping("/actions")
public class EcoActionController {

    private final EcoActionService ecoActionService;
    private final UserService userService;

    @Autowired
    public EcoActionController(EcoActionService ecoActionService, UserService userService) {
        this.ecoActionService = ecoActionService;
        this.userService = userService;
    }

    /**
     * Display the list of actions for the current user.
     */
    @GetMapping
    public String listActions(Model model) {
        User user = getCurrentUser();
        List<EcoAction> actions = ecoActionService.findByUser(user);
        model.addAttribute("actions", actions);
        return "actions";
    }

    /**
     * Show a form to log a new eco action.
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("action", new EcoAction());
        model.addAttribute("types", ActionType.values());
        return "add_action";
    }

    /**
     * Process adding a new action.
     */
    @PostMapping("/add")
    public String addAction(@RequestParam("actionType") String actionType,
                            @RequestParam("date") String date,
                            @RequestParam("description") String description,
                            @RequestParam("points") int points) {
        User user = getCurrentUser();
        EcoAction action = EcoAction.builder()
                .user(user)
                .actionType(ActionType.valueOf(actionType))
                .date(LocalDate.parse(date))
                .description(description)
                .points(points)
                .build();
        ecoActionService.save(action);
        return "redirect:/actions";
    }

    /**
     * Show edit form for an existing action.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = getCurrentUser();
        // fetch the action; assume user owns it for brevity
        Optional<EcoAction> actionOpt = ecoActionService.findByUser(user).stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
        if (actionOpt.isEmpty()) {
            return "redirect:/actions";
        }
        model.addAttribute("action", actionOpt.get());
        model.addAttribute("types", ActionType.values());
        return "edit_action";
    }

    /**
     * Process updating an existing action.
     */
    @PostMapping("/edit/{id}")
    public String updateAction(@PathVariable Long id,
                               @RequestParam("actionType") String actionType,
                               @RequestParam("date") String date,
                               @RequestParam("description") String description,
                               @RequestParam("points") int points) {
        User user = getCurrentUser();
        List<EcoAction> actions = ecoActionService.findByUser(user);
        for (EcoAction action : actions) {
            if (action.getId().equals(id)) {
                action.setActionType(ActionType.valueOf(actionType));
                action.setDate(LocalDate.parse(date));
                action.setDescription(description);
                action.setPoints(points);
                ecoActionService.save(action);
                break;
            }
        }
        return "redirect:/actions";
    }

    /**
     * Delete an action by ID.
     */
    @PostMapping("/delete/{id}")
    public String deleteAction(@PathVariable Long id) {
        ecoActionService.delete(id);
        return "redirect:/actions";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email).orElseThrow();
    }
}
