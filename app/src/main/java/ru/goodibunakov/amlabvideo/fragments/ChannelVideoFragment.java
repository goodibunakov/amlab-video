package ru.goodibunakov.amlabvideo.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marshalchen.ultimaterecyclerview.ItemTouchListenerAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.goodibunakov.amlabvideo.R;
import ru.goodibunakov.amlabvideo.adapter.AdapterList;
import ru.goodibunakov.amlabvideo.utils.Utils;

public class ChannelVideoFragment extends Fragment {

    private Unbinder unbinder;

    private static final String TAG = ChannelVideoFragment.class.getSimpleName();
    private static final String TAGS = "URL";

    private int videoType;
    private String channelId;

    private OnVideoSelectedListener callback;

    private AdapterList adapterList = null;

    private ArrayList<HashMap<String, String>> tempVideoData = new ArrayList<>();
    private ArrayList<HashMap<String, String>> videoData = new ArrayList<>();

    private String nextPageToken = "";
    private String videoIds = "";
    private String duration = "00:00";

    private boolean mIsStillLoading = true;

    private boolean isAppFirstLaunched = true;

    private boolean isFirstVideo = true;

    @BindView(R.id.ultimate_recycler_view)
    UltimateRecyclerView ultimateRecyclerView;
    @BindView(R.id.lblNoResult)
    TextView mLblNoResult;
    @BindView(R.id.lytRetry)
    LinearLayout mLytRetry;
    @BindView(R.id.prgLoading)
    ContentLoadingProgressBar prgLoading;
    @BindView(R.id.raisedRetry)
    AppCompatButton btnRetry;


    public interface OnVideoSelectedListener {
        void onVideoSelected(String ID);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_video, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();

        videoType = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(bundle).getString(Utils.TAG_VIDEO_TYPE)));
        channelId = bundle.getString(Utils.TAG_CHANNEL_ID);

        prgLoading.setVisibility(View.VISIBLE);

        isAppFirstLaunched = true;
        isFirstVideo = true;

        videoData = new ArrayList<>();

        adapterList = new AdapterList(getActivity(), videoData);
        ultimateRecyclerView.setAdapter(adapterList);
        ultimateRecyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        ultimateRecyclerView.setLayoutManager(linearLayoutManager);
