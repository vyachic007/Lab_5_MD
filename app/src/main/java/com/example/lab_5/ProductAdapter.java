package com.example.lab_5;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    //чтобы адаптер мог сообщить MainActivity, что пользователь что-то сделал с товаром
    public interface OnProductActionListener {
        void onProductClick(Product product);

        void onProductLongClick(Product product);

        void onEditClick(Product product);

        void onDeleteClick(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private final Set<Long> selectedIds = new HashSet<>();

    private OnProductActionListener listener;
    private boolean selectionMode = false;

    private int fontSize = 16;
    private int itemHeightPx = 0;
    private boolean showManufacturer = true;
    private boolean showCountry = true;
    private boolean showQuantity = true;
    private boolean largePrice = false;

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    //Передача списка товаров в адаптер
    public void setProducts(List<Product> products) {
        this.products.clear();
        this.products.addAll(products);
        notifyDataSetChanged(); //Данные изменились, перерисуй список.
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;

        if (!selectionMode) {
            selectedIds.clear();
        }

        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    // Выбор/снятие выбора товара
    public void toggleSelection(Product product) {
        long id = product.getId();

        if (selectedIds.contains(id)) { //выбран ли этот товар или нет
            selectedIds.remove(id); //если да- снимаем галочку
        } else {
            selectedIds.add(id);
        }

        notifyDataSetChanged();
    }

    public void selectAll() {
        selectedIds.clear();

        for (Product product : products) {
            selectedIds.add(product.getId());
        }

        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    //выбраны ли все товары
    public boolean areAllSelected() {
        return !products.isEmpty() && selectedIds.size() == products.size();
    }

    public int getSelectedCount() {
        return selectedIds.size();
    }

    public List<Product> getSelectedProducts() {
        List<Product> selectedProducts = new ArrayList<>();

        for (Product product : products) {
            if (selectedIds.contains(product.getId())) {
                selectedProducts.add(product);
            }
        }

        return selectedProducts;
    }

    public void applySettings(
            int fontSize,
            int itemHeightPx,
            boolean showManufacturer,
            boolean showCountry,
            boolean showQuantity,
            boolean largePrice
    ) {
        this.fontSize = fontSize;
        this.itemHeightPx = itemHeightPx;
        this.showManufacturer = showManufacturer;
        this.showCountry = showCountry;
        this.showQuantity = showQuantity;
        this.largePrice = largePrice;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        //Получаем объект, который умеет превращать XML-разметку в View
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);


        //Создаём ViewHolder, который будет хранить ссылки на элементы карточки
        return new ProductViewHolder(view);
    }


    //заполняем карточку данными
    @Override
    public void onBindViewHolder(
            @NonNull ProductViewHolder holder,
            int position
    ) {
        Product product = products.get(position);
        boolean selected = selectedIds.contains(product.getId()); //Проверяем, выбран ли этот товар

        //Передаём товар и состояние выбора в ViewHolder, чтобы он отобразил карточку
        holder.bind(product, selected);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    //ViewHolder хранит ссылки на элементы одной карточки товара
    class ProductViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final CheckBox checkBox;

        private final TextView nameTextView;
        private final TextView articleTextView;
        private final TextView manufacturerTextView;
        private final TextView countryTextView;
        private final TextView categoryTextView;
        private final TextView priceTextView;
        private final TextView quantityTextView;

        private final LinearLayout itemButtonsLayout;

        private final MaterialButton editButton;
        private final MaterialButton deleteButton;


        //конструктор получает 1у карточку товара
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.productItemRoot);
            checkBox = itemView.findViewById(R.id.productCheckBox);

            nameTextView = itemView.findViewById(R.id.productNameTextView);
            articleTextView = itemView.findViewById(R.id.productArticleTextView);
            manufacturerTextView = itemView.findViewById(R.id.productManufacturerTextView);
            countryTextView = itemView.findViewById(R.id.productCountryTextView);
            categoryTextView = itemView.findViewById(R.id.productCategoryTextView);
            priceTextView = itemView.findViewById(R.id.productPriceTextView);
            quantityTextView = itemView.findViewById(R.id.productQuantityTextView);

            itemButtonsLayout = itemView.findViewById(R.id.itemButtonsLayout);

            editButton = itemView.findViewById(R.id.editProductButton);
            deleteButton = itemView.findViewById(R.id.deleteProductButton);
        }


        //заполняем карточку данными товара
        public void bind(Product product, boolean selected) {
            nameTextView.setText(product.getName());

            articleTextView.setText(itemView.getContext().getString(
                    R.string.product_article_format,
                    product.getArticle()
            ));

            manufacturerTextView.setText(itemView.getContext().getString(
                    R.string.product_manufacturer_format,
                    product.getManufacturer()
            ));

            countryTextView.setText(itemView.getContext().getString(
                    R.string.product_country_format,
                    product.getCountry()
            ));

            categoryTextView.setText(itemView.getContext().getString(
                    R.string.product_category_format,
                    product.getCategory()
            ));

            priceTextView.setText(itemView.getContext().getString(
                    R.string.product_price_format,
                    product.getPrice()
            ));

            quantityTextView.setText(itemView.getContext().getString(
                    R.string.product_quantity_format,
                    product.getQuantity()
            ));

            //применяем настройки карточки
            applyTextSettings();
            applyVisibilitySettings();
            applyHeightSettings();

            //Если режим выбора включён — показываем чекбокс
            checkBox.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
            checkBox.setChecked(selected);

            itemButtonsLayout.setVisibility(selectionMode ? View.GONE : View.VISIBLE);

            if (selected) { //если товар выбран
                root.setBackgroundColor(Color.parseColor("#EDE7F6"));
            } else {
                root.setBackgroundColor(Color.WHITE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product); //адаптер сообщает об этом в MainActivity
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onProductLongClick(product);
                }

                return true;
            });

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(product);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(product);
                }
            });

            checkBox.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }


        //применяем размер шрифта
        private void applyTextSettings() {
            nameTextView.setTextSize(fontSize + 2);
            articleTextView.setTextSize(fontSize);
            manufacturerTextView.setTextSize(fontSize);
            countryTextView.setTextSize(fontSize);
            categoryTextView.setTextSize(fontSize);
            quantityTextView.setTextSize(fontSize);

            if (largePrice) {
                priceTextView.setTextSize(fontSize + 4);
            } else {
                priceTextView.setTextSize(fontSize);
            }
        }


        // показываем или скрываем отдельные поля карточки
        private void applyVisibilitySettings() {
            manufacturerTextView.setVisibility(showManufacturer ? View.VISIBLE : View.GONE);
            countryTextView.setVisibility(showCountry ? View.VISIBLE : View.GONE);
            quantityTextView.setVisibility(showQuantity ? View.VISIBLE : View.GONE);
        }


        //применяем высоту карточки
        private void applyHeightSettings() {
            //Получаем текущие параметры размера карточки
            ViewGroup.LayoutParams params = root.getLayoutParams();

            if (itemHeightPx > 0) {
                params.height = itemHeightPx;
            } else {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            root.setLayoutParams(params);
        }
    }
}