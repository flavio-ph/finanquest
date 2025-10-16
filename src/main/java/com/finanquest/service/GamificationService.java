package com.finanquest.service;

import com.finanquest.entity.Achievement;
import com.finanquest.entity.Transaction;
import com.finanquest.entity.User;
import com.finanquest.repository.AchievementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private static final long XP_PER_TRANSACTION = 10L;

    private final UserService userService;
    private final AchievementRepository achievementRepository;

    @Transactional
    public void processNewTransaction(Transaction transaction) {
        User user = transaction.getUser();

        userService.grantExperiencePoints(user, XP_PER_TRANSACTION);

        checkAnUnlcockAchievements(user);
    }

    private void checkAnUnlcockAchievements(User user) {
        List<Achievement> allAchievements = achievementRepository.findAll();

        for (Achievement achievement: allAchievements) {
            boolean isAlreadyUnlocked = user.getUnlockedAchievements().contains(achievement);

            if(!isAlreadyUnlocked && isConditionMet(user, achievement)) {
                unlockAchievementForUser(user, achievement);
            }

        }
    }

    private boolean isConditionMet(User user, Achievement achievement) {

        switch (achievement.getUnlockCondition()) {
            case "REGISTER_5_TRANSACTIONS":
                return user.getTransactions() != null && user.getTransactions().size() >= 5;


            case "FIRST_INCOME":
                return user.getTransactions().stream()
                        .anyMatch(t -> t.getType() == Transaction.TransactionType.RECEITA);

            default:
                return false;
        }
    }

    private void unlockAchievementForUser(User user, Achievement achievement) {
        user.getUnlockedAchievements().add(achievement);
        userService.grantExperiencePoints(user, achievement.getRewardExperiencePoints());
    }


}
