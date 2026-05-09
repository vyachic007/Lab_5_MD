package com.example.lab_5;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ProductAdapter productAdapter;

    private RecyclerView productsRecyclerView;

    private View selectionContainer;
    private TextView selectionCounterTextView;

    private Button selectAllButton;
    private Button shareButton;
    private Button deleteSelectedButton;
    private Button exitSelectionButton;
    private Button settingsButton;

    private FloatingActionButton addProductFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();
        initDatabase();
        initRecyclerView();
        initListeners();

        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (productAdapter != null) {
            applySettingsToAdapter();
        }
    }

    private void initViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView);

        selectionContainer = findViewById(R.id.selectionContainer);
        selectionCounterTextView = findViewById(R.id.selectionCounterTextView);

        selectAllButton = findViewById(R.id.selectAllButton);
        shareButton = findViewById(R.id.shareButton);
        deleteSelectedButton = findViewById(R.id.deleteSelectedButton);
        exitSelectionButton = findViewById(R.id.exitSelectionButton);
        settingsButton = findViewById(R.id.settingsButton);

        addProductFab = findViewById(R.id.addProductFab);
    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(this);
    }

    private void initRecyclerView() {
        productAdapter = new ProductAdapter();

        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        productAdapter.setOnProductActionListener(new ProductAdapter.OnProductActionListener() {
            @Override
            public void onProductClick(Product product) {
                if (productAdapter.isSelectionMode()) { //если вкл режим выбора
                    productAdapter.toggleSelection(product);
                    updateSelectionUi();
                }
            }

            @Override
            public void onProductLongClick(Product product) {
                if (!productAdapter.isSelectionMode()) {
                    enterSelectionMode();
                }

                productAdapter.toggleSelection(product);
                updateSelectionUi();
            }

            @Override
            public void onEditClick(Product product) {
                showProductDialog(product);
            }

            @Override
            public void onDeleteClick(Product product) {
                confirmDeleteProduct(product);
            }
        });
    }

    private void initListeners() {
        addProductFab.setOnClickListener(v -> showProductDialog(null));

        selectAllButton.setOnClickListener(v -> {
            if (productAdapter.areAllSelected()) {
                productAdapter.clearSelection();
            } else {
                productAdapter.selectAll();
            }

            updateSelectionUi();
        });

        shareButton.setOnClickListener(v -> shareSelectedProducts());

        deleteSelectedButton.setOnClickListener(v -> confirmDeleteSelectedProducts());

        exitSelectionButton.setOnClickListener(v -> exitSelectionMode());

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadProducts() {
        List<Product> products = databaseHelper.getAllProducts();
        productAdapter.setProducts(products);
        applySettingsToAdapter();
    }

    private void showProductDialog(Product productToEdit) {
        boolean isEditMode = productToEdit != null;

        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_product, null);

        EditText articleEditText = dialogView.findViewById(R.id.articleEditText);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        EditText manufacturerEditText = dialogView.findViewById(R.id.manufacturerEditText);
        EditText countryEditText = dialogView.findViewById(R.id.countryEditText);
        EditText categoryEditText = dialogView.findViewById(R.id.categoryEditText);
        EditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);

        if (isEditMode) {
            articleEditText.setText(productToEdit.getArticle());
            nameEditText.setText(productToEdit.getName());
            manufacturerEditText.setText(productToEdit.getManufacturer());
            countryEditText.setText(productToEdit.getCountry());
            categoryEditText.setText(productToEdit.getCategory());
            priceEditText.setText(String.valueOf(productToEdit.getPrice()));
            quantityEditText.setText(String.valueOf(productToEdit.getQuantity()));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isEditMode ? R.string.edit_product : R.string.add_product)
                .setView(dialogView)
                .setPositiveButton(isEditMode ? R.string.save : R.string.add, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {  //после нажатия ДОБ/ИЗМ
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE); //сохр/доб-ть

            positiveButton.setOnClickListener(v -> {
                Product product = readProductFromDialog(
                        articleEditText,
                        nameEditText,
                        manufacturerEditText,
                        countryEditText,
                        categoryEditText,
                        priceEditText,
                        quantityEditText
                );

                if (product == null) {
                    return;
                }

                if (isEditMode) {
                    product.setId(productToEdit.getId()); //сохрн-м старый ИД
                    databaseHelper.updateProduct(product);
                    Toast.makeText(this, R.string.record_updated, Toast.LENGTH_SHORT).show();
                } else {
                    databaseHelper.insertProduct(product);
                    Toast.makeText(this, R.string.record_added, Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
                loadProducts();
            });
        });

        dialog.show();
    }

    private Product readProductFromDialog(
            EditText articleEditText,
            EditText nameEditText,
            EditText manufacturerEditText,
            EditText countryEditText,
            EditText categoryEditText,
            EditText priceEditText,
            EditText quantityEditText
    ) {
        String article = articleEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String manufacturer = manufacturerEditText.getText().toString().trim();
        String country = countryEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();
        String priceText = priceEditText.getText().toString().trim();
        String quantityText = quantityEditText.getText().toString().trim();

        if (article.isEmpty()
                || name.isEmpty()
                || manufacturer.isEmpty()
                || country.isEmpty()
                || category.isEmpty()
                || priceText.isEmpty()
                || quantityText.isEmpty()) {

            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return null;
        }

        double price;
        int quantity;

        try {
            price = Double.parseDouble(priceText);
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException exception) {
            Toast.makeText(this, R.string.price_and_quantity_must_be_numbers, Toast.LENGTH_SHORT).show();
            return null;
        }

        if (price < 0) {
            Toast.makeText(this, R.string.price_cannot_be_negative, Toast.LENGTH_SHORT).show();
            return null;
        }

        if (quantity < 0) {
            Toast.makeText(this, R.string.quantity_cannot_be_negative, Toast.LENGTH_SHORT).show();
            return null;
        }

        return new Product(
                article,
                name,
                manufacturer,
                country,
                category,
                price,
                quantity
        );
    }

    private void confirmDeleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_record_title)
                .setMessage(getString(R.string.delete_product_message, product.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    databaseHelper.deleteProduct(product.getId());
                    Toast.makeText(this, R.string.record_deleted, Toast.LENGTH_SHORT).show();
                    loadProducts();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirmDeleteSelectedProducts() {
        List<Product> selectedProducts = productAdapter.getSelectedProducts();

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, R.string.no_selected_records, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_selected_title)
                .setMessage(getString(R.string.delete_selected_message, selectedProducts.size()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    databaseHelper.deleteProducts(selectedProducts);
                    Toast.makeText(this, R.string.selected_records_deleted, Toast.LENGTH_SHORT).show();

                    exitSelectionMode();
                    loadProducts();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void enterSelectionMode() {
        productAdapter.setSelectionMode(true);

        selectionContainer.setVisibility(View.VISIBLE);

        addProductFab.setVisibility(View.GONE);
        settingsButton.setVisibility(View.GONE);

        updateSelectionUi();
    }

    private void exitSelectionMode() {
        productAdapter.setSelectionMode(false);

        selectionContainer.setVisibility(View.GONE);

        addProductFab.setVisibility(View.VISIBLE);
        settingsButton.setVisibility(View.VISIBLE);

        updateSelectionUi();
    }

    private void updateSelectionUi() { //обновляем ниж панель
        int selectedCount = productAdapter.getSelectedCount();

        selectionCounterTextView.setText(getString(R.string.selected_count, selectedCount));

        if (productAdapter.areAllSelected()) {
            selectAllButton.setText(R.string.reset);
        } else {
            selectAllButton.setText(R.string.select);
        }

        shareButton.setEnabled(selectedCount > 0);
        deleteSelectedButton.setEnabled(selectedCount > 0);
    }

    private void shareSelectedProducts() {
        List<Product> selectedProducts = productAdapter.getSelectedProducts();

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, R.string.select_at_least_one_product, Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder textBuilder = new StringBuilder();

        textBuilder.append(getString(R.string.selected_products_title)).append("\n\n");

        for (int i = 0; i < selectedProducts.size(); i++) {
            Product product = selectedProducts.get(i);

            textBuilder.append(getString(R.string.product_number, i + 1)).append("\n");
            textBuilder.append(product.toShareText());

            if (i < selectedProducts.size() - 1) { //текущий товар не последний?
                textBuilder.append("\n\n")
                        .append(getString(R.string.product_share_separator))
                        .append("\n\n");
            }
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, textBuilder.toString());

        Intent chooser = Intent.createChooser(sendIntent, getString(R.string.share_chooser_title));
        startActivity(chooser);
    }

    private void applySettingsToAdapter() {
        SharedPreferences preferences = getSharedPreferences( //ключ:значение (файл настройки приложения)
                SettingsActivity.PREFS_NAME,
                MODE_PRIVATE
        );

        int fontSize = preferences.getInt(SettingsActivity.KEY_FONT_SIZE, 16);
        int itemHeightDp = preferences.getInt(SettingsActivity.KEY_ITEM_HEIGHT, 0);

        boolean showManufacturer = preferences.getBoolean(
                SettingsActivity.KEY_SHOW_MANUFACTURER,
                true
        );

        boolean showCountry = preferences.getBoolean(
                SettingsActivity.KEY_SHOW_COUNTRY,
                true
        );

        boolean showQuantity = preferences.getBoolean(
                SettingsActivity.KEY_SHOW_QUANTITY,
                true
        );

        boolean largePrice = preferences.getBoolean(
                SettingsActivity.KEY_LARGE_PRICE,
                false
        );

        int itemHeightPx = 0;

        if (itemHeightDp > 0) {
            itemHeightPx = dpToPx(itemHeightDp);
        }

        productAdapter.applySettings(
                fontSize,
                itemHeightPx,
                showManufacturer,
                showCountry,
                showQuantity,
                largePrice
        );
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;  //плотность экрана
        return Math.round(dp * density);
    }
}