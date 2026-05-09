package com.example.lab_5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "app_settings";

    public static final String KEY_FONT_SIZE = "font_size";
    public static final String KEY_ITEM_HEIGHT = "item_height";
    public static final String KEY_SHOW_MANUFACTURER = "show_manufacturer";
    public static final String KEY_SHOW_COUNTRY = "show_country";
    public static final String KEY_SHOW_QUANTITY = "show_quantity";
    public static final String KEY_LARGE_PRICE = "large_price";

    private static final int MIN_FONT_SIZE = 12;

    private TextView fontSizeTextView;
    private TextView itemHeightTextView;

    private SeekBar fontSizeSeekBar; //размер шрифта
    private SeekBar itemHeightSeekBar; //меняет высоту карточки товара


    //переключатели
    private SwitchMaterial showManufacturerSwitch;
    private SwitchMaterial showCountrySwitch;
    private SwitchMaterial showQuantitySwitch;

    private CheckBox largePriceCheckBox;

    private Button saveSettingsButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        loadSettings(); //Загружает сохранённые настройки из SharedPreferences
        initListeners();
    }

    //связываем Java код с XML-разметкой
    private void initViews() {
        fontSizeTextView = findViewById(R.id.fontSizeTextView);
        itemHeightTextView = findViewById(R.id.itemHeightTextView);

        fontSizeSeekBar = findViewById(R.id.fontSizeSeekBar);
        itemHeightSeekBar = findViewById(R.id.itemHeightSeekBar);

        showManufacturerSwitch = findViewById(R.id.showManufacturerSwitch);
        showCountrySwitch = findViewById(R.id.showCountrySwitch);
        showQuantitySwitch = findViewById(R.id.showQuantitySwitch);

        largePriceCheckBox = findViewById(R.id.largePriceCheckBox);

        saveSettingsButton = findViewById(R.id.saveSettingsButton);
        backButton = findViewById(R.id.backButton);
    }

    private void loadSettings() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        int fontSize = preferences.getInt(KEY_FONT_SIZE, 16);
        int itemHeight = preferences.getInt(KEY_ITEM_HEIGHT, 0);

        boolean showManufacturer = preferences.getBoolean(KEY_SHOW_MANUFACTURER, true);
        boolean showCountry = preferences.getBoolean(KEY_SHOW_COUNTRY, true);
        boolean showQuantity = preferences.getBoolean(KEY_SHOW_QUANTITY, true);
        boolean largePrice = preferences.getBoolean(KEY_LARGE_PRICE, false);

        fontSizeSeekBar.setProgress(fontSize - MIN_FONT_SIZE);
        itemHeightSeekBar.setProgress(itemHeight);

        showManufacturerSwitch.setChecked(showManufacturer);
        showCountrySwitch.setChecked(showCountry);
        showQuantitySwitch.setChecked(showQuantity);

        largePriceCheckBox.setChecked(largePrice);

        updateFontSizeText(fontSize); //Обновляем текстовую надпись рядом с ползунком размера шрифта
        updateItemHeightText(itemHeight);
    }

    private void initListeners() {
        //SeekBar.OnSeekBarChangeListener — интерфейс, который позволяет реагировать на изменение ползунка
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //когда знач ползунка меняется
            @Override
            public void onProgressChanged(
                    SeekBar seekBar,
                    int progress,
                    boolean fromUser //польз или программа
            ) {
                int fontSize = MIN_FONT_SIZE + progress;
                updateFontSizeText(fontSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { //польз двиг полз-к
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { //опускаем ползунок
            }
        });

        //Назначается слушатель изменений для второго ползунка — itemHeightSeekBar
        itemHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(
                    SeekBar seekBar,
                    int progress,
                    boolean fromUser
            ) {
                updateItemHeightText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        saveSettingsButton.setOnClickListener(v -> {
            saveSettings();//метод берет значения и сохр-т их в SharedPreferences
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
            finish();
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void saveSettings() {
        int fontSize = MIN_FONT_SIZE + fontSizeSeekBar.getProgress();
        int itemHeight = itemHeightSeekBar.getProgress();

        boolean showManufacturer = showManufacturerSwitch.isChecked();
        boolean showCountry = showCountrySwitch.isChecked();
        boolean showQuantity = showQuantitySwitch.isChecked();
        boolean largePrice = largePriceCheckBox.isChecked();

        //Открываем файл настроек(имя, приватный режим)
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        preferences.edit()
                .putInt(KEY_FONT_SIZE, fontSize)
                .putInt(KEY_ITEM_HEIGHT, itemHeight)
                .putBoolean(KEY_SHOW_MANUFACTURER, showManufacturer)
                .putBoolean(KEY_SHOW_COUNTRY, showCountry)
                .putBoolean(KEY_SHOW_QUANTITY, showQuantity)
                .putBoolean(KEY_LARGE_PRICE, largePrice)
                .apply();
    }

    private void updateFontSizeText(int fontSize) {
        fontSizeTextView.setText(getString(R.string.font_size_format, fontSize));
    }

    private void updateItemHeightText(int itemHeight) {//текущее знач высоты, выбранное на ползунке
        if (itemHeight == 0) {
            itemHeightTextView.setText(R.string.item_height_auto);
        } else {
            itemHeightTextView.setText(getString(R.string.item_height_format, itemHeight));
        }
    }
}