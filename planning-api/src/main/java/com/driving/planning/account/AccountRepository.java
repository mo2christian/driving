package com.driving.planning.account;

import com.driving.planning.account.domain.Account;
import com.driving.planning.common.constraint.Email;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Traced
@RequestScoped
public class AccountRepository {

    private final Logger logger;

    private final MongoDatabase mongoDatabase;

    private final MongoClient mongoClient;

    @Inject
    public AccountRepository(Logger logger, MongoDatabase mongoDatabase, MongoClient mongoClient) {
        this.logger = logger;
        this.mongoDatabase = mongoDatabase;
        this.mongoClient = mongoClient;
    }

    public List<Account> list(){
        logger.debug("List account");
        var cursor = mongoDatabase.getCollection(Account.COLLECTION_NAME, Account.class)
                .find()
                .cursor();
        var list = new ArrayList<Account>();
        while (cursor.hasNext()){
            list.add(cursor.next());
        }
        return list;
    }

    public Optional<Account> findByEmail(@Email String email){
        logger.debugf("Find account by mail : %s", email);
        var account = mongoDatabase.getCollection(Account.COLLECTION_NAME, Account.class)
                .find(eq("email", email))
                .first();
        if (account != null){
            logger.debugf("account found %s", account.getEmail());
        }
        return Optional.ofNullable(account);
    }

    public void createInSchema(@NotNull String schema, @NotNull Account account){
        logger.debugf("create user %s in schema %s", account.getEmail(), schema);
        mongoClient.getDatabase(schema)
                .getCollection(Account.COLLECTION_NAME, Account.class)
                .insertOne(account);
    }
}
