package ru.effective_mobile.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.effective_mobile.auth_service.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM users ORDER BY id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<User> findUsersWithLimitAndOffset(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    int countUsers();
}
