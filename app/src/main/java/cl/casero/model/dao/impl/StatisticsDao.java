package cl.casero.model.dao.impl;

import java.util.ArrayList;
import java.util.List;

import cl.casero.model.Customer;
import cl.casero.model.MonthlyStatistic;
import cl.casero.model.SQLiteOpenHelperImpl;
import cl.casero.model.Statistic;
import cl.casero.model.Transaction;
import cl.casero.model.dao.AbstractDao;
import cl.casero.model.enums.SaleType;
import cl.casero.model.enums.TransactionType;

public class StatisticsDao extends AbstractDao<Statistic> {

    @Override
    public void create(Statistic statistic) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        String date = dateFormat.format(statistic.getDate());

        query =
                "INSERT INTO " +
                        "estadistica " +
                        "VALUES(" +
                        "null, " +
                        "'" + statistic.getType() + "'," +
                        "'" + statistic.getAmount() + "'," +
                        "'" + date + "'," +
                        "'" + statistic.getSaleType() + "'," +
                        "'" + statistic.getItemsCount() + "'" +
                        ")";

        sqLiteDatabase.execSQL(query);
        sqLiteDatabase.close();
    }

    @Override
    public List<Statistic> read() {
        return null;
    }

    @Override
    public void update(Statistic object) {

    }

    @Override
    public void delete(Number id) {

    }

    @Override
    public Statistic readById(Number id) {
        return null;
    }

    @Override
    public List<Statistic> readBy(String filter) {
        return null;
    }

    public int getFinishCardsCount(String startDate, String endDate, boolean isDateRange) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        int finishCardsCount = -1;

        query =
                "SELECT " +
                        "COUNT(0) " +
                        "FROM " +
                        "   movimiento " +
                        "WHERE " +
                        "saldo = 0 AND " +
                        "fecha >= '" + startDate + "' AND " +
                        "fecha <" + (isDateRange ? "=" : "") + " '" + endDate + "'";

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                finishCardsCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return finishCardsCount;
    }

    public int getNewCardsCount(String startDate, String endDate, boolean isDateRange) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        int newCardsCount = -1;

        query =
                "SELECT " +
                        "COUNT(0) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.SALE.getId() + "' AND " +
                        "tipoVenta = '" + SaleType.NEW_SALE.getId() + "' AND " +
                        "fecha >= '" + startDate + "' AND " +
                        "fecha <" + (isDateRange ? "=" : "") + " '" + endDate + "'";


        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                newCardsCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return newCardsCount;
    }

    public int getMaintenanceCount(String startDate, String endDate, boolean isDateRange) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        int maintenanceCount = -1;

        query =
                "SELECT " +
                        "COUNT(0) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.SALE.getId() + "' AND " +
                        "tipoVenta = '" + SaleType.MAINTENANCE.getId() + "' AND " +
                        "fecha >= '" + startDate + "' AND " +
                        "fecha <" + (isDateRange ? "=" : "") + " '" + endDate + "'";


        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                maintenanceCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return maintenanceCount;
    }

    public int getTotalItemsCount(String startDate, String endDate, boolean isDateRange) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        int totalItemsCount = -1;

        query =
                "SELECT " +
                        "SUM(cantPrendas) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.SALE.getId() + "' AND " +
                        "fecha >= '" + startDate + "' AND " +
                        "fecha <" + (isDateRange ? "=" : "") + " '" + endDate + "'";


        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                totalItemsCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return totalItemsCount;
    }

    public int getPaymentsCount(String startDate, String endDate, boolean isDateRange) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        int paymentsCount = -1;

        query =
                "SELECT " +
                        "sum(monto) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.PAYMENT.getId() + "' AND " +
                        "fecha >= '" + startDate + "' AND " +
                        "fecha <" + (isDateRange ? "=" : "") + " '" + endDate + "'";


        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                paymentsCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return paymentsCount;
    }

    public int getSalesCount(String startDate, String endDate, boolean isDateRange) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
        int salesCount = -1;

        query =
                "SELECT " +
                        "sum(monto) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.SALE.getId() + "' AND " +
                        "fecha >= '" + startDate + "' AND " +
                        "fecha <" + (isDateRange ? "=" : "") + " '" + endDate + "'";


        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                salesCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return salesCount;
    }

    public MonthlyStatistic getMonthlyStatistic(String startDate, String endDate, boolean isDateRange) {
        // si isDateRange es verdadero, debo incluir ambos DAYS (>= <=) si no no
        // si isDateRange es falso, es porque quiere ver un mes completo
        // y en ese caso no debo incluir el último día del rango (que es el 1ero del próx. mes)

        int finishCardsCount = getFinishCardsCount(startDate, endDate, isDateRange);
        int newCardsCount = getNewCardsCount(startDate, endDate, isDateRange);
        int maintenanceCount = getMaintenanceCount(startDate, endDate, isDateRange);
        int totalItemsCount = getTotalItemsCount(startDate, endDate, isDateRange);
        int paymentsCount = getPaymentsCount(startDate, endDate, isDateRange);
        int salesCount = getSalesCount(startDate, endDate, isDateRange);

        MonthlyStatistic monthlyStatistic = new MonthlyStatistic();

        monthlyStatistic.setFinishedCardsCount(finishCardsCount);
        monthlyStatistic.setNewCardsCount(newCardsCount);
        monthlyStatistic.setMaintenanceCount(maintenanceCount);
        monthlyStatistic.setTotalItemsCount(totalItemsCount);
        monthlyStatistic.setPaymentsCount(paymentsCount);
        monthlyStatistic.setSalesCount(salesCount);

        return monthlyStatistic;
    }

    // usado para el gráfico
    public MonthlyStatistic getMonthlyStatistic(int month, int year) {
        MonthlyStatistic monthlyStatistic = new MonthlyStatistic();

        int endYear = year;
        int endMonth = month + 1;

        if (month == 12) {
            endYear = (year + 1);
            endMonth = 1;
        }

        String dateQuery =
                "fecha >= '" + year + "-" + (month < 10 ? "0" : "") + month + "-01' AND " +
                        "fecha < '" + endYear + "-" + (endMonth < 10 ? "0" : "") + endMonth + "-01'";

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        // 1.- select tarjetas terminadas
        query =
                "SELECT " +
                        "COUNT(0) " +
                        "FROM " +
                        "movimiento " +
                        "WHERE " +
                        "saldo = 0 AND " + dateQuery;

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                monthlyStatistic.setFinishedCardsCount(cursor.getInt(0));
            } while (cursor.moveToNext());
        }


        // 2.- select tarjetas nuevas
        query =
                "SELECT " +
                        "COUNT(0) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.SALE.getId() + "' AND " +
                        "tipoVenta = '" + SaleType.NEW_SALE.getId() + "' AND " + dateQuery;

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                monthlyStatistic.setNewCardsCount(cursor.getInt(0));
            } while (cursor.moveToNext());
        }


        // 3.- select Mantenciones
        query =
                "SELECT " +
                        "COUNT(0) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.SALE.getId() + "' AND " +
                        "tipoVenta = '" + SaleType.MAINTENANCE.getId() + "' AND " + dateQuery;

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                monthlyStatistic.setMaintenanceCount(cursor.getInt(0));
            } while (cursor.moveToNext());
        }


        // 4.- select total prendas
        query =
                "SELECT " +
                        "sum(cantPrendas) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.SALE.getId() + "' AND " + dateQuery;

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                monthlyStatistic.setTotalItemsCount(cursor.getInt(0));
            } while (cursor.moveToNext());
        }


        // 5.- select cobro
        query =
                "SELECT " +
                        "sum(monto) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.PAYMENT.getId() + "' AND " + dateQuery;

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                monthlyStatistic.setPaymentsCount(cursor.getInt(0));
            } while (cursor.moveToNext());
        }


        // 6.- select ventas
        query =
                "SELECT " +
                        "sum(monto) " +
                        "FROM " +
                        "estadistica " +
                        "WHERE " +
                        "tipo = '" + TransactionType.SALE.getId() + "' AND " + dateQuery;

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                monthlyStatistic.setSalesCount(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return monthlyStatistic;
    }

    public int getTotalDebt() {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "SUM(deuda) " +
                        "FROM " +
                        "cliente";

        cursor = sqLiteDatabase.rawQuery(query, null);

        int totalDebt = 0;

        if (cursor.moveToFirst()) {
            do {
                totalDebt = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return totalDebt;
    }

    public int getAverageDebt() {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "AVG(deuda) " +
                        "FROM " +
                        "cliente";

        cursor = sqLiteDatabase.rawQuery(query, null);

        int averageDebt = 0;

        if (cursor.moveToFirst()) {
            do {
                averageDebt = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return averageDebt;
    }

    public int getCustomersCount(String sector) {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "COUNT(0) " +
                        "FROM " +
                        "cliente " +
                        "WHERE " +
                        "sector = '" + sector + "'";

        cursor = sqLiteDatabase.rawQuery(query, null);

        int customersCount = 0;

        if (cursor.moveToFirst()) {
            do {
                customersCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return customersCount;
    }

    public List<Customer> getDebtors(int limit) {
        List<Customer> debtors = new ArrayList<>();
        Customer customer;

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "* " +
                        "FROM " +
                        "cliente " +
                        "ORDER BY deuda DESC " +
                        "LIMIT " + limit;

        cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                customer = new Customer();

                customer.setId(cursor.getInt(0));
                customer.setName(cursor.getString(1));
                customer.setSector(cursor.getString(2));
                customer.setAddress(cursor.getString(3));
                customer.setDebt(cursor.getInt(4));

                debtors.add(customer);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return debtors;
    }

    public List<Customer> getBestCustomers(int limit) {
        List<Customer> customers = new ArrayList<>();
        Customer customer;

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "* " +
                        "FROM " +
                        "cliente " +
                        "ORDER BY deuda ASC " +
                        "LIMIT " + limit;

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

    public int getCustomersCount() {
        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        query =
                "SELECT " +
                        "COUNT(0) " +
                        "FROM " +
                        "cliente";

        cursor = sqLiteDatabase.rawQuery(query, null);

        int customersCount = 0;

        if (cursor.moveToFirst()) {
            do {
                customersCount = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();

        return customersCount;
    }

    public void deleteBy(Transaction transaction) {
        int type = transaction.getType();
        int amount = transaction.getAmount();
        String rawDate = transaction.getRawDate();

        sqLiteOpenHelper = new SQLiteOpenHelperImpl(this.context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        sqLiteDatabase.execSQL(
                "DELETE FROM estadistica " +
                        "WHERE tipo = '" + type + "' AND " +
                        "monto = '" + amount + "' AND " +
                        "fecha = '" + rawDate + "'"
        );

        sqLiteDatabase.close();
    }
}
