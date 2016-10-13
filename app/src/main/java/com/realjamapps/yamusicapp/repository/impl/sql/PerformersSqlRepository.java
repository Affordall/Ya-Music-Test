package com.realjamapps.yamusicapp.repository.impl.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.realjamapps.yamusicapp.database.sql.DatabaseHandler;
import com.realjamapps.yamusicapp.database.sql.mapper.CursorToPerformerMapper;
import com.realjamapps.yamusicapp.database.sql.mapper.IBaseMapper;
import com.realjamapps.yamusicapp.models.Performer;
import com.realjamapps.yamusicapp.repository.IRepository;
import com.realjamapps.yamusicapp.specifications.ISpecification;
import com.realjamapps.yamusicapp.specifications.ISqlSpecification;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PerformersSqlRepository implements IRepository<Performer> {

    private final IBaseMapper<Cursor, Performer> toPerformerMapper;
    private DatabaseHandler handler;

    @Inject
    public PerformersSqlRepository(DatabaseHandler handler) {
        this.handler = handler;
        this.toPerformerMapper = new CursorToPerformerMapper();
    }

    @Override
    public void add(Performer item) {
        handler.addPerformer(item);
    }

    @Override
    public void add(Iterable<Performer> items) {
        for (Performer item : items) {
            handler.addPerformer(item);
        }
    }

    @Override
    public void update(Performer item) {
        handler.updatePerformer(item);
    }

    @Override
    public void remove(Performer item) {
        handler.removePerformer(item);
    }

    @Override
    public void remove(ISpecification specification) {

    }

    @Override
    public boolean isItemExist(String name) {
        return handler.isPerformerExist(name);
    }

    @Override
    public List<Performer> query(ISpecification specification) {
        final ISqlSpecification sqlSpecification = (ISqlSpecification) specification;
        final SQLiteDatabase database = handler.getReadableDatabase();
        final List<Performer> performerList = new ArrayList<>();
        try {
            final Cursor cursor = database.rawQuery(sqlSpecification.toSqlQuery(), new String[]{});
            if (database.isOpen())
            for (int i = 0, size = cursor.getCount(); i < size; i++) {
                cursor.moveToPosition(i);
                performerList.add(toPerformerMapper.map(cursor));
            }
            cursor.close();
            return performerList;
        } finally {
            database.close();
        }
    }
}
