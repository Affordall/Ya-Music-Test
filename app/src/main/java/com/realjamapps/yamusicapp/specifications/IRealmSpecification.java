package com.realjamapps.yamusicapp.specifications;

import io.realm.Realm;
import io.realm.RealmResults;

public interface IRealmSpecification extends ISpecification {
    RealmResults<?> toRealmResults(Realm realm);
}