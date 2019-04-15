package com.aldkhel.aldkhel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aldkhel.aldkhel.models.Category;
import com.aldkhel.aldkhel.utils.Consts;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    private List<Long> expandableListParentIds;
    private HashMap<Long, List<Long>> expandableListDetailIds;

    private Map<String, List<Category>> categoriesMap;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Consts.APP_FONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_menu);

        expandableListView = findViewById(R.id.expandableListView);



//        expandableListDetail = loadFakeData();
//        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());



//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v,
//                                        int groupPosition, int childPosition, long id) {
//
//                Category category = new Category();
//                category.setId(expandableListDetailIds.get(groupPosition).get(childPosition));
//                category.setName(expandableListDetail.get(groupPosition).get(childPosition));
//
//
//
//                if (expandableListDetail.get(groupPosition).size() == 0) {
//                    Intent i = new Intent(MenuActivity.this, ProductsActivity.class);
//                    i.putExtra("url", Consts.API_URL + "show/products_new.php?category_id=" + category.getId());
//                    i.putExtra("category", category);
//                    startActivity(i);
//                } else {
//
//                    List<Category> categories = new ArrayList<>();
//
//                    for (int i=0;i<expandableListDetail.get(groupPosition).size();i++) {
//                        Category temp = new Category();
//                        temp.setId(expandableListDetailIds.get(groupPosition).get(childPosition));
//                        temp.setName(expandableListDetail.get(groupPosition).get(childPosition));
//
//                        categories.add(temp);
//                    }
//
//                    Intent i = new Intent(MenuActivity.this, CategoriesActivity.class);
//                    i.putExtra("category", category);
//                    i.putExtra("categories", (Parcelable) categories);
//                    startActivity(i);
//                }
//
//
//
//                return true;
//            }
//        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String title = expandableListTitle.get(groupPosition);

                Category category = new Category();
                category.setId(1);
                category.setName(title);



                if (categoriesMap.get(title).size() == 0) {

                    Intent i = new Intent(MenuActivity.this, ProductsActivity.class);
                    i.putExtra("url", Consts.API_URL + "show/products_new.php?category_id=" + category.getId());
                    i.putExtra("category", category);
                    startActivity(i);

                } else {
                    Intent i = new Intent(MenuActivity.this, CategoriesActivity.class);
                    i.putExtra("category", category);
                    i.putExtra("categories", (ArrayList<Category>) categoriesMap.get(title));
                    startActivity(i);
                }

                return true;
            }
        });

        getCategories();

    }

    @Override
    protected void onResume() {
        super.onResume();

        long id = PreferenceManager.getDefaultSharedPreferences(this)
                .getLong("id", 0);
        if (id > 0) {
            findViewById(R.id.bAccount).setVisibility(View.VISIBLE);
            findViewById(R.id.bLogin).setVisibility(View.GONE);
        } else {
            findViewById(R.id.bAccount).setVisibility(View.GONE);
            findViewById(R.id.bLogin).setVisibility(View.VISIBLE);
        }

    }

    public void onClick(View view) {

        int viewId = view.getId();

        if (viewId == R.id.bHome) {
            finish();
        }

        if (viewId == R.id.bAccount) {
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if (viewId == R.id.bLogin) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        if (viewId == R.id.bCart) {
            startActivity(new Intent(this, CartActivity.class));
        }

    }

    private void getCategories() {

        expandableListDetail = new HashMap<>();
        expandableListDetailIds = new HashMap<>();

        categoriesMap = new HashMap<>();

        AndroidNetworking.get(Consts.API_URL + "show/subcates.php")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {

                            for (int i=0;i<response.length();i++) {
                                List<String> names = new ArrayList<>();
                                List<Long> ids = new ArrayList<>();

                                List<Category> categories = new ArrayList<>();

                                JSONObject json = response.getJSONObject(i);
                                JSONArray subcategory = json.getJSONArray("subcategoryone");




                                for (int j=0;j<subcategory.length();j++) {
                                    JSONObject temp = subcategory.getJSONObject(j);
                                    names.add(temp.getString("name"));
                                    ids.add(temp.getLong("category_id"));


                                    ArrayList<Category> subc = new ArrayList<>();

                                    for (int c=0;c<temp.getJSONArray("subcategorytwo").length();c++) {
                                        JSONObject cc = temp.getJSONArray("subcategorytwo").getJSONObject(c);
                                        subc.add(Category.fromJson(cc));
                                    }


                                    Category category = Category.fromJson(temp);
                                    category.setSubCategories(subc);
                                    category.setJsonString(temp.getJSONArray("subcategorytwo").toString());
                                    categories.add(category);

                                }

                                categoriesMap.put(json.getString("name"), categories);

                                expandableListDetail.put(json.getString("name"), names);
                                expandableListDetailIds.put(json.getLong("category_id"), ids);
                            }

                            expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
                            expandableListAdapter = new CustomExpandableListAdapter(MenuActivity.this, expandableListTitle, expandableListDetail);
                            expandableListView.setAdapter(expandableListAdapter);

                            expandableListParentIds = new ArrayList<>(expandableListDetailIds.keySet());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        error.printStackTrace();
                        Toast.makeText(MenuActivity.this, R.string.connection_err, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private HashMap<String, List<String>> loadFakeData() {

        HashMap<String, List<String>> expandableListDetail = new HashMap<>();

        List<String> cricket = new ArrayList<>();
        cricket.add("شاورما");
        cricket.add("بيرقر");
        cricket.add("مندي");
        cricket.add("دجاج");

        List<String> football = new ArrayList<>();
        football.add("برتقال");
        football.add("منقة");
        football.add("جوافة");

        List<String> basketball = new ArrayList<>();
        basketball.add("بيتزا");
        basketball.add("فطيرة الهنا");

        expandableListDetail.put("لحمة", cricket);
        expandableListDetail.put("عصائر", football);
        expandableListDetail.put("فطائر", basketball);

        return expandableListDetail;

    }

    class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableListTitle;
        private HashMap<String, List<String>> expandableListDetail;

        CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                    HashMap<String, List<String>> expandableListDetail) {
            this.context = context;
            this.expandableListTitle = expandableListTitle;
            this.expandableListDetail = expandableListDetail;
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .get(expandedListPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String expandedListText = (String) getChild(listPosition, expandedListPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_list, null);
            }
            TextView expandedListTextView = convertView
                    .findViewById(R.id.expandedListItem);
            expandedListTextView.setText(expandedListText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .size();
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.expandableListTitle.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return this.expandableListTitle.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_list_header, null);
            }
            TextView listTitleTextView = (TextView) convertView
                    .findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }


}
