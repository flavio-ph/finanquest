package com.finanquest.service.gamitication;

import com.finanquest.entity.User;

public interface AchievementStrategy {

    boolean isMet(User user);

    String getConditionName();
}


