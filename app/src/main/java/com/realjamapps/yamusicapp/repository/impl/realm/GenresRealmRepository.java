//package com.realjamapps.yamusicapp.repository.impl.realm;
//
//import com.realjamapps.yamusicapp.app.YaMusicApp;
//import com.realjamapps.yamusicapp.database.sql.tables.TableGenres;
//import com.realjamapps.yamusicapp.models.Genres;
//import com.realjamapps.yamusicapp.repository.IRepository;
//import com.realjamapps.yamusicapp.specifications.IRealmSpecification;
//import com.realjamapps.yamusicapp.specifications.ISpecification;
//
//import java.util.List;
//import java.util.UUID;
//
//import javax.inject.Inject;
//
//import io.realm.Realm;
//import io.realm.RealmQuery;
//import io.realm.RealmResults;
//
//public class GenresRealmRepository implements IRepository<Genres> {
//
//    @Inject
//    public GenresRealmRepository() {
//    }
//
//    @Override
//    public void add(Genres item) {
//        final Realm realm = Realm.getInstance(YaMusicApp.getRealmConfiguration());
//        realm.executeTransaction(realm1 -> {
//            final Genres pageItemRealm = realm1.createObject(Genres.class);
//            pageItemRealm.setId(item.getId()); //UUID.randomUUID().getMostSignificantBits()
//            pageItemRealm.setName(item.getName());
//        });
//        realm.close();
//    }
//
//    @Override
//    public void add(Iterable<Genres> items) {
//        for (Genres item : items) {
//            add(item);
//        }
//    }
//
//    @Override
//    public void update(Genres item) {
//
//    }
//
//    @Override
//    public void remove(Genres item) {
//
//    }
//
//    @Override
//    public void remove(ISpecification specification) {
//
//    }
//
//    @Override
//    public boolean isItemExist(String name) {
//        final Realm realm = Realm.getInstance(YaMusicApp.getRealmConfiguration());
//        RealmQuery<Genres> query = realm.where(Genres.class)
//                .equalTo(TableGenres.KEY_GENRES_NAME, name);
//        return query.count() != 0;
//    }
//
//    @Override
//    public List<Genres> query(ISpecification specification) {
//        final IRealmSpecification realmSpecification = (IRealmSpecification) specification;
//        final Realm realm = Realm.getInstance(YaMusicApp.getRealmConfiguration());
//        final RealmResults<Genres> realmResults = (RealmResults<Genres>) realmSpecification.toRealmResults(realm);
//        final List<Genres> list = realm.copyFromRealm(realmResults);
//        realm.close();
//        return list;
//    }
//}
