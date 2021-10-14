package wpmask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class WPmask extends LinearLayout implements WPmask_eCountriesAdapter.OnSetCounty {
    private final Context context;
    String PhoneNumber= "";
    String DefaultCountry = "ua";
    ArrayList<String> PriorityCountries=new ArrayList<>();

    ArrayList<String> AcceptCountries=new ArrayList<>();

    public ImageView Flag;
    public LinearLayout Delimetr;
    public EditText PhoneNumberView;
    public TextView PhoneCode;
    public String PhoneNumberValueBefore="";
    public float scale;
//    public FrameLayout FrameMenuCountry;
//    public LinearLayout MenuCountry_bg;
    public LinearLayout MenuCountry_box;
    public LinearLayout MenuCountry_bottom;
    public TextView MenuCountry_bottom_button_close;
    public RecyclerView MenuCountry_list;
    public LinearLayout MenuCountry_border_delimeter;
    public WPmask_eCountriesAdapter mWPmask_eCountriesAdapter;

//    public View rootView;

    public AlertDialog countriesDialog;
    public final WPmask_eCountries wp_eCountries;
    Boolean allowMask = true;
    public WPmask(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
//        rootView = ((Activity)context). getWindow().getDecorView().getRootView();


        this.scale = context.getResources().getDisplayMetrics().density;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.WPmask);

        String PhoneNumber_tmp=null;
        String priorityCountries_tmp=null;
        String defaultCountry_tmp=null;
        String AcceptCountries_tmp=null;

        defaultCountry_tmp = attributes.getString(R.styleable.WPmask_defaultCountry);
        PhoneNumber_tmp = attributes.getString(R.styleable.WPmask_phoneNumber);
        priorityCountries_tmp = attributes.getString(R.styleable.WPmask_PriorityCountries);
        AcceptCountries_tmp = attributes.getString(R.styleable.WPmask_AcceptCountries);

        if(defaultCountry_tmp != null){
            DefaultCountry = defaultCountry_tmp;
        }

        if(priorityCountries_tmp != null){
            PriorityCountries = new  ArrayList<>(Arrays.asList(priorityCountries_tmp.split(",")));
        }
        if(AcceptCountries_tmp != null){
            AcceptCountries = new  ArrayList<>(Arrays.asList(AcceptCountries_tmp.split(",")));
        }

        wp_eCountries = new WPmask_eCountries(context,DefaultCountry,AcceptCountries,PriorityCountries);
        mWPmask_eCountriesAdapter=new WPmask_eCountriesAdapter(context,wp_eCountries);
        mWPmask_eCountriesAdapter.Register_onSetCounty(this);

        attributes.recycle();
        CreateElements("");

        if (PhoneNumber_tmp != null) {
            InitNumber(PhoneNumber_tmp);
        }
    }

    private void CreateElements(String Number){
        Log.v("###","CreateElements"+Number);

        Flag = new ImageView(this.context);
        Delimetr = new LinearLayout(this.context);
        PhoneNumberView = new EditText(this.context);
        PhoneCode = new TextView(this.context);
        MenuCountry_box = new LinearLayout(this.context);
        MenuCountry_list = new RecyclerView(this.context);
        MenuCountry_bottom = new LinearLayout(this.context);
        MenuCountry_bottom_button_close = new TextView(this.context);
        MenuCountry_border_delimeter = new LinearLayout(this.context);
        MenuCountry_border_delimeter.setOrientation(LinearLayout.VERTICAL);
        MenuCountry_border_delimeter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, get_pixels(this.context,1)));
        MenuCountry_border_delimeter.setBackgroundColor(Color.BLACK);

        LayoutParams FlagParams = new LayoutParams(get_pixels(this.context,50), LayoutParams.MATCH_PARENT);
        LayoutParams PhoneCodeParams = new LayoutParams(get_pixels(this.context,70), LayoutParams.MATCH_PARENT);
        LayoutParams DelimetrParams = new LayoutParams(get_pixels(this.context,1), get_pixels(this.context,40));
        LayoutParams PhoneNumberParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LayoutParams MenuCountryParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LayoutParams MenuCountry_boxParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LayoutParams bottomParams_button_close = new LayoutParams(LayoutParams.WRAP_CONTENT, get_pixels(this.context,30));

        FlagParams.setMargins(get_pixels(this.context,8),get_pixels(this.context,8),get_pixels(this.context,8),get_pixels(this.context,8));
        Flag.setLayoutParams(FlagParams);

        Delimetr.setLayoutParams(DelimetrParams);
        PhoneNumberView.setLayoutParams(PhoneNumberParams);
        PhoneCode.setLayoutParams(PhoneCodeParams);
        PhoneCode.setGravity(Gravity.CENTER | Gravity.START);
        PhoneNumberView.setGravity(Gravity.CENTER);
        if(Number.length() >0 ){
            PhoneNumberView.setText(Number);
        }else {
            PhoneNumberView.setHint(wp_eCountries.pattern);
        }
        PhoneCode.setText(("+"+wp_eCountries.code));

        Delimetr.setBackgroundColor(Color.LTGRAY);
        Flag.setImageDrawable(wp_eCountries.getCountryDrawable(wp_eCountries.shortName));
        PhoneNumberView.setBackgroundColor(Color.TRANSPARENT);
        PhoneNumberView.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.addView(Flag);
        this.addView(PhoneCode);
        this.addView(Delimetr);
        this.addView(PhoneNumberView);
        PhoneCode.setOnClickListener(v -> {
            countriesDialog.show();
            hideKeyboard();
        });
        Flag.setOnClickListener(v -> {
            countriesDialog.show();
            hideKeyboard();

        });
        MenuCountry_bottom_button_close.setOnClickListener(v -> countriesDialog.dismiss());

        PhoneNumberView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged();

            }
        });
