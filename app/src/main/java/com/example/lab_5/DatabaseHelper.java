package com.example.lab_5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stationery_store.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PRODUCTS = "products";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ARTICLE = "article";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MANUFACTURER = "manufacturer";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";

    private static final String CREATE_PRODUCTS_TABLE =
            "CREATE TABLE " + TABLE_PRODUCTS + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_ARTICLE + " TEXT NOT NULL, "
                    + COLUMN_NAME + " TEXT NOT NULL, "
                    + COLUMN_MANUFACTURER + " TEXT NOT NULL, "
                    + COLUMN_COUNTRY + " TEXT NOT NULL, "
                    + COLUMN_CATEGORY + " TEXT NOT NULL, "
                    + COLUMN_PRICE + " REAL NOT NULL, "
                    + COLUMN_QUANTITY + " INTEGER NOT NULL"
                    + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRODUCTS_TABLE);
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        insertProductInternal(db, new Product(
                "A001",
                "Ручка шариковая синяя",
                "Erich Krause",
                "Россия",
                "Письменные принадлежности",
                45.50,
                120
        ));

        insertProductInternal(db, new Product(
                "A002",
                "Тетрадь 48 листов",
                "Hatber",
                "Россия",
                "Бумажная продукция",
                38.00,
                200
        ));

        insertProductInternal(db, new Product(
                "A003",
                "Карандаш простой HB",
                "Koh-i-Noor",
                "Чехия",
                "Письменные принадлежности",
                25.00,
                150
        ));

        insertProductInternal(db, new Product(
                "A004",
                "Папка-регистратор",
                "Brauberg",
                "Китай",
                "Офисные принадлежности",
                210.00,
                45
        ));

        insertProductInternal(db, new Product(
                "A005",
                "Ластик белый",
                "Faber-Castell",
                "Германия",
                "Письменные принадлежности",
                30.00,
                90
        ));
    }

    //стартовое заполнение
    private long insertProductInternal(SQLiteDatabase db, Product product) {
        ContentValues values = createContentValues(product); //колонка product -> значение
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public long insertProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase(); //открытие БД для записи
        return db.insert(TABLE_PRODUCTS, null, createContentValues(product));
    }

    public int updateProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();

        return db.update(
                TABLE_PRODUCTS,
                createContentValues(product), //новые значения
                COLUMN_ID + " = ?",  //условие обновления
                new String[]{String.valueOf(product.getId())} //кол-во изм-х строк
        );
    }

    public int deleteProduct(long id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_PRODUCTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteProducts(List<Product> products) {  //ДЛЯ мультивыбора
        SQLiteDatabase db = getWritableDatabase();
        int deletedCount = 0;

        db.beginTransaction();

        try {
            for (Product product : products) {
                deletedCount += db.delete(
                        TABLE_PRODUCTS,
                        COLUMN_ID + " = ?",
                        new String[]{String.valueOf(product.getId())}
                );
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return deletedCount;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PRODUCTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ID + " DESC"
        );

        try { //cursor - рез-т запроса к БД хранит
            while (cursor.moveToNext()) { //Пока есть следующая строка, читаем товар
                Product product = new Product();

                product.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                product.setArticle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTICLE)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                product.setManufacturer(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MANUFACTURER)));
                product.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY)));
                product.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                product.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)));

                products.add(product);
            }
        } finally {
            cursor.close();
        }

        return products;
    }

    private ContentValues createContentValues(Product product) { //Product в ContentValues(контейнер для записи в БД)
        ContentValues values = new ContentValues();

        //каждая характеристика товара кладётся в соответствующий столбец
        values.put(COLUMN_ARTICLE, product.getArticle());
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_MANUFACTURER, product.getManufacturer());
        values.put(COLUMN_COUNTRY, product.getCountry());
        values.put(COLUMN_CATEGORY, product.getCategory());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_QUANTITY, product.getQuantity());

        return values;
    }
}