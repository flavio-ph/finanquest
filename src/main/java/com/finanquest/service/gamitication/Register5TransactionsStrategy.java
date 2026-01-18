package com.finanquest.service.gamitication;

import com.finanquest.entity.User;
import org.springframework.stereotype.Component;

@Component
public class Register5TransactionsStrategy implements AchievementStrategy {

    @Override
    public boolean isMet(User user) {
        return user.getTransactions() != null && user.getTransactions().size() >= 5;
    }

    @Override
    public String getConditionName() {
        return "REGISTER_5_TRANSACTIONS";
    }
}