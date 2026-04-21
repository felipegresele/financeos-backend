package com.financeos.service;

import com.financeos.dto.GoalDTOs.*;
import com.financeos.entity.Goal;
import com.financeos.entity.User;
import com.financeos.exception.BusinessException;
import com.financeos.exception.ResourceNotFoundException;
import com.financeos.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;

    @Transactional(readOnly = true)
    public List<GoalResponse> getAll(User user) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
            .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse getById(String id, User user) {
        return toResponse(findOwned(id, user));
    }

    public GoalResponse create(GoalRequest req, User user) {
        var goal = Goal.builder()
            .name(req.getName())
            .targetAmount(req.getTargetAmount())
            .currentAmount(req.getCurrentAmount() != null ? req.getCurrentAmount() : BigDecimal.ZERO)
            .icon(req.getIcon())
            .color(req.getColor())
            .deadline(req.getDeadline())
            .user(user)
            .build();
        return toResponse(goalRepository.save(goal));
    }

    public GoalResponse update(String id, GoalRequest req, User user) {
        var goal = findOwned(id, user);
        goal.setName(req.getName());
        goal.setTargetAmount(req.getTargetAmount());
        goal.setIcon(req.getIcon());
        goal.setColor(req.getColor());
        goal.setDeadline(req.getDeadline());
        return toResponse(goalRepository.save(goal));
    }

    public GoalResponse deposit(String id, GoalDepositRequest req, User user) {
        var goal = findOwned(id, user);
        if (goal.getStatus() != Goal.GoalStatus.IN_PROGRESS) {
            throw new BusinessException("Goal is not in progress");
        }
        goal.setCurrentAmount(goal.getCurrentAmount().add(req.getAmount()));
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        }
        return toResponse(goalRepository.save(goal));
    }

    public void delete(String id, User user) {
        goalRepository.delete(findOwned(id, user));
    }

    private Goal findOwned(String id, User user) {
        return goalRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
    }

    private GoalResponse toResponse(Goal g) {
        double progress = 0;
        if (g.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progress = g.getCurrentAmount()
                .divide(g.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
        }
        return GoalResponse.builder()
            .id(g.getId())
            .name(g.getName())
            .targetAmount(g.getTargetAmount())
            .currentAmount(g.getCurrentAmount())
            .icon(g.getIcon())
            .color(g.getColor())
            .deadline(g.getDeadline())
            .status(g.getStatus())
            .progressPercent(Math.min(progress, 100))
            .remaining(g.getTargetAmount().subtract(g.getCurrentAmount()).max(BigDecimal.ZERO))
            .build();
    }
}
