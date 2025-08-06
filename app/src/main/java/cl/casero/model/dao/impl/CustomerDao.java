package cl.casero.model.dao.impl;

import java.util.ArrayList;
import java.util.List;

import cl.casero.model.SQLiteOpenHelperImpl;
import cl.casero.model.Customer;
import cl.casero.model.Transaction;
import cl.casero.model.dao.AbstractDao;
import cl.casero.model.enums.TransactionType;
import cl.casero.model.util.Util;

public class CustomerDao extends AbstractDao<Customer> {

    @Override
    public void create(Customer customer) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "INSERT INTO " +
                        "   cliente " +
                        "VALUES(" +
                        "null, " +
                        "'" + customer.getName() + "', " +
                        "'" + customer.getSector() + "', " +
                        "'" + customer.getAddress() + "', " +
                        "'" + customer.getDebt() + "'" +
                        ")";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    @Override
    public List<Customer> read() {
        List<Customer> customers = new ArrayList<>();
        Customer customer;

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "* " +
                        "FROM " +
                        "cliente";

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                customer = new Customer();

                customer.setId(cursor.getInt(0));
                customer.setName(cursor.getString(1));
                customer.setSector(cursor.getString(2));
                customer.setAddress(cursor.getString(3));
                customer.setDebt(cursor.getInt(4));

                customers.add(customer);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return customers;
    }

    @Override
    public void update(Customer customer) {
    }

    @Override
    public void delete(Number id) {
    }

    @Override
    public Customer readById(Number id) {
        Customer customer = null;

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "* " +
                        "FROM " +
                        "cliente " +
                        "WHERE " +
                        "id = '" + id + "'";

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                customer = new Customer();

                customer.setId(cursor.getInt(0));
                customer.setName(cursor.getString(1));
                customer.setSector(cursor.getString(2));
                customer.setAddress(cursor.getString(3));
                customer.setDebt(cursor.getInt(4));
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return customer;
    }

    @Override
    public List<Customer> readBy(String filter) {
        List<Customer> customers = new ArrayList<>();
        Customer customer;

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        filter = Util.removeAccentMark(filter);

        query =
                "SELECT * FROM cliente WHERE " +
                        "REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(nombre),'á','a'), 'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') LIKE '%" + filter + "%' OR " +
                        "REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(direccion),'á','a'), 'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') LIKE '%" + filter + "%' OR " +
                        "REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(sector),'á','a'), 'é','e'),'í','i'),'ó','o'),'ú','u'),'ñ','n') LIKE '%" + filter + "%' " +
                        "ORDER BY nombre ASC";

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                customer = new Customer();

                customer.setId(cursor.getInt(0));
                customer.setName(cursor.getString(1));
                customer.setSector(cursor.getString(2));
                customer.setAddress(cursor.getString(3));
                customer.setDebt(cursor.getInt(4));

                customers.add(customer);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return customers;
    }

    public int getLastCustomerId() {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "MAX(id) " +
                        "FROM " +
                        "cliente";

        cursor = sqLiteDatabase.rawQuery(query, null);

        int id = -1;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return id;
    }

    public int getDebt(int customerId) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "deuda " +
                        "FROM " +
                        "cliente " +
                        "WHERE " +
                        "id = '" + customerId + "'";

        cursor = sqLiteDatabase.rawQuery(query, null);

        int debt = 0;

        if (cursor.moveToFirst()) {
            do {
                debt = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return debt;
    }

    public void updateAddress(long customerId, String newAddress) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "UPDATE " +
                        "cliente " +
                        "SET " +
                        "direccion = '" + newAddress + "' " +
                        "WHERE " +
                        "id = '" + customerId + "'";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    public void updateCustomerBalance(Transaction transaction) {
        int amount = getNewBalance(transaction);
        int customerId = transaction.getCustomerId();

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        sqLiteDatabase.execSQL(
                "UPDATE cliente " +
                        "SET deuda = (deuda + " + amount + ") " +
                        "WHERE id = " + customerId
        );

        sqLiteDatabase.close();
    }

    private int getNewBalance(Transaction transaction) {
        int amount = transaction.getAmount();
        int type = transaction.getType();
        TransactionType transactionType = TransactionType.getTransactionType(type);

        if(transactionType == TransactionType.SALE){
            return amount * -1;
        }else{
            return amount;
        }
    }
}
