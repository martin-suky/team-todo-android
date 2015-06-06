package cz.suky.teamtodo.android.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cz.suky.teamtodo.android.model.AbstractModel;

/**
 * Created by suky on 6.6.15.
 */
public abstract class AbstractDaoImpl<Model extends AbstractModel> implements AbstractDao<Model> {

    private final TodoDbHelper dbHelper;

    public AbstractDaoImpl(TodoDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public Model getById(long id) {
        SQLiteDatabase rDb = getRDb();
        Cursor cursor = new QueryBuilder()
                .select(getAllColumns())
                .from(getTableName())
                .where()
                .equals(AbstractModel.COLUMN_ID, id + "")
                .execute(rDb);

        if (cursor.getCount() == 1 && cursor.moveToFirst()) {
            return mapToModel(cursor);
        } else {
            throw new IllegalArgumentException("No row with id=" + id);
        }
    }

    @Override
    public List<Model> getAll() {
        Cursor cursor = new QueryBuilder()
                .select(getAllColumns())
                .from(getTableName())
                .execute(getRDb());
        if (cursor.getCount() > 0) {
            return mapToModels(cursor);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void save(Model model) {
        if (model.getId() == null) {
            new InsertBuilder()
                    .table(getTableName())
                    .execute(getWDb(), mapToValues(model));
        } else {
            new UpdateBuilder()
                    .table(getTableName())
                    .equals(AbstractModel.COLUMN_ID, model.getId() + "")
                    .execute(getWDb(), mapToValues(model));
        }
    }

    @Override
    public void delete(Model model) {

    }

    protected SQLiteDatabase getWDb() {
        return dbHelper.getWritableDatabase();
    }

    protected SQLiteDatabase getRDb() {
        return dbHelper.getReadableDatabase();
    }

    protected abstract String getTableName();

    protected abstract String[] getAllColumns();

    protected abstract Model mapToModel(Cursor cursor);

    protected abstract ContentValues mapToValues(Model model);

    private List<Model> mapToModels(Cursor cursor) {
        List<Model> models = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            models.add(mapToModel(cursor));
        }
        return models;
    }
}