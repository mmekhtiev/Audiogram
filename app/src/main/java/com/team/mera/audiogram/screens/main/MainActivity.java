package com.team.mera.audiogram.screens.main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.team.mera.audiogram.R;
import com.team.mera.audiogram.models.TrackDescription;
import com.team.mera.audiogram.screens.common.BaseFragment;
import com.team.mera.audiogram.screens.common.BasePermissionActivity;
import com.team.mera.audiogram.screens.common.FragmentListener;
import com.team.mera.audiogram.screens.home.HomeFragment;
import com.team.mera.audiogram.utils.DrawUtils;
import com.team.mera.audiogram.utils.FileUtils;
import com.team.mera.audiogram.utils.NotificationUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends BasePermissionActivity implements FragmentListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Unbinder mUnbinder;
    private ArrayList<TrackDescription> selectedToComposit;


    @Override
    protected String[] getDesiredPermissions() {
        return new String[] {"android.permission.READ_EXTERNAL_STORAGE"};
    }

    @Override
    protected void onPermissionDenied() {
        NotificationUtils.showToast(this, "Storage reading is necessary for the application");
        onBackPressed();
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
        }

        if (savedInstanceState == null) {
            open(new HomeFragment(), false);
        }
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void open(BaseFragment fragment, boolean useBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (useBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.replace(R.id.main_content, fragment).commit();
    }

    @Override
    public void openHome() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        open(new HomeFragment(), false);
    }

    @Override
    public void back() {
        onBackPressed();
    }

    @Override
    public void setDescriptions(ArrayList<TrackDescription> descriptions) {
        selectedToComposit = descriptions;
    }

    @Override
    public ArrayList<TrackDescription> getDescriptions() {
        return selectedToComposit;
    }
}
