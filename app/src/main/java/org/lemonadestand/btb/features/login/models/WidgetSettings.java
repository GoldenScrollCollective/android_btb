package org.lemonadestand.btb.features.login.models;

import com.google.gson.annotations.SerializedName;

public class WidgetSettings {
    @SerializedName("button")
    Button button;

    @SerializedName("values")
    Values values;

    @SerializedName("form")
    Form form;


    public void setButton(Button button) {
        this.button = button;
    }
    public Button getButton() {
        return button;
    }

    public void setValues(Values values) {
        this.values = values;
    }
    public Values getValues() {
        return values;
    }

    public void setForm(Form form) {
        this.form = form;
    }
    public Form getForm() {
        return form;
    }


}
