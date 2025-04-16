package com.zentu.zentu_core.user.repository;

import com.zentu.zentu_core.base.enums.State;
import com.zentu.zentu_core.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findAllByState(State state);

    Optional<User> findByIdAndState(UUID id, State state);

    Optional<User> findByPhoneNumberAndState(String phoneNumber, State state);

    Optional<User> findByEmailAndState(String email, State state);

    boolean existsByPhoneNumberAndState(String phoneNumber, State state);

    boolean existsByEmailAndState(String email, State state);
}