//        FrameMenuCountry.setVisibility(GONE);
        MenuCountry_boxParams.setMargins(get_pixels(this.context,40),0,get_pixels(this.context,40),0);
//        MenuCountry_bg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        MenuCountry_bg.setBackgroundColor(Color.BLACK);
//        MenuCountry_bg.setGravity(Gravity.CENTER_VERTICAL);
//        MenuCountry_bg.setAlpha(0.8f);
        MenuCountry_boxParams.setMargins(get_pixels(this.context,40),get_pixels(this.context,150),get_pixels(this.context,40),get_pixels(this.context,150));

        MenuCountry_box.setLayoutParams(MenuCountry_boxParams);
        MenuCountry_box.setBackgroundColor(Color.WHITE);
        MenuCountry_box.setOrientation(VERTICAL);

        MenuCountry_list.setAdapter(mWPmask_eCountriesAdapter);
        MenuCountry_list.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, get_pixels(this.context,300)));
        MenuCountry_list.setLayoutManager(new LinearLayoutManager(context));
        MenuCountry_list.setPadding(get_pixels(this.context,5),get_pixels(this.context,5),get_pixels(this.context,5),get_pixels(this.context,5));
        MenuCountry_list.setHasFixedSize(true);
        MenuCountry_list.setScrollBarSize(get_pixels(this.context,15));
        MenuCountry_list.setScrollContainer(true);
        MenuCountry_list.setVerticalScrollBarEnabled(true);

        MenuCountry_bottom.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, get_pixels(this.context,50)));
        MenuCountry_bottom.setBackgroundColor(Color.WHITE);
        MenuCountry_bottom.setGravity(Gravity.CENTER);
        //MenuCountry_bottom.setPadding(get_pixels(this.context,5),get_pixels(this.context,5),get_pixels(this.context,5),get_pixels(this.context,10));

        MenuCountry_bottom_button_close.setLayoutParams(bottomParams_button_close);
        MenuCountry_bottom_button_close.setText("Отмена");
        MenuCountry_bottom_button_close.setBackgroundColor(Color.WHITE);
        MenuCountry_bottom.addView(MenuCountry_bottom_button_close);

