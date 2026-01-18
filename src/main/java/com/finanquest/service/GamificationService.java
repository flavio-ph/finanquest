package com.finanquest.service;

import com.finanquest.entity.Achievement;
import com.finanquest.entity.Transaction;
import com.finanquest.entity.User;
import com.finanquest.repository.AchievementRepository;
import com.finanquest.service.gamitication.AchievementStrategy;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GamificationService {

    private static final long XP_PER_TRANSACTION = 10L;

    private final UserService userService;
    private final AchievementRepository achievementRepository;
    private final Map<String, AchievementStrategy> strategies;

    // O Spring injeta automaticamente todas as implementações de AchievementStrategy
    public GamificationService(UserService userService,
                               AchievementRepository achievementRepository,
                               List<AchievementStrategy> strategyList) {
        this.userService = userService;
        this.achievementRepository = achievementRepository;
        // Cria um mapa para acesso rápido: "NOME_DA_CONDICAO" -> Instância da Estratégia
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(AchievementStrategy::getConditionName, Function.identity()));
    }

    @Transactional
    public void processNewTransaction(Transaction transaction) {
        User user = transaction.getUser();

        // 1. Concede XP pela transação
        userService.grantExperiencePoints(user, XP_PER_TRANSACTION);

        // 2. Verifica se desbloqueou conquistas
        checkAndUnlockAchievements(user);
    }

    private void checkAndUnlockAchievements(User user) {
        List<Achievement> allAchievements = achievementRepository.findAll();

        for (Achievement achievement : allAchievements) {
            // Se o utilizador já tem a conquista, ignora
            if (user.getUnlockedAchievements().contains(achievement)) {
                continue;
            }

            // Busca a estratégia correspondente à condição do banco de dados
            AchievementStrategy strategy = strategies.get(achievement.getUnlockCondition());

            // Se existir uma estratégia implementada e a condição for cumprida
            if (strategy != null && strategy.isMet(user)) {
                unlockAchievementForUser(user, achievement);
            }
        }
    }

    private void unlockAchievementForUser(User user, Achievement achievement) {
        user.getUnlockedAchievements().add(achievement);
        userService.grantExperiencePoints(user, achievement.getRewardExperiencePoints());
        // Aqui poderia adicionar um log ou notificação: "Conquista desbloqueada!"
    }
}