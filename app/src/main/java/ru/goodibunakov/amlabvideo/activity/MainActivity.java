package ru.goodibunakov.amlabvideo.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.youtube.player.YouTubePlayer;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.goodibunakov.amlabvideo.R;
import ru.goodibunakov.amlabvideo.fragments.ChannelVideoFragment;
import ru.goodibunakov.amlabvideo.fragments.VideoFragment;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnFullscreenListener,
        ChannelVideoFragment.OnVideoSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragment_container)
    FrameLayout layoutList;

    private Drawer drawer;
    private View decorView;

    private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private VideoFragment videoFragment;
    private boolean isFullscreen;

    private String[] channelNames;
    private String[] channelId;
    private String[] videoTypes;

    private int selectedDrawerItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Resources resources = getResources();
        channelNames = resources.getStringArray(R.array.channel_names);
        channelId = resources.getStringArray(R.array.channel_id);
        videoTypes = resources.getStringArray(R.array.video_types);

        PrimaryDrawerItem[] primaryDrawerItems = new PrimaryDrawerItem[channelId.length];

        for (int i = 0; i < channelId.length; i++) {
            primaryDrawerItems[i] = new PrimaryDrawerItem()
                    .withName(channelNames[i])
                    .withIdentifier(i)
                    .withSelectable(false);
        }

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();

        drawer = new DrawerBuilder(this)
                .withActivity(this)
                .withAccountHeader(accountHeader)
                .withDisplayBelowStatusBar(true)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(primaryDrawerItems)
                .addStickyDrawerItems(new SecondaryDrawerItem()
                        .withName(getString(R.string.about))
                        .withIdentifier(channelId.length - 1)
                        .withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                    }
                })
                .withShowDrawerOnFirstLaunch(true)
                .build();

    }

    @Override
    public void onFullscreen(boolean b) {

    }

    @Override
    public void onVideoSelected(String ID) {

    }
}