//        ((ViewGroup) rootView).addView(FrameMenuCountry);
//        FrameMenuCountry.addView(MenuCountry_bg);


        MenuCountry_box.addView(MenuCountry_list);
        MenuCountry_box.addView(MenuCountry_border_delimeter);
        MenuCountry_box.addView(MenuCountry_bottom);
        if(Number.length() > 0){
            textChanged();
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(MenuCountry_box);
        countriesDialog = builder.create();
    }

    public void InitNumber(String number){

        wp_eCountries.setCountry("ua");
        UpdateElements();
        try {

        for (int i = 0; i < wp_eCountries.countries.length(); i++) {
                if (number.length() < wp_eCountries.countries.getJSONObject(i).getString("code").length()){
                    break;
                }

            ArrayList<String> phoneArr = new ArrayList<>();
            for (int y = 0; y < number.toCharArray().length; y++){
                phoneArr.add(number.toCharArray()[y] + "");
            }
            String code = "";
            for (int y = 0; y < wp_eCountries.countries.getJSONObject(i).getString("code").length(); y++){
                code += phoneArr.get(y);
            }
            if (code.equals(wp_eCountries.countries.getJSONObject(i).getString("code"))){
                wp_eCountries.setCountry(wp_eCountries.countries.getJSONObject(i).getString("short_name"));
                String strForPhone = "";
                for (int y = wp_eCountries.countries.getJSONObject(i).getString("code").length(); y < phoneArr.size(); y++){
                    strForPhone += phoneArr.get(y);
                }
                allowMask = true;
                PhoneNumberView.setText(strForPhone);
                UpdateElements();
            }

        }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

    }
    public  String getNumber(){
        return wp_eCountries.code+PhoneNumberView.getText().toString().replaceAll("\\D","");
    }
    public  void setNumber(String number){
        InitNumber(number);
    }
    public  Boolean isValid(){
        return  (this.getNumber().length() == this.wp_eCountries.len) ? true :false;
    }
    public void UpdateElements(){
        String pCode = "+"+wp_eCountries.code;
        PhoneCode.setText(pCode);
        Flag.setImageDrawable(wp_eCountries.getCountryDrawable(null));
        PhoneNumberView.setHint(wp_eCountries.pattern);
        countriesDialog.dismiss();
        textChanged();
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(PhoneCode.getWindowToken(), 0);
    }
    public static Integer get_pixels(Context context,Integer dps){
        return  (int) (dps * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onSetCounty(int i) {
        try {
            JSONObject CountryTmp = new JSONObject(wp_eCountries.countries.getString(i));
            wp_eCountries.setCountry(CountryTmp.getString("shortName"));
        }catch (JSONException e){
            Log.v("###",e.getMessage().toString());
        }
        this.UpdateElements();
    }
    public void textChanged(){
        if (allowMask == false){
            allowMask = true;
            return;
        }

        if (PhoneNumberView.getText().length() < 1){
            return;

        }
        char[] textChars = PhoneNumberView.getText().toString().toCharArray();
        if (textChars[textChars.length - 1] == ' ' || textChars[textChars.length - 1] == '-'){
            return;
        }

        ArrayList<String> ints = new ArrayList<>();
        ArrayList<String> intFromField = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ints.add(i + "");
        }
        for (int i = 0; i < textChars.length; i++){
            for (int y = 0; y < ints.size(); y++){
                if ((textChars[i] + "").equals(ints.get(y))){
                    intFromField.add(ints.get(y));
                }

            }
        }
        char[] maskChars = wp_eCountries.pattern.toCharArray();
        int indexInt = 0;
        String returnedString = "";
        for (int i = 0; i < maskChars.length; i++) {
            if (indexInt >= intFromField.size()){
                break;
            }
            if (maskChars[i] == 'X'){
                returnedString += intFromField.get(indexInt);
                indexInt += 1;
            }else{
                returnedString += maskChars[i];
            }
        }
        allowMask = false;
        PhoneNumberView.setText(returnedString);
        PhoneNumberView.setSelection(returnedString.length());

    }
}

class WPmask_eCountries {
    public String shortName;
    public String code;
    public Integer len;
    public String name;
    public String pattern;
    public Bitmap flagBitmap;
    public JSONArray countries = new JSONArray();
    public JSONArray countries_all = new JSONArray();

    public JSONObject country;
    public Context context;
    public WPmask_eCountries(Context context,String shortName,ArrayList<String> AcceptCountries,ArrayList<String> PriorityCountries){
        this.context=context;
        try {
            JSONArray countries_tmp = new JSONArray("[" +
                    "{\"shortName\":\"bl\",\"code\":\"375\",\"len\":12,\"name\":\"Belarus (Беларусь)\",\"pattern\":\"XX XXX-XX-XX\"}," +
                    "{\"shortName\":\"ee\",\"code\":\"372\",\"len\":10,\"name\":\"Estonia (Eesti)\",\"pattern\":\"XXX-XX-XX\"}," +
                    "{\"shortName\":\"int\",\"code\":\"88239\",\"len\":15,\"name\":\"International Networks\",\"pattern\":\"XXX XXX-XX-XX\"}," +
                    "{\"shortName\":\"il\",\"code\":\"972\",\"len\":12,\"name\":\"Israel (\u202Bישראל\u202C\u200E)\",\"pattern\":\"XX XXX-XXXX\"}," +
                    "{\"shortName\":\"pl\",\"code\":\"48\",\"len\":11,\"name\":\"Poland (Polska)\",\"pattern\":\"XX XXX-XX-XX\"}," +
                    "{\"shortName\":\"ru\",\"code\":\"7\",\"len\":11,\"name\":\"Russia (Россия)\",\"pattern\":\"XXX XXX-XX-XX\"}," +
                    "{\"shortName\":\"tr\",\"code\":\"90\",\"len\":12,\"name\":\"Turkey (Türkiye)\",\"pattern\":\"XXX XXX-XX-XX\"}," +
                    "{\"shortName\":\"ua\",\"code\":\"380\",\"len\":12,\"name\":\"Ukraine (Україна)\",\"pattern\":\"XX XXX-XX-XX\"}" +
                    "]");
            countries_all = countries_tmp;
            if(PriorityCountries.size() > 0){
                for(int i =0;i < countries_tmp.length();i++) {
                    JSONObject CountryTmp = countries_tmp.getJSONObject(i);
                    if (PriorityCountries.contains(CountryTmp.getString("shortName"))){
                        countries.put(countries_tmp.getString(i));
                    }
                }
            }
            if(AcceptCountries.size() > 0){
                for(int i =0;i < countries_tmp.length();i++){
                    JSONObject CountryTmp = countries_tmp.getJSONObject(i);
                    if(PriorityCountries.size() > 0) {
                        if (!PriorityCountries.contains(CountryTmp.getString("shortName")) && AcceptCountries.contains(CountryTmp.getString("shortName"))) {
                            countries.put(countries_tmp.getString(i));
                        }
                    }else{
                        if (AcceptCountries.contains(CountryTmp.getString("shortName"))) {
                            countries.put(countries_tmp.getString(i));
                        }
                    }
                }
            }else{
                for(int i =0;i < countries_tmp.length();i++){
                    JSONObject CountryTmp = countries_tmp.getJSONObject(i);
                    if(PriorityCountries.size() > 0) {
                        if (!PriorityCountries.contains(CountryTmp.getString("shortName"))) {
                            countries.put(countries_tmp.getString(i));
                        }
                    }else{
                        countries.put(countries_tmp.getString(i));
                    }
                }
            }

        }catch (JSONException e){
            Log.v("###",e.getMessage().toString());
        }



        this.setCountry(shortName);
    }
    public void setCountry(String shortName){
        try {
            country = getCountry(shortName);
            this.code = country.getString("code");
            this.shortName = country.getString("shortName");
            this.len = country.getInt("len");
            this.name = country.getString("name");
            this.pattern = country.getString("pattern");
        }catch (JSONException e){
            Log.v("###",e.getMessage().toString());
        }
    }
    public JSONObject getCountry(String shortName){
        JSONObject CountryRes = new JSONObject();
        for (int i=0; i < countries_all.length(); i++) {
            try {
                JSONObject CountryTmp = new JSONObject(countries_all.getString(i));

                if(CountryTmp.getString("shortName").equals(shortName)){
                    CountryRes=CountryTmp;
                    break;
                }
            }catch (JSONException e){
                Log.v("###",e.getMessage().toString());
            }
        }
        return CountryRes;
    }
    public Drawable getCountryDrawable(String shortName){
        if(shortName == null){
            shortName=this.shortName;
        }
        switch (shortName){
            case "ua":
//                return getDrawable(context.getResources().getDrawable(R.drawable.wpmask_ua));
                return context.getResources().getDrawable(R.drawable.wpmask_ua);
            case "bl":
                return context.getResources().getDrawable(R.drawable.wpmask_bl);
            case "ee":
                return context.getResources().getDrawable(R.drawable.wpmask_ee);
            case "int":
                return context.getResources().getDrawable(R.drawable.wpmask_int);
            case "pl":
                return context.getResources().getDrawable(R.drawable.wpmask_pl);
            case "il":
                return context.getResources().getDrawable(R.drawable.wpmask_il);
            case "ru":
                return context.getResources().getDrawable(R.drawable.wpmask_ru);
            case "tr":
                return context.getResources().getDrawable(R.drawable.wpmask_tr);
            default:
                return context.getResources().getDrawable(R.drawable.wpmask_ua);
        }
    }
}

class WPmask_eCountriesAdapter extends RecyclerView.Adapter<WPmask_eCountriesAdapter.RecViewHolder> {
    public Context mContext;
    public JSONArray mCountries;
    public WPmask_eCountries WPmask_eCountries;

    //public String LogTag ="###";
    public LinearLayout MenuCountry_item;
    public LinearLayout MenuCountry_row;
    public LinearLayout MenuCountry_border;

    public ImageView MenuCountry_item_flag;
    public TextView MenuCountry_item_code;
    public TextView MenuCountry_item_country_name;

    public WPmask_eCountriesAdapter (Context context, WPmask_eCountries wPmask_eCountries){
        mContext = context;
        mCountries = wPmask_eCountries.countries;
        WPmask_eCountries = wPmask_eCountries;
    }
    public OnSetCounty onSetCounty;
    public void Register_onSetCounty(OnSetCounty onSetCounty){ this.onSetCounty = onSetCounty; }
    public interface OnSetCounty{ void onSetCounty(int position);}

    @NonNull
    @Override
    public RecViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        MenuCountry_item = new LinearLayout(mContext);
        MenuCountry_item.setOrientation(LinearLayout.HORIZONTAL);
        MenuCountry_item.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, WPmask.get_pixels(mContext,50)));
        MenuCountry_item.setGravity(Gravity.CENTER);

        MenuCountry_row = new LinearLayout(mContext);
        MenuCountry_row.setOrientation(LinearLayout.VERTICAL);
        MenuCountry_row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, WPmask.get_pixels(mContext,50)));
        MenuCountry_row.setGravity(Gravity.CENTER);

        MenuCountry_border = new LinearLayout(mContext);
        MenuCountry_border.setOrientation(LinearLayout.VERTICAL);
        MenuCountry_border.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, WPmask.get_pixels(mContext,1)));
        MenuCountry_border.setBackgroundColor(Color.BLACK);

        MenuCountry_item_flag = new ImageView(mContext);
        MenuCountry_item_flag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,WPmask.get_pixels(mContext,30),2.0f));
        MenuCountry_item_code = new TextView(mContext);
        MenuCountry_item_code.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,2.0f));
        MenuCountry_item_country_name = new TextView(mContext);
        MenuCountry_item_country_name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1.0f));
        MenuCountry_item_code.setTextSize(WPmask.get_pixels(mContext,5));
        MenuCountry_item_country_name.setTextSize(WPmask.get_pixels(mContext,5));

        MenuCountry_item.addView(MenuCountry_item_flag);
        MenuCountry_item.addView(MenuCountry_item_code);
        MenuCountry_item.addView(MenuCountry_item_country_name);
        MenuCountry_row.addView(MenuCountry_item);
        MenuCountry_row.addView(MenuCountry_border);
        return new RecViewHolder(MenuCountry_row,MenuCountry_item_flag,MenuCountry_item_code,MenuCountry_item_country_name);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, final int i) {
        try {
            JSONObject CountryTmp = new JSONObject(mCountries.getString(i));
            String pCode = "+" + CountryTmp.getString("code");
            holder.mCode.setText(pCode);
            holder.mCountry_name.setText(CountryTmp.getString("name"));
            holder.mFlag.setImageDrawable(WPmask_eCountries.getCountryDrawable(CountryTmp.getString("shortName")));
            holder.mitem.setOnClickListener(v -> onSetCounty.onSetCounty(i));
        }catch (JSONException e){
            Log.v("###",e.getMessage().toString());
        }

    }

    @Override
    public int getItemCount() {
        return  mCountries.length();
    }

    public static class RecViewHolder extends RecyclerView.ViewHolder{
        public TextView mCountry_name;
        public TextView mCode;
        public ImageView mFlag;
        public LinearLayout mitem;

        public RecViewHolder(LinearLayout item,ImageView flag,TextView code, TextView country_name){
            super(item);
            mitem = item;
            mFlag = flag;
            mCode = code;
            mCountry_name = country_name;
        }
    }
}