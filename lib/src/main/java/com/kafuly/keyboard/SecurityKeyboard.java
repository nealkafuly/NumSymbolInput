package com.kafuly.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Build;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kafuly.utils.DisplayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SecurityKeyboard extends PopupWindow {

    private KeyboardView keyboardView;
    private Keyboard mKeyboardLetter;
    private Keyboard mKeyboardNumber, mKeyboardSymbol;
    private boolean isNumber = false;
    private boolean isUpper = false;
    private TextView tvSymbol, tvLetter, tvNumber;
    private View mMainView;
    private EditText curEditText;
    private SecurityConfigure configuration;
    private ViewGroup mParentLayout;
    private Context mContext;


    private SecurityKeyboard() {
        throw new IllegalArgumentException("fuck");
    }


    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    public SecurityKeyboard(ViewGroup parentLayout, SecurityConfigure securityConfigure) {
        super(parentLayout.getContext());
        if (securityConfigure == null) {
            configuration = new SecurityConfigure();
        } else {
            configuration = securityConfigure;
        }
        mParentLayout = parentLayout;
        mContext = parentLayout.getContext();
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            mMainView = inflater.inflate(R.layout.keyboard, null);
        }
        this.setContentView(mMainView);
        this.setWidth(DisplayUtils.getScreenWidth(mContext));
        this.setHeight(LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#00000000"));
        this.setBackgroundDrawable(dw);

        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setPopupWindowTouchModal(this, false);

        this.setAnimationStyle(R.style.PopupKeybroad);
        if (DisplayUtils.dp2px(mContext, 236) > (int) (DisplayUtils
                .getScreenHeight(mContext) * 3.0f / 5.0f)) {
            mKeyboardLetter = new Keyboard(mContext,
                    R.xml.keyboard_english_land);
            mKeyboardNumber = new Keyboard(mContext, R.xml.keyboard_number_land);
            mKeyboardSymbol = new Keyboard(mContext,
                    R.xml.keyboard_symbols_shift_land);
        } else {
            mKeyboardLetter = new Keyboard(mContext, R.xml.keyboard_english);
            mKeyboardNumber = new Keyboard(mContext, R.xml.keyboard_number);
            mKeyboardSymbol = new Keyboard(mContext,
                    R.xml.keyboard_symbols_shift);
        }

        keyboardView = (KeyboardView) mMainView.findViewById(R.id.keyboard_view);


        tvSymbol = (TextView) mMainView.findViewById(R.id.tv_symbol);
        tvLetter = (TextView) mMainView.findViewById(R.id.tv_letter);
        tvNumber = (TextView) mMainView.findViewById(R.id.tv_number);
        Button btnHideKeyboard = (Button) mMainView.findViewById(R.id.hide_keyboard_btn);
        if (btnHideKeyboard != null) {
            btnHideKeyboard.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard();
                }
            });
        }
        if (!configuration.isLetterEnabled()) {
            tvLetter.setVisibility(View.GONE);
        }
        if (!configuration.isNumberEnabled()) {
            tvNumber.setVisibility(View.GONE);
        }
        if (!configuration.isSymbolEnabled()) {
            tvSymbol.setVisibility(View.GONE);
        }

        switchKeyboardType(configuration.getDefaultKeyboardType(),
                configuration.getSelectedColor(), configuration.getUnselectedColor());
        switch (configuration.getDefaultKeyboardType().getCode()) {
            case 0:
                keyboardView.setKeyboard(mKeyboardLetter);
                break;
            case 1:
                keyboardView.setKeyboard(mKeyboardNumber);
                break;
            case 2:
                keyboardView.setKeyboard(mKeyboardSymbol);
                break;
            default:
                keyboardView.setKeyboard(mKeyboardLetter);
                break;
        }
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(new OnKeyboardActionListenerImp());
        tvNumber.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                switchKeyboardType(KeyboardType.NUMBER,
                        configuration.getSelectedColor(),
                        configuration.getUnselectedColor());
                keyboardView.setKeyboard(mKeyboardNumber);
            }
        });
        tvLetter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                switchKeyboardType(KeyboardType.LETTER,
                        configuration.getSelectedColor(),
                        configuration.getUnselectedColor());
                keyboardView.setKeyboard(mKeyboardLetter);
            }
        });
        tvSymbol.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                switchKeyboardType(KeyboardType.SYMBOL,
                        configuration.getSelectedColor(),
                        configuration.getUnselectedColor());
                keyboardView.setKeyboard(mKeyboardSymbol);
            }
        });

        if (mParentLayout != null) {
            int viewCount = mParentLayout.getChildCount();
            for (int i = 0; i < viewCount; i++) {
                final View view = mParentLayout.getChildAt(i);
                if (view != null) {
                    if (view instanceof EditText) {
                        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    if (!isShowing()) {
                                        showKeyboard((EditText) view);
                                    }
                                }
                            }
                        });
                        view.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (!isShowing()) {
                                    showKeyboard((EditText) view);
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @SuppressLint("PrivateApi")
    private void setPopupWindowTouchModal(PopupWindow popupWindow, boolean touchModal) {
        Method method;
        try {
            method = PopupWindow.class.getDeclaredMethod("setTouchModal",
                    boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, touchModal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideSystemKeyboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            curEditText.setShowSoftInputOnFocus(false);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(curEditText, false);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    private void changeKey() {
        List<Key> keylist = mKeyboardLetter.getKeys();
        if (isUpper) {
            isUpper = false;
            for (Key key : keylist) {
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
                if (key.codes[0] == -1) {
                    key.icon = mContext.getResources().getDrawable(
                            R.drawable.keyboard_shift);
                }
            }
        } else {
            isUpper = true;
            for (Key key : keylist) {
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
                if (key.codes[0] == -1) {
                    key.icon = mContext.getResources().getDrawable(
                            R.drawable.keyboard_shift_c);
                }
            }
        }
    }


    private void showKeyboard(View view) {
        int realHeight = 0;
        int yOff;

        yOff = realHeight - DisplayUtils.dp2px(mContext, 231);

        if (DisplayUtils.dp2px(mContext, 236) > (int) (DisplayUtils
                .getScreenHeight(mContext) * 3.0f / 5.0f)) {
            yOff = DisplayUtils.getScreenHeight(mContext)
                    - DisplayUtils.dp2px(mContext, 199);
        }

        int height = getContentView().getMeasuredHeight();
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in);
        showAtLocation(view, Gravity.BOTTOM | Gravity.LEFT, 0, -height);
        getContentView().setAnimation(animation);
    }


    public void hideKeyboard() {
        this.dismiss();
    }

    public void showKeyboard(EditText editText) {
        curEditText = editText;
        if (curEditText == null) {
            return;
        }
        hideSystemKeyboard();
        showKeyboard(mParentLayout);
    }

    private boolean isLetter(String str) {
        String letterStr = mContext.getString(R.string.aToz);
        return letterStr.contains(str.toLowerCase());
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void switchKeyboardType(KeyboardType keyboardType, int selectedColor, int unSelectedColor) {
        switch (keyboardType.getCode()) {
            case 0:
                tvLetter.setTextColor(selectedColor);
                tvSymbol.setTextColor(unSelectedColor);
                tvNumber.setTextColor(unSelectedColor);
                break;
            case 1:
                tvNumber.setTextColor(selectedColor);
                tvSymbol.setTextColor(unSelectedColor);
                tvLetter.setTextColor(unSelectedColor);
                break;
            case 2:
                tvSymbol.setTextColor(selectedColor);
                tvLetter.setTextColor(unSelectedColor);
                tvNumber.setTextColor(unSelectedColor);
                break;
            default:
                throw new IllegalArgumentException("不支持的键盘类型");
        }
    }

    public void switchKeyBordTypeByOutSide(KeyboardType type) {
        switch (type) {
            case SYMBOL:
                switchKeyboardType(KeyboardType.SYMBOL,
                        configuration.getSelectedColor(),
                        configuration.getUnselectedColor());
                keyboardView.setKeyboard(mKeyboardSymbol);
                break;
            case LETTER:
                switchKeyboardType(KeyboardType.LETTER,
                        configuration.getSelectedColor(),
                        configuration.getUnselectedColor());
                keyboardView.setKeyboard(mKeyboardLetter);
                break;
            case NUMBER:
                switchKeyboardType(KeyboardType.NUMBER,
                        configuration.getSelectedColor(),
                        configuration.getUnselectedColor());
                keyboardView.setKeyboard(mKeyboardNumber);
                break;
        }
    }

    private class OnKeyboardActionListenerImp implements OnKeyboardActionListener {

        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = curEditText.getText();
            int start = curEditText.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                changeKey();
                keyboardView.setKeyboard(mKeyboardLetter);

            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                if (isNumber) {
                    isNumber = false;
                    keyboardView.setKeyboard(mKeyboardLetter);
                } else {
                    isNumber = true;
                    keyboardView.setKeyboard(mKeyboardNumber);
                }
            } else if (primaryCode == 57419) {
                if (start > 0) {
                    curEditText.setSelection(start - 1);
                }
            } else if (primaryCode == 57421) {
                if (start < curEditText.length()) {
                    curEditText.setSelection(start + 1);
                }
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }
    }


    public void release() {
        if (mContext != null) {
            mContext = null;
        }
        if (curEditText != null) {
            curEditText = null;
        }
        if (mParentLayout != null) {
            mParentLayout = null;
        }
    }


}
