package com.finanquest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
import org.springframework.scheduling.config.Task;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

=======
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
>>>>>>> 68cbc396a23e0d31e3ac6ca74566986b9baaaff0
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
<<<<<<< HEAD

=======
>>>>>>> 68cbc396a23e0d31e3ac6ca74566986b9baaaff0

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private int level;

    private long experiencePoints;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_achievements",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    @Builder.Default
    private Set<Achievement> unlockedAchievements = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_challenges",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "challenge_id")
    )
    @Builder.Default
    private Set<Challenge> activeChallenges = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
<<<<<<< HEAD
        return List.of();
=======
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
>>>>>>> 68cbc396a23e0d31e3ac6ca74566986b9baaaff0
    }

    @Override
    public String getUsername() {
<<<<<<< HEAD
        return "";
=======
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
>>>>>>> 68cbc396a23e0d31e3ac6ca74566986b9baaaff0
    }
}