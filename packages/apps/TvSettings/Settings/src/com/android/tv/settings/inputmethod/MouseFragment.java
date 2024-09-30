package com.android.tv.settings.inputmethod;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.TwoStatePreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.droidlogic.app.SystemControlManager;


public class MouseFragment extends SettingsPreferenceFragment {
    private InputManager mIm;
    private Integer mMouseSettingMax;
    private Integer mMouseSettingMin;
    private Integer mMouseSettingNew;
    private Integer mMouseSettingOld;
    private TwoStatePreference mMouseTouchInput;
    private TextView mPercentTextView;
    private Preference mPointerSpeed;
    private SystemControlManager mSystemControlManager = SystemControlManager.getInstance();
    private TwoStatePreference mToggleLargePointerIconPreference;

    private boolean getMouseTouchInputEnabled() {
        return this.mSystemControlManager.getPropertyString("persist.vendor.mouse_touch_input", "0").equals("1");
    }

    private Integer getPointerSpeed() {
        return Integer.valueOf(this.mIm.getPointerSpeed(getContext()));
    }

    private void handleToggleLargePointerIconPreferenceClick() {
        Settings.Secure.putInt(getContext().getContentResolver(), "accessibility_large_pointer_icon", this.mToggleLargePointerIconPreference.isChecked() ? 1 : 0);
    }

    public static MouseFragment newInstance() {
        return new MouseFragment();
    }

    private void refresh() {
        this.mMouseTouchInput.setChecked(getMouseTouchInputEnabled());
        this.mToggleLargePointerIconPreference.setChecked(Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_large_pointer_icon", 0) != 0);
        this.mPointerSpeed.setSummary(getPointerSpeed().toString());
    }

    private void setMouseTouchInputEnabled(boolean z) {
        this.mSystemControlManager.setProperty("persist.vendor.mouse_touch_input", z ? "1" : "0");
    }


    public void setPointerSpeed(Integer num, boolean z) {
        if (z) {
            this.mIm.setPointerSpeed(getContext(), num.intValue());
        } else {
            this.mIm.tryPointerSpeed(num.intValue());
        }
    }

    private void showMouseSettingDialog(final String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (str.equals("pointer_speed")) {
            Integer pointerSpeed = getPointerSpeed();
            this.mMouseSettingOld = pointerSpeed;
            this.mMouseSettingNew = pointerSpeed;
            this.mMouseSettingMin = -7;
            this.mMouseSettingMax = 7;
            builder.setTitle(R.string.pointer_speed);
        }
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.mouse_settings_dialog, (ViewGroup) null);
        final Integer num = this.mMouseSettingMin;
        Integer valueOf = Integer.valueOf(this.mMouseSettingMax.intValue() - this.mMouseSettingMin.intValue());
        if (str.equals("pointer_speed")) {
            valueOf = Integer.valueOf(getPointerSpeed().intValue() - num.intValue());
        }
        Integer num2 = valueOf;
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { 
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (str.equals("pointer_speed") && MouseFragment.this.mMouseSettingNew != MouseFragment.this.mMouseSettingOld) {
                    MouseFragment.this.setPointerSpeed(MouseFragment.this.mMouseSettingNew, true);
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { 
           
            public void onClick(DialogInterface dialogInterface, int i) {
                if (str.equals("pointer_speed")) {
                    MouseFragment.this.setPointerSpeed(MouseFragment.this.mMouseSettingOld, false);
                }
                dialogInterface.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface dialogInterface) {
                if (str.equals("pointer_speed")) {
                    MouseFragment.this.setPointerSpeed(MouseFragment.this.mMouseSettingOld, false);
                }
                dialogInterface.dismiss();
            }
        });
        this.mPercentTextView = (TextView) inflate.findViewById(R.id.position_percent);
        SeekBar seekBar = (SeekBar) inflate.findViewById(R.id.position_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
           
            public void onProgressChanged(SeekBar seekBar2, int i, boolean z) {
                MouseFragment.this.mPercentTextView.setText(Integer.toString(num.intValue() + i));
                if (str.equals("pointer_speed")) {
                    MouseFragment.this.mMouseSettingNew = Integer.valueOf(num.intValue() + i);
                    MouseFragment.this.setPointerSpeed(MouseFragment.this.mMouseSettingNew, false);
                }
            }

            
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }
        });
        seekBar.setMax(this.mMouseSettingMax.intValue() - this.mMouseSettingMin.intValue());
        seekBar.setProgress(num2.intValue());
        this.mPercentTextView.setText(Integer.toString(num2.intValue() + num.intValue()));
        builder.setView(inflate);
        builder.create().show();
    }

   
    public int getMetricsCategory() {
        return 46;
    }

    
    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.mouse, null);
        this.mIm = (InputManager) getContext().getSystemService("input");
        this.mMouseTouchInput = (TwoStatePreference) findPreference("mouse_touch_input");
        this.mToggleLargePointerIconPreference = (TwoStatePreference) findPreference("toggle_large_pointer_icon");
        this.mPointerSpeed = findPreference("pointer_speed");
    }

    
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (key == null) {
            return super.onPreferenceTreeClick(preference);
        }
        char c = 65535;
        int hashCode = key.hashCode();
        if (hashCode != 448413637) {
            if (hashCode != 462273072) {
                if (hashCode == 1852710538 && key.equals("toggle_large_pointer_icon")) {
                    c = 1;
                }
            } else if (key.equals("mouse_touch_input")) {
                c = 0;
            }
        } else if (key.equals("pointer_speed")) {
            c = 2;
        }
        switch (c) {
            case 0:
                setMouseTouchInputEnabled(this.mMouseTouchInput.isChecked());
                refresh();
                return true;
            case 1:
                handleToggleLargePointerIconPreferenceClick();
                refresh();
                return true;
            case 2:
                showMouseSettingDialog(preference.getKey());
                refresh();
                return true;
            default:
                return super.onPreferenceTreeClick(preference);
        }
    }

    public void onResume() {
        super.onResume();
        refresh();
    }
}
