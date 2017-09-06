package com.mcafeweb.RecyclerViews;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.mcafeweb.BlogActivity;
import com.mcafeweb.utils.DBHelper;
import com.mcafeweb.utils.Helper;
import com.mcafeweb.Models.BlogModel;
import com.mcafeweb.R;
import com.mcafeweb.library.LinkSourceContent;
import com.mcafeweb.library.LinkViewCallback;
import com.mcafeweb.library.TextCrawler;
import com.mcafeweb.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Balpreet on 17-Mar-17.
 */

public class RecyclerViewAdapterBlog extends RecyclerView.Adapter<RecyclerViewAdapterBlog.MyViewHolder> implements VolleyHelper.VolleyResponse {
    private List<BlogModel> BlogModelList;

    final String TAG = "Recycler View Blog";

    Helper helper;
    //Context context;
    AppCompatActivity appCompatActivity;

    DBHelper dbHelper;
    SQLiteDatabase db;
    VolleyHelper volleyHelper;
    String parentName;
    List<AsyncTask> tasks;

    public RecyclerViewAdapterBlog(List<BlogModel> blogModelList,AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
        BlogModelList = blogModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_blogs, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view, new OnLikeListener(), new OnShareListener(), new OnViewListener(), new OnLongClickListener());
        //context = parent.getContext();
        helper = new Helper(appCompatActivity);
        setupDatabase();
        parentName = parent.toString();
        String[] intermediate = parentName.split("id/");
        String s = intermediate[1];
        Log.v(TAG, "Parent Name : " + s);
        tasks = new ArrayList<>();
        volleyHelper = new VolleyHelper(this,appCompatActivity);
        return myViewHolder;
    }

    private void setupDatabase() {
        dbHelper = new DBHelper(appCompatActivity);
        db = appCompatActivity.getApplicationContext().openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper.onCreate(db);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        final BlogModel blog_Model = BlogModelList.get(position);
        holder.position = position;
        holder.onLikeListener.updatePosition(position);
        holder.onShareListener.updatePosition(position);
        holder.onViewListener.updatePosition(position);
        holder.onLongClickListener.updatePosition(position);

        if(blog_Model.getTitle().equals("") || blog_Model.getTitle().equals(null))
        {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.mainLayout.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.mainLayout.setVisibility(View.VISIBLE);
        }

        holder.textViewSharerName.setText(helper.getStringFromDate(blog_Model.getBlogSharedDate()) + " by " + blog_Model.getSharerName());
        //holder.textViewSharedOn.setText("at " +
        holder.textViewTitle.setText(blog_Model.getTitle());
        holder.textViewBriefDescription.setText(blog_Model.getBlogBrief());
        holder.textViewCanonicalURL.setText(blog_Model.getCanonicalURL());
        holder.Image.setImageBitmap(blog_Model.getBlogImage());

        holder.textViewLikes.setText(helper.getStringNumeric(blog_Model.getBlogLikes()));
        holder.textViewShares.setText(helper.getStringNumeric(blog_Model.getBlogShares()));
        holder.textViewViews.setText(helper.getStringNumeric(blog_Model.getBlogViews()));
    }

    private void performLike(int position) {


        String url = helper.baseURL + "likeBlog.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("blogid", BlogModelList.get(position).getBlogID() + "");
        params.put("userid", dbHelper.getUserID() + "");
        volleyHelper.makeStringRequest(url, "Like_Blog", params);

    }

    private void performShare(int position) {

        Log.v(TAG, "Share Position in function = " + position);

        String url = helper.baseURL + "shareBlog.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("blogid", BlogModelList.get(position).getBlogID() + "");
        params.put("userid", dbHelper.getUserID() + "");
        volleyHelper.makeStringRequest(url,"Share_Blog_"+position,params);


        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, BlogModelList.get(position).getBlogUrl());
        sendIntent.setType("text/plain");
        appCompatActivity.startActivity(sendIntent);
    }

    private void performView(int position) {
        RecyclerViewAdapterBlog.this.notifyItemChanged(position);

        BlogModel blogModel = BlogModelList.get(position);


        String url = helper.baseURL + "viewBlog.php5";
        Map<String, String> params = new HashMap<String, String>();
        params.put("blogid", BlogModelList.get(position).getBlogID() + "");
        params.put("userid", dbHelper.getUserID() + "");
        volleyHelper.makeStringRequest(url,"View_Blog",params);

        Intent intent = new Intent(appCompatActivity, BlogActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("blogid", blogModel.getBlogID());
        bundle.putString("url", blogModel.getBlogUrl());
        bundle.putString("canonicalURL", blogModel.getCanonicalURL());
        bundle.putString("title", blogModel.getTitle());
        bundle.putInt("contributorid", blogModel.getBlogSharedByUser());
        bundle.putInt("likes", blogModel.getBlogLikes());
        bundle.putInt("views", blogModel.getBlogViews());
        bundle.putInt("shares", blogModel.getBlogShares());
        bundle.putString("brief", blogModel.getBlogBrief());
        bundle.putString("image", helper.getStringFromBitmap(blogModel.getBlogImage()));
        bundle.putInt("category", blogModel.getBlogCategory());
        bundle.putString("date", blogModel.getBlogSharedDate());

        intent.putExtra("bundle", bundle);
        appCompatActivity.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return BlogModelList == null ? 0 : BlogModelList.size();
    }

    private BlogModel getBlogModel(int blogid) {
        for (BlogModel model : BlogModelList
                ) {
            if (model.getBlogID() == blogid) {
                return model;
            }
        }
        return null;
    }

    private void updateBlogRow(BlogModel model) {
        if (parentName.contains("recycler_view_blogs")) {
            //dbHelper.updateBlog( model, DBHelper.TABLE_BLOG);
        } else if (parentName.contains("recycler_view_fav_blogs")) {
            //dbHelper.updateBlog( model, DBHelper.TABLE_FAV_BLOG_LIST_CONTENT);
        }
    }

    @Override
    public void onResponse(String result) {
        if (result == null || result.equals("")) {
            Log.v(TAG, "Result is Null");
            Toast.makeText(appCompatActivity, "Please Try again after some time", Toast.LENGTH_SHORT).show();
        } else {
            Log.v(TAG, "Result is Not Null");
            JSONObject json = helper.getJson(result);
            try {
                if (json.getString(helper.ACTION).toLowerCase().equals("trying to perform like")) {
                    if (json.getString("perform_like_result").equals(helper.SUCCESS)) {
                        int blogid = json.getInt("blog_id");

                        BlogModel model = getBlogModel(blogid);
                        model.setBlogLikes(json.getInt("new_likes"));

                        updateBlogRow(model);
                    } else {
                        int blogid = json.getInt("blog_id");
                        for (int i = 0; i < BlogModelList.size(); i++) {
                            if (blogid == BlogModelList.get(i).getBlogID()) {
                                BlogModel model = getBlogModel(blogid);
                                model.setBlogLikes(model.getBlogLikes() - 1);
                                updateBlogRow(model);
                            }
                        }
                    }

                } else if (json.getString(helper.ACTION).toLowerCase().equals("trying to perform share")) {
                    if (json.getString("perform_share_result").equals(helper.SUCCESS)) {
                        int blogid = json.getInt("blog_id");

                        BlogModel model = getBlogModel(blogid);
                        model.setBlogShares(json.getInt("new_shares"));
                        updateBlogRow(model);
                    }
                } else if (json.getString(helper.ACTION.toLowerCase()).equals("trying to perform view")) {
                    if (json.getString("perform_view_result").equals(helper.SUCCESS)) {
                        int blogid = json.getInt("blog_id");

                        BlogModel model = getBlogModel(blogid);
                        model.setBlogViews(json.getInt("new_views"));
                        updateBlogRow(model);
                        //RecyclerViewAdapterBlog.this.notifyDataSetChanged();
                    }
                }
            } catch (JSONException jse) {
                Toast.makeText(appCompatActivity, "Please Try again after some time", Toast.LENGTH_SHORT).show();
            }
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
        try {
            final String jsonString = new String(result.data,
                    HttpHeaderParser.parseCharset(result.headers));
            JSONObject json = new JSONObject(jsonString);

        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(VolleyError error) {

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        private LinearLayout mainLayout;
        private TextView textViewTitle;
        private TextView textViewSharerName;
        //private TextView textViewSharedOn;
        private TextView textViewBriefDescription;
        private TextView textViewCanonicalURL;
        private ImageView Image;

        private AppCompatImageButton buttonLikeBlog;
        private AppCompatImageButton buttonShareBlog;
        private AppCompatImageButton buttonViewBlog;

        private TextView textViewLikes;
        private TextView textViewShares;
        private TextView textViewViews;

        private OnLikeListener onLikeListener;
        private OnShareListener onShareListener;
        private OnViewListener onViewListener;
        private OnLongClickListener onLongClickListener;

        private View view;
        private int position;

        Bitmap[] currentImageSet = new Bitmap[1];
        LinkViewCallback callback = new LinkViewCallback() {

            @Override
            public void onBeforeLoading() {

                currentImageSet = null;
            }

            @Override
            public void onAfterLoading(final LinkSourceContent linkSourceContent, boolean isNull) {

                if (isNull || linkSourceContent.getFinalUrl().equals("")) {

                } else {
                    currentImageSet = new Bitmap[linkSourceContent.getImages().size()];
                    Log.v(TAG,"Current Image Set Size = " + linkSourceContent.getTitle().toString() + linkSourceContent.getImages().size() );

                    if (linkSourceContent.getImages().size() > 0) {
                        UrlImageViewHelper.setUrlDrawable(Image, linkSourceContent.getImages().get(0), new UrlImageViewCallback() {

                            @Override
                            public void onLoaded(ImageView imageView,
                                                 Bitmap loadedBitmap, String url,
                                                 boolean loadedFromCache) {
                                if (loadedBitmap != null) {
                                    currentImageSet[0] = loadedBitmap;
                                    BlogModelList.get(position).setBlogImage(loadedBitmap);
                                    updateBlogRow(BlogModelList.get(position));
                                    Log.v(TAG, "Image Loaded at : " + position);
                                    RecyclerViewAdapterBlog.this.notifyItemChanged(position);

                                }
                            }
                        });

                    } else {

                    }

                    if (linkSourceContent.getTitle().equals(""))
                        linkSourceContent.setTitle(view.getContext().getString(R.string.enter_title));
                    if (linkSourceContent.getDescription().equals(""))
                        linkSourceContent
                                .setDescription(view.getContext().getString(R.string.enter_description));

                    BlogModelList.get(position).setTitle(linkSourceContent.getTitle());
                    BlogModelList.get(position).setCanonicalURL(linkSourceContent.getCannonicalUrl());
                    BlogModelList.get(position).setBlogBrief(linkSourceContent.getDescription());
                    Log.v(TAG, "Content Loaded at : " + position);
                }
            }
        };


        private MyViewHolder(View itemView, OnLikeListener onLikeListener, OnShareListener onShareListener, OnViewListener onViewListener, OnLongClickListener onLongClickListener) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar_blog);
            mainLayout = (LinearLayout) itemView.findViewById(R.id.main_layout_blog);
            textViewSharerName = (TextView) itemView.findViewById(R.id.text_view_blog_sharer);
            //textViewSharedOn = (TextView) itemView.findViewById(R.id.text_view_blog_shared_on);

            textViewTitle = (TextView) itemView.findViewById(R.id.text_view_blog_title);
            textViewBriefDescription = (TextView) itemView.findViewById(R.id.text_view_blog_description);
            textViewCanonicalURL = (TextView) itemView.findViewById(R.id.blog_canonical_url);
            Image = (ImageView) itemView.findViewById(R.id.imageViewBlog);

            buttonLikeBlog = (AppCompatImageButton) itemView.findViewById(R.id.blog_Like);
            buttonShareBlog = (AppCompatImageButton) itemView.findViewById(R.id.blog_Share);
            buttonViewBlog = (AppCompatImageButton) itemView.findViewById(R.id.blog_views);

            textViewLikes = (TextView) itemView.findViewById(R.id.textview_blog_likes);
            textViewShares = (TextView) itemView.findViewById(R.id.textview_blog_shares);
            textViewViews = (TextView) itemView.findViewById(R.id.textview_blog_views);

            this.onLikeListener = onLikeListener;
            this.onShareListener = onShareListener;
            this.onViewListener = onViewListener;

            this.textViewSharerName.setOnClickListener(this.onViewListener);
//            this.textViewSharedOn.setOnClickListener(this.onViewListener);
            this.textViewTitle.setOnClickListener(this.onViewListener);
            this.textViewBriefDescription.setOnClickListener(this.onViewListener);
            this.textViewCanonicalURL.setOnClickListener(this.onViewListener);
            this.Image.setOnClickListener(this.onViewListener);

            this.textViewLikes.setOnClickListener(this.onLikeListener);
            this.textViewShares.setOnClickListener(this.onShareListener);
            this.textViewViews.setOnClickListener(this.onViewListener);

            this.buttonLikeBlog.setOnClickListener(this.onLikeListener);
            this.buttonShareBlog.setOnClickListener(this.onShareListener);
            this.buttonViewBlog.setOnClickListener(this.onViewListener);
            this.onLongClickListener = onLongClickListener;

            this.view = itemView;


        }
    }

    class OnLikeListener implements View.OnClickListener {
        private int position;

        public void updatePosition(int position) {

            this.position = position;
        }

        @Override
        public void onClick(View view) {
            BlogModelList.get(position).setBlogLikes(BlogModelList.get(position).getBlogLikes() + 1);
            RecyclerViewAdapterBlog.this.notifyItemChanged(position);
            performLike(position);
        }
    }

    class OnShareListener implements View.OnClickListener {
        private int position;

        public void updatePosition(int position) {

            this.position = position;
        }

        @Override
        public void onClick(View view) {
            BlogModelList.get(position).setBlogShares(BlogModelList.get(position).getBlogShares() + 1);
            RecyclerViewAdapterBlog.this.notifyItemChanged(position);
            performShare(position);
        }
    }

    class OnViewListener implements View.OnClickListener {
        private int position;

        public void updatePosition(int position) {

            this.position = position;
        }

        @Override
        public void onClick(View view) {
            BlogModelList.get(position).setBlogViews(BlogModelList.get(position).getBlogViews() + 1);
            RecyclerViewAdapterBlog.this.notifyItemChanged(position);
            performView(position);
        }
    }

    class OnLongClickListener implements View.OnLongClickListener {
        private int position;

        public void updatePosition(int position) {

            this.position = position;
        }

        @Override
        public boolean onLongClick(View view) {
            Log.d(TAG, "Detected Long Click");
            return false;
        }
    }
}

