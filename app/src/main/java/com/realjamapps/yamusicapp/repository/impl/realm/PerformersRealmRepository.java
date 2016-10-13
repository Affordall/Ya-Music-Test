//package com.realjamapps.yamusicapp.repository.impl.realm;
//
//import com.realjamapps.yamusicapp.app.YaMusicApp;
//import com.realjamapps.yamusicapp.database.sql.tables.TablePerformers;
//import com.realjamapps.yamusicapp.models.Performer;
//import com.realjamapps.yamusicapp.repository.IRepository;
//import com.realjamapps.yamusicapp.specifications.IRealmSpecification;
//import com.realjamapps.yamusicapp.specifications.ISpecification;
//
//import java.util.List;
//
//import javax.inject.Inject;
//
//import io.realm.Realm;
//import io.realm.RealmQuery;
//import io.realm.RealmResults;
//
//public class PerformersRealmRepository implements IRepository<Performer> {
//
//    @Inject
//    public PerformersRealmRepository() {
//    }
//
//    @Override
//    public void add(Performer item) {
//        final Realm realm = Realm.getInstance(YaMusicApp.getRealmConfiguration());
//        realm.executeTransaction(realm1 -> {
//            final Performer performerRealm = realm1.createObject(Performer.class);
//            performerRealm.setmId(item.getmId());
//            performerRealm.setmName(item.getmName());
//            performerRealm.setmGenres(item.getmGenres());
//            performerRealm.setmTracks(item.getmTracks());
//            performerRealm.setmAlbums(item.getmAlbums());
//            performerRealm.setmLink(item.getmLink());
//            performerRealm.setmDescription(item.getmDescription());
//            performerRealm.setmCoverSmall(item.getmCoverSmall());
//            performerRealm.setmCoverBig(item.getmCoverBig());
//        });
//        realm.close();
//    }
//
//    @Override
//    public void add(Iterable<Performer> items) {
//        for (Performer item : items) {
//            add(item);
//        }
//    }
//
//    @Override
//    public void update(Performer item) {
//
//    }
//
//    @Override
//    public void remove(Performer item) {
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
//        RealmQuery<Performer> query = realm.where(Performer.class)
//                .equalTo(TablePerformers.KEY_PERFORMER_NAME, name);
//        return query.count() != 0;
//    }
//
//    @Override
//    public List<Performer> query(ISpecification specification) {
//        final IRealmSpecification realmSpecification = (IRealmSpecification) specification;
//        final Realm realm = Realm.getInstance(YaMusicApp.getRealmConfiguration());
//        final RealmResults<Performer> realmResults = (RealmResults<Performer>) realmSpecification.toRealmResults(realm);
//        final List<Performer> list = realm.copyFromRealm(realmResults);
//        realm.close();
//        return list;
//    }
//}
