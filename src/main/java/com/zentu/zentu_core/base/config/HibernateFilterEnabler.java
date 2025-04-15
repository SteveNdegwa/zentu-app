package com.zentu.zentu_core.base.config;

import com.zentu.zentu_core.base.enums.State;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class HibernateFilterEnabler {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void enableFilter() {
        entityManager.unwrap(Session.class)
                .enableFilter("stateFilter")
                .setParameter("state", State.ACTIVE.name());
    }
}