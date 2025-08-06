package cl.casero.model.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cl.casero.model.SQLiteOpenHelperImpl;
import cl.casero.model.Statistic;
import cl.casero.model.Transaction;
import cl.casero.model.dao.AbstractDao;
import cl.casero.model.enums.SaleType;
import cl.casero.model.enums.TransactionType;

public class TransactionDao extends AbstractDao<Transaction> {

    private final StatisticsDao statisticsDao;

    public TransactionDao() {
        statisticsDao = new StatisticsDao();
    }

    @Override
    public void create(Transaction transaction) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        String transactionDate = dateFormat.format(transaction.getDate());

        query =
                "INSERT INTO " +
                        "movimiento " +
                        "VALUES(" +
                        "NULL, " +
                        "'" + transaction.getCustomerId() + "'," +
                        "'" + transactionDate + "'," +
                        "'" + transaction.getDetail() + "'," +
                        "'" + transaction.getAmount() + "'," +
                        "'" + transaction.getBalance() + "'," +
                        "'" + transaction.getType() + "'" +
                        ")";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    @Override
    public List<Transaction> read() {
        return Collections.emptyList();
    }

    @Override
    public void update(Transaction transaction) {

    }

    @Override
    public void delete(Number id) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        sqLiteDatabase.execSQL("DELETE FROM movimiento WHERE id = " + id);
        sqLiteDatabase.close();
    }

    @Override
    public Transaction readById(Number id) {
        Transaction transaction = new Transaction();

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "* " +
                        "FROM " +
                        "movimiento " +
                        "WHERE " +
                        "id = " + id;

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            transaction.setId(cursor.getInt(0));
            transaction.setCustomerId(cursor.getInt(1));
            transaction.setRawDate(cursor.getString(2));
            transaction.setDetail(cursor.getString(3));
            transaction.setAmount(cursor.getInt(4));
            transaction.setBalance(cursor.getInt(5));
            transaction.setType(cursor.getInt(6));
        }

        sqLiteDatabase.close();

        return transaction;
    }

    @Override
    public List<Transaction> readBy(String filter) {
        return Collections.emptyList();
    }

    public List<Transaction> readByCustomer(int customerId, boolean ascending) {
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction;

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "* " +
                        "FROM " +
                        "movimiento " +
                        "WHERE " +
                        "cliente = '" + customerId + "' " +
                        "ORDER BY fecha " + (ascending ? "ASC" : "DESC");

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                transaction = new Transaction();

                transaction.setId(cursor.getInt(0));
                transaction.setCustomerId(cursor.getInt(1));
                transaction.setRawDate(cursor.getString(2));
                transaction.setDetail(cursor.getString(3));
                transaction.setAmount(cursor.getInt(4));
                transaction.setBalance(cursor.getInt(5));
                transaction.setType(cursor.getInt(6));

                transactions.add(transaction);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return transactions;
    }

    public void updateDebt(Transaction transaction) {
        updateDebt(transaction.getCustomerId(), transaction.getBalance());
    }

    public void updateDebt(int customerId, int newDebt) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "UPDATE " +
                        "cliente " +
                        "SET " +
                        "deuda = '" + newDebt + "' " +
                        "WHERE " +
                        "id = '" + customerId + "'";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }
}
