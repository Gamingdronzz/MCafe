package com.mcafeweb;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mcafeweb.Models.InterestModel;
import com.mcafeweb.library.LinkViewCallback;
import com.mcafeweb.library.LinkSourceContent;
import com.mcafeweb.library.TextCrawler;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.utils.VolleyHelper;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CurateBlog extends AppCompatActivity implements VolleyHelper.VolleyResponse {

    private FloatLabeledEditText EditTextBlogLink;
    private TextView textViewBlogTitle;

    private Button buttonCurateBlog;

    private Button buttonGeneratePreview;

    private TextCrawler textCrawler;
    private ViewGroup dropPreview;

    private Bitmap[] currentImageSet;

    private Spinner BlogCategory;
    String selectedCategoryName;
    Helper helper;

    NestedScrollView CreateBlogLayout;
    LinearLayout progressLayout;

    DBHelper dbHelper;
    SQLiteDatabase db;

    String TAG = "CurateBlog";
    VolleyHelper volleyHelper;

    String description = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        getSupportActionBar().setTitle("Curate Blog");
        setupDatabase();

        bindViews();
        showProgressLayout(false);


        List<String> Categories = new ArrayList<>();
        InterestModel[] interestList = dbHelper.getInterestList();

        for (int i = 0; i < interestList.length; i++) {
            Categories.add(interestList[i].getInterestName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Categories);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        BlogCategory.setAdapter(dataAdapter);
        initSubmitButton();

        buttonCurateBlog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                curateBlog();
            }
        });

        dbHelper.printTable(DBHelper.TABLE_INTERESTS);

    }

    private void bindViews() {
        helper = new Helper(this);
        volleyHelper = new VolleyHelper(this,this);
        EditTextBlogLink = (FloatLabeledEditText) findViewById(R.id.create_Blog_Link);
        textViewBlogTitle = (TextView) findViewById(R.id.text_view_blog_title);
        BlogCategory = (Spinner) findViewById(R.id.spinner_Blog_Category);

        CreateBlogLayout = (NestedScrollView) findViewById(R.id.LinearCreateBlogLayout);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
        buttonGeneratePreview = (Button) findViewById(R.id.generate_preview);

        /** Where the previews will be dropped */
        dropPreview = (ViewGroup) findViewById(R.id.drop_preview);

        /** Where the previews will be dropped */

        textCrawler = new TextCrawler();
        buttonCurateBlog = (Button) findViewById(R.id.curate_blog);

    }


    /**
     * Adding listener to the button
     */
    public void initSubmitButton() {
        buttonGeneratePreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                releasePreviewArea();
                textCrawler.makePreview(callback, EditTextBlogLink.getEditText().getText().toString());
            }
        });
    }

    /** Callback to update your view. Totally customizable. */
    /** onBeforeLoading() will be called before the crawling. onAfterLoading() after. */
    /**
     * You can customize this to update your view
     */
    private LinkViewCallback callback = new LinkViewCallback() {
        /**
         * This view is used to be updated or added in the layout after getting
         * the result
         */
        private View mainView;
        private LinearLayout linearLayout;
        private View loading;
        private ImageView imageView;

        @Override
        public void onBeforeLoading() {
            hideSoftKeyboard();

            currentImageSet = null;


            /** Inflating the preview layout */
            mainView = getLayoutInflater().inflate(R.layout.main_view, null);

            linearLayout = (LinearLayout) mainView.findViewById(R.id.external);

            /**
             * Inflating a loading layout into MainActivity View LinearLayout
             */


            loading = getLayoutInflater().inflate(R.layout.loading,
                    linearLayout);

            dropPreview.addView(mainView);
        }

        @Override
        public void onAfterLoading(final LinkSourceContent linkSourceContent, boolean isNull) {

            /** Removing the loading layout */
            linearLayout.removeAllViews();

            if (isNull || linkSourceContent.getFinalUrl().equals("")) {
                /**
                 * Inflating the content layout into MainActivity View LinearLayout
                 */
                View failed = getLayoutInflater().inflate(R.layout.failed,
                        linearLayout);

                TextView titleTextView = (TextView) failed
                        .findViewById(R.id.text);
                titleTextView.setText(getString(R.string.failed_preview) + "\n"
                        + linkSourceContent.getFinalUrl());

                failed.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        releasePreviewArea();
                    }
                });

            } else {
//                postButton.setVisibility(View.VISIBLE);

                currentImageSet = new Bitmap[linkSourceContent.getImages().size()];

                /**
                 * Inflating the content layout into MainActivity View LinearLayout
                 */
                final View content = getLayoutInflater().inflate(
                        R.layout.preview_content, linearLayout);

                /** Fullfilling the content layout */
                final LinearLayout infoWrap = (LinearLayout) content
                        .findViewById(R.id.info_wrap);
                final LinearLayout titleWrap = (LinearLayout) infoWrap
                        .findViewById(R.id.title_wrap);

                final ImageView imageSet = (ImageView) content
                        .findViewById(R.id.image_post_set);
                final TextView titleTextView = (TextView) titleWrap
                        .findViewById(R.id.title);
                final TextView titleEditText = (TextView) titleWrap
                        .findViewById(R.id.input_title);
                final TextView urlTextView = (TextView) content
                        .findViewById(R.id.url);
                final TextView descriptionTextView = (TextView) content
                        .findViewById(R.id.description);
                final TextView descriptionEditText = (TextView) content
                        .findViewById(R.id.input_description);

                titleTextView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        titleTextView.setVisibility(View.GONE);

                        titleEditText.setText(TextCrawler
                                .extendedTrim(titleTextView.getText()
                                        .toString()));
                        titleEditText.setVisibility(View.VISIBLE);
                    }
                });
                titleEditText
                        .setOnEditorActionListener(new OnEditorActionListener() {

                            @Override
                            public boolean onEditorAction(TextView arg0,
                                                          int arg1, KeyEvent arg2) {

                                if (arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                    titleEditText.setVisibility(View.GONE);

                                    titleTextView.setVisibility(View.VISIBLE);

                                    hideSoftKeyboard();
                                }

                                return false;
                            }
                        });
                descriptionTextView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        descriptionTextView.setVisibility(View.GONE);

                        descriptionEditText.setText(TextCrawler
                                .extendedTrim(descriptionTextView.getText()
                                        .toString()));
                        descriptionEditText.setVisibility(View.VISIBLE);
                    }
                });
                descriptionEditText
                        .setOnEditorActionListener(new OnEditorActionListener() {

                            @Override
                            public boolean onEditorAction(TextView arg0,
                                                          int arg1, KeyEvent arg2) {

                                if (arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                    descriptionEditText
                                            .setVisibility(View.GONE);
                                    descriptionTextView
                                            .setVisibility(View.VISIBLE);

                                    hideSoftKeyboard();
                                }

                                return false;
                            }
                        });


                if (linkSourceContent.getImages().size() > 0) {
Log.v(TAG,"Image Size : " + linkSourceContent.getImages().size());

                    UrlImageViewHelper.setUrlDrawable(imageSet, linkSourceContent
                            .getImages().get(0), new UrlImageViewCallback() {

                        @Override
                        public void onLoaded(ImageView imageView,
                                             Bitmap loadedBitmap, String url,
                                             boolean loadedFromCache) {
                            if (loadedBitmap != null) {
                                //currentImage = loadedBitmap;
                                currentImageSet[0] = loadedBitmap;
                            }
                        }
                    });

                } else {
                    showHideImage(imageSet, infoWrap, false);
                }

                if (linkSourceContent.getTitle().equals(""))
                    linkSourceContent.setTitle("");
                if (linkSourceContent.getDescription().equals(""))
                    linkSourceContent
                            .setDescription("No Description Available" );

                titleTextView.setText(linkSourceContent.getTitle());
                textViewBlogTitle.setText(linkSourceContent.getTitle());
                urlTextView.setText(linkSourceContent.getCannonicalUrl());
                descriptionTextView.setText(linkSourceContent.getDescription());
                description = linkSourceContent.getDescription();
                Log.d(TAG,"Description = " + description);
                if(description.length() > 120)
                {
                    description = description.substring(0,119);
                }

            }
        }
    };

    private void showHideImage(View image, View parent, boolean show) {
        if (show) {
            image.setVisibility(View.VISIBLE);
            parent.setPadding(5, 5, 5, 5);
            parent.setLayoutParams(new LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 2f));
        } else {
            image.setVisibility(View.GONE);
            parent.setPadding(5, 5, 5, 5);
            parent.setLayoutParams(new LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 3f));
        }
    }

    /**
     * Hide keyboard
     */
    private void hideSoftKeyboard() {
        hideSoftKeyboard(EditTextBlogLink.getEditText());
    }

    private void hideSoftKeyboard(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager
                .hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void releasePreviewArea() {
        buttonGeneratePreview.setEnabled(true);
        dropPreview.removeAllViews();
    }

    public void showProgressLayout(boolean value) {
        if (value) {
            CreateBlogLayout.setVisibility(View.INVISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            CreateBlogLayout.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.INVISIBLE);
        }


    }

    private void setupDatabase() {
        dbHelper = new DBHelper(this);
        db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }

    void curateBlog() {
        if (checkInput()) {
            selectedCategoryName = BlogCategory.getSelectedItem().toString();
            int selectedCategoryID = dbHelper.getInterestID(selectedCategoryName);
            Log.v(TAG,"Description : " + description);
            showProgressLayout(true);
            String link = EditTextBlogLink.getEditText().getText().toString();
            String title = textViewBlogTitle.getText().toString();


            String url = helper.baseURL + "curateBlog.php5";
            Map<String, String> params = new HashMap<String, String>();
            params.put("blog_title", title);
            params.put("blog_link", link);
            params.put("blog_canonical_link", link);
            params.put("blog_image",helper.getStringFromBitmap(currentImageSet[0]));
            params.put("blog_shared_by",dbHelper.getUserID() + "");
            params.put("blog_brief",description);
            params.put("blog_category",selectedCategoryID+"");
            volleyHelper.makeStringRequest(url,"Curate_Blog",params);
        } else {
            Toast.makeText(this,"Please Generate Preview First",Toast.LENGTH_SHORT).show();
        }
    }


    boolean checkInput() {
        String title = textViewBlogTitle.getText().toString();
        textViewBlogTitle.setText(title);

        String link = EditTextBlogLink.getEditText().getText().toString();
        link = link.replaceAll("\\s+", "");
        EditTextBlogLink.getEditText().setText(link);

        if (link.length() == 0) {
            Toast.makeText(this, "Enter Blog Link", Toast.LENGTH_SHORT).show();
            EditTextBlogLink.getEditText().requestFocus();
            return false;
        }

        if (title.length() == 0) {
            return false;
        }
        if(currentImageSet == null)
        {
            return  false;
        }

        return true;
    }

    @Override
    public void onResponse(String result) {
        showProgressLayout(false);
        Helper helper = new Helper(this);
        JSONObject json = helper.getJson(result);
        try {
            if (json.getString(helper.CREATE_BLOG_RESULT).equals(helper.SUCCESS)) {
                Toast.makeText(this, "Successfully Created Blog", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Unable to Create Blog\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException jse) {
            Toast.makeText(this, "Unable to Create Blog\nPlease Try again after some time", Toast.LENGTH_LONG).show();
            jse.printStackTrace();
        }
    }

    @Override
    public void onResponse(JSONObject result) {

    }

    @Override
    public void onResponse(JSONArray result) {

    }

    @Override
    public void onResponse(NetworkResponse result) {

    }

    @Override
    public void onError(VolleyError error) {
        showProgressLayout(false);
    }
}