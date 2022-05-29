package com.driving.planning.account;

import com.driving.planning.account.domain.Account;
import com.driving.planning.common.constraint.Email;
import com.mongodb.client.MongoClient;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Traced
@ApplicationScoped
public class AccountRepository implements PanacheMongoRepository<Account> {

    private final Logger logger;

    private final MongoClient mongoClient;

    @Inject
    public AccountRepository(Logger logger, MongoClient mongoClient) {
        this.logger = logger;
        this.mongoClient = mongoClient;
    }

    public Optional<Account> findByEmail(@Email String email){
        return list("email", email)
                .stream()
                .findFirst();
    }

    public void createInSchema(@NotNull String schema, @NotNull Account account){
        logger.debugf("create user %s in schema %s", account.getEmail(), schema);
        mongoClient.getDatabase(schema)
                .getCollection(Account.COLLECTION_NAME, Account.class)
                .insertOne(account);
    }
}
