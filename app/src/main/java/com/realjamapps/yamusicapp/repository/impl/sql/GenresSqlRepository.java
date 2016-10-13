package com.realjamapps.yamusicapp.repository.impl.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.realjamapps.yamusicapp.database.sql.DatabaseHandler;
import com.realjamapps.yamusicapp.database.sql.mapper.CursorToGenresMapper;
import com.realjamapps.yamusicapp.database.sql.mapper.IBaseMapper;
import com.realjamapps.yamusicapp.models.Genres;
import com.realjamapps.yamusicapp.repository.IRepository;
import com.realjamapps.yamusicapp.specifications.ISpecification;
import com.realjamapps.yamusicapp.specifications.ISqlSpecification;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GenresSqlRepository implements IRepository<Genres> {

    private final IBaseMapper<Cursor, Genres> toGenresMapper;
    private final DatabaseHandler handler;

    @Inject
    public GenresSqlRepository(DatabaseHandler handler) {
        this.handler = handler;
        this.toGenresMapper = new CursorToGenresMapper();
    }

    @Override
    public void add(Genres item) {
        handler.addGenre(item);
    }

    @Override
    public void add(Iterable<Genres> items) {
        for (Genres item : items) {
            handler.addGenre(item);
        }
    }

    @Override
    public void update(Genres item) {
        handler.updateGenre(item);
    }

    @Override
    public void remove(Genres item) {
        handler.removeGenre(item);
    }

    @Override
    public void remove(ISpecification specification) {

    }

    @Override
    public boolean isItemExist(String name) {
        return handler.isGenreExist(name);
    }

    @Override
    public List<Genres> query(ISpecification specification) {
        final ISqlSpecification sqlSpecification = (ISqlSpecification) specification;
        final SQLiteDatabase database = handler.getReadableDatabase();
        final List<Genres> genresList = new ArrayList<>();
        try {
            final Cursor cursor = database.rawQuery(sqlSpecification.toSqlQuery(), new String[]{});
            if (database.isOpen())
            for (int i = 0, size = cursor.getCount(); i < size; i++) {
                cursor.moveToPosition(i);
                genresList.add(toGenresMapper.map(cursor));
            }
            cursor.close();
            return genresList;
        } finally {
            database.close();
        }
    }
}
