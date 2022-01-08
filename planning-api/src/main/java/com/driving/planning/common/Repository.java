package com.driving.planning.common;

import org.bson.types.ObjectId;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    void insert(T t);

    void update(@NotNull T t);

    void delete(@NotNull ObjectId id);

    List<T> list();

    Optional<T> findById(@NotNull String id);

}