//        ultimateRecyclerView.enableLoadmore();

        adapterList.setCustomLoadMoreView(LayoutInflater.from(getActivity())
                .inflate(R.layout.progressbar, null));

        ultimateRecyclerView.setOnLoadMoreListener((itemsCount, maxLastVisiblePosition) -> {
            if (mIsStillLoading) {
                mIsStillLoading = false;
                adapterList.setCustomLoadMoreView(LayoutInflater.from(getActivity())
                        .inflate(R.layout.progressbar, null));

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        getVideoData();
                    }
                }, 1000);
            } else {
                disableLoadmore();
            }
        });


        ItemTouchListenerAdapter itemTouchListenerAdapter =
                new ItemTouchListenerAdapter(ultimateRecyclerView.mRecyclerView,
                        new ItemTouchListenerAdapter.RecyclerViewOnItemClickListener() {
                            @Override
                            public void onItemClick(RecyclerView parent, View clickedView, int position) {
                                if (position < videoData.size()) {
                                    callback.onVideoSelected(videoData.get(position).get(Utils.KEY_VIDEO_ID));
                                }
                            }

                            @Override
                            public void onItemLongClick(RecyclerView recyclerView, View view, int i) {
                            }
                        });


        ultimateRecyclerView.mRecyclerView.addOnItemTouchListener(itemTouchListenerAdapter);

        getVideoData();

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (OnVideoSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnVideoSelectedListener");
        }
    }


    private void getVideoData() {

        videoIds = "";
        final String[] videoId = new String[1];

        String url;
        if (videoType == 2) {
            url = Utils.API_YOUTUBE + Utils.FUNCTION_PLAYLIST_ITEMS_YOUTUBE +
                    Utils.PARAM_PART_YOUTUBE + "snippet,id&" +
                    Utils.PARAM_FIELD_PLAYLIST_YOUTUBE + "&" +
                    Utils.PARAM_KEY_YOUTUBE + getResources().getString(R.string.youtube_apikey) + "&" +
                    Utils.PARAM_PLAYLIST_ID_YOUTUBE + channelId + "&" +
                    Utils.PARAM_PAGE_TOKEN_YOUTUBE + nextPageToken + "&" +
                    Utils.PARAM_MAX_RESULT_YOUTUBE + Utils.PARAM_RESULT_PER_PAGE;
        } else {
            url = Utils.API_YOUTUBE + Utils.FUNCTION_SEARCH_YOUTUBE +
                    Utils.PARAM_PART_YOUTUBE + "snippet,id&" + Utils.PARAM_ORDER_YOUTUBE + "&" +
                    Utils.PARAM_TYPE_YOUTUBE + "&" +
                    Utils.PARAM_FIELD_SEARCH_YOUTUBE + "&" +
                    Utils.PARAM_KEY_YOUTUBE + getResources().getString(R.string.youtube_apikey) + "&" +
                    Utils.PARAM_CHANNEL_ID_YOUTUBE + channelId + "&" +
                    Utils.PARAM_PAGE_TOKEN_YOUTUBE + nextPageToken + "&" +
                    Utils.PARAM_MAX_RESULT_YOUTUBE + Utils.PARAM_RESULT_PER_PAGE;
        }

        Log.d(TAGS, url);

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    JSONArray dataItemArray;
                    JSONObject itemIdObject, itemSnippetObject, itemSnippetThumbnailsObject,
                            itemSnippetResourceIdObject;

                    @Override
                    public void onResponse(JSONObject response) {
                        Activity activity = getActivity();
                        if (activity != null && isAdded()) {
                            try {
                                dataItemArray = response.getJSONArray(Utils.ARRAY_ITEMS);

                                if (dataItemArray.length() > 0) {
                                    haveResultView();
                                    for (int i = 0; i < dataItemArray.length(); i++) {
                                        HashMap<String, String> dataMap = new HashMap<>();

                                        JSONObject itemsObject = dataItemArray.getJSONObject(i);
                                        itemSnippetObject = itemsObject.
                                                getJSONObject(Utils.OBJECT_ITEMS_SNIPPET);

                                        if (videoType == 2) {
                                            itemSnippetResourceIdObject = itemSnippetObject.
                                                    getJSONObject(Utils.OBJECT_ITEMS_SNIPPET_RESOURCE_ID);
                                            dataMap.put(Utils.KEY_VIDEO_ID,
                                                    itemSnippetResourceIdObject.
                                                            getString(Utils.KEY_VIDEO_ID));
                                            videoId[0] = itemSnippetResourceIdObject.
                                                    getString(Utils.KEY_VIDEO_ID);

                                            videoIds = videoIds + itemSnippetResourceIdObject.
                                                    getString(Utils.KEY_VIDEO_ID) + ",";
                                        } else {
                                            itemIdObject = itemsObject.
                                                    getJSONObject(Utils.OBJECT_ITEMS_ID);
                                            dataMap.put(Utils.KEY_VIDEO_ID,
                                                    itemIdObject.getString(Utils.KEY_VIDEO_ID));
                                            videoId[0] = itemIdObject.getString(Utils.KEY_VIDEO_ID);

                                            videoIds = videoIds + itemIdObject.
                                                    getString(Utils.KEY_VIDEO_ID) + ",";
                                        }

                                        if (isFirstVideo && i == 0) {
                                            isFirstVideo = false;
                                            callback.onVideoSelected(videoId[0]);
                                        }

                                        dataMap.put(Utils.KEY_TITLE,
                                                itemSnippetObject.getString(Utils.KEY_TITLE));

                                        String formattedPublishedDate = Utils.formatPublishedDate(
                                                getActivity(),
                                                itemSnippetObject.getString(Utils.KEY_PUBLISHED_AT));

                                        dataMap.put(Utils.KEY_PUBLISHED_AT, formattedPublishedDate);

                                        itemSnippetThumbnailsObject = itemSnippetObject.
                                                getJSONObject(Utils.OBJECT_ITEMS_SNIPPET_THUMBNAILS);
                                        itemSnippetThumbnailsObject = itemSnippetThumbnailsObject.
                                                getJSONObject
                                                        (Utils.OBJECT_ITEMS_SNIPPET_THUMBNAILS_MEDIUM);
                                        dataMap.put(Utils.KEY_URL_THUMBNAILS,
                                                itemSnippetThumbnailsObject.getString
                                                        (Utils.KEY_URL_THUMBNAILS));

                                        tempVideoData.add(dataMap);
                                    }

                                    getDuration();

                                    if (dataItemArray.length() == Utils.PARAM_RESULT_PER_PAGE) {
                                        nextPageToken = response.getString(Utils.ARRAY_PAGE_TOKEN);

                                    } else {
                                        nextPageToken = "";
                                        disableLoadmore();
                                    }

                                    isAppFirstLaunched = false;

                                } else {
                                    if (isAppFirstLaunched &&
                                            adapterList.getAdapterItemCount() <= 0) {
                                        noResultView();
                                    }
                                    disableLoadmore();
                                }

                            } catch (JSONException e) {
                                Log.d(Utils.TAG_FANDROID + TAG, "JSON Parsing error: " +
                                        e.getMessage());
                                prgLoading.setVisibility(View.GONE);
                            }
                            prgLoading.setVisibility(View.GONE);
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Activity activity = getActivity();
                        if (activity != null && isAdded()) {
                            Log.d(Utils.TAG_FANDROID + TAG, "on Error Response: " + error.getMessage());
                            try {
                                String msgSnackBar;
                                if (error instanceof NoConnectionError) {
                                    msgSnackBar = getResources().getString(R.string.no_internet_connection);
                                } else {
                                    msgSnackBar = getResources().getString(R.string.response_error);
                                }

                                if (videoData.size() == 0) {
                                    retryView();

                                } else {
                                    adapterList.setCustomLoadMoreView(null);
                                    adapterList.notifyDataSetChanged();
                                }

                                Utils.showSnackBar(getActivity(), msgSnackBar);
                                prgLoading.setVisibility(View.GONE);

                            } catch (Exception e) {
                                Log.d(Utils.TAG_FANDROID + TAG, "failed catch volley " + e.toString());
                                prgLoading.setVisibility(View.GONE);
                            }
                        }
                    }
                }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(Utils.ARG_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).getRequestQueue().add(request);

    }

    private void getDuration() {
        String url = Utils.API_YOUTUBE + Utils.FUNCTION_VIDEO_YOUTUBE +
                Utils.PARAM_PART_YOUTUBE + "contentDetails&" +
                Utils.PARAM_FIELD_VIDEO_YOUTUBE + "&" +
                Utils.PARAM_KEY_YOUTUBE + getResources().getString(R.string.youtube_apikey) + "&" +
                Utils.PARAM_VIDEO_ID_YOUTUBE + videoIds;

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    JSONArray dataItemArrays;
                    JSONObject itemContentObject;

                    @Override
                    public void onResponse(JSONObject response) {
                        Activity activity = getActivity();
                        if (activity != null && isAdded()) {
                            try {
                                haveResultView();
                                dataItemArrays = response.getJSONArray(Utils.ARRAY_ITEMS);
                                if (dataItemArrays.length() > 0 && !tempVideoData.isEmpty()) {
                                    for (int i = 0; i < dataItemArrays.length(); i++) {
                                        HashMap<String, String> dataMap = new HashMap<>();

                                        JSONObject itemsObjects = dataItemArrays.getJSONObject(i);

                                        itemContentObject = itemsObjects.
                                                getJSONObject(Utils.OBJECT_ITEMS_CONTENT_DETAIL);
                                        duration = itemContentObject.
                                                getString(Utils.KEY_DURATION);

                                        String mDurationInTimeFormat = Utils.
                                                getTimeFromString(duration);

                                        dataMap.put(Utils.KEY_DURATION, mDurationInTimeFormat);
                                        dataMap.put(Utils.KEY_URL_THUMBNAILS,
                                                tempVideoData.get(i).get(Utils.KEY_URL_THUMBNAILS));
                                        dataMap.put(Utils.KEY_TITLE,
                                                tempVideoData.get(i).get(Utils.KEY_TITLE));
                                        dataMap.put(Utils.KEY_VIDEO_ID,
                                                tempVideoData.get(i).get(Utils.KEY_VIDEO_ID));
                                        dataMap.put(Utils.KEY_PUBLISHED_AT,
                                                tempVideoData.get(i).get(Utils.KEY_PUBLISHED_AT));

                                        videoData.add(dataMap);

                                        adapterList.notifyItemInserted(videoData.size());

                                    }
                                    mIsStillLoading = true;

                                    tempVideoData.clear();
                                    tempVideoData = new ArrayList<>();

                                } else {
                                    if (isAppFirstLaunched && adapterList.getAdapterItemCount() <= 0) {
                                        noResultView();
                                    }
                                    disableLoadmore();
                                }

                            } catch (JSONException e) {
                                Log.d(Utils.TAG_FANDROID + TAG,
                                        "JSON Parsing error: " + e.getMessage());
                                prgLoading.setVisibility(View.GONE);
                            }
                            prgLoading.setVisibility(View.GONE);
                        }
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Activity activity = getActivity();
                        if (activity != null && isAdded()) {
                            Log.d(Utils.TAG_FANDROID + TAG, "on Error Response: " + error.getMessage());
                            try {
                                String msgSnackBar;
                                if (error instanceof NoConnectionError) {
                                    msgSnackBar = getResources().getString(R.string.no_internet_connection);
                                } else {
                                    msgSnackBar = getResources().getString(R.string.response_error);
                                }

                                if (videoData.size() == 0) {
                                    retryView();
                                }

                                Utils.showSnackBar(getActivity(), msgSnackBar);
                                prgLoading.setVisibility(View.GONE);

                            } catch (Exception e) {
                                Log.d(Utils.TAG_FANDROID + TAG, "failed catch volley " + e.toString());
                                prgLoading.setVisibility(View.GONE);
                            }
                        }
                    }
                }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(Utils.ARG_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).getRequestQueue().add(request);
    }

    private void retryView() {
        mLytRetry.setVisibility(View.VISIBLE);
        ultimateRecyclerView.setVisibility(View.GONE);
        mLblNoResult.setVisibility(View.GONE);
    }

    private void haveResultView() {
        mLytRetry.setVisibility(View.GONE);
        ultimateRecyclerView.setVisibility(View.VISIBLE);
        mLblNoResult.setVisibility(View.GONE);
    }

    private void noResultView() {
        mLytRetry.setVisibility(View.GONE);
        ultimateRecyclerView.setVisibility(View.GONE);
        mLblNoResult.setVisibility(View.VISIBLE);

    }

    private void disableLoadmore() {
        mIsStillLoading = false;
        if (ultimateRecyclerView.isLoadMoreEnabled()) {
            ultimateRecyclerView.disableLoadmore();
        }
        adapterList.notifyDataSetChanged();
    }

    @OnClick(R.id.raisedRetry)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.raisedRetry:
                prgLoading.setVisibility(View.VISIBLE);
                haveResultView();
                getVideoData();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}