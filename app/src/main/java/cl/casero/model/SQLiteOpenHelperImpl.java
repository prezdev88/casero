package cl.casero.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteOpenHelperImpl extends SQLiteOpenHelper {

    private static final String CUSTOMER_TABLE =
            "CREATE TABLE cliente(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT," +
                    "sector TEXT," +
                    "direccion TEXT," +
                    "deuda INTEGER" +
                    ")";

    private static final String TRANSACTION_TYPE_TABLE =
            "CREATE TABLE transactionType(\n" +
            "    id INTEGER PRIMARY KEY,\n" +
            "    name TEXT\n" +
            ")";

    private static final String TRANSACTION_TYPE_INSERT =
            "INSERT INTO transactionType VALUES\n" +
                    "(0, 'SALE'),\n" +
                    "(1, 'PAYMENT'),\n" +
                    "(2, 'REFUND'),\n" +
                    "(3, 'DEBT_FORGIVENESS'),\n" +
                    "(4, 'INITIAL_BALANCE')";

    private static final String TRANSACTION_TABLE =
            "CREATE TABLE movimiento(\n" +
                    "   id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "   cliente INTEGER,\n" +
                    "   fecha TEXT,\n" +
                    "   detalle TEXT,\n" +
                    "   amount INTEGER,\n" +
                    "   saldo INTEGER,\n" +
                    "   type INTEGER,\n" +
                    "   FOREIGN KEY(cliente) REFERENCES cliente(id),\n" +
                    "   FOREIGN KEY(type) REFERENCES transactionType(id)\n" +
                    ")";

    private static final String STATISTICS_TABLE =
            "CREATE TABLE estadistica(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "tipo INTEGER," + // venta o abono (K.SALE K.PAYMENT)
                    "monto INTEGER," +
                    "fecha TEXT," +
                    "tipoVenta INTEGER," +  // ventaNueva o Mantencion (K.NUEVA, K.MAINTENANCE)
                    // y -1 cuando es abono
                    "cantPrendas INTEGER" +// cantidad de prendas al momento de una venta
                    // -1 cuando es abono
                    ")";

    private static final String DATABASE_NAME = "casero.sqlite"; // O Database.NAME
    private static final int DATABASE_VERSION = 2;

    public SQLiteOpenHelperImpl(Context context) {
        super(
                context.getApplicationContext(), // Usa el contexto que te pasan
                DATABASE_NAME,                   // Solo el nombre del archivo
                null,
                DATABASE_VERSION
        );
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CUSTOMER_TABLE);
        sqLiteDatabase.execSQL(TRANSACTION_TYPE_TABLE);
        sqLiteDatabase.execSQL(TRANSACTION_TYPE_INSERT);
        sqLiteDatabase.execSQL(TRANSACTION_TABLE);
        sqLiteDatabase.execSQL(STATISTICS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion == 2){
            sqLiteDatabase.execSQL("ALTER TABLE movimiento\n" +
                    "ADD COLUMN amount INTEGER;");
            sqLiteDatabase.execSQL("ALTER TABLE movimiento\n" +
                    "ADD COLUMN type TEXT;");
            sqLiteDatabase.execSQL("UPDATE movimiento\n" +
                    "SET amount = CAST(SUBSTR(detalle, instr(detalle, '$')+1) AS INTEGER),\n" +
                    "type = SUBSTR(detalle, instr(detalle, '[')+1, instr(detalle, ']')-2);\n");
            sqLiteDatabase.execSQL("CREATE TABLE transactionType(\n" +
                    "    id INTEGER PRIMARY KEY,\n" +
                    "    name TEXT\n" +
                    ");");
            sqLiteDatabase.execSQL("INSERT INTO transactionType VALUES\n" +
                    "(0, 'SALE'),\n" +
                    "(1, 'PAYMENT'),\n" +
                    "(2, 'REFUND'),\n" +
                    "(3, 'DEBT_FORGIVENESS'),\n" +
                    "(4, 'INITIAL_BALANCE');");
            sqLiteDatabase.execSQL("UPDATE movimiento\n" +
                    "SET type = 4\n" +
                    "WHERE type = '';");
            sqLiteDatabase.execSQL("UPDATE movimiento\n" +
                    "SET type = 0\n" +
                    "WHERE type = 'Venta';");
            sqLiteDatabase.execSQL("UPDATE movimiento\n" +
                    "SET type = 1\n" +
                    "WHERE type = 'Abono';");
            sqLiteDatabase.execSQL("UPDATE movimiento\n" +
                    "SET type = 3\n" +
                    "WHERE type = 'CONDONACIÓN DEUDA';");
            sqLiteDatabase.execSQL("UPDATE movimiento\n" +
                    "SET type = 2\n" +
                    "WHERE type = 'Devolución';");
            sqLiteDatabase.execSQL("CREATE TABLE temporal(\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    cliente INTEGER,\n" +
                    "    fecha TEXT,\n" +
                    "    detalle TEXT,\n" +
                    "\tamount INTEGER,\n" +
                    "    saldo INTEGER,\n" +
                    "\ttype INTEGER,\n" +
                    "    FOREIGN KEY(cliente) REFERENCES cliente(id),\n" +
                    "\tFOREIGN KEY(type) REFERENCES transactionType(id)\n" +
                    ");");
            sqLiteDatabase.execSQL("INSERT INTO temporal(\n" +
                    "\tid, cliente, fecha, detalle, amount, saldo, type\n" +
                    ") SELECT id, cliente, fecha, detalle, amount, saldo, type\n" +
                    "FROM movimiento;");
            sqLiteDatabase.execSQL("DROP TABLE movimiento;");
            sqLiteDatabase.execSQL("CREATE TABLE movimiento(\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    cliente INTEGER,\n" +
                    "    fecha TEXT,\n" +
                    "    detalle TEXT,\n" +
                    "\tamount INTEGER,\n" +
                    "    saldo INTEGER,\n" +
                    "\ttype INTEGER,\n" +
                    "    FOREIGN KEY(cliente) REFERENCES cliente(id),\n" +
                    "\tFOREIGN KEY(type) REFERENCES transactionType(id)\n" +
                    ");");
            sqLiteDatabase.execSQL("INSERT INTO movimiento(\n" +
                    "\tid, cliente, fecha, detalle, amount, saldo, type\n" +
                    ") SELECT id, cliente, fecha, detalle, amount, saldo, type\n" +
                    "FROM temporal;\n");
            sqLiteDatabase.execSQL("DROP TABLE temporal");
        }
    }

}
