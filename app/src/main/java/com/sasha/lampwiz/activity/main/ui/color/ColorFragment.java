package com.sasha.lampwiz.activity.main.ui.color;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sasha.lampwiz.R;
import com.sasha.lampwiz.activity.main.MainViewModel;
import com.sasha.lampwiz.bluetooth.gatt.characteristic.NeopixelColor;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.slider.AlphaSlider;
import com.flask.colorpicker.slider.LightnessSlider;

public class ColorFragment extends Fragment implements OnColorChangedListener {

    private MainViewModel mainViewModel;
    private ColorPickerView colorPickerView;
    private AlphaSlider alphaSlider;
    private LightnessSlider lightnessSlider;

    private boolean isInitialized = false;

    private int selectedColor;
    private int selectedAlpha;
    private int selectedBright;

    @SuppressWarnings("unused")
    public static ColorFragment newInstance() {

        return new ColorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_color, container, false);

        if (!this.isInitialized) {

            final FragmentActivity activity = this.getActivity();
            this.mainViewModel = new ViewModelProvider((null != activity) ? activity : this).get(MainViewModel.class);

            this.colorPickerView = root.findViewById(R.id.color_picker_view);
            this.colorPickerView.addOnColorChangedListener(this);

            this.alphaSlider = root.findViewById(R.id.color_picker_alpha_slider);
            this.alphaSlider.setColorPicker(this.colorPickerView);
            this.alphaSlider.setOnValueChangedListener(this::onAlphaChanged);
            this.alphaSlider.setVisibility(View.GONE);

            this.lightnessSlider = root.findViewById(R.id.color_picker_lightness_slider);
            this.lightnessSlider.setColorPicker(this.colorPickerView);
            this.lightnessSlider.setOnValueChangedListener(this::onLightnessChanged);

            this.isInitialized = true;
        }

        return root;
    }

    @Override
    public void onResume() {

        super.onResume();

        if (null != this.mainViewModel) {
            final NeopixelColor neopixelColor = this.mainViewModel.getNeopixelColor();
            if ((null != neopixelColor) && neopixelColor.isValid()) {

                this.selectedColor  = neopixelColor.color();
                this.selectedAlpha  = neopixelColor.alpha();
                this.selectedBright = neopixelColor.bright();

                this.colorPickerView.setColor(this.selectedColor, false);
                this.alphaSlider.setColor(this.selectedColor);
                this.lightnessSlider.setColor(this.selectedColor);
            }
        }
    }

    @Override
    public void onColorChanged(int selectedColor) {

        this.selectedColor = selectedColor;

        this.mainViewModel.updateNeopixelColor(selectedColor, this.selectedAlpha, this.selectedBright);
    }

    public void onAlphaChanged(float value) {

        this.selectedAlpha = Math.round(value * NeopixelColor.MAX_ALPHA) & 0xFF;
    }

    public void onLightnessChanged(float value) {

        this.selectedBright = Math.round(value * NeopixelColor.MAX_BRIGHT) & 0xFF;
    }

}
