package io.github.geniot.sayagain.repositories;

import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.function.Function;

@Service
public class RepositoryHelper {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public <E, R> R refreshAndUse(
            E entity,
            Function<E, R> usageFunction) {
        em.refresh(entity);
        return usageFunction.apply(entity);
    }

}