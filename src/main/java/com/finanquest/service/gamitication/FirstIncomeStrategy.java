package com.finanquest.service.gamitication;

import com.finanquest.entity.Transaction;
import com.finanquest.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FirstIncomeStrategy implements AchievementStrategy {

    @Override
    public boolean isMet(User user) {
        return user.getTransactions() != null && user.getTransactions().stream()
                .anyMatch(t -> t.getType() == Transaction.TransactionType.RECEITA);
    }

    @Override
    public String getConditionName() {
        return "FIRST_INCOME";
    }
